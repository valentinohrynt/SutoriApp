package com.inoo.sutoriapp.ui.story.ui.addstory

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.location.Location
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.switchmaterial.SwitchMaterial
import com.inoo.sutoriapp.databinding.ActivityAddStoryBinding
import com.inoo.sutoriapp.ui.customview.CustomEditText
import com.inoo.sutoriapp.utils.Utils.compressImageSize
import com.inoo.sutoriapp.utils.Utils.createCustomTempFile
import com.inoo.sutoriapp.utils.Utils.showToast
import com.inoo.sutoriapp.utils.Utils.uriToFile
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.utils.Utils.rotateImageIfRequired
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.FileOutputStream

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class AddStoryActivity : AppCompatActivity() {
    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding!!

    private val requestCodePermission = 10
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        AddStoryViewModelFactory(this)
    }

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var cameraXContainer: ConstraintLayout
    private lateinit var previewView: PreviewView
    private lateinit var buttonCapture: ImageButton
    private lateinit var buttonFlipCamera: ImageButton
    private lateinit var buttonGallery: Button

    private lateinit var addStoryContainer: ConstraintLayout
    private lateinit var imagePreview: ImageView
    private lateinit var edAddDescription: CustomEditText
    private lateinit var buttonAdd: ImageButton
    private lateinit var switchLocation: SwitchMaterial

    private lateinit var progressBar: ProgressBar

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationIcon: ImageView

    private var currentImageUri: Uri? = null
    private var isFrontCamera: Boolean = false
    private var isToastShown: Boolean = false
    private lateinit var imageCapture: ImageCapture

    private var lat: Double = 0.0
    private var long: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraXContainer = binding.cameraxContainer
        previewView = binding.previewView
        buttonFlipCamera = binding.buttonFlipCamera
        buttonCapture = binding.buttonCapture
        buttonGallery = binding.buttonGallery

        addStoryContainer = binding.addStoryContainer
        imagePreview = binding.ivImagePreview
        edAddDescription = binding.edAddDescription
        buttonAdd = binding.buttonAdd

        switchLocation = binding.switchLocation
        locationIcon = binding.locationIcon

        progressBar = binding.progressBar

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupView()

    }

    private fun setupView() {
        requestPermissions()

        buttonFlipCamera.setOnClickListener {
            flipCamera()
        }

        buttonCapture.setOnClickListener {
            takePhoto()
        }

        buttonGallery.setOnClickListener {
            openGallery()
        }

        buttonAdd.setOnClickListener {
            sendStory()
        }

        switchLocation.setOnClickListener {
            if (switchLocation.isChecked) {
                locationIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_location_on
                    )
                )
                getUserLocation()
            } else {
                locationIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_location_off
                    )
                )
            }
        }
    }

    private fun requestPermissions() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            showPermissionDialog()
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun allPermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                baseContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showPermissionDialog() {
        ActivityCompat.requestPermissions(this, requiredPermissions, requestCodePermission)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodePermission) {
            if (allPermissionsGranted()) {
                startCamera()
            }
        }
    }

    private fun startCamera() {
        if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            previewView.scaleX = -1f
        } else {
            previewView.scaleX = 1f
        }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                showToast(
                    this,
                    getString(R.string.failed_camera)
                )
                Log.e("AddStoryActivity", "$e")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun sendStory() {
        if (currentImageUri == null) {
            showToast(this@AddStoryActivity, getString(R.string.image_required))
            return
        }

        val imageFile = uriToFile(currentImageUri!!, this).compressImageSize()
        val description = edAddDescription.text.toString()
        val latString = lat.toString()
        val longString = long.toString()

        if (description.isEmpty()) {
            showToast(this@AddStoryActivity, getString(R.string.description_required))
            return
        }

        val requestBodyDescription =
            RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        val requestLat = RequestBody.create("text/plain".toMediaTypeOrNull(), latString)
        val requestLong = RequestBody.create("text/plain".toMediaTypeOrNull(), longString)

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
        val photoPart = MultipartBody.Part.createFormData("photo", imageFile.name, requestFile)

        postStory(requestBodyDescription, photoPart, requestLat, requestLong)

        observeLoading()
        observeUploadResponse()
        observeError()
    }

    private fun observeLoading() {
        addStoryViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun observeUploadResponse() {
        addStoryViewModel.uploadResponse.observe(this) { uploadResponse ->
            if (uploadResponse != null && !isToastShown) {
                showToast(this, getString(R.string.post_success))
                isToastShown = true
                addStoryViewModel.clearError()
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun postStory(
        requestBodyDescription: RequestBody,
        photoPart: MultipartBody.Part,
        requestLat: RequestBody,
        requestLong: RequestBody
    ) {
        addStoryViewModel.postAddStory(requestBodyDescription, photoPart, requestLat, requestLong)
    }

    private fun observeError() {
        addStoryViewModel.error.observe(this) { error ->
            if (error != null) {
                showToast(this, getString(R.string.post_failed))
                progressBar.visibility = View.GONE
                addStoryViewModel.clearError()
            }
        }
    }

    private fun flipCamera() {
        if (!isFrontCamera) {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            isFrontCamera = true
            startCamera()
        } else {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            isFrontCamera = false
            startCamera()
        }
    }

    private fun takePhoto() {
        if (!::imageCapture.isInitialized) {
            return showToast(this, getString(R.string.camera_not_ready))
        }

        val photoFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    currentImageUri = output.savedUri

                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                    val rotatedBitmap = rotateImageIfRequired(bitmap, currentImageUri!!)

                    val rotatedFile = createCustomTempFile(application)
                    val outputStream = FileOutputStream(rotatedFile)
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    imagePreview.setImageURI(currentImageUri)
                    cameraXContainer.visibility = ConstraintLayout.GONE
                    addStoryContainer.visibility = ConstraintLayout.VISIBLE

                    currentImageUri = Uri.fromFile(rotatedFile)
                }

                override fun onError(exc: ImageCaptureException) {
                    showToast(this@AddStoryActivity, getString(R.string.take_picture_failed))
                }
            }
        )
    }

    private fun openGallery() {
        Log.d("AddStoryActivity", "Opening gallery")
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        launcherGallery.launch(intent)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                currentImageUri = it

                imagePreview.post {
                    imagePreview.setImageURI(it)
                    cameraXContainer.visibility = ConstraintLayout.GONE
                    addStoryContainer.visibility = ConstraintLayout.VISIBLE
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getUserLocation()
        } else {
            showToast(this, getString(R.string.location_permission_denied))
        }
    }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        lat = latitude
                        long = longitude
                    } else {
                        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
                .addOnFailureListener {
                }

        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

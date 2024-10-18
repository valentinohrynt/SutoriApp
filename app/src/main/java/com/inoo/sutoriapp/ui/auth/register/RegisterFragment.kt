package com.inoo.sutoriapp.ui.auth.register

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.databinding.FragmentRegisterBinding
import com.inoo.sutoriapp.ui.customview.CustomEditText
import com.inoo.sutoriapp.ui.customview.CustomButton
import com.inoo.sutoriapp.ui.customview.EmailEditText
import com.inoo.sutoriapp.ui.customview.PasswordEditText
import com.inoo.sutoriapp.utils.Utils.showToast

class RegisterFragment : Fragment() {
    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var isToastShown = false

    private lateinit var registerButton: CustomButton
    private lateinit var nameEditText: CustomEditText
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var loginSpan: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var overlay: FrameLayout
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerButton = binding.registerButton
        nameEditText = binding.edRegisterName
        emailEditText = binding.edRegisterEmail
        passwordEditText = binding.edRegisterPassword
        loginSpan = binding.tvLogin
        progressBar = binding.progressBar
        overlay = binding.overlay

        val spannableString = SpannableString(loginSpan.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val start = loginSpan.text.indexOf(getString(R.string.login_now))
        val end = start + getString(R.string.login_now).length
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.md_theme_primary))
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        loginSpan.text = spannableString
        loginSpan.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        setRegisterButtonEnable()

        nameEditText.hint = getString(R.string.name_hint)
        emailEditText.hint = getString(R.string.email_hint)
        passwordEditText.hint = getString(R.string.password_hint)

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    setRegisterButtonEnable()
                } else {
                    setRegisterButtonDisable()
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                checkForEditTextErrors()
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                checkForEditTextErrors()
            }
        })

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            handleRegister(name, email, password)
        }

        observeLoading()
        observeError()
    }

    private fun checkForEditTextErrors() {
        if (emailEditText.error != null || passwordEditText.error != null) {
            setRegisterButtonDisable()
        } else {
            setRegisterButtonEnable()
        }
    }

    private fun observeLoading() {
        registerViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                overlay.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE

                overlay.alpha = 0f
                overlay.animate()
                    .alpha(0.5f)
                    .setDuration(300)
                    .setListener(null)
            } else {
                overlay.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        overlay.visibility = View.GONE
                        progressBar.visibility = View.GONE
                    }
            }
        }
    }

    private fun observeError() {
        registerViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                if (errorMessage == "emailTaken") {
                    showToast(requireContext(), getString(R.string.email_taken))
                    setRegisterButtonEnable()
                    registerViewModel.clearError()
                }
                else {
                    showToast(requireContext(), getString(R.string.register_failed_no_internet))
                    setRegisterButtonEnable()
                    registerViewModel.clearError()
                }
            }
        }
    }

    private fun handleRegister(name: String, email: String, password: String) {
        setRegisterButtonDisable()
        registerViewModel.handleRegisterResult(name, email, password)

        registerViewModel.registerResponse.observe(viewLifecycleOwner) { registerResponse ->
            if (registerResponse != null) {
                val message = registerResponse.message
                if (message != null && !isToastShown) {
                    showToast(requireContext(), getString(R.string.register_success))
                    isToastShown = true

                    if (findNavController().currentDestination?.id != R.id.loginFragment) {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                }
            } else {
                setRegisterButtonEnable()
                showToast(requireContext(), getString(R.string.register_failed))
            }
        }
    }

    private fun setRegisterButtonEnable() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        registerButton.isEnabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
    }

    private fun setRegisterButtonDisable() {
        registerButton.isEnabled = false
    }
}
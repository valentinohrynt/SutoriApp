package com.inoo.sutoriapp.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.databinding.FragmentLoginBinding
import com.inoo.sutoriapp.ui.customview.CustomButton
import com.inoo.sutoriapp.ui.customview.EmailEditText
import com.inoo.sutoriapp.ui.customview.PasswordEditText
import com.inoo.sutoriapp.ui.story.ui.StoryActivity
import com.inoo.sutoriapp.utils.EspressoIdlingResource.idlingResource
import com.inoo.sutoriapp.utils.Utils.showToast
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var isToastShown = false

    private lateinit var loginButton: CustomButton
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var registerSpan: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var overlay: FrameLayout

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton = binding.loginButton
        emailEditText = binding.edLoginEmail
        passwordEditText = binding.edLoginPassword
        registerSpan = binding.tvRegister
        progressBar = binding.progressBar
        overlay = binding.overlay

        val spannableString = SpannableString(registerSpan.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val start = registerSpan.text.indexOf(getString(R.string.register_now))
        val end = start + getString(R.string.register_now).length
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.md_theme_primary))
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        registerSpan.text = spannableString
        registerSpan.movementMethod = LinkMovementMethod.getInstance()

        setLoginButtonEnable()

        emailEditText.hint = getString(R.string.email_hint)
        passwordEditText.hint = getString(R.string.password_hint)

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
        checkLoginStatus()
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            handleLogin(email, password)
        }
        observeLoading()
        observeError()
    }

    private fun checkForEditTextErrors() {
        if (emailEditText.error != null || passwordEditText.error != null) {
            setLoginButtonDisable()
        } else {
            setLoginButtonEnable()
        }
    }

    private fun checkLoginStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.token.combine(loginViewModel.name) { token, name ->
                token to name
            }.collect { (token, name) ->
                if (token.isNotEmpty() && name.isNotEmpty()) {
                    val intent = Intent(requireContext(), StoryActivity::class.java)
                    intent.putExtra("name", name)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }


    private fun observeLoading() {
        loginViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                overlay.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE

                overlay.alpha = 0f
                overlay.animate()
                    .alpha(0.8f)
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
        loginViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                if (!idlingResource.isIdleNow) {
                    idlingResource.decrement()
                }
                showToast(requireContext(), errorMessage)
                setLoginButtonEnable()
                loginViewModel.clearError()
            }
        }
    }

    private fun handleLogin(email: String, password: String) {
        idlingResource.increment()
        try {
            setLoginButtonDisable()
            loginViewModel.handleLoginResult(email, password)

            loginViewModel.loginResponse.observe(viewLifecycleOwner) { loginResponse ->
                if (loginResponse != null) {
                    val token = loginResponse.loginResult?.token
                    val name = loginResponse.loginResult?.name
                    Log.d("LoginFragment", "Token: $token, Name: $name")
                    if (token != null && name != null && !isToastShown) {
//                        loginViewModel.saveSession(token, name)
                        val widgetSharedPreferences = requireContext().getSharedPreferences(
                            "SutoriAppWidgetSharedPreferences",
                            Context.MODE_PRIVATE
                        )
                        widgetSharedPreferences.edit().putString("token", token).apply()
                        showToast(requireContext(), getString(R.string.login_success))
                        isToastShown = true
                        if (token.isNotEmpty() && name.isNotEmpty()) {
                            val intent = Intent(requireContext(), StoryActivity::class.java)
                            intent.putExtra("name", name)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                } else {
                    showToast(requireContext(), getString(R.string.login_failed))
                    setLoginButtonEnable()
                }
            }
        } finally {
            if (!idlingResource.isIdleNow) {
                idlingResource.decrement()
            }
        }
    }


    private fun setLoginButtonEnable() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
    }

    private fun setLoginButtonDisable() {
        loginButton.isEnabled = false
    }
}
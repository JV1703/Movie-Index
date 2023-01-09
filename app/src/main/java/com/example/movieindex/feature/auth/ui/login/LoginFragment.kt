package com.example.movieindex.feature.auth.ui.login

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.movieindex.core.common.extensions.collectLatestLifecycleFlow
import com.example.movieindex.core.common.extensions.makeToast
import com.example.movieindex.databinding.FragmentLoginBinding
import com.example.movieindex.feature.auth.AuthViewModel
import com.github.razir.progressbutton.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()
    private val navArgs: LoginFragmentArgs by navArgs()
    private var arg: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arg = navArgs.authArgs
        arg?.let {
            when (it) {
                AuthViewModel.AuthNavigationArgs.Register.name -> {
                    Snackbar.make(binding.root,
                        "please verify your account through your email",
                        Snackbar.LENGTH_SHORT).setAction("Ok") {}.show()
                }
                AuthViewModel.AuthNavigationArgs.ForgetPassword.name -> {
                    Snackbar.make(binding.root,
                        "Password reset instruction has been sent to your email",
                        Snackbar.LENGTH_SHORT).setAction("Ok") {}.show()
                }
            }
        }
        bindProgressButton(binding.loginButton)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameTil.editText?.text.toString()
            val password = binding.passwordTil.editText?.text.toString()
            Timber.i("login - username: $username, password: $password")
            loginUser(username = username, password = password)
        }

        binding.registerCtaHighlight.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment(authArgs = AuthViewModel.AuthNavigationArgs.Register)
            findNavController().navigate(action)
        }

        binding.forgetPassword.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment(authArgs = AuthViewModel.AuthNavigationArgs.ForgetPassword)
            findNavController().navigate(action)
        }

        collectLatestLifecycleFlow(viewModel.authUiState) { authUiState: AuthViewModel.AuthUiState ->
            Timber.i("isLoggedIn: $authUiState")
            when (authUiState) {
                is AuthViewModel.AuthUiState.IsNotLoggedIn -> {
                    if (authUiState.isLoading) {
                        binding.loginButton.attachTextChangeAnimator {
                            fadeOutMills = 150
                            fadeInMills = 150
                        }

                        binding.loginButton.showProgress {
                            buttonText = "Logging In"
                            gravity = DrawableButton.GRAVITY_TEXT_END

                            progressColor = Color.WHITE
                        }
                    } else {
                        binding.loginButton.hideProgress("Log In")
                    }
                }
                is AuthViewModel.AuthUiState.IsLoggedIn -> {
                    val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
                    findNavController().navigate(action)
                }
            }

        }

        collectLatestLifecycleFlow(viewModel.loginEvents) { authEvents: AuthViewModel.AuthEvents ->

            when (authEvents) {
                is AuthViewModel.AuthEvents.AuthError -> {
                    val msg = authEvents.errMsg
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).setAction("Ok") {}
                        .show()
                }
                is AuthViewModel.AuthEvents.InvalidInput -> {
                    val msg = authEvents.errMsg
                    makeToast(msg)
                }
                is AuthViewModel.AuthEvents.Success -> {
                    val msg = "Login success"
                    makeToast(msg)
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loginUser(username: String, password: String) {
        viewModel.validateUserLogin(username, password)
    }

}
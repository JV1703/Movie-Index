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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


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
            loginUser(username = binding.usernameTil.editText?.text.toString(),
                password = binding.passwordTil.editText?.text.toString())
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

        collectLatestLifecycleFlow(viewModel.uiState.map { it.isLoggedIn }
            .distinctUntilChanged()) { isLoggedIn: Boolean ->
            if (isLoggedIn) {
                val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
                findNavController().navigate(action)
            }
        }

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            if (uiState.isLoading) {
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

            uiState.userMsg?.let { msg ->
                makeToast(msg)
                viewModel.userMsgShown()
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
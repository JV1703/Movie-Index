package com.example.movieindex.feature.auth.ui.register

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.movieindex.core.data.remote.NetworkConstants.TMDB_RESET_PASSWORD_SUCCESS_CHECKER
import com.example.movieindex.core.data.remote.NetworkConstants.TMDB_RESET_REGISTER_SUCCESS_CHECKER
import com.example.movieindex.databinding.FragmentRegisterBinding
import com.example.movieindex.feature.auth.AuthViewModel
import timber.log.Timber

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val navArgs: RegisterFragmentArgs by navArgs()
    private var destination: AuthViewModel.AuthNavigationArgs? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        destination = navArgs.authArgs

        binding.swipeRefresh.setOnRefreshListener {
            binding.webView.reload()
            binding.swipeRefresh.isRefreshing = false
        }

        destination?.let {
            binding.webView.apply {
                val keySentence = getKeySentence(it)
                Timber.i("args: $it")
                Timber.i("key sentence: $keySentence")
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        binding.progressInd.isGone = false
                    }


                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        binding.progressInd.isGone = true
                    }
                }
                loadUrl(it.url)
                keySentence.let {
                    Timber.i("key sentence: $keySentence")
                    findAllAsync(it)
                    setFindListener(findListener)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val action =
                        RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(authArgs = null)
                    findNavController().navigate(action)
                }
            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getKeySentence(destination: AuthViewModel.AuthNavigationArgs): String =
        when (destination) {
            AuthViewModel.AuthNavigationArgs.Register -> {
                TMDB_RESET_REGISTER_SUCCESS_CHECKER
            }
            AuthViewModel.AuthNavigationArgs.ForgetPassword -> {
                TMDB_RESET_PASSWORD_SUCCESS_CHECKER
            }
        }

    private val findListener =
        WebView.FindListener { activeMatchOrdinal: Int, numberOfMatches: Int, isDoneCounting: Boolean ->

            if (numberOfMatches > 0 && isDoneCounting) {
                val action =
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(authArgs = destination!!.name)
                findNavController().navigate(action)
            }

        }
}
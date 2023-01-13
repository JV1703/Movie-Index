package com.example.movieindex.feature.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movieindex.core.common.extensions.collectLatestLifecycleFlow
import com.example.movieindex.databinding.FragmentSplashBinding
import com.example.movieindex.feature.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectLatestLifecycleFlow(viewModel.uiState.map { it.isLoggedIn }) { isLoggedIn ->

            binding.logo.animate().setDuration(1_000).alpha(1F).withEndAction {
                if (isLoggedIn) {
                    val action = SplashFragmentDirections.actionSplashFragmentToMainFragment()
                    findNavController().navigate(action)
                } else {
                    val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                    findNavController().navigate(action)
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.movieindex.feature.main.ui.account

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.movieindex.R
import com.example.movieindex.core.common.extensions.collectLatestLifecycleFlow
import com.example.movieindex.core.common.extensions.loadCircleImage
import com.example.movieindex.core.common.extensions.makeToast
import com.example.movieindex.databinding.FragmentAccountBinding
import com.example.movieindex.feature.list.movie_list.ListType
import com.example.movieindex.feature.main.ui.MainFragmentDirections
import com.github.razir.progressbutton.DrawableButton
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private var _rootNavController: NavController? = null
    private val rootNavController get() = _rootNavController!!

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        _rootNavController = activity?.let { activity ->
            Navigation.findNavController(activity, R.id.root_nav_container)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logOutButton.setOnClickListener {
            viewModel.logout()
        }

        collectLatestLifecycleFlow(viewModel.uiState.map { it.isLoggedIn }
            .distinctUntilChanged()) { isLoggedIn: Boolean ->
            if (!isLoggedIn) {
                val action = MainFragmentDirections.actionMainFragmentToLoginFragment()
                rootNavController.navigate(action)
            }
        }

        collectLatestLifecycleFlow(viewModel.uiState.map { it.userMsg }
            .distinctUntilChanged()) { msg ->
            msg?.let {
                makeToast(it)
                viewModel.userMsgShown()
            }
        }

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            val accountDetails = uiState.accountDetails
            val isLoading = uiState.isLoading

            if (isLoading) {
                binding.logOutButton.attachTextChangeAnimator {
                    fadeOutMills = 150
                    fadeInMills = 150
                }

                binding.logOutButton.showProgress {
                    buttonText = "Logging Out"
                    gravity = DrawableButton.GRAVITY_TEXT_END

                    progressColor = Color.BLACK
                }
            } else {
                binding.logOutButton.hideProgress("Log Out")
            }

            accountDetails?.let {
                binding.username.text =
                    it.name.ifEmpty { accountDetails.username }
                binding.profilePicture.loadCircleImage(it.avatarPath)
            }

            binding.favoriteCard.setOnClickListener {
                val action =
                    MainFragmentDirections.actionMainFragmentToMovieListFragment(listType = ListType.Favorite)
                rootNavController.navigate(action)
            }

            binding.watchlistCard.setOnClickListener {
                val action =
                    MainFragmentDirections.actionMainFragmentToMovieListFragment(listType = ListType.Watchlist)
                rootNavController.navigate(action)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
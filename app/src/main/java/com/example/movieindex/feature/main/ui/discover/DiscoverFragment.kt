package com.example.movieindex.feature.main.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.movieindex.R
import com.example.movieindex.databinding.FragmentDiscoverBinding

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!
    private var _rootNavController: NavController? = null
    private val rootNavController get() = _rootNavController!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        _rootNavController = activity?.let { activity ->
            Navigation.findNavController(activity, R.id.root_nav_container)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
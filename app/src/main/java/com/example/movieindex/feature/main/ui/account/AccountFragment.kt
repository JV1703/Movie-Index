package com.example.movieindex.feature.main.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.movieindex.R
import com.example.movieindex.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private var _rootNavController: NavController? = null
    private val rootNavController get() = _rootNavController!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
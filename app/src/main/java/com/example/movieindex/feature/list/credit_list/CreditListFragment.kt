package com.example.movieindex.feature.list.credit_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.example.movieindex.core.common.extensions.collectLatestLifecycleFlow
import com.example.movieindex.databinding.FragmentCreditListBinding
import com.example.movieindex.feature.list.credit_list.adapter.CastAdapter
import com.example.movieindex.feature.list.credit_list.adapter.CrewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditListFragment : Fragment() {

    private var _binding: FragmentCreditListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreditListViewModel by viewModels()

    private lateinit var castAdapter: CastAdapter
    private lateinit var crewAdapter: CrewAdapter
    private lateinit var creditAdapter: ConcatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreditListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCreditAdapter()

        collectLatestLifecycleFlow(viewModel.credits) { credits ->
            castAdapter.submitList(credits.casts)
            crewAdapter.submitList(credits.crews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCreditAdapter() {
        castAdapter = CastAdapter()
        castAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
        crewAdapter = CrewAdapter()
        crewAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
        creditAdapter = ConcatAdapter()
        creditAdapter.apply {
            addAdapter(castAdapter)
            addAdapter(crewAdapter)
        }

        binding.creditsRv.adapter = creditAdapter
    }

}
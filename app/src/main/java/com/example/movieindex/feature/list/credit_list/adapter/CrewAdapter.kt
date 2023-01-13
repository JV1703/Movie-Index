package com.example.movieindex.feature.list.credit_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieindex.core.common.CrewListHelper
import com.example.movieindex.core.common.extensions.loadErrorImage
import com.example.movieindex.core.common.extensions.loadImageRoundedCorner
import com.example.movieindex.core.data.remote.NetworkConstants.BASE_IMG_URL
import com.example.movieindex.core.data.remote.NetworkConstants.CREDIT_IMG_SIZE
import com.example.movieindex.databinding.CreditListHeaderVhItemBinding
import com.example.movieindex.databinding.CreditListVhItemBinding

class CrewAdapter : ListAdapter<CrewListHelper, RecyclerView.ViewHolder>(DiffUtilCallback) {

    class CreditsViewHolder(private val binding: CreditListVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(crew: CrewListHelper.CrewDetails) {

            crew.crew.profilePath?.let {
                binding.profilePicture.loadImageRoundedCorner(source = BASE_IMG_URL + CREDIT_IMG_SIZE + it,
                    radius = 8)
            } ?: binding.profilePicture.loadErrorImage()

            binding.name.text = crew.crew.name
            binding.role.text = crew.crew.job
        }
    }

    class CreditsHeaderViewHolder(private val binding: CreditListHeaderVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: CrewListHelper.Header) {
            binding.subHeader.isGone = true
            binding.header.text = header.header
            binding.itemCount.text = header.headerDetails.toString()
        }
    }

    class CreditsSubHeaderViewHolder(private val binding: CreditListHeaderVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subHeader: CrewListHelper.SubHeader) {
            binding.header.isGone = true
            binding.itemCount.isGone = true
            binding.subHeader.text = subHeader.subHeader
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> {
                CreditsViewHolder(CreditListVhItemBinding.inflate(layoutInflater, parent, false))
            }
            2 -> {
                CreditsHeaderViewHolder(CreditListHeaderVhItemBinding.inflate(layoutInflater,
                    parent,
                    false))
            }
            3 -> {
                CreditsSubHeaderViewHolder(CreditListHeaderVhItemBinding.inflate(layoutInflater,
                    parent,
                    false))
            }
            else -> throw IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentCredit = getItem(position)

        when (holder) {
            is CreditsViewHolder -> {
                val cast = currentCredit as CrewListHelper.CrewDetails
                holder.bind(cast)
            }
            is CreditsHeaderViewHolder -> {
                val header = currentCredit as CrewListHelper.Header
                holder.bind(header)
            }
            is CreditsSubHeaderViewHolder -> {
                val subHeader = currentCredit as CrewListHelper.SubHeader
                holder.bind(subHeader)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CrewListHelper.CrewDetails -> {
                1
            }
            is CrewListHelper.Header -> {
                2
            }
            is CrewListHelper.SubHeader -> {
                3
            }
            else -> throw IllegalArgumentException("Invalid Item")
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<CrewListHelper>() {
        override fun areItemsTheSame(
            oldItem: CrewListHelper,
            newItem: CrewListHelper,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CrewListHelper,
            newItem: CrewListHelper,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
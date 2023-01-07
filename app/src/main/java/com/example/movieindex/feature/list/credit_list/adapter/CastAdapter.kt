package com.example.movieindex.feature.list.credit_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieindex.core.common.CastListHelper
import com.example.movieindex.core.common.extensions.loadErrorImage
import com.example.movieindex.core.common.extensions.loadImageRoundedCorner
import com.example.movieindex.core.data.remote.NetworkConstants.BASE_IMG_URL
import com.example.movieindex.core.data.remote.NetworkConstants.CREDIT_IMG_SIZE
import com.example.movieindex.databinding.CreditListHeaderVhItemBinding
import com.example.movieindex.databinding.CreditListVhItemBinding

class CastAdapter : ListAdapter<CastListHelper, RecyclerView.ViewHolder>(DiffUtilCallback) {

    class CreditsViewHolder(private val binding: CreditListVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cast: CastListHelper.CastDetails) {

            cast.cast.profilePath?.let {
                binding.profilePicture.loadImageRoundedCorner(source = BASE_IMG_URL + CREDIT_IMG_SIZE + it,
                    radius = 8)
            } ?: binding.profilePicture.loadErrorImage()

            binding.name.text = cast.cast.name
            binding.role.text = cast.cast.character
        }
    }

    class CreditsHeaderViewHolder(private val binding: CreditListHeaderVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: CastListHelper.Header) {
            binding.subHeader.isGone = true
            binding.header.text = header.header
            binding.itemCount.text = header.headerDetails.toString()
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
            else -> throw IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentCredit = getItem(position)

        when (holder) {
            is CreditsViewHolder -> {
                val cast = currentCredit as CastListHelper.CastDetails
                holder.bind(cast)
            }
            is CreditsHeaderViewHolder -> {
                val header = currentCredit as CastListHelper.Header
                holder.bind(header)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CastListHelper.CastDetails -> {
                1
            }
            is CastListHelper.Header -> {
                2
            }
            else -> throw IllegalArgumentException("Invalid Item")
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<CastListHelper>() {
        override fun areItemsTheSame(
            oldItem: CastListHelper,
            newItem: CastListHelper,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CastListHelper,
            newItem: CastListHelper,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
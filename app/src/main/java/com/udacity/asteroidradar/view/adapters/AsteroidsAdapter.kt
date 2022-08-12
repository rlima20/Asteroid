package com.udacity.asteroidradar.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.databinding.ListItemBinding
import com.udacity.asteroidradar.network.AsteroidDTO

class AsteroidsAdapter(
    val clickListener: AsteroidClickListener
) : ListAdapter<AsteroidDTO, AsteroidListViewwHoder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<AsteroidDTO>() {
        override fun areItemsTheSame(oldItem: AsteroidDTO, newItem: AsteroidDTO): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: AsteroidDTO, newItem: AsteroidDTO): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidListViewwHoder {
        return AsteroidListViewwHoder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidListViewwHoder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }
}

class AsteroidListViewwHoder(private var binding: ListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(listener: AsteroidClickListener, asteroidDTO: AsteroidDTO) {
        binding.asteroid = asteroidDTO
        binding.clickListener = listener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): AsteroidListViewwHoder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemBinding.inflate(layoutInflater, parent, false)
            return AsteroidListViewwHoder(binding)
        }
    }
}

class AsteroidClickListener(val clickListener: (asteroidDTO: AsteroidDTO) -> Unit) {
    fun onClick(asteroidDTO: AsteroidDTO) = clickListener(asteroidDTO)
}

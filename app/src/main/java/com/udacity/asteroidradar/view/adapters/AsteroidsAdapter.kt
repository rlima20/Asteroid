package com.udacity.asteroidradar.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.databinding.ListItemBinding
import com.udacity.asteroidradar.models.Asteroid

class AsteroidsAdapter(
    val clickListener: AsteroidClickListener
) : ListAdapter<Asteroid, AsteroidListViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidListViewHolder {
        return AsteroidListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidListViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }
}

class AsteroidListViewHolder(private var binding: ListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(listener: AsteroidClickListener, asteroid: Asteroid) {
        binding.asteroid = asteroid
        binding.clickListener = listener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): AsteroidListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemBinding.inflate(layoutInflater, parent, false)
            return AsteroidListViewHolder(binding)
        }
    }
}

class AsteroidClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}

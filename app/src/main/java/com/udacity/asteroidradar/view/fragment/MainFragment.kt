package com.udacity.asteroidradar.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.view.adapters.AsteroidClickListener
import com.udacity.asteroidradar.view.adapters.AsteroidsAdapter
import com.udacity.asteroidradar.viewmodel.MainViewModel

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(requireActivity().application))
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setupBinding(inflater).root
    }

    private fun setupBinding(inflater: LayoutInflater): FragmentMainBinding {
        val binding = FragmentMainBinding.inflate(inflater)
        val adapter = AsteroidsAdapter(AsteroidClickListener {})

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.asteroidRecycler.adapter = adapter

        viewModel.pod.observe(viewLifecycleOwner, Observer { pod ->
            binding.activityMainImageOfTheDay.downLoadImage(pod.url)
        })

        setHasOptionsMenu(true)
        return binding
    }

    private fun ImageView.downLoadImage(url: String) {
        Glide
            .with(this@MainFragment)
            .load(url)
            .centerCrop()
            .into(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}

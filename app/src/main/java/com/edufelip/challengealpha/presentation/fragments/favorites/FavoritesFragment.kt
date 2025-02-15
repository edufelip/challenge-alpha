package com.edufelip.challengealpha.presentation.fragments.favorites

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edufelip.challengealpha.R
import com.edufelip.challengealpha.data.local.room.models.Favorite
import com.edufelip.challengealpha.databinding.FragmentFavoritesBinding
import com.edufelip.challengealpha.presentation.base.decorations.SpaceStartAndEndItemDecoration
import com.edufelip.challengealpha.presentation.base.decorations.SpaceTopAndBottomItemDecoration
import com.edufelip.challengealpha.presentation.base.decorations.SpacesItemDecoration
import com.edufelip.challengealpha.presentation.base.models.StateUI
import com.edufelip.challengealpha.presentation.base.utils.hide
import com.edufelip.challengealpha.presentation.base.utils.show
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesFragment @Inject constructor(
    val adapter: FavoritesAdapter,
    var mFavoritesViewModel: FavoritesViewModel? = null
) : Fragment(), OnDeleteClick {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        observeFavorites()
        observeDeleteFavoriteState()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFavoritesViewModel?.getFavoritesList()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.favoritesToolbar.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.favorites_toolbar_title)
        }
        binding.favoritesToolbar.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        val spaceSmall = resources.getDimension(R.dimen.padding_or_margin_small).toInt()
        binding.favoritesRecyclerView.apply {
            adapter = this@FavoritesFragment.adapter.apply {
                setOnDeleteClick(this@FavoritesFragment)
            }
            layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
            addItemDecoration(
                SpacesItemDecoration(
                    spaceSmall,
                    false
                )
            )
            addItemDecoration(
                SpaceTopAndBottomItemDecoration(spaceSmall, 0)
            )
            addItemDecoration(
                SpaceStartAndEndItemDecoration(spaceSmall, true)
            )
        }
    }

    private fun setupViewModel() {
        mFavoritesViewModel = mFavoritesViewModel ?: ViewModelProvider(requireActivity()).get(
            FavoritesViewModel::class.java
        )
    }

    private fun observeFavorites() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mFavoritesViewModel?.favoritesStateList?.collect {
                    when (val state = it.getContentIfNotHandled()) {
                        is StateUI.Error -> showErrorToast()
                        is StateUI.Idle -> Unit
                        is StateUI.Processed -> {
                            if (state.data.isEmpty()) {
                                binding.favoritesListEmptyTextview.show()
                                binding.favoritesRecyclerView.hide()
                            }
                            adapter.setItems(state.data)
                        }
                        is StateUI.Processing -> Unit
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeDeleteFavoriteState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mFavoritesViewModel?.deleteFavoriteState?.collect {
                    when (it.getContentIfNotHandled()) {
                        is StateUI.Error -> showErrorToast()
                        is StateUI.Idle -> Unit
                        is StateUI.Processed -> mFavoritesViewModel?.getFavoritesList()
                        is StateUI.Processing -> Unit
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showDeleteDialog(item: Favorite) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.getString(R.string.label_yes)) { _, _ ->
            mFavoritesViewModel?.deleteFavoriteState(item)
        }
        builder.setNegativeButton(this.getString(R.string.label_no)) { _, _ ->
        }
        builder.setTitle(this.getString(R.string.fragment_favorites_delete_message) + " '${item.name}'?")
        builder.create().show()
    }

    private fun showErrorToast() {
        Toast.makeText(
            requireContext(),
            requireActivity().getString(R.string.favorites_fail_load),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDeleteClick(item: Favorite) {
        showDeleteDialog(item)
    }
}
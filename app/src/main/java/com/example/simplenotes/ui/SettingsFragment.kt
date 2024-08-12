package com.example.simplenotes.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import android.widget.Switch
import android.widget.TextView
import com.example.simplenotes.R

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var filterSwitch: Switch
    private lateinit var searchingSwitch: Switch
    private lateinit var filterLabel: TextView
    private lateinit var searchLabel: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterSwitch = view.findViewById(R.id.filter_switch)
        searchingSwitch = view.findViewById(R.id.searching_switch)
        filterLabel = view.findViewById(R.id.filter_label)
        searchLabel = view.findViewById(R.id.search_label)

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val isQuickSort = sharedPreferences.getBoolean("filterAlgorithm", true) // true untuk QuickSort
        val isKMP = sharedPreferences.getBoolean("searchAlgorithm", true) // true untuk KMP

        filterSwitch.isChecked = isQuickSort
        searchingSwitch.isChecked = isKMP

        updateLabels(isQuickSort, isKMP)

        filterSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("filterAlgorithm", isChecked).apply()
            updateLabels(isChecked, searchingSwitch.isChecked)
        }

        searchingSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("searchAlgorithm", isChecked).apply()
            updateLabels(filterSwitch.isChecked, isChecked)
        }
    }

    private fun updateLabels(isQuickSort: Boolean, isKMP: Boolean) {
        filterLabel.text = "Filtering Algorithm (default ${if (isQuickSort) "QuickSort" else "Merge Sort"})"
        searchLabel.text = "Searching Algorithm (default ${if (isKMP) "KMP" else "BM"})"

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.edit().putBoolean("filterAlgorithm", isQuickSort).apply()
        sharedPreferences.edit().putBoolean("searchAlgorithm", isKMP).apply()
    }

}

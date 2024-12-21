package com.example.weatherforecastapp.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import com.example.weatherforecastapp.model.Unit
import com.example.weatherforecastapp.repository.WeatherDbRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: WeatherDbRepository
): ViewModel() {
    private val _unitList = MutableStateFlow<List<Unit>>(emptyList())
    val unitList = _unitList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getUnits()
                    .distinctUntilChanged()
                    .collect { listOfUnits ->
                        if (listOfUnits.isEmpty()) {
                            Log.d("SettingsViewModel", "No units found in the database.")
                        } else {
                            _unitList.value = listOfUnits
                            Log.d("SettingsViewModel", "Loaded units: $listOfUnits")
                        }
                    }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error fetching units: ${e.message}", e)
            }
        }
    }

    fun insertUnit(unit: Unit) = viewModelScope.launch { repository.insertUnit(unit) }
    fun updateUnit(unit: Unit) = viewModelScope.launch { repository.updateUnit(unit) }
    fun deleteUnit(unit: Unit) = viewModelScope.launch { repository.deleteUnit(unit) }
    fun deleteAllUnits() = viewModelScope.launch { repository.deleteAllUnits() }


}
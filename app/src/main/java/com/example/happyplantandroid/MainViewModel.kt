package com.example.happyplantandroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _uiState = MutableStateFlow<PlantUiState>(
        PlantUiState.Empty(
            ::fetchConditions
        )
    )
    val uiState: StateFlow<PlantUiState> = _uiState

    init {
        _uiState.value = PlantUiState.Loading
        fetchConditions()
    }
    private fun fetchConditions() {
        viewModelScope.launch {
            _isRefreshing.update{true}
            delay(3000)
            try {
                val newData = repository.fetchConditions()
                _uiState.value = PlantUiState.Loaded(newData, ::fetchConditions)
            } catch (e: Exception) {
                _uiState.value = PlantUiState.Error(e.message ?: "Unknown Error")
            }
            _isRefreshing.update{false}
        }
    }
}
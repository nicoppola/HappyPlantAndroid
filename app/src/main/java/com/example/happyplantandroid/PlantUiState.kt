package com.example.happyplantandroid

sealed class PlantUiState {
    class Empty(val onRefresh:() -> Unit) : PlantUiState()
    object Loading : PlantUiState()
    class Loaded(val data: List<PlantUiModel>, val onRefresh:() -> Unit) : PlantUiState()
    class Error(val message: String) : PlantUiState()
}

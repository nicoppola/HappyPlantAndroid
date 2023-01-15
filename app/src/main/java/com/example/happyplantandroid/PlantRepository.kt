package com.example.happyplantandroid

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke

class PlantRepository(private val influxDbService: InfluxDbService) {

    suspend fun fetchConditions(): List<PlantUiModel> {
        return (Dispatchers.IO){
            influxDbService.getLatest()
        }
//        return (Dispatchers.IO) {
//            listOf(
//                PlantUiModel("Bedroom", "40%", "74°"),
//                PlantUiModel("Office", "32%", "68°"),
//                PlantUiModel("Kitchen", "38%", "71°")
//            )
//        }
    }
}
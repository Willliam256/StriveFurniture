package com.empire.strivefurniture.ui.repository

import com.empire.strivefurniture.models.FurnitureItem
import kotlinx.coroutines.flow.Flow

interface DataService {
    fun getFurnitureItems(): Flow<List<FurnitureItem>>
}
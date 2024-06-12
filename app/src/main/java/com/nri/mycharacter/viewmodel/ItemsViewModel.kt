package com.nri.mycharacter.viewmodel

import androidx.lifecycle.ViewModel
import com.nri.mycharacter.entity.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ItemsViewModel: ViewModel() {
    private val itemsState = MutableStateFlow<List<Item>>(listOf())
    val items: StateFlow<List<Item>> = itemsState.asStateFlow()

    fun add(item: Item) {
        itemsState.update { currentState ->
            currentState.plusElement(item)
        }
    }
}
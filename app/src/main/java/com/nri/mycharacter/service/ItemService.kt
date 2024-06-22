package com.nri.mycharacter.service

import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemType

interface ItemService {

    fun findAll(): List<Item>
    fun findById(id: Long): Item
    fun findFiltered(filter: ItemFilter): List<Item>
    fun delete(item: Item)
}

data class ItemFilter(
    val name: String,
    val types: List<ItemType>
)

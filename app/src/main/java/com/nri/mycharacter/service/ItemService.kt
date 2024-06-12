package com.nri.mycharacter.service

import com.nri.mycharacter.entity.Item

interface ItemService {

    fun findAll(): List<Item>
    fun findById(id: Long): Item
    fun findFiltered(filter: ItemFilter): List<Item>
}

data class ItemFilter(
    val name: String
)

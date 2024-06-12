package com.nri.mycharacter.service

import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.Item_
import com.nri.mycharacter.store.ObjectBox
import org.koin.core.component.KoinComponent

class ItemServiceImpl: ItemService, KoinComponent {

    private val itemBox = ObjectBox.store.boxFor(Item::class.java)

    override fun findAll(): List<Item> = itemBox
        .query()
        .order(Item_.name)
        .build()
        .findLazyCached()

    override fun findById(id: Long): Item {
        return itemBox[id]
    }

    override fun findFiltered(filter: ItemFilter): List<Item> {
        val queryBuilder = itemBox.query()
        if (filter.name.isNotBlank()) {
            queryBuilder.apply(Item_.name.contains(filter.name))
        }
        return queryBuilder.build().findLazyCached()
    }
}

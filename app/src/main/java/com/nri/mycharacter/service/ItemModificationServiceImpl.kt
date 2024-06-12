package com.nri.mycharacter.service

import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemModification
import com.nri.mycharacter.store.ObjectBox
import org.koin.core.component.KoinComponent

class ItemModificationServiceImpl: ItemModificationService, KoinComponent {
    private val itemModsBox = ObjectBox.store.boxFor(ItemModification::class.java)

    override fun findModificationsForItem(item: Item): List<ItemModification> {
        val itemType = item.itemType
        return itemModsBox
            .query()
            .filter { mod -> mod.itemTypeToCost.isEmpty() ||
                    mod.itemTypeToCost.containsKey(itemType.name)
            }
            .build()
            .find()
    }
}

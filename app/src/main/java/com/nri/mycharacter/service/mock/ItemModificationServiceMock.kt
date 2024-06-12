package com.nri.mycharacter.service.mock

import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemModification
import com.nri.mycharacter.service.ItemModificationService

class ItemModificationServiceMock: ItemModificationService {
    override fun findModificationsForItem(item: Item): List<ItemModification> {
        return listOf(
            ItemModification(
                name = "Modification 1"
            ),
            ItemModification(
                name = "Modification 2"
            )
        )
    }
}
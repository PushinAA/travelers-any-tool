package com.nri.mycharacter.service

import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemModification

interface ItemModificationService {
    fun findModificationsForItem(item: Item): List<ItemModification>
}

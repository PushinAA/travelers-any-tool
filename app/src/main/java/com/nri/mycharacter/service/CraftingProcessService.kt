package com.nri.mycharacter.service

import com.nri.mycharacter.entity.CraftingProcess
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.viewmodel.PrepareToCraftViewModel

interface CraftingProcessService {
    fun findAll(): List<CraftingProcess>
    fun findById(id: Long): CraftingProcess?
    fun createSpellTriggerItem(itemName: String, itemType: ItemType, viewModel: PrepareToCraftViewModel)
    fun save(process: CraftingProcess)
    suspend fun delete(process: CraftingProcess)
}
package com.nri.mycharacter.service.mock

import com.nri.mycharacter.entity.CraftingProcess
import com.nri.mycharacter.entity.CraftingProcess_
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.service.CraftingProcessService
import com.nri.mycharacter.utils.craftCost
import com.nri.mycharacter.utils.difficultClass
import com.nri.mycharacter.viewmodel.PrepareToCraftViewModel
import io.objectbox.relation.ToOne
import kotlinx.coroutines.CoroutineDispatcher

class CraftingProcessServiceMock: CraftingProcessService {
    override fun findAll(): List<CraftingProcess> {
        return listOf(findById(1L), findById(2L))
    }

    override fun findById(id: Long): CraftingProcess {
        val itemService = ItemServiceMock()
        val process = CraftingProcess()
        val item = itemService.findById(id)
        process.id = id
        process.mustPayed = item.craftCost()
        process.finalCost = item.marketCost
        process.finalDifficultClass = item.difficultClass()
        if (id == 1L) {
            process.moneyCrafted = item.marketCost.div(3)
        } else {
            process.moneyCrafted = item.marketCost
            process.masterwork = true
        }
        process.item = ToOne(process, CraftingProcess_.item)
        process.item.target = item
        return process
    }

    override fun createSpellTriggerItem(
        itemName: String, itemType: ItemType, viewModel: PrepareToCraftViewModel
    ) {}

    override fun save(process: CraftingProcess) {}
    override suspend fun delete(process: CraftingProcess) {}
}
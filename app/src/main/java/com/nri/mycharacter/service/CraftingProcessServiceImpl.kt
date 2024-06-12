package com.nri.mycharacter.service

import com.nri.mycharacter.entity.CraftingProcess
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.store.ObjectBox
import com.nri.mycharacter.viewmodel.PrepareToCraftViewModel
import io.objectbox.kotlin.awaitCallInTx
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class CraftingProcessServiceImpl: CraftingProcessService, KoinComponent {

    private val craftingProcessStore = ObjectBox.store.boxFor(CraftingProcess::class.java)

    override fun findAll(): List<CraftingProcess> {
        //craftingProcessStore.removeAll()
        return craftingProcessStore.all
    }

    override fun findById(id: Long): CraftingProcess = craftingProcessStore[id]

    override fun createSpellTriggerItem(itemName: String, itemType: ItemType, viewModel: PrepareToCraftViewModel) {
        val process = viewModel.collectSpellTrigger(itemName, itemType)
        save(process)
    }

    override fun save(process: CraftingProcess) {
        craftingProcessStore.put(process)
    }

    override suspend fun delete(process: CraftingProcess) {
        ObjectBox.store.awaitCallInTx {
            craftingProcessStore.remove(process.id)
        }
    }
}

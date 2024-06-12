package com.nri.mycharacter.module

import com.nri.mycharacter.infra.service.ChangesetService
import com.nri.mycharacter.service.CraftingProcessService
import com.nri.mycharacter.service.CraftingProcessServiceImpl
import com.nri.mycharacter.service.FeatService
import com.nri.mycharacter.service.FeatServiceImpl
import com.nri.mycharacter.service.ItemModificationService
import com.nri.mycharacter.service.ItemModificationServiceImpl
import com.nri.mycharacter.service.ItemService
import com.nri.mycharacter.service.ItemServiceImpl
import com.nri.mycharacter.viewmodel.ItemsViewModel
import com.nri.mycharacter.viewmodel.PrepareToCraftViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun MainModule() = module {
    includes(
        module { single { ItemServiceImpl() as ItemService } },
        module { single { FeatServiceImpl() as FeatService } },
        module { single { ItemModificationServiceImpl() as ItemModificationService } },
        module { single { CraftingProcessServiceImpl() as CraftingProcessService } },
        module { single { ChangesetService() } },
        module { viewModel { ItemsViewModel() } },
        module { viewModel { PrepareToCraftViewModel() } }
    )
}
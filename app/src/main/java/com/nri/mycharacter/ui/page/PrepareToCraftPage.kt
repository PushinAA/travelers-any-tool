package com.nri.mycharacter.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.service.CraftingProcessService
import com.nri.mycharacter.service.FeatService
import com.nri.mycharacter.service.ItemModificationService
import com.nri.mycharacter.service.ItemService
import com.nri.mycharacter.service.mock.CraftingProcessServiceMock
import com.nri.mycharacter.service.mock.FeatServiceMock
import com.nri.mycharacter.service.mock.ItemModificationServiceMock
import com.nri.mycharacter.service.mock.ItemServiceMock
import com.nri.mycharacter.ui.component.SwitchWithText
import com.nri.mycharacter.ui.component.CounterWithText
import com.nri.mycharacter.ui.component.DropdownList
import com.nri.mycharacter.ui.component.ItemContainer
import com.nri.mycharacter.ui.navigation.MainAppRoutes
import com.nri.mycharacter.utils.calculateDifficultClassForIgnoredPreqs
import com.nri.mycharacter.utils.craftingSkill
import com.nri.mycharacter.utils.modifiedCraftCost
import com.nri.mycharacter.utils.modifiedCraftTimeString
import com.nri.mycharacter.utils.modifiedDifficultClassString
import com.nri.mycharacter.utils.toCostString
import com.nri.mycharacter.viewmodel.PrepareToCraftViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

@Composable
fun PrepareToCraftPage(itemId: Long, navHostController: NavHostController) {
    val itemService = koinInject<ItemService>()
    val item = itemService.findById(itemId)
    val viewModel: PrepareToCraftViewModel = koinViewModel()
    val processService = koinInject<CraftingProcessService>()
    Column(
        modifier = Modifier.verticalScroll(
            state = rememberScrollState()
        )
    ) {
        ItemContainer(item = item) {}
        Spacer(modifier = Modifier.height(10.dp))
        CrafterModifiersBlock(viewModel = viewModel, itemType = item.itemType)
        Spacer(modifier = Modifier.height(10.dp))
        if (item.itemType.isMundane) {
            ItemModificationsBlock(viewModel = viewModel, item = item)
            Spacer(modifier = Modifier.height(10.dp))
        } else {
            RequirementsBlock(viewModel = viewModel, item = item)
            Spacer(modifier = Modifier.height(10.dp))
        }
        CalculationResultsBlock(viewModel = viewModel, item = item)
    }
    ExtendedFloatingActionButton(
        text = { Text(text = "Start crafting") },
        icon = { Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = "Start",
            modifier = Modifier.size(30.dp)
        ) },
        onClick = {
            val process = viewModel.collect(item)
            processService.save(process)
            navHostController.navigate(
                MainAppRoutes.CraftingProcess.route,
                NavOptions.Builder()
                    .setPopUpTo(
                        route = MainAppRoutes.StartingPage.route,
                        inclusive = false
                    ).build()
            )
        },
        modifier = Modifier
            .requiredSize(170.dp, 50.dp)
            .absoluteOffset(y = 320.dp)
    )
}

@Composable
fun CrafterModifiersBlock(
    viewModel: PrepareToCraftViewModel,
    itemType: ItemType
) {
    val openBlock = remember { mutableStateOf(true) }
    val discountTrait = viewModel.discountTrait.collectAsState()
    val participants = viewModel.coopCraftingParticipants.collectAsState()
    val fcb = viewModel.fcb.collectAsState()
    Column(
        modifier = Modifier.padding(start = 5.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openBlock.value = !openBlock.value }
        ) {
            Text(
                text = "Crafter modifiers",
                style = typography.headlineSmall
            )
            if (openBlock.value) {
                Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "Hide")
            } else {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Expand")
            }
        }
        if (openBlock.value) {
            Spacer(modifier = Modifier.height(10.dp))
            if (!itemType.isMundane) {
                SwitchWithText(
                    text = "Spark of Creation (or same 5% discount)",
                    checkedValue = discountTrait.value,
                    style = typography.bodyLarge
                ) {
                    viewModel.updateDiscountTraits(it)
                }
            }
            CounterWithText(
                counter = participants.value,
                text = "Cooperative crafting participants",
                onInc = { viewModel.incCoopCraft() },
                onDec = { viewModel.decCoopCraft() },
                style = typography.bodyLarge)
            if (!itemType.isMundane) {
                CounterWithText(
                    counter = fcb.value,
                    text = "Dwarf Wizard FCB",
                    onInc = { viewModel.incFcb() },
                    onDec = { viewModel.decFcb() },
                    style = typography.bodyLarge)
            }
        }
    }
}

@Composable
fun ItemModificationsBlock(
    viewModel: PrepareToCraftViewModel,
    item: Item
) {
    val itemModService = koinInject<ItemModificationService>()
    val openBlock = remember { mutableStateOf(true) }
    val masterwork = viewModel.masterwork.collectAsState()
    val strMod = viewModel.strMod.collectAsState()
    val count = viewModel.count.collectAsState()
    val modification = viewModel.modification.collectAsState()
    Column(
        modifier = Modifier.padding(start = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openBlock.value = !openBlock.value }
        ) {
            Text(
                text = "Item modifications",
                style = typography.headlineSmall
            )
            if (openBlock.value) {
                Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "Hide")
            } else {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Expand")
            }
        }
        if (openBlock.value) {
            Spacer(modifier = Modifier.height(10.dp))
            SwitchWithText(
                text = "Masterwork",
                checkedValue = masterwork.value,
                enabled = modification.value?.isMasterworkIncluded?.not() ?: true,
                style = typography.bodyLarge
            ) {
                viewModel.updateMasterwork(it)
            }
            if (viewModel.itemsWithStrMod.contains(item.name)) {
                CounterWithText(
                    counter = strMod.value,
                    text = "Strength rating",
                    onInc = { viewModel.incStrMod() },
                    onDec = { viewModel.decStrMod() },
                    style = typography.bodyLarge,
                    max = 5
                )
            }
            if (item.itemType == ItemType.AMMUNITION) {
                CounterWithText(
                    counter = count.value,
                    text = "Amount",
                    onInc = { viewModel.incCount() },
                    onDec = { viewModel.decCount() },
                    style = typography.bodyLarge,
                    min = 1
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            DropdownList(
                itemsSupplier = { itemModService.findModificationsForItem(item) },
                rowNameTransformation = { it.name ?: "" },
                onSelect = {
                    viewModel.updateModification(it)
                    if (it.isMasterworkIncluded) {
                        viewModel.updateMasterwork(true)
                    }
                },
                onClear = { viewModel.updateModification(null) },
                selectedItem = modification.value,
                placeholder = "Select item modification"
            )
        }
    }
}

@Composable
fun RequirementsBlock(
    viewModel: PrepareToCraftViewModel,
    item: Item
) {
    val featService = koinInject<FeatService>()
    val openBlock = remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.padding(start = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openBlock.value = !openBlock.value }
        ) {
            Text(
                text = "Requirements",
                style = typography.headlineSmall
            )
            if (openBlock.value) {
                Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "Hide")
            } else {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Expand")
            }
        }
        if (openBlock.value) {
            val requirements = item.requirements
            val skills = requirements
                .target
                .skills
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() } ?: listOf()
            for (skill in skills) {
                val checked = remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(10.dp))
                SwitchWithText(
                    text = skill,
                    checkedValue = checked.value,
                    style = typography.bodyLarge
                ) {
                    if (it) {
                        viewModel.removeIgnoredSkill(skill)
                    } else {
                        viewModel.addIgnoredSkill(skill)
                    }
                    checked.value = it
                }
            }
            val feats = requirements.target.feats?.map { it.trim() } ?: listOf()
            for (feat in feats) {
                val isItemCreation = featService.find(feat) != null
                val checked = remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(10.dp))
                SwitchWithText(
                    text = feat,
                    checkedValue = checked.value,
                    enabled = !isItemCreation,
                    style = typography.bodyLarge
                ) {
                    if (it) {
                        viewModel.removeIgnoredFeat(feat)
                    } else {
                        viewModel.addIgnoredFeat(feat)
                    }
                    checked.value = it
                }
            }
            val spells = requirements.target.spells?.map { it.trim() } ?: listOf()
            for (spell in spells) {
                val checked = remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(10.dp))
                SwitchWithText(
                    text = spell,
                    checkedValue = checked.value,
                    enabled = item.itemType != ItemType.STAFF,
                    style = typography.bodyLarge
                ) {
                    if (it) {
                        viewModel.removeIgnoredSpell(spell)
                    } else {
                        viewModel.addIgnoredSpell(spell)
                    }
                    checked.value = it
                }
            }
            val alternativeChoices = requirements.target.alternativeChoice.toList()
            for (choice in alternativeChoices) {
                val choiceStr = choice.choice?.joinToString(" or ")
                if (choiceStr.isNullOrBlank()) {
                    continue
                }
                val checked = remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(10.dp))
                SwitchWithText(
                    text = choiceStr,
                    checkedValue = checked.value,
                    style = typography.bodyLarge
                ) {
                    if (it) {
                        viewModel.removeIgnoredAlternativeChoice(choice)
                    } else {
                        viewModel.addIgnoredAlternativeChoice(choice)
                    }
                    checked.value = it
                }
            }
            val special = requirements.target.addPrep?.trim()
            if (!special.isNullOrBlank()) {
                val checked = remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(10.dp))
                SwitchWithText(
                    text = special,
                    checkedValue = checked.value,
                    style = typography.bodyLarge
                ) {
                    viewModel.updateIgnoredSpecial(checked.value)
                    checked.value = it
                }
            }
            val casterLevel = requirements.target.casterLevel
            if (casterLevel > 0) {
                val checked = remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(10.dp))
                SwitchWithText(
                    text = "creator must have at least ${casterLevel}CL",
                    checkedValue = checked.value,
                    style = typography.bodyLarge
                ) {
                    viewModel.updateIgnoredCasterLevel(checked.value)
                    checked.value = it
                }
            }
        }
    }
}

@Composable
fun CalculationResultsBlock(
    viewModel: PrepareToCraftViewModel,
    item: Item
) {
    val masterwork = viewModel.masterwork.collectAsState()
    val strMod = viewModel.strMod.collectAsState()
    val count = viewModel.count.collectAsState()
    val mod = viewModel.modification.collectAsState()
    val discount = viewModel.discountTrait.collectAsState()
    val ignoredSkills = viewModel.ignoredSkills.collectAsState()
    val ignoredFeats = viewModel.ignoredFeats.collectAsState()
    val ignoredSpells = viewModel.ignoredSpells.collectAsState()
    val ignoredSpecial = viewModel.ignoredSpecial.collectAsState()
    val ignoredCasterLevel = viewModel.ignoredCasterLevel.collectAsState()
    val ignoredAltChoices = viewModel.ignoredAlternativeChoices.collectAsState()
    val coopCraftParts = viewModel.coopCraftingParticipants.collectAsState()
    val fcb = viewModel.fcb.collectAsState()
    val dcForIgnoredPreqs = calculateDifficultClassForIgnoredPreqs(
        ignoredSkills.value,
        ignoredFeats.value,
        ignoredSpells.value,
        ignoredSpecial.value,
        ignoredCasterLevel.value,
        ignoredAltChoices.value
    )
    Column(
        modifier = Modifier.padding(start = 5.dp, bottom = 150.dp)
    ) {
        Text(text = "Results", style = typography.headlineSmall)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Final DC ${item.modifiedDifficultClassString(
                masterwork.value,
                mod.value,
                dcForIgnoredPreqs,
                strMod.value
            )}",
            style = typography.titleMedium
        )
        if (masterwork.value &&
            mod.value?.isMasterworkIncluded != true &&
            item.itemType != ItemType.SIEGE_ENGINE) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "DC 20 ${item.craftingSkill()} for masterwork components",
                style = typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "You must pay ${item.modifiedCraftCost(
                mod.value,
                discount.value,
                masterwork.value, 
                strMod.value,
                count.value
            ).toCostString()}",
            style = typography.titleMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "You must spent near ${item.modifiedCraftTimeString(
                masterwork.value,
                mod.value,
                dcForIgnoredPreqs,
                coopCraftParts.value,
                fcb.value)}",
            style = typography.titleMedium
        )
    }
}

@Preview
@Composable
fun PrepareToCraftMagicPreview() {
    val navHostController = rememberNavController()
    if (KoinPlatformTools.defaultContext().getOrNull() != null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PrepareToCraftPage(itemId = 1, navHostController)
        }
    } else {
        KoinApplication(
            application = {
                modules(
                    module { single { ItemServiceMock() as ItemService } },
                    module { single { ItemModificationServiceMock() as ItemModificationService } },
                    module { single { FeatServiceMock() as FeatService } },
                    module { single { CraftingProcessServiceMock() as CraftingProcessService } },
                    module { viewModel { PrepareToCraftViewModel() } }
                )
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PrepareToCraftPage(itemId = 1, navHostController)
            }
        }
    }
}

@Preview
@Composable
fun PrepareToCraftMundanePreview() {
    val navHostController = rememberNavController()
    if (KoinPlatformTools.defaultContext().getOrNull() != null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PrepareToCraftPage(itemId = 2, navHostController)
        }
    } else {
        KoinApplication(
            application = {
                modules(
                    module { single { ItemServiceMock() as ItemService } },
                    module { single { ItemModificationServiceMock() as ItemModificationService } },
                    module { single { FeatServiceMock() as FeatService } },
                    module { single { CraftingProcessServiceMock() as CraftingProcessService } },
                    module { viewModel { PrepareToCraftViewModel() } }
                )
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PrepareToCraftPage(itemId = 2, navHostController)
            }
        }
    }
}

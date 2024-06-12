package com.nri.mycharacter.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.service.CraftingProcessService
import com.nri.mycharacter.service.FeatService
import com.nri.mycharacter.service.ItemModificationService
import com.nri.mycharacter.service.ItemService
import com.nri.mycharacter.service.mock.CraftingProcessServiceMock
import com.nri.mycharacter.service.mock.FeatServiceMock
import com.nri.mycharacter.service.mock.ItemModificationServiceMock
import com.nri.mycharacter.service.mock.ItemServiceMock
import com.nri.mycharacter.ui.component.CounterWithText
import com.nri.mycharacter.ui.navigation.MainAppRoutes
import com.nri.mycharacter.utils.calcFinalCraftCost
import com.nri.mycharacter.utils.calcFinalCraftTime
import com.nri.mycharacter.utils.calcFinalDc
import com.nri.mycharacter.utils.toCostString
import com.nri.mycharacter.viewmodel.PrepareToCraftViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import kotlin.math.ceil

@Composable
fun PrepareToCraftItemFromSpellPage(
    navHostController: NavHostController,
    itemType: ItemType
) {
    val viewModel: PrepareToCraftViewModel = koinViewModel()
    val processService = koinInject<CraftingProcessService>()
    val itemName = remember {
        mutableStateOf(
            when (itemType) {
                ItemType.WAND -> "Some wand"
                ItemType.SCROLL -> "Some scroll"
                ItemType.POTION -> "Some potion"
                else -> "Some item"
            }
        )
    }
    Column(
        modifier = Modifier.verticalScroll(
            state = rememberScrollState()
        ),
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = itemName.value,
            onValueChange = { itemName.value = it },
            textStyle = typography.headlineMedium,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            isError = itemName.value.isBlank(),
            label = {
                Text(text = "Item name")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
        )
        SpellInfoBlock(viewModel = viewModel, itemType = itemType)
        Spacer(modifier = Modifier.height(10.dp))
        CrafterModifiersBlock(viewModel = viewModel, itemType = itemType)
        Spacer(modifier = Modifier.height(10.dp))
        CalculationResultsBlock(viewModel = viewModel, itemType = itemType)
    }
    ExtendedFloatingActionButton(
        text = { Text(text = "Start crafting") },
        icon = { Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = "Start",
            modifier = Modifier.size(30.dp)
        ) },
        onClick = {
            processService.createSpellTriggerItem(itemName.value, itemType, viewModel)
            navHostController.navigate(MainAppRoutes.CraftingProcess.route)
        },
        modifier = Modifier
            .requiredSize(170.dp, 50.dp)
            .absoluteOffset(y = 350.dp)
    )
}

@Composable
fun SpellInfoBlock(
    viewModel: PrepareToCraftViewModel,
    itemType: ItemType
) {
    val spellLevel = viewModel.spellLevel.collectAsState()
    val casterLevel = viewModel.casterLevel.collectAsState()
    Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp)) {
        Spacer(modifier = Modifier.height(10.dp))
        CounterWithText(
            counter = spellLevel.value,
            text = "Spell level",
            onInc = { viewModel.incSpellLevel() },
            onDec = { viewModel.decSpellLevel() },
            style = typography.bodyLarge,
            max = if (itemType == ItemType.WAND) 4 else if (itemType == ItemType.POTION) 3 else 9
        )
        CounterWithText(
            counter = casterLevel.value,
            text = "Caster level (minimum which you can cast spell)",
            onInc = { viewModel.incCasterLevel() },
            onDec = { viewModel.decCasterLevel() },
            style = typography.bodyLarge,
            min = 1,
            max = 20
        )
    }
}

@Composable
fun CalculationResultsBlock(
    viewModel: PrepareToCraftViewModel,
    itemType: ItemType
) {
    val spellLevel = viewModel.spellLevel.collectAsState()
    val casterLevel = viewModel.casterLevel.collectAsState()
    val discount = viewModel.discountTrait.collectAsState()
    val coopCraftParts = viewModel.coopCraftingParticipants.collectAsState()
    val fcb = viewModel.fcb.collectAsState()
    Column(
        modifier = Modifier.padding(start = 5.dp, bottom = 150.dp)
    ) {
        Text(text = "Results", style = typography.headlineSmall)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Final DC ${calcFinalDc(
                casterLevel.value
            )} ${itemType.baseCraftSkill.joinToString(" or ") {it.humanReadableName}}",
            style = typography.titleMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "You must pay ${calcFinalCraftCost(
                itemType,
                spellLevel.value,
                casterLevel.value,
                discount.value
            ).toCostString()}",
            style = typography.titleMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "You must spent near ${calcFinalCraftTime(
                itemType,
                spellLevel.value,
                casterLevel.value,
                discount.value,
                coopCraftParts.value,
                fcb.value
            )}",
            style = typography.titleMedium
        )
    }
}

@Preview
@Composable
fun PrepareToCraftItemFromSpellPagePreview() {
    val navHostController = rememberNavController()
    if (KoinPlatformTools.defaultContext().getOrNull() != null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PrepareToCraftItemFromSpellPage(navHostController, ItemType.WAND)
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
                PrepareToCraftItemFromSpellPage(navHostController, ItemType.WAND)
            }
        }
    }
}

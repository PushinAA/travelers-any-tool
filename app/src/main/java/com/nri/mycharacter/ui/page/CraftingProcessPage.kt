package com.nri.mycharacter.ui.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.sharp.Build
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.KeyboardArrowDown
import androidx.compose.material.icons.sharp.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nri.mycharacter.entity.CraftingProcess
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.service.CraftingProcessService
import com.nri.mycharacter.service.FeatService
import com.nri.mycharacter.service.ItemModificationService
import com.nri.mycharacter.service.ItemService
import com.nri.mycharacter.service.mock.CraftingProcessServiceMock
import com.nri.mycharacter.service.mock.FeatServiceMock
import com.nri.mycharacter.service.mock.ItemModificationServiceMock
import com.nri.mycharacter.service.mock.ItemServiceMock
import com.nri.mycharacter.ui.component.CircularProgressIndicatorWithContent
import com.nri.mycharacter.ui.component.CounterWithText
import com.nri.mycharacter.ui.component.SwitchWithText
import com.nri.mycharacter.utils.acceleratedDc
import com.nri.mycharacter.utils.calcFinalCost
import com.nri.mycharacter.utils.craftingSkill
import com.nri.mycharacter.utils.doCraft
import com.nri.mycharacter.utils.doCraftMasterworkComponents
import com.nri.mycharacter.utils.getMasterworkProgress
import com.nri.mycharacter.utils.getProgress
import com.nri.mycharacter.utils.getTimeSpent
import com.nri.mycharacter.utils.isFinished
import com.nri.mycharacter.utils.toCostString
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import kotlin.math.roundToInt

@Composable
fun CraftingProcessPage() {
    val craftingProcessService = koinInject<CraftingProcessService>()
    val processes = craftingProcessService.findAll()
        .sortedByDescending { it.getProgress() + it.getMasterworkProgress() }
    val processedState = remember { mutableStateOf(processes) }
    if (processes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Empty list",
                style = typography.headlineMedium,
                color = Color.LightGray
            )
        }
    } else {
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { padding ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                items(
                    items = processedState.value,
                    key = { process ->
                        process.id
                    }
                ) { process ->
                    val offsetX = remember { mutableFloatStateOf(0f) }
                    CraftingProcessCard(
                        process = process,
                        modifier = Modifier
                            .offset { IntOffset(offsetX.floatValue.roundToInt(), 0) }
                            .draggable(
                                state = rememberDraggableState { delta ->
                                    if (offsetX.floatValue + delta >= 0) {
                                        offsetX.floatValue += delta
                                    }
                                },
                                orientation = Orientation.Horizontal,
                                onDragStopped = {
                                    if (offsetX.floatValue > 100) {
                                        processedState.value =
                                            processedState.value.minusElement(process)
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Successfully deleted",
                                                actionLabel = "Rollback",
                                                withDismissAction = true,
                                                duration = SnackbarDuration.Short
                                            )
                                            when (result) {
                                                SnackbarResult.Dismissed -> craftingProcessService.delete(
                                                    process
                                                )

                                                SnackbarResult.ActionPerformed -> processedState.value =
                                                    processedState.value
                                                        .plusElement(
                                                            process
                                                        )
                                                        .sortedByDescending { it.getProgress() + it.getMasterworkProgress() }
                                            }
                                        }
                                    } else {
                                        offsetX.floatValue = 0f
                                    }
                                }
                            ),
                        onBuild = { finished ->
                            if (finished) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Finished! Now you must pay ${process.mustPayed.toCostString()}",
                                        withDismissAction = true
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CraftingProcessCard(
    process: CraftingProcess,
    modifier: Modifier = Modifier,
    onBuild: (Boolean) -> Unit = {}
) {
    val craftingProcessService = koinInject<CraftingProcessService>()
    OutlinedCard(
        modifier = modifier
            .width(width = 300.dp)
            .padding(top = 20.dp)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        val processState = remember {
            mutableStateOf(process, neverEqualPolicy())
        }
        val byHours = remember { mutableStateOf(false) }
        val adventuring = remember { mutableStateOf(false) }
        val accelerated = remember { mutableStateOf(false) }
        val checkResult = remember {
            mutableIntStateOf(process.finalDifficultClass)
        }
        val daysSpent = remember { mutableIntStateOf(1) }
        val hoursSpent = remember { mutableIntStateOf(4) }
        val expandedMasterwork = remember { mutableStateOf(false) }
        Text(
            text = "${
                processState
                    .value
                    .item
                    .target
                    .name ?: ""
            }${
                if (processState.value.count > 1) " (${processState.value.count})"
                else ""
            }",
            style = typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 5.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = 15.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 5.dp)
        ) {
            Text(text = "1d", style = typography.labelLarge)
            Spacer(modifier = Modifier.width(5.dp))
            Switch(
                checked = byHours.value,
                onCheckedChange = { byHours.value = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer
                ),
                thumbContent = {
                    Icon(imageVector = Icons.Outlined.Settings, contentDescription = "Change")
                }
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "1h", style = typography.labelLarge)
        }
        Spacer(modifier = Modifier.padding(top = 15.dp))
        Text(
            text = "Total cost: ${processState.value.calcFinalCost().toCostString()}",
            style = typography.bodyLarge,
            modifier = Modifier.padding(start = 5.dp)
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Money crafted: ${processState.value.moneyCrafted.toCostString()}",
            style = typography.bodyLarge,
            modifier = Modifier.padding(start = 5.dp)
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Time spent: ${processState.value.getTimeSpent()}",
            style = typography.bodyLarge,
            modifier = Modifier.padding(start = 5.dp)
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = "Required check: ${
                processState.value.finalDifficultClass.acceleratedDc(
                    processState.value.item.target.itemType,
                    accelerated.value
                )
            } ${processState.value.item.target.craftingSkill()}",
            style = typography.bodyLarge,
            modifier = Modifier.padding(start = 5.dp)
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        if (!processState.value.item.target.itemType.isMundane) {
            SwitchWithText(
                text = "Adventuring",
                checkedValue = adventuring.value,
                modifier = Modifier.padding(start = 5.dp),
                style = typography.bodyLarge
            ) {
                adventuring.value = it
            }
        }
        SwitchWithText(
            text = "Accelerated",
            checkedValue = accelerated.value,
            modifier = Modifier.padding(start = 5.dp),
            style = typography.bodyLarge
        ) {
            accelerated.value = it
            checkResult.intValue = processState.value.finalDifficultClass.acceleratedDc(
                processState.value.item.target.itemType,
                accelerated.value
            )
        }
        CounterWithText(
            counter = checkResult.intValue,
            text = "Check result",
            onInc = { checkResult.intValue += 1 },
            onDec = { checkResult.intValue -= 1 },
            modifier = Modifier.padding(start = 5.dp),
            style = typography.bodyLarge
        )
        Spacer(modifier = Modifier.padding(top = 5.dp))
        if (byHours.value) {
            CounterWithText(
                counter = hoursSpent.intValue,
                text = "Hours spent",
                onInc = { hoursSpent.intValue += 4 },
                onDec = { hoursSpent.intValue -= 4 },
                modifier = Modifier.padding(start = 5.dp),
                min = 4,
                max = 8,
                style = typography.bodyLarge
            )
        } else {
            CounterWithText(
                counter = daysSpent.intValue,
                text = "Days spent",
                onInc = { daysSpent.intValue += 1 },
                onDec = { daysSpent.intValue -= 1 },
                modifier = Modifier.padding(start = 5.dp),
                style = typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircularProgressIndicatorWithContent(
                progress = processState.value.getProgress(),
                trackColor = Color.LightGray,
                strokeWidth = 6.dp,
                modifier = Modifier
                    .size(80.dp)
                    .padding(5.dp)
            ) {
                if (processState.value.getProgress() < 1) {
                    Text(
                        text = "${processState.value.getProgress().times(100).toInt()}%",
                        style = typography.titleMedium
                    )
                } else {
                    Icon(
                        imageVector = Icons.Sharp.Check,
                        contentDescription = "Finished"
                    )
                }
            }
            OutlinedButton(
                onClick = { processState.value = processState.value
                    .apply { doCraft(
                        checkResult = checkResult.intValue,
                        adventuring = adventuring.value,
                        accelerated = accelerated.value,
                        byHours = byHours.value,
                        daysSpent = daysSpent.intValue,
                        hoursSpent = hoursSpent.intValue
                    ) }
                    craftingProcessService.save(processState.value)
                    onBuild.invoke(processState.value.isFinished())
                },
                enabled = processState.value.getProgress() < 1,
                modifier = Modifier
                    .size(80.dp)
                    .padding(5.dp)
            ) {
                Icon(imageVector = Icons.Sharp.Build, contentDescription = "Build")
            }
        }
        if (process.masterwork && process.item.target.itemType != ItemType.SIEGE_ENGINE) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp)
                    .clickable { expandedMasterwork.value = !expandedMasterwork.value }
            ) {
                if (expandedMasterwork.value) {
                    Icon(imageVector = Icons.Sharp.KeyboardArrowUp, contentDescription = "Hide")
                } else {
                    Icon(imageVector = Icons.Sharp.KeyboardArrowDown, contentDescription = "Expand")
                }
                Text(
                    text = "Masterwork components",
                    style = typography.bodyLarge
                )
            }
            if (expandedMasterwork.value) {
                HorizontalDivider(thickness = 2.dp)
                Spacer(modifier = Modifier.padding(top = 15.dp))
                Text(
                    text = "Components cost: ${processState.value
                        .item
                        .target
                        .itemType
                        .masterworkCost
                        .toCostString()}",
                    style = typography.bodyLarge,
                    modifier = Modifier.padding(start = 5.dp)
                )
                Spacer(modifier = Modifier.padding(top = 15.dp))
                Text(
                    text = "Money crafted: ${processState.value.masterworkCrafted.toCostString()}",
                    style = typography.bodyLarge,
                    modifier = Modifier.padding(start = 5.dp)
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(
                    text = "Required check: ${processState.value.item.target.itemType.masterworkDc} ${processState.value.item.target.craftingSkill()}",
                    style = typography.bodyLarge,
                    modifier = Modifier.padding(start = 5.dp)
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CircularProgressIndicatorWithContent(
                        progress = processState.value.getMasterworkProgress(),
                        trackColor = Color.LightGray,
                        strokeWidth = 6.dp,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(5.dp)
                    ) {
                        if (processState.value.getMasterworkProgress() < 1) {
                            Text(
                                text = "${
                                    processState.value.getMasterworkProgress().times(100).toInt()
                                }%",
                                style = typography.titleMedium
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Sharp.Check,
                                contentDescription = "Finished"
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            processState.value = processState.value
                                .apply {
                                    doCraftMasterworkComponents(
                                        checkResult.intValue,
                                        daysSpent.intValue,
                                        adventuring.value,
                                        byHours.value
                                    )
                                }
                            craftingProcessService.save(processState.value)
                            onBuild.invoke(processState.value.isFinished())
                        },
                        enabled = processState.value.getMasterworkProgress() < 1,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(5.dp)
                    ) {
                        Icon(imageVector = Icons.Sharp.Build, contentDescription = "Build")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CraftingProcessPagePreview() {
    if (KoinPlatformTools.defaultContext().getOrNull() != null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CraftingProcessPage()
        }
    } else {
        KoinApplication(
            application = {
                modules(
                    module { single { ItemServiceMock() as ItemService } },
                    module { single { ItemModificationServiceMock() as ItemModificationService } },
                    module { single { FeatServiceMock() as FeatService } },
                    module { single { CraftingProcessServiceMock() as CraftingProcessService } }
                )
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                CraftingProcessPage()
            }
        }
    }
}

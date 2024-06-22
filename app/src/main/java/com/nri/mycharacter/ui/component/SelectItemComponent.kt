package com.nri.mycharacter.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.service.ItemFilter
import com.nri.mycharacter.service.ItemService
import com.nri.mycharacter.service.mock.ItemServiceMock
import com.nri.mycharacter.utils.craftCost
import com.nri.mycharacter.utils.toCostString
import com.nri.mycharacter.viewmodel.ItemsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module
import java.util.Comparator

@Composable
fun SelectItem(
    onClick: (Item) -> Unit
) {
    val itemService = koinInject<ItemService>()
    val itemName = remember { mutableStateOf("") }
    val itemTypes = remember {
        ItemType.getTypes()
            .associateWith { true }
            .toList()
            .toMutableStateMap()
    }
    Box {
        Column {
            FilterInput(value = itemName.value) {
                itemName.value = it
            }
            LazyColumn(
                modifier = Modifier
                    .verticalScroll(ScrollState(0))
                    .height(750.dp)
            ) {
                val items = itemService.findFiltered(
                    ItemFilter(
                        itemName.value,
                        itemTypes.toMap()
                            .filter { it.value }
                            .keys
                            .toList()
                    )
                )
                for (it in items) {
                    item {
                        ItemContainer(
                            onClick = { item -> onClick.invoke(item) },
                            item = it
                        )
                    }
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .height(500.dp)
                .width(200.dp)
                .offset(160.dp, 220.dp)
        ) {
            val expanded = remember { mutableStateOf(false) }
            if (expanded.value) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .height(400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    for (type in itemTypes.toMap().toSortedMap { v1, v2 -> v1.ordinal - v2.ordinal }) {
                        ItemTypeFilterChip(
                            itemType = type.key,
                            selected = type.value
                        ) {
                            itemTypes[type.key] = !itemTypes[type.key]!!
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
            ItemTypeButton {
                expanded.value = !expanded.value
            }
        }
    }
}

@Composable
fun FilterInput(
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
        },
        placeholder = {
              Row {
                  Text(text = "Search")
              }
        },
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
    )
}

@Composable
fun ItemTypeButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Filter")
    }
}

@Composable
fun ItemTypeFilterChip(
    itemType: ItemType,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = itemType.humanReadable)
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.LightGray
        )
    )
}

@Preview(showSystemUi = true)
@Composable
fun SelectItemPreview() {
    KoinApplication(
        application = {
            modules(
                module { single { ItemServiceMock() as ItemService } },
                module { viewModel { ItemsViewModel() } }
            )
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SelectItem(onClick = {})
        }
    }
}

@Composable
fun ItemContainer(
    item: Item,
    onClick: (Item) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .height(90.dp)
            .padding(3.dp)
            .clickable { onClick.invoke(item) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(topStartPercent = 10, topEndPercent = 10)
                    )
            ) {
                Text(
                    text = item.name!!,
                    style = typography.bodyLarge,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .border(
                    width = 1.dp,
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(bottomStartPercent = 10, bottomEndPercent = 10)
                )
        ) {
            CostsBlock(item = item)
        }
    }
}

@Composable
fun CostsBlock(item: Item) {
    Row(
        modifier = Modifier
            .padding(start = 3.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        CostBox(cost = item.marketCost, title = "Cost")
        CostBox(cost = item.craftCost(), title = "Craft")
    }
}

@Composable
fun CostBox(cost: Int, title: String) {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .width(180.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "$title: ",
            style = typography.bodyLarge,
            fontWeight = FontWeight.Black
        )
        Text(
            text = cost.toCostString(),
            style = typography.bodyLarge,
        )
    }
}

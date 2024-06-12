package com.nri.mycharacter.ui.component

import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nri.mycharacter.entity.AlternativeChoice
import com.nri.mycharacter.entity.Feat
import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.entity.Item_
import com.nri.mycharacter.entity.Requirement
import com.nri.mycharacter.entity.Requirement_
import com.nri.mycharacter.utils.baseCraftTimeString
import com.nri.mycharacter.utils.craftCost
import com.nri.mycharacter.utils.difficultClassString
import com.nri.mycharacter.utils.spellsString
import com.nri.mycharacter.utils.toCostString
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Composable
fun ItemDetailDialog(
    onDismissRequest: () -> Unit,
    onAccept: (Item) -> Unit,
    item: Item
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column {
            Card(
                shape = RoundedCornerShape(percent = 5),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(ScrollState(0))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ItemTitle(name = item.name!!)
                    DetailParameter(parameter = "Base crafting time", value = item.baseCraftTimeString())
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailParameter(parameter = "Aura", value = item.aura ?: "")
                    DetailParameter(parameter = "CL", value = item.casterLevel.toString())
                    DetailParameter(parameter = "Weight", value = item.weight.toString())
                    DetailParameter(parameter = "Slot", value = item.slot.name.lowercase())
                    DetailParameter(parameter = "Price", value = item.marketCost.toCostString())
                    Spacer(modifier = Modifier.height(10.dp))
                    ItemDescription(description = item.description ?: "")
                    Spacer(modifier = Modifier.height(10.dp))
                    ItemCraftReqs(item)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ButtonWithText(
                    onClick = { onAccept.invoke(item) },
                    text = "Accept",
                    shape = RoundedCornerShape(percent = 25),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ItemTitle(name: String) {
    Box(
        modifier = Modifier
            .height(45.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name, style = typography.titleLarge)
    }
}

@Composable
fun DetailParameter(
    parameter: String,
    value: String
) {
    Box(
        modifier = Modifier
            .padding(start = 5.dp)

            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row {
            Text(
                text = "$parameter:",
                style = typography.bodyMedium,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(text = value, style = typography.bodyMedium)
        }
    }
}

@Composable
fun ItemDescription(
    description: String
) {
    val openDescription = remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Description",
            style = typography.bodyLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.clickable { openDescription.value = !openDescription.value }
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (openDescription.value) {
            Text(text = description, style = typography.bodyMedium)
        }
    }
}

@Composable
fun ItemCraftReqs(item: Item) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Crafting requirements",
            style = typography.bodyLarge,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(start = 5.dp, end = 5.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        DetailParameter(parameter = "DC", value = item.difficultClassString())
        Spacer(modifier = Modifier.height(10.dp))
        DetailParameter(parameter = "Cost", value = item.craftCost().toCostString())
        if (item.requirements.target.casterLevel > 0) {
            DetailParameter(
                parameter = "CL",
                value = item.requirements.target.casterLevel.toString()
            )
        }
        if (!item.requirements.target.skills.isNullOrBlank()) {
            DetailParameter(
                parameter = "Skills",
                value = item.requirements.target.skills!!
            )
        }
        val featNames = item.requirements.target.feats?.filterNot { it.isBlank() }
        if (!featNames.isNullOrEmpty()) {
            DetailParameter(
                parameter = "Feats",
                value = featNames.joinToString(", ")
            )
        }
        val spells = item.spellsString()
        if (spells.isNotEmpty()) {
            DetailParameter(
                parameter = "Spells",
                value = spells
            )
        }
        val addPrep = item.requirements.target.addPrep ?: ""
        if (addPrep.isNotEmpty()) {
            DetailParameter(parameter = "Special", value = addPrep)
        }
    }
}

@Preview
@Composable
fun ItemDetailDialogPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val item = Item(
            name = "Traveler’s Any-Tool",
            marketCost = 12345,
            aura = "moderate transmutation",
            casterLevel = 9,
            weight = 2f,
            description = "This implement at first seems to be nothing but a 12-inch iron bar lined with small plates and spikes. It can be folded, twisted, hinged, and bent, to form almost any known tool. Hammers, shovels, even a block and tackle (without rope) are possible. It can duplicate any tool the wielder can clearly visualize that contains only limited moving parts, such as a pair of scissors, but not a handloom. It cannot be used to replace missing or broken parts of machines or vehicles unless a mundane tool would have done the job just as well.",
            itemType = ItemType.WONDROUS_ITEM
        )
        val req = Requirement(
            spells = listOf("major creation"),
            addPrep = "creator must be gay",
            casterLevel = 1,
            skills = "Swim 5 ranks",
            feats = listOf("Craft Wondrous Item")
        )
        val alternativeChoice = AlternativeChoice(choice = listOf("wish", "miracle"))
        req.alternativeChoice = ToMany(req, Requirement_.alternativeChoice)
        req.alternativeChoice.add(alternativeChoice)
        item.requirements = ToOne(item, Item_.requirements)
        item.requirements.target = req
        ItemDetailDialog(
            onDismissRequest = {},
            onAccept = {},
            item
        )
    }
}

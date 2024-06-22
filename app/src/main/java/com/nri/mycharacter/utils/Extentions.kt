package com.nri.mycharacter.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.nri.mycharacter.entity.AlternativeChoice
import com.nri.mycharacter.entity.CraftingProcess
import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemModification
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.entity.Item_
import com.nri.mycharacter.entity.Requirement
import com.nri.mycharacter.viewmodel.PrepareToCraftViewModel
import io.objectbox.relation.ToOne
import kotlin.math.ceil
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.times
import kotlin.time.toDuration

fun Int.toCostString(): String {
    var cost = this
    val cp: Int = cost % 10
    cost /= 10
    val sp: Int = cost % 10
    cost /= 10
    return "${if (cost > 0) "$cost gp " else ""}${if (sp > 0) "$sp sp " else ""}${if (cp >= 0) "$cp cp " else ""}"
}

fun Item.baseCraftTimeString(): String {
    val craftDays = if (baseCraftTime > 0) {
        baseCraftTime
    } else {
        if (itemType.isMundane) {
            mundaneCraftBaseTimeDays(
                requirements.target.difficultClass, marketCost.toDouble()
            )
        } else {
            magicCraftBaseTimeDays(marketCost.toDouble())
        }
    }
    return if (craftDays == 1) "$craftDays day" else "$craftDays days"
}

fun mundaneCraftBaseTimeDays(difficultClass: Int, marketPrice: Double): Int {
    return ceil(marketPrice
        .div(10)
        .div(difficultClass * difficultClass)
        .times(7)
    ).toInt()
}

fun magicCraftBaseTimeDays(marketPrice: Double): Int {
    return ceil(marketPrice
        .div(100)
        .div(1000)
    ).toInt()
}

fun Item.modifiedCraftTimeString(
    masterwork: Boolean,
    mod: ItemModification?,
    dcForIgnoredPreqs: Int,
    coopCraftParts: Int,
    fcb: Int,
    strMod: Int = 0,
    count: Int = 1
): String {
    val difficultClass = difficultClass(masterwork, mod, dcForIgnoredPreqs, strMod)
    val marketCost = modifiedMarketCost(mod, masterwork, strMod, count).toDouble()
    val craftDays = if (itemType.isMundane) {
        mundaneCraftBaseTimeDays(
            difficultClass,
            marketCost
        )
    } else {
        magicCraftBaseTimeDays(
            marketCost
        )
    }
    val result = ceil(
        craftDays.toDouble()
            .div(if (coopCraftParts > 0) coopCraftParts + 1 else 1)
            .div(1 + (0.2.times(fcb)))
    ).toInt()
    return if (result == 1) "$result day" else "$result days"
}

fun Item.craftCost(): Int {
    return if (craftCost > 0) {
        craftCost
    } else {
        if (itemType.isMundane) {
            marketCost / 3
        } else {
            marketCost / 2
        }
    }
}

fun Item.modifiedMarketCost(
    mod: ItemModification?,
    masterwork: Boolean,
    strMod: Int = 0,
    count: Int = 1
): Int {
    val base = (marketCost + marketCost.times(strMod)).times(count)
    return if (mod == null) {
        base
    } else {
        val cost = mod.itemTypeToCost[itemType.name]
        when {
            mod.isPerLbs -> {
                base + (if (cost.isNullOrBlank()) mod.costMod else cost.toInt() * weight)
                    .toInt()
            }
            mod.isFlatCostMod -> {
                base + if (cost.isNullOrBlank()) mod.costMod.toInt() else cost.toInt()
            }
            else -> {
                (base * if (cost.isNullOrBlank()) mod.costMod else cost.toFloat()).toInt()
            }
        }
    } + if (masterwork) itemType.masterworkCost else 0
}

fun Item.modifiedCraftCost(
    mod: ItemModification?,
    discount: Boolean,
    masterwork: Boolean,
    strMod: Int = 0,
    count: Int = 1
): Int {
    val modifiedMarketCost = modifiedMarketCost(mod, masterwork, strMod, count)
    return when {
        itemType.isMundane -> {
            modifiedMarketCost / 3
        }
        discount -> {
            ((modifiedMarketCost / 2) * 0.95).toInt()
        }
        else -> {
            modifiedMarketCost / 2
        }
    }
}

fun Item.spellsString(): String {
    val reqSpells = requirements.target.spells
        ?.filterNot { it.isBlank() }
        ?.joinToString(", ") ?: ""
    val altSpellsList = requirements.target.alternativeChoice
        .toArray()
        .toList() as List<AlternativeChoice>
    val altSpells = altSpellsList
        .mapNotNull { it.choice }
        .joinToString(", ") { it.joinToString(" or ") }
    return reqSpells + (if (reqSpells.isNotBlank() && altSpells.isNotBlank()) ", " else "") + altSpells
}

fun Item.difficultClass(): Int {
    return if (requirements.target.difficultClass > 0) {
        requirements.target.difficultClass
    } else {
        casterLevel + 5
    }
}

fun Item.craftingSkill(): String {
    return if (!requirements.target.craftSkill.isNullOrBlank()) {
        requirements.target.craftSkill!!
    } else {
        itemType.baseCraftSkill.joinToString(" or ") { it.humanReadableName }
    }
}

fun Item.difficultClassString(): String = "${difficultClass()} ${craftingSkill()}"

fun Item.difficultClass(
    masterwork: Boolean,
    mod: ItemModification?,
    dcForIgnoredPreqs: Int,
    strMod: Int = 0
): Int {
    val baseDifficultClass = difficultClass()
    return baseDifficultClass +
            dcForIgnoredPreqs +
            (mod?.difficultClassMod ?: 0) +
            (strMod.times(2)) +
            if (itemType == ItemType.SIEGE_ENGINE && masterwork) 5 else 0
}

fun Item.modifiedDifficultClassString(
    masterwork: Boolean,
    mod: ItemModification?,
    dcForIgnoredPreqs: Int,
    strMod: Int = 0
): String = "${difficultClass(masterwork, mod, dcForIgnoredPreqs, strMod)} ${craftingSkill()}"

fun calculateDifficultClassForIgnoredPreqs(
    ignoredSkills: List<String>,
    ignoredFeats: List<String>,
    ignoredSpells: List<String>,
    ignoredSpecial: Boolean,
    ignoredCasterLevel: Boolean,
    ignoredAlternativeChoices: List<AlternativeChoice>
): Int = (ignoredSkills.size +
        ignoredFeats.size +
        ignoredSpells.size +
        (if (ignoredSpecial) 1 else 0) +
        (if (ignoredCasterLevel) 1 else 0) +
        ignoredAlternativeChoices.size) * 5

fun CraftingProcess.calcFinalCost() = finalCost +
        if (masterwork && item.target.itemType != ItemType.SIEGE_ENGINE)
            item.target.itemType.masterworkCost
        else
            0

fun CraftingProcess.getProgress() = moneyCrafted.toFloat().div(finalCost)

fun CraftingProcess.getMasterworkProgress() = masterworkCrafted
    .toFloat()
    .div(item.target.itemType.masterworkCost)

fun CraftingProcess.isFinished() = getProgress() == 1f &&
        (!masterwork || getMasterworkProgress() == 1f)

fun CraftingProcess.getTimeSpent(): String {
    val duration = timeSpent.toDuration(DurationUnit.HOURS)
    val days = duration.inWholeDays
    val hours = duration.minus(days.toDuration(DurationUnit.DAYS)).inWholeHours
    return "${if (days == 1L)
        "$days day"
    else if (days > 1L) 
        "$days days" 
    else 
        ""} ${if (hours == 1L)
            "$hours hour" 
    else if (hours > 1L || (days == 0L && hours == 0L))
        "$hours hours" 
    else ""}"
}

fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

fun DrawScope.drawCircularIndicatorTrack(
    color: Color,
    stroke: Stroke
) = drawCircularIndicator(270f, 360f, color, stroke)

fun DrawScope.drawDeterminateCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) = drawCircularIndicator(startAngle, sweep, color, stroke)

fun CraftingProcess.doCraft(
    checkResult: Int = 0,
    adventuring: Boolean = false,
    accelerated: Boolean = false,
    byHours: Boolean = false,
    daysSpent: Int = 1,
    hoursSpent: Int = 1,
) {
    val dc = finalDifficultClass.acceleratedDc(item.target.itemType, accelerated)
    if (checkResult < dc) {
        timeSpent += if (byHours) {
            hoursSpent
        } else {
            daysSpent * 24
        }
        return
    }
    val progress = if (item.target.itemType.isMundane) {
        dc.toDouble()
            .times(checkResult)
            .div(7)
            .div(24)
            .times(if (byHours) hoursSpent else daysSpent * 24)
            .times(10)
    } else {
        100000.0
            .times(if (accelerated) 2 else 1)
            .div(8)
            .times(if (byHours) hoursSpent else daysSpent * 8)
    }.div(
        if (adventuring) {
            when (byHours) {
                true -> 2
                false -> 4
            }
        } else {
            1
        }
    ).toInt()
    moneyCrafted = min(finalCost, moneyCrafted + progress)
    timeSpent += if (byHours) {
        hoursSpent
    } else {
        daysSpent * 24
    }
}

fun Int.acceleratedDc(itemType: ItemType, accelerated: Boolean) = this
    .plus(
        if (accelerated) {
            if (itemType.isMundane) 10
            else 5
        } else {
            0
        }
    )

fun CraftingProcess.doCraftMasterworkComponents(
    checkResult: Int = 0,
    daysSpent: Int = 1,
    adventuring: Boolean,
    byHours: Boolean
) {
    if (checkResult < item.target.itemType.masterworkDc) {
        timeSpent += daysSpent
        return
    }
    val progress = item.target.itemType.masterworkDc.toDouble()
        .times(checkResult)
        .div(7)
        .times(daysSpent)
        .times(10)
        .div(
            if (adventuring) {
                when (byHours) {
                    true -> 2
                    false -> 4
                }
            } else {
                1
            }
        )
        .toInt()
    masterworkCrafted = min(item.target.itemType.masterworkCost, masterworkCrafted + progress)
    timeSpent += daysSpent
}

fun ItemType.createTempItem(): Item {
    val item = Item(
        name = when (this) {
            ItemType.WAND -> "Some wand"
            ItemType.SCROLL -> "Some scroll"
            ItemType.POTION -> "Some potion"
            else -> "Some item"
        },
        itemType = this,
        temporary = true
    )
    val reqs = Requirement(
        feats = baseFeats
    )
    item.requirements.target = reqs
    return item
}

fun calcFinalDc(
    casterLevel: Int
) = 5.plus(casterLevel)

fun calcFinalCraftCost(
    itemType: ItemType,
    spellLevel: Int,
    casterLevel: Int,
    discount: Boolean
) = when(itemType) {
    ItemType.WAND -> 37500.0
    ItemType.SCROLL -> 1250.0
    ItemType.POTION -> 2500.0
    else -> 0.0
}.times(if (spellLevel == 0) 0.5 else spellLevel.toDouble())
    .times(casterLevel)
    .times(if (discount) 0.95 else 1.0)
    .toInt()

fun calcFinalCraftTime(
    itemType: ItemType,
    spellLevel: Int,
    casterLevel: Int,
    discount: Boolean,
    coopCraftParts: Int,
    fcb: Int
): String {
    val cost = calcFinalCraftCost(itemType, spellLevel, casterLevel, discount).times(2)
    if ((itemType == ItemType.POTION || itemType == ItemType.SCROLL) && cost <= 25000) {
        return "2 hours"
    }
    val dayProgress = 100000
        .plus(fcb * 20000)
        .plus(100000.times(coopCraftParts))
    val days = ceil(cost.toDouble().div(dayProgress)).toInt()
    return "$days ${if (days == 1) "day" else "days"}"
}

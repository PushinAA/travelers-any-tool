package com.nri.mycharacter.viewmodel

import androidx.lifecycle.ViewModel
import com.nri.mycharacter.entity.AlternativeChoice
import com.nri.mycharacter.entity.CraftingProcess
import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemModification
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.utils.calcFinalCraftCost
import com.nri.mycharacter.utils.calcFinalDc
import com.nri.mycharacter.utils.calculateDifficultClassForIgnoredPreqs
import com.nri.mycharacter.utils.createTempItem
import com.nri.mycharacter.utils.difficultClass
import com.nri.mycharacter.utils.modifiedCraftCost
import com.nri.mycharacter.utils.modifiedDifficultClassString
import com.nri.mycharacter.utils.modifiedMarketCost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update

class PrepareToCraftViewModel: ViewModel() {
    private val _discountTrait = MutableStateFlow(false)
    private val _coopCraftingParticipants = MutableStateFlow(0)
    private val _fcb = MutableStateFlow(0)
    private val _masterwork = MutableStateFlow(false)
    private val _strMod = MutableStateFlow(0)
    private val _count = MutableStateFlow(1)
    private val _modification = MutableStateFlow<ItemModification?>(null)
    private val _ignoredSkills = MutableStateFlow<List<String>>(listOf())
    private val _ignoredFeats = MutableStateFlow<List<String>>(listOf())
    private val _ignoredSpells = MutableStateFlow<List<String>>(listOf())
    private val _ignoredSpecial = MutableStateFlow(false)
    private val _ignoredCasterLevel = MutableStateFlow(false)
    private val _ignoredAlternativeChoices = MutableStateFlow<List<AlternativeChoice>>(listOf())
    private val _spellLevel = MutableStateFlow(0)
    private val _casterLevel = MutableStateFlow(1)

    val itemsWithStrMod = listOf(
        "Composite longbow",
        "Composite shortbow",
        "Reflex bow",
        "Spear-sling",
        "Hornbow, orc",
        "Horse bow"
    )

    val discountTrait = _discountTrait.asStateFlow()
    val coopCraftingParticipants = _coopCraftingParticipants.asStateFlow()
    val fcb = _fcb.asStateFlow()
    val masterwork = _masterwork.asStateFlow()
    val strMod = _strMod.asStateFlow()
    val count = _count.asStateFlow()
    val modification = _modification.asStateFlow()
    val ignoredSkills = _ignoredSkills.asStateFlow()
    val ignoredFeats = _ignoredFeats.asStateFlow()
    val ignoredSpells = _ignoredSpells.asStateFlow()
    val ignoredSpecial = _ignoredSpecial.asStateFlow()
    val ignoredCasterLevel = _ignoredCasterLevel.asStateFlow()
    val ignoredAlternativeChoices = _ignoredAlternativeChoices.asStateFlow()
    val spellLevel = _spellLevel.asStateFlow()
    val casterLevel = _casterLevel.asStateFlow()

    fun updateDiscountTraits(value: Boolean) {
        _discountTrait.update { _ -> value }
    }

    fun updateCoopCraft(value: Int) {
        _coopCraftingParticipants.update { _ -> value }
    }

    fun incCoopCraft() {
        _coopCraftingParticipants.update { it + 1 }
    }

    fun decCoopCraft() {
        _coopCraftingParticipants.update { it - 1 }
    }

    fun updateFcb(value: Int) {
        _fcb.update { _ -> value }
    }

    fun incFcb() {
        _fcb.update { it + 1 }
    }

    fun decFcb() {
        _fcb.update { it - 1 }
    }

    fun updateMasterwork(value: Boolean) {
        _masterwork.update { _ -> value }
    }

    fun incStrMod() {
        _strMod.update { it + 1 }
    }

    fun decStrMod() {
        _strMod.update { it - 1 }
    }

    fun incCount() {
        _count.update { it + 1 }
    }

    fun decCount() {
        _count.update { it - 1 }
    }

    fun updateModification(value: ItemModification?) {
        _modification.update { _ -> value }
    }

    fun addIgnoredSkill(value: String) {
        _ignoredSkills.update { it.plusElement(value) }
    }

    fun removeIgnoredSkill(value: String) {
        _ignoredSkills.update { it.minusElement(value) }
    }

    fun addIgnoredFeat(value: String) {
        _ignoredFeats.update { it.plusElement(value) }
    }

    fun removeIgnoredFeat(value: String) {
        _ignoredFeats.update { it.minusElement(value) }
    }

    fun addIgnoredSpell(value: String) {
        _ignoredSpells.update { it.plusElement(value) }
    }

    fun removeIgnoredSpell(value: String) {
        _ignoredSpells.update { it.minusElement(value) }
    }

    fun updateIgnoredSpecial(value: Boolean) {
        _ignoredSpecial.update { _ -> value }
    }

    fun updateIgnoredCasterLevel(value: Boolean) {
        _ignoredCasterLevel.update { _ -> value }
    }

    fun addIgnoredAlternativeChoice(value: AlternativeChoice) {
        _ignoredAlternativeChoices.update { it.plusElement(value) }
    }

    fun removeIgnoredAlternativeChoice(value: AlternativeChoice) {
        _ignoredAlternativeChoices.update { it.minusElement(value) }
    }

    fun incSpellLevel() {
        _spellLevel.update { it + 1 }
    }

    fun decSpellLevel() {
        _spellLevel.update { it - 1 }
    }

    fun incCasterLevel() {
        _casterLevel.update { it + 1 }
    }

    fun decCasterLevel() {
        _casterLevel.update { it - 1 }
    }

    fun collect(item: Item): CraftingProcess {
        val process = CraftingProcess(
            finalCost = item.modifiedMarketCost(
                _modification.value,
                _masterwork.value,
                _strMod.value,
                _count.value
            ),
            mustPayed = item.modifiedCraftCost(
                _modification.value,
                _discountTrait.value,
                _masterwork.value,
                _strMod.value,
                _count.value
            ),
            finalDifficultClass = item.difficultClass(
                _masterwork.value,
                _modification.value,
                calculateDifficultClassForIgnoredPreqs(
                    _ignoredSkills.value,
                    _ignoredFeats.value,
                    _ignoredSpells.value,
                    _ignoredSpecial.value,
                    _ignoredCasterLevel.value,
                    _ignoredAlternativeChoices.value
                ),
                _strMod.value
            ),
            masterwork = _masterwork.value && (_modification.value == null ||
                    _modification.value?.isMasterworkIncluded != true),
            count = _count.value,
            coopCraftingParts = _coopCraftingParticipants.value,
            fcb = _fcb.value,
            discountTrait = _discountTrait.value,
            ignoredSkills = _ignoredSkills.value,
            ignoredFeats = _ignoredFeats.value,
            ignoredSpells = _ignoredSpells.value,
            ignoredSpecial = _ignoredSpecial.value,
            ignoredCasterLevel = _ignoredCasterLevel.value
        )
        process.ignoredAlternativeChoices.addAll(_ignoredAlternativeChoices.value)
        process.item.target = item
        _modification.value
            .apply {
                if (this != null) process.itemModifications.add(this)
            }
        return process
    }

    fun collectSpellTrigger(itemName: String, itemType: ItemType): CraftingProcess {
        val item = itemType.createTempItem()
        item.name = itemName
        val craftCost = calcFinalCraftCost(
            itemType, _spellLevel.value, _casterLevel.value, _discountTrait.value
        )
        val process = CraftingProcess(
            finalCost = craftCost.times(2),
            mustPayed = craftCost,
            finalDifficultClass = calcFinalDc(_casterLevel.value),
            coopCraftingParts = _coopCraftingParticipants.value,
            fcb = _fcb.value,
            discountTrait = _discountTrait.value
        )
        process.item.target = item
        return process
    }
}

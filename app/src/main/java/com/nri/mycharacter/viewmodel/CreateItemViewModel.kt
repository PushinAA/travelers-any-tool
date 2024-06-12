package com.nri.mycharacter.viewmodel

import androidx.lifecycle.ViewModel
import com.nri.mycharacter.entity.Feat
import com.nri.mycharacter.entity.ItemSlot
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.entity.Spell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateItemViewModel: ViewModel() {
    private val _name = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _marketCost = MutableStateFlow(0)
    private val _craftCost = MutableStateFlow(0)
    private val _casterLevel = MutableStateFlow(0)
    private val _aura = MutableStateFlow("")
    private val _weight = MutableStateFlow(0f)
    private val _slot = MutableStateFlow(ItemSlot.NONE)
    private val _itemType = MutableStateFlow(ItemType.OTHER)
    private val _reqCasterLevel = MutableStateFlow(0)
    private val _reqAddPrep = MutableStateFlow("")
    private val _skills = MutableStateFlow("")
    private val _craftSkill = MutableStateFlow("")
    private val _difficultClass = MutableStateFlow(0)
    private val _feats = MutableStateFlow<List<Feat>>(listOf())
    private val _spells = MutableStateFlow<List<Spell>>(listOf())

    val name = _name.asStateFlow()
    val description = _description.asStateFlow()
    val marketCost = _marketCost.asStateFlow()
    val craftCost = _craftCost.asStateFlow()
    val casterLevel = _casterLevel.asStateFlow()
    val aura = _aura.asStateFlow()
    val weight = _weight.asStateFlow()
    val slot = _slot.asStateFlow()
    val itemType = _itemType.asStateFlow()
    val reqCasterLevel = _reqCasterLevel.asStateFlow()
    val reqAddPrep = _reqAddPrep.asStateFlow()
    val skills = _skills.asStateFlow()
    val craftSkill = _craftSkill.asStateFlow()
    val difficultClass = _difficultClass.asStateFlow()
    val feats = _feats.asStateFlow()
    val spells = _spells.asStateFlow()

    fun updateName(name: String) {
        _name.update { _ -> name }
    }

    fun updateDescription(description: String) {
        _description.update { _ -> description }
    }

    fun updateMarketCost(marketCost: Int) {
        _marketCost.update { _ -> marketCost }
    }

    fun updateCraftCost(craftCost: Int) {
        _craftCost.update { _ -> craftCost }
    }

    fun updateCasterLevel(casterLevel: Int) {
        _casterLevel.update { _ -> casterLevel }
    }

    fun updateAura(aura: String) {
        _aura.update { _ -> aura }
    }

    fun updateWeight(weight: Float) {
        _weight.update { _ -> weight }
    }

    fun updateSlot(slot: ItemSlot) {
        _slot.update { _ -> slot }
    }

    fun updateItemType(itemType: ItemType) {
        _itemType.update { _ -> itemType }
    }

    fun updateReqCasterLevel(reqCasterLevel: Int) {
        _reqCasterLevel.update { _ -> reqCasterLevel }
    }

    fun updateReqAddPrep(reqAddPrep: String) {
        _reqAddPrep.update { _ -> reqAddPrep }
    }

    fun updateSkills(skills: String) {
        _skills.update { _ -> skills }
    }

    fun updateCraftSkill(craftSkill: String) {
        _craftSkill.update { _ -> craftSkill }
    }

    fun updateDifficultClass(difficultClass: Int) {
        _difficultClass.update { _ -> difficultClass }
    }

    fun addFeat(feat: Feat) {
        _feats.update { it.plusElement(feat) }
    }

    fun removeFeat(feat: Feat) {
        _feats.update { it.minusElement(feat) }
    }

    fun addSpell(spell: Spell) {
        _spells.update { it.plusElement(spell) }
    }

    fun removeSpell(spell: Spell) {
        _spells.update { it.minusElement(spell) }
    }
}

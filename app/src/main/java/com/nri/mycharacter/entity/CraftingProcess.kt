package com.nri.mycharacter.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
data class CraftingProcess (
    @Id
    var id: Long = 0,
    var finalCost: Int = 0,
    var mustPayed: Int = 0,
    var finalTime: Int = 0,
    var finalDifficultClass: Int = 0,
    var timeSpent: Int = 0,
    var moneyCrafted: Int = 0,
    var masterwork: Boolean = false,
    var masterworkCrafted: Int = 0,
    var coopCraftingParts: Int = 0,
    var acceleratedCrafting: Boolean = false,
    var fcb: Int = 0,
    var discountTrait: Boolean = false,
    var ignoredSkills: List<String> = listOf(),
    var ignoredFeats: List<String> = listOf(),
    var ignoredSpells: List<String> = listOf(),
    var ignoredSpecial: Boolean = false,
    var ignoredCasterLevel: Boolean = false,
) {
    lateinit var item: ToOne<Item>
    lateinit var itemModifications: ToMany<ItemModification>
    lateinit var template: ToMany<Template>
    lateinit var ignoredAlternativeChoices: ToMany<AlternativeChoice>
}

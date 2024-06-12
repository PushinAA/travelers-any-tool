package com.nri.mycharacter.entity

import com.fasterxml.jackson.annotation.JsonSetter
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class ItemModification (
    @Id
    var id: Long = 0,
    var name: String? = null,
    var description: String? = null,
    var costMod: Float = 0f,
    var weightMod: Float = 0f,
    var craftProcessMod: Float = 0f,
    var casterLevelMod: Int = 0,
    var difficultClassMod: Int = 0,
    var isFlatCostMod: Boolean = false,
    var isFlatWeightMod: Boolean = false,
    var isPerLbs: Boolean = false,
    var isMasterworkIncluded: Boolean = false,
    var itemTypeToCost: MutableMap<String, String> = mutableMapOf()
) {
    lateinit var requirements: ToOne<Requirement>

    @JsonSetter("requirements")
    fun setRequirements(requirement: Requirement) {
        requirements.target = requirement
    }
}

package com.nri.mycharacter.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ModificatorItem(
    @Id
    var id: Long = 0,
    var name: String? = null,
    var description: String? = null,
    var costMod: Float = 0f,
    var timeMod: Float = 0f
)

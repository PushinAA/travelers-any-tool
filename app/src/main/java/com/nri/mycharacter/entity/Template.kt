package com.nri.mycharacter.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class Template (
    @Id
    var id: Long = 0,
    var name: String? = null,
    var costMod: Float = 0f,
    var dcMod: Int = 0
) {
    lateinit var requirements: ToOne<Requirement>
}
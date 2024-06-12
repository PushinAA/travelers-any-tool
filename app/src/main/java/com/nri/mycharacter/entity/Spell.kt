package com.nri.mycharacter.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class Spell (
    @Id
    var id: Long = 0,
    @Unique
    var name: String? = null,
    var description: String? = null
)
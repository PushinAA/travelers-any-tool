package com.nri.mycharacter.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class AlternativeChoice(
    @Id
    var id: Long = 0,
    var choice: List<String>? = null
)

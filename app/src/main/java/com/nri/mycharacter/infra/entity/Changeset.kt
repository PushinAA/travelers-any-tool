package com.nri.mycharacter.infra.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class Changeset(
    @Id
    var id: Long = 0,
    @Unique
    var changeset: String? = null
)

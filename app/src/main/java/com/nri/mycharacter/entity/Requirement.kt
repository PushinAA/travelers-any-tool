package com.nri.mycharacter.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.nri.mycharacter.store.ObjectBox
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.kotlin.boxFor
import io.objectbox.query.QueryBuilder
import io.objectbox.relation.ToMany

@Entity
data class Requirement(
    @Id
    var id: Long = 0,
    var casterLevel: Int = 0,
    var addPrep: String? = null,
    var skills: String? = null,
    var craftSkill: String? = null,
    var difficultClass: Int = 0,
    var spells: List<String>? = null,
    @JsonProperty("featNames")
    var feats: List<String>? = null
) {
    lateinit var alternativeChoice: ToMany<AlternativeChoice>

    @JsonSetter("alternativeSpells")
    fun setAlternativeChoice(alternativeSpells: List<List<String>>) {
        if (alternativeSpells.isEmpty()) {
            return
        }
        val choices = alternativeSpells
            .map { AlternativeChoice(choice = it) }
        this.alternativeChoice.addAll(choices)
    }
}

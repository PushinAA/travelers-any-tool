package com.nri.mycharacter.entity

import com.fasterxml.jackson.annotation.JsonSetter
import com.nri.mycharacter.store.ObjectBox
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.query.QueryBuilder
import io.objectbox.relation.ToMany

@Entity
data class AlternativeSpells(
    @Id
    var id: Long = 0
) {
    lateinit var alternativeChoice: ToMany<Spell>

    @JsonSetter("spellNames")
    fun setSpells(spellNames: List<String>) {
        if (spellNames.isEmpty()) {
            return
        }
        val spellBox = ObjectBox.store.boxFor(Spell::class.java)
        val spells = spellBox
            .query()
            .`in`(Spell_.name, spellNames.toTypedArray(), QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build().find()
        this.alternativeChoice.addAll(spells)
    }
}
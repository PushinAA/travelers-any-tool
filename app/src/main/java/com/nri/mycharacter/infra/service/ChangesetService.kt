package com.nri.mycharacter.infra.service

import android.content.res.Resources
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.readValue
import com.nri.mycharacter.R
import com.nri.mycharacter.entity.Enchant
import com.nri.mycharacter.entity.Feat
import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemModification
import com.nri.mycharacter.infra.entity.Changeset
import com.nri.mycharacter.infra.entity.Changeset_
import com.nri.mycharacter.store.ObjectBox
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.QueryBuilder
import org.koin.core.component.KoinComponent

class ChangesetService: KoinComponent {

    private val objectMapper = jacksonMapperBuilder()
        .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        .build()
    private val changesetBox: Box<Changeset> = ObjectBox.store.boxFor()

    fun applyAll(resources: Resources) {
        changesetBox.removeAll()
        applyChangesets<Feat>(resources, R.raw.init_feats, true)
        // TODO включить после доработки заклов
        //applyChangesets<Spell>(resources, R.raw.init_spells)
        applyChangesets<Item>(resources, R.raw.init_items, true)
        applyChangesets<Item>(resources, R.raw.init_wondrous_items)
        applyChangesets<ItemModification>(resources, R.raw.init_item_modifications, true)
        applyChangesets<Enchant>(resources, R.raw.init_enchants, true)
    }

    private inline fun <reified T> applyChangesets(
        resources: Resources,
        resourceId: Int,
        clean: Boolean = false
    ) {
        val changesets = objectMapper
            .readValue<List<ChangesetEntry<T>>>(resources.openRawResource(resourceId))
        val box: Box<T> = ObjectBox.store.boxFor()
        // TODO выпилить
        if (clean) {
            box.removeAll()
        }
        ObjectBox.store.runInTx {
            changesets.forEach {
                val executed = changesetBox
                    .query()
                    .equal(Changeset_.changeset, it.changeset, QueryBuilder.StringOrder.CASE_SENSITIVE)
                    .build().findUnique()
                if (executed == null) {
                    applyChangeset<T>(it)
                }
            }
        }
    }

    private inline fun <reified T> applyChangeset(changesetEntry: ChangesetEntry<T>) {
        val box: Box<T> = ObjectBox.store.boxFor()
        box.put(changesetEntry.items)
        changesetBox.put(
            Changeset(changeset = changesetEntry.changeset)
        )
    }
}

data class ChangesetEntry<T>(
    val changeset: String,
    val items: List<T>
)

package com.nri.mycharacter.service

import com.nri.mycharacter.entity.Feat
import com.nri.mycharacter.entity.Feat_
import com.nri.mycharacter.store.ObjectBox
import io.objectbox.query.QueryBuilder
import org.koin.core.component.KoinComponent

class FeatServiceImpl: FeatService, KoinComponent {

    private val featStore = ObjectBox.store.boxFor(Feat::class.java)

    override fun save(feat: Feat): Long {
        return featStore.put(feat)
    }

    override fun findAll() = featStore.all

    override fun find(id: Long): Feat? = featStore[id]

    override fun find(name: String): Feat? = featStore
        .query()
        .equal(Feat_.name, name, QueryBuilder.StringOrder.CASE_SENSITIVE)
        .build()
        .findUnique()


    override fun delete(feat: Feat) {
        featStore.remove(feat)
    }
}
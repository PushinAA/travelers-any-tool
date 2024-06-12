package com.nri.mycharacter.service.mock

import com.nri.mycharacter.entity.Feat
import com.nri.mycharacter.service.FeatService

class FeatServiceMock: FeatService {
    override fun save(feat: Feat): Long {
        return 0
    }

    override fun findAll(): List<Feat> {
        return listOf(
            Feat(name = "Feat 1", description = "Description of feat 1"),
            Feat(name = "Feat 2", description = "Description of feat 2")
        )
    }

    override fun find(id: Long): Feat? {
        return null
    }

    override fun find(name: String): Feat? {
        return Feat(name = "Craft Wondrous Item", description = "Description of feat 1")
    }

    override fun delete(feat: Feat) {}
}
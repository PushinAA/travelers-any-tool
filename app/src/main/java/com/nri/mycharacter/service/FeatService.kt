package com.nri.mycharacter.service

import com.nri.mycharacter.entity.Feat

interface FeatService {

    fun save(feat: Feat): Long
    fun findAll(): List<Feat>
    fun find(id: Long): Feat?
    fun find(name: String): Feat?
    fun delete(feat: Feat)
}
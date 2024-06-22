package com.nri.mycharacter.service.mock

import com.nri.mycharacter.entity.AlternativeChoice
import com.nri.mycharacter.entity.Feat
import com.nri.mycharacter.entity.Item
import com.nri.mycharacter.entity.ItemType
import com.nri.mycharacter.entity.Item_
import com.nri.mycharacter.entity.Requirement
import com.nri.mycharacter.entity.Requirement_
import com.nri.mycharacter.service.ItemFilter
import com.nri.mycharacter.service.ItemService
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import org.koin.core.component.KoinComponent

class ItemServiceMock: ItemService, KoinComponent {

    override fun findAll(): List<Item> {
        return (1..100)
            .map { Item(name = "Item $it", marketCost = 10 * it, craftCost = 5 * it) }
            .toList()
        /*return listOf(
            Item(name = "Cloak of resistance +2", marketCost = 2000, craftCost = 1000),
            Item(name = "Amulet of mighty fists +2", marketCost = 3000, craftCost = 1500)
        )*/
    }

    override fun findById(id: Long): Item {
        return if (id == 1L) {
            getMagicItem()
        } else {
            getMundaneItem()
        }
    }

    override fun findFiltered(filter: ItemFilter): List<Item> {
        return (1..100)
            .map { Item(name = "Item $it", marketCost = 10 * it, craftCost = 5 * it) }
            .filter { it.name?.contains(filter.name) ?: true }
            .toList()
    }

    override fun delete(item: Item) {}

    private fun getMagicItem(): Item {
        val item = Item(
            name = "Traveler’s Any-Tool",
            marketCost = 12345,
            aura = "moderate transmutation",
            casterLevel = 9,
            weight = 2f,
            description = "This implement at first seems to be nothing but a 12-inch iron bar lined with small plates and spikes. It can be folded, twisted, hinged, and bent, to form almost any known tool. Hammers, shovels, even a block and tackle (without rope) are possible. It can duplicate any tool the wielder can clearly visualize that contains only limited moving parts, such as a pair of scissors, but not a handloom. It cannot be used to replace missing or broken parts of machines or vehicles unless a mundane tool would have done the job just as well.",
            itemType = ItemType.WONDROUS_ITEM
        )
        val req = Requirement(
            spells = listOf("major creation"),
            difficultClass = 13,
            addPrep = "creator must be gay",
            casterLevel = 1,
            skills = "Swim 5 ranks",
            feats = listOf("Craft Wondrous Item")
        )
        val alternativeChoice = AlternativeChoice(choice = listOf("wish", "miracle"))
        req.alternativeChoice = ToMany(req, Requirement_.alternativeChoice)
        req.alternativeChoice.add(alternativeChoice)
        item.requirements = ToOne(item, Item_.requirements)
        item.requirements.target = req
        return item
    }

    private fun getMundaneItem(): Item {
        val item = Item(
            name = "Some simple item",
            marketCost = 1200,
            aura = "",
            casterLevel = 0,
            weight = 2f,
            description = "Simple description",
            itemType = ItemType.TWO_HANDED_WEAPON
        )
        val req = Requirement(
            spells = listOf(),
            addPrep = "",
            casterLevel = 0,
            skills = "",
            craftSkill = "Craft (weapon)",
            difficultClass = 15
        )
        item.requirements = ToOne(item, Item_.requirements)
        item.requirements.target = req
        return item
    }
}

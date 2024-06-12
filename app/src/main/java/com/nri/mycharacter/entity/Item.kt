package com.nri.mycharacter.entity

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonSetter
import com.nri.mycharacter.utils.CraftSkill
import com.nri.mycharacter.utils.CraftSkill.*
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import io.objectbox.relation.ToOne

@Entity
data class Item (
    @Id
    var id: Long = 0,
    var name: String? = null,
    var marketCost: Int = 0,
    var craftCost: Int = 0,
    var casterLevel: Int = 0,
    var aura: String? = null,
    var weight: Float = 0f,
    @Convert(converter = ItemSlotConverter::class, dbType = Int::class)
    var slot: ItemSlot = ItemSlot.NONE,
    var description: String? = null,
    @Convert(converter = ItemTypeConverter::class, dbType = Int::class)
    var itemType: ItemType = ItemType.OTHER,
    var baseCraftTime: Int = 0,
    var temporary: Boolean = false
) {
    lateinit var requirements: ToOne<Requirement>

    @JsonSetter("requirements")
    fun setRequirements(requirement: Requirement) {
        requirements.target = requirement
    }
}

enum class ItemSlot {
    HEAD,
    SHOULDERS,
    BODY,
    BELT,
    CHEST,
    FEET,
    HAND,
    HEADBAND,
    NECK,
    WRISTS,
    ARMOR,
    RING,
    @JsonEnumDefaultValue
    NONE
}

enum class ItemType(
    val isMundane: Boolean,
    val masterworkCost: Int = 0,
    val baseCraftSkill: List<CraftSkill> = listOf(APPROPRIATE),
    val masterworkDc: Int = 20,
    val baseFeats: List<String> = listOf()
) {
    CLOTHING(true),
    LIGHT_ARMOR(true, 15000, listOf(CRAFT_ARMOR)),
    MEDIUM_ARMOR(true, 15000, listOf(CRAFT_ARMOR)),
    HEAVY_ARMOR(true, 15000, listOf(CRAFT_ARMOR)),
    SHIELD(true, 15000, listOf(CRAFT_ARMOR)),
    AMMUNITION(true, 600, listOf(CRAFT_BOW, CRAFT_WEAPON)),
    LIGHT_WEAPON(true, 30000, listOf(CRAFT_WEAPON)),
    ONE_HANDED_WEAPON(true, 30000, listOf(CRAFT_WEAPON)),
    TWO_HANDED_WEAPON(true, 30000, listOf(CRAFT_WEAPON)),
    RANGED_WEAPON(true, 30000, listOf(CRAFT_BOW, CRAFT_WEAPON)),
    SIEGE_ENGINE(true, 30000, listOf(CRAFT_SIEGE), 5),
    ALCHEMICAL(isMundane = true, baseCraftSkill = listOf(CRAFT_ALCHEMY)),
    WAND(isMundane = false, baseCraftSkill = listOf(
        SPELLCRAFT, CRAFT_JEWELRY, CRAFT_SCULPTURES, PROFESSION_WOODCUTTER),
        baseFeats = listOf("Craft Wand")
    ),
    STAFF(isMundane = false, baseCraftSkill = listOf(
        SPELLCRAFT, CRAFT_JEWELRY, CRAFT_SCULPTURES, PROFESSION_WOODCUTTER)),
    RING(isMundane = false, baseCraftSkill = listOf(SPELLCRAFT, CRAFT_JEWELRY)),
    WONDROUS_ITEM(isMundane = false, baseCraftSkill = listOf(SPELLCRAFT, APPROPRIATE)),
    ROD(isMundane = false, baseCraftSkill = listOf(
        SPELLCRAFT, CRAFT_JEWELRY, CRAFT_SCULPTURES, CRAFT_WEAPON)),
    POTION(isMundane = false, baseCraftSkill = listOf(SPELLCRAFT, CRAFT_ALCHEMY),
        baseFeats = listOf("Brew Potion")),
    SCROLL(isMundane = false, baseCraftSkill = listOf(
        SPELLCRAFT, CRAFT_CALLIGRAPHY, PROFESSION_SCRIBE),
        baseFeats = listOf("Scribe Scroll")),
    MAGIC_WEAPON(isMundane = false, baseCraftSkill = listOf(SPELLCRAFT, CRAFT_BOW, CRAFT_WEAPON)),
    MAGIC_ARMOR(isMundane = false, baseCraftSkill = listOf(SPELLCRAFT, CRAFT_ARMOR)),
    MAGIC_SHIELD(isMundane = false, baseCraftSkill = listOf(SPELLCRAFT, CRAFT_ARMOR)),
    MAGIC_PLANT(false),
    CONSTRUCT(false),
    OTHER(true)
}

class ItemSlotConverter : PropertyConverter<ItemSlot?, Int?> {
    override fun convertToEntityProperty(databaseValue: Int?): ItemSlot {
        if (databaseValue == null) {
            return ItemSlot.NONE
        }
        for (slot in ItemSlot.entries) {
            if (slot.ordinal == databaseValue) {
                return slot
            }
        }
        return ItemSlot.NONE
    }

    override fun convertToDatabaseValue(entityProperty: ItemSlot?): Int? {
        return entityProperty?.ordinal
    }
}

class ItemTypeConverter : PropertyConverter<ItemType?, Int?> {
    override fun convertToEntityProperty(databaseValue: Int?): ItemType {
        if (databaseValue == null) {
            return ItemType.OTHER
        }
        for (slot in ItemType.entries) {
            if (slot.ordinal == databaseValue) {
                return slot
            }
        }
        return ItemType.OTHER
    }

    override fun convertToDatabaseValue(entityProperty: ItemType?): Int? {
        return entityProperty?.ordinal
    }
}

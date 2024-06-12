package com.nri.mycharacter.entity

import com.fasterxml.jackson.annotation.JsonSetter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import io.objectbox.relation.ToOne

@Entity
data class Enchant(
    @Id
    var id: Long = 0,
    var name: String? = null,
    var marketCost: Int = 0,
    var craftCost: Int = 0,
    var enchantmentsPrice: Int = 0,
    var casterLevel: Int = 0,
    var aura: String? = null,
    var description: String? = null,
    @Convert(converter = EnchantTypeConverter::class, dbType = Int::class)
    var type: EnchantType? = null
) {
    lateinit var requirements: ToOne<Requirement>

    @JsonSetter("requirements")
    fun setRequirements(requirement: Requirement) {
        requirements.target = requirement
    }
}

enum class EnchantType {
    ARMOR,
    WEAPON
}

class EnchantTypeConverter : PropertyConverter<EnchantType?, Int> {
    override fun convertToEntityProperty(databaseValue: Int?): EnchantType? {
        if (databaseValue == null) {
            return null
        }
        for (slot in EnchantType.entries) {
            if (slot.ordinal == databaseValue) {
                return slot
            }
        }
        return null
    }

    override fun convertToDatabaseValue(entityProperty: EnchantType?): Int? {
        return entityProperty?.ordinal
    }
}
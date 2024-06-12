package com.nri.mycharacter.utils

enum class CraftSkill(
    val humanReadableName: String
) {
    CRAFT_ARMOR("Craft (armor)"),
    CRAFT_BOW("Craft (bow)"),
    CRAFT_WEAPON("Craft (weapon)"),
    CRAFT_ALCHEMY("Craft (alchemy)"),
    CRAFT_SIEGE("Craft (siege)"),
    CRAFT_JEWELRY("Craft (jewelry)"),
    CRAFT_SCULPTURES("Craft (sculptures)"),
    CRAFT_CALLIGRAPHY("Craft (calligraphy)"),
    PROFESSION_SCRIBE("Profession (scribe)"),
    PROFESSION_WOODCUTTER("Profession (woodcutter)"),
    SPELLCRAFT("Spellcraft"),
    APPROPRIATE("Any appropriate (consult with your GM)")
}
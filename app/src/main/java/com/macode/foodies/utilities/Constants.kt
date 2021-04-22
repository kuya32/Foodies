package com.macode.foodies.utilities

object Constants {
    const val DISH_TYPES: String = "DishType"
    const val DISH_CATEGORY: String ="DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"
    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"
    const val EXTRA_DISH_DETAILS: String = "DishDetails"
    const val ALL_ITEMS: String = "All"
    const val FILTER_SELECTION: String = "FilerSelection"

    fun dishTypes(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Appetizers")
        list.add("Candy & Sweets")
        list.add("Dressings")
        list.add("Lunch")
        list.add("Snacks")
        list.add("Dinner")
        list.add("Salad")
        list.add("Side Dish")
        list.add("Dessert")
        list.add("Other")
        list.add("Breakfast")
        return list
    }

    fun dishCategories(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Mexican")
        list.add("Italian")
        list.add("Spanish")
        list.add("American")
        list.add("British")
        list.add("Thai")
        list.add("Japanese")
        list.add("Chinese")
        list.add("Indian")
        list.add("Russian")
        list.add("Jewish")
        list.add("German")
        list.add("French")
        list.add("Hawaiian")
        list.add("Brazilian")
        list.add("Cuban")
        list.add("Greek")
        list.add("Mormon")
        list.add("Cajun")
        list.add("Korean")
        list.add("Filipino")
        list.add("Vietnamese")
        list.add("Caribbean")
        list.add("Vegan")
        return list
    }

    fun dishCookingTimes(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("25")
        list.add("30")
        list.add("45")
        list.add("50")
        list.add("60")
        list.add("70")
        list.add("75")
        list.add("80")
        list.add("85")
        list.add("90")
        list.add("105")
        list.add("120")
        list.add("150")
        list.add("180")
        return list
    }
}
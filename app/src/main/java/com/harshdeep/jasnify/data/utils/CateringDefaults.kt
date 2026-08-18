package com.harshdeep.jasnify.data.utils

import com.harshdeep.jasnify.data.local.CateringItemEntity
import com.harshdeep.jasnify.presentation.components.chip.Dietary
import java.util.UUID

data class CateringItemTemplate(
    val name: String,
    val dietary: Dietary,
    val type: String, // Matches the 'type' field in CateringItemEntity
    val cuisine: String
) {
    fun toEntity(eventId: String): CateringItemEntity {
        return CateringItemEntity(
            id = UUID.randomUUID().toString(),
            eventId = eventId,
            name = name,
            dietary = dietary,
            type = type,
            cuisine = cuisine,
            isSynced = false,
            lastUpdated = System.currentTimeMillis()
        )
    }
}

object CateringDefaults {

    // --- APPETIZERS ---
    val PANEER_TIKKA = CateringItemTemplate("Paneer Tikka", Dietary.Veg, "Appetizers", "North Indian")
    val CHICKEN_MALAI_TIKKA = CateringItemTemplate("Chicken Malai Tikka", Dietary.NonVeg, "Appetizers", "Mughlai")
    val CRISPY_CHILLI_POTATO = CateringItemTemplate("Crispy Chilli Potato", Dietary.Veg, "Appetizers", "Chinese")
    val MUTTON_SEEKH_KEBAB = CateringItemTemplate("Mutton Seekh Kebab", Dietary.NonVeg, "Appetizers", "Mughlai")
    val AMRITSARI_FISH_FRY = CateringItemTemplate("Amritsari Fish Fry", Dietary.NonVeg, "Appetizers", "Punjabi")
    val HARA_BHARA_KEBAB = CateringItemTemplate("Hara Bhara Kebab", Dietary.Veg, "Appetizers", "North Indian")
    val CHEESE_CORN_BALLS = CateringItemTemplate("Cheese Corn Balls", Dietary.Veg, "Appetizers", "Continental")
    val GARLIC_BUTTER_PRAWNS = CateringItemTemplate("Garlic Butter Prawns", Dietary.NonVeg, "Appetizers", "Continental")
    val VEG_SPRING_ROLLS = CateringItemTemplate("Veg Spring Rolls", Dietary.Veg, "Appetizers", "Chinese")
    val DAHI_KE_KEBAB = CateringItemTemplate("Dahi Ke Kebab", Dietary.Veg, "Appetizers", "North Indian")
    val MINI_BURGERS = CateringItemTemplate("Mini Burgers", Dietary.NonVeg, "Appetizers", "Continental")
    val FRENCH_FRIES = CateringItemTemplate("French Fries", Dietary.Veg, "Appetizers", "Continental")
    val ASSORTED_SANDWICHES = CateringItemTemplate("Assorted Sandwiches", Dietary.Veg, "Appetizers", "Continental")
    val BRUSCHETTA = CateringItemTemplate("Tomato Basil Bruschetta", Dietary.Veg, "Appetizers", "Italian")
    val PERI_PERI_CHICKEN_WINGS = CateringItemTemplate("Peri Peri Chicken Wings", Dietary.NonVeg, "Appetizers", "Continental")

    // --- BEVERAGES ---
    val MANGO_LASSI = CateringItemTemplate("Mango Lassi", Dietary.Veg, "Beverages", "Indian")
    val MASALA_LEMONADE = CateringItemTemplate("Masala Lemonade", Dietary.Veg, "Beverages", "Indian")
    val VIRGIN_MOJITO = CateringItemTemplate("Virgin Mojito", Dietary.Veg, "Beverages", "Continental")
    val COLD_COFFEE = CateringItemTemplate("Cold Coffee with Ice Cream", Dietary.Veg, "Beverages", "Continental")
    val ICED_PEACH_TEA = CateringItemTemplate("Iced Peach Tea", Dietary.Veg, "Beverages", "Continental")
    val BLUE_LAGOON = CateringItemTemplate("Blue Lagoon Mocktail", Dietary.Veg, "Beverages", "Continental")
    val SWEET_SALT_LIME_SODA = CateringItemTemplate("Sweet & Salt Lime Soda", Dietary.Veg, "Beverages", "Indian")
    val THANDAI = CateringItemTemplate("Thandai", Dietary.Veg, "Beverages", "Indian")
    val ASSORTED_SOFT_DRINKS = CateringItemTemplate("Assorted Soft Drinks", Dietary.Veg, "Beverages", "Global")
    val WATERMELON_BASIL_COOLER = CateringItemTemplate("Watermelon Basil Cooler", Dietary.Veg, "Beverages", "Continental")
    val MASALA_CHAI = CateringItemTemplate("Kullhad Masala Chai", Dietary.Veg, "Beverages", "Indian")
    val FILTER_COFFEE = CateringItemTemplate("Filter Coffee", Dietary.Veg, "Beverages", "South Indian")

    // --- MAIN COURSE ---
    val RAJMA_RICE = CateringItemTemplate("Rajma Rice Bowl", Dietary.Veg, "Main Course", "North Indian")
    val DAL_MAKHANI = CateringItemTemplate("Dal Makhani", Dietary.Veg, "Main Course", "Punjabi")
    val BUTTER_CHICKEN = CateringItemTemplate("Butter Chicken", Dietary.NonVeg, "Main Course", "Punjabi")
    val MUTTON_ROGAN_JOSH = CateringItemTemplate("Mutton Rogan Josh", Dietary.NonVeg, "Main Course", "Kashmiri")
    val PANEER_LABABDAR = CateringItemTemplate("Paneer Lababdar", Dietary.Veg, "Main Course", "North Indian")
    val HYDERABADI_CHICKEN_BIRYANI = CateringItemTemplate("Hyderabadi Chicken Biryani", Dietary.NonVeg, "Main Course", "Hyderabadi")
    val VEG_DUM_BIRYANI = CateringItemTemplate("Veg Dum Biryani", Dietary.Veg, "Main Course", "Indian")
    val CHICKEN_TIKKA_MASALA = CateringItemTemplate("Chicken Tikka Masala", Dietary.NonVeg, "Main Course", "Mughlai")
    val CHICKEN_HAKKA_NOODLES = CateringItemTemplate("Chicken Hakka Noodles", Dietary.NonVeg, "Main Course", "Chinese")
    val VEG_HAKKA_NOODLES = CateringItemTemplate("Veg Hakka Noodles", Dietary.Veg, "Main Course", "Chinese")
    val FISH_CURRY = CateringItemTemplate("Fish Curry", Dietary.NonVeg, "Main Course", "Coastal")
    val CHILLI_PANEER = CateringItemTemplate("Chilli Paneer", Dietary.Veg, "Main Course", "Indo-Chinese")
    val VEG_MANCHURIAN = CateringItemTemplate("Veg Manchurian", Dietary.Veg, "Main Course", "Indo-Chinese")
    val CHICKEN_MANCHURIAN = CateringItemTemplate("Chicken Manchurian", Dietary.NonVeg, "Main Course", "Indo-Chinese")
    val PENNE_ARRABBIATA = CateringItemTemplate("Penne Arrabbiata Pasta", Dietary.Veg, "Main Course", "Italian")
    val ALFREDO_CHICKEN_PASTA = CateringItemTemplate("Fettuccine Alfredo Chicken", Dietary.NonVeg, "Main Course", "Italian")
    val TANDOORI_ROTI = CateringItemTemplate("Tandoori Roti", Dietary.Veg, "Main Course", "Indian Bread")
    val BUTTER_NAAN = CateringItemTemplate("Butter Naan", Dietary.Veg, "Main Course", "Indian Bread")
    val PANEER_STUFFED_NAAN = CateringItemTemplate("Paneer Stuffed Naan", Dietary.Veg, "Main Course", "Indian Bread")
    val GARLIC_NAAN = CateringItemTemplate("Garlic Naan", Dietary.Veg, "Main Course", "Indian Bread")

    // --- DESSERTS ---
    val CLASSIC_CHEESECAKE = CateringItemTemplate("Classic Cheesecake", Dietary.Veg, "Desserts", "Continental")
    val MOONG_DAL_HALWA = CateringItemTemplate("Moong Dal Halwa", Dietary.Veg, "Desserts", "Rajasthani")
    val CHOCOLATE_BROWNIE = CateringItemTemplate("Warm Chocolate Brownie", Dietary.Veg, "Desserts", "Continental")
    val KESARI_PHIRNI = CateringItemTemplate("Kesari Phirni", Dietary.Veg, "Desserts", "North Indian")
    val TIRAMISU_CUPS = CateringItemTemplate("Tiramisu Cups", Dietary.Veg, "Desserts", "Italian")
    val FRESH_FRUIT_CREAM = CateringItemTemplate("Fresh Fruit Cream", Dietary.Veg, "Desserts", "Global")
    val SHAHI_TUKDA = CateringItemTemplate("Shahi Tukda", Dietary.Veg, "Desserts", "Awadhi")
    val VANILLA_ICE_CREAM = CateringItemTemplate("Vanilla Bean Ice Cream", Dietary.Veg, "Desserts", "Global")
    val GULAB_JAMUN = CateringItemTemplate("Gulab Jamun with Rabri", Dietary.Veg, "Desserts", "North Indian")
    val RED_VELVET_PASTRY = CateringItemTemplate("Red Velvet Pastry", Dietary.Veg, "Desserts", "Continental")

    // --- BUNDLES ---
    val weddingTemplates = listOf(
        PANEER_TIKKA, CHICKEN_MALAI_TIKKA, CRISPY_CHILLI_POTATO, MUTTON_SEEKH_KEBAB,
        AMRITSARI_FISH_FRY, HARA_BHARA_KEBAB, CHEESE_CORN_BALLS,
        GARLIC_BUTTER_PRAWNS, DAHI_KE_KEBAB, VEG_SPRING_ROLLS,
        MANGO_LASSI, MASALA_LEMONADE, VIRGIN_MOJITO, COLD_COFFEE,
        ICED_PEACH_TEA, BLUE_LAGOON, SWEET_SALT_LIME_SODA, THANDAI, ASSORTED_SOFT_DRINKS,
        RAJMA_RICE, DAL_MAKHANI, BUTTER_CHICKEN, MUTTON_ROGAN_JOSH,
        PANEER_LABABDAR, HYDERABADI_CHICKEN_BIRYANI, VEG_DUM_BIRYANI, CHICKEN_TIKKA_MASALA,
        CHICKEN_HAKKA_NOODLES, FISH_CURRY, CHILLI_PANEER, VEG_MANCHURIAN,
        CHICKEN_MANCHURIAN, TANDOORI_ROTI, BUTTER_NAAN, PANEER_STUFFED_NAAN, GARLIC_NAAN,
        CLASSIC_CHEESECAKE, MOONG_DAL_HALWA, CHOCOLATE_BROWNIE, KESARI_PHIRNI,
        TIRAMISU_CUPS, FRESH_FRUIT_CREAM, SHAHI_TUKDA, VANILLA_ICE_CREAM, GULAB_JAMUN
    )

    val casualPartyTemplates = listOf(
        MINI_BURGERS, FRENCH_FRIES, CHEESE_CORN_BALLS, PERI_PERI_CHICKEN_WINGS,
        VEG_SPRING_ROLLS, CRISPY_CHILLI_POTATO,
        VIRGIN_MOJITO, COLD_COFFEE, BLUE_LAGOON, ASSORTED_SOFT_DRINKS, WATERMELON_BASIL_COOLER,
        CHILLI_PANEER, CHICKEN_HAKKA_NOODLES, VEG_HAKKA_NOODLES, PENNE_ARRABBIATA,
        CHOCOLATE_BROWNIE, RED_VELVET_PASTRY, VANILLA_ICE_CREAM
    )

    val corporateTemplates = listOf(
        HARA_BHARA_KEBAB, ASSORTED_SANDWICHES, BRUSCHETTA, DAHI_KE_KEBAB,
        MASALA_LEMONADE, ICED_PEACH_TEA, MASALA_CHAI, FILTER_COFFEE,
        DAL_MAKHANI, PANEER_LABABDAR, BUTTER_NAAN, PENNE_ARRABBIATA, VEG_DUM_BIRYANI,
        FRESH_FRUIT_CREAM, CLASSIC_CHEESECAKE, TIRAMISU_CUPS
    )

    val festivalTemplates = listOf(
        CRISPY_CHILLI_POTATO, VEG_SPRING_ROLLS, PANEER_TIKKA,
        ASSORTED_SOFT_DRINKS, SWEET_SALT_LIME_SODA, WATERMELON_BASIL_COOLER,
        VEG_DUM_BIRYANI, HYDERABADI_CHICKEN_BIRYANI, VEG_HAKKA_NOODLES, CHICKEN_HAKKA_NOODLES,
        GULAB_JAMUN, VANILLA_ICE_CREAM
    )

    val generalTemplates = listOf(
        CHEESE_CORN_BALLS, HARA_BHARA_KEBAB,
        MASALA_LEMONADE, SWEET_SALT_LIME_SODA,
        VEG_DUM_BIRYANI, DAL_MAKHANI, BUTTER_NAAN,
        VANILLA_ICE_CREAM, GULAB_JAMUN
    )
}
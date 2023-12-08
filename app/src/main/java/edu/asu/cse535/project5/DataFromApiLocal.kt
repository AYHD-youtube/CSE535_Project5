package edu.asu.cse535.project5

import edu.asu.cse535.project5.datamodel.ReccomendedDietResponse
import kotlin.random.Random

object DataFromApiLocal {
    private val mealNames = listOf(
        "Mixed Salad",
        "Greek Yogurt with Berries",
        "Vegetable Stir Fry",
        "Grilled Chicken Breast",
        "Whole Wheat Sandwich with Turkey and Avocado",
        "Quinoa and Black Bean Salad",
        "Baked Salmon with Steamed Broccoli",
        "Tomato and Basil Pasta",
        "Cauliflower Rice Bowl with Tofu",
        "Spinach and Feta Omelette",
        "Lentil Soup",
        "Veggie Wrap with Hummus",
        "Chicken Caesar Salad",
        "Shrimp and Avocado Salad",
        "Grilled Tuna Steak",
        "Sweet Potato and Black Bean Chili",
        "Zucchini Noodles with Pesto",
        "Roasted Turkey and Cranberry Sandwich",
        "Chickpea and Spinach Curry",
        "Beef and Broccoli Stir Fry",
        "Butternut Squash Soup",
        "Caprese Salad with Balsamic Glaze",
        "Eggplant Parmesan",
        "Spicy Tuna Roll",
        "Chicken Tikka Masala",
        "Steak Salad with Blue Cheese",
        "Mushroom Risotto",
        "Stuffed Bell Peppers",
        "Cobb Salad",
        "Seared Scallops with Quinoa",
        "Pan-Seared Duck Breast",
        "Falafel with Tzatziki Sauce",
        "Pulled Pork Sandwich",
        "Kale and Quinoa Salad",
        "Chicken Alfredo Pasta",
        "Baked Cod with Lemon and Dill",
        "Vegan Burrito Bowl",
        "Pesto Chicken Wrap",
        "Spaghetti Carbonara",
        "Mediterranean Chickpea Salad",
        "Teriyaki Chicken with Rice",
        "Roasted Vegetable Tart",
        "Sushi Bowl with Salmon and Avocado",
        "Thai Green Curry with Chicken",
        "Quiche Lorraine",
        "Grilled Veggie Panini",
        "Beef Stroganoff",
        "Ratatouille",
        "Pork Tenderloin with Roasted Vegetables",
        "Tofu and Vegetable Stir Fry"
    )

    fun generateReccomendationDiet(record : Map<String, String>): ReccomendedDietResponse {
        val randomNumber = Random.nextInt(150, 451)
        val randomMeal = mealNames.random()
        return ReccomendedDietResponse(randomMeal, randomNumber)
    }
}
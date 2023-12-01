import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error
import numpy as np
import random
import requests

# Sample Data Creation
def create_mock_data(n=100):
    data = pd.DataFrame({
        'age': np.random.randint(18, 65, size=n),
        'height': np.random.uniform(150, 200, size=n),  # in cm
        'weight': np.random.uniform(50, 100, size=n),  # in kg
        'gender': np.random.choice(['male', 'female'], size=n),
        'activity_level': np.random.choice(['sedentary', 'light', 'moderate', 'active', 'very active'], size=n),
        'diet_type': np.random.choice(['vegetarian', 'vegan', 'low-carb', 'balanced'], size=n)
    })
    return data

# BMI Calculation
def calculate_bmi(weight, height):
    bmi = weight / (height/100)**2
    return bmi

# BMR Calculation
def calculate_bmr(row):
    if row['gender'] == 'male':
        bmr = 88.362 + (13.397 * row['weight']) + (4.799 * row['height']) - (5.677 * row['age'])
    else:
        bmr = 447.593 + (9.247 * row['weight']) + (3.098 * row['height']) - (4.330 * row['age'])
    return bmr

# TDEE Calculation
def calculate_tdee(row):
    activity_factors = {'sedentary': 1.2, 'light': 1.375, 'moderate': 1.55, 'active': 1.725, 'very active': 1.9}
    tdee = row['bmr'] * activity_factors[row['activity_level']]
    return tdee

# Data Preprocessing
data = create_mock_data()
data['bmi'] = data.apply(lambda row: calculate_bmi(row['weight'], row['height']), axis=1)
data['bmr'] = data.apply(calculate_bmr, axis=1)
data['tdee'] = data.apply(calculate_tdee, axis=1)

# Feature Engineering for Model
# Here we just use BMI, Age, and Gender as features for simplicity
features = data[['bmi', 'age', 'gender']]
features = pd.get_dummies(features)  # One-hot encode categorical variables

# Target Variable
target = data['tdee']

# Splitting Data
X_train, X_test, y_train, y_test = train_test_split(features, target, test_size=0.2, random_state=42)

# Model Training
model = RandomForestRegressor(n_estimators=100, random_state=42)
model.fit(X_train, y_train)

# Sample Prediction
def make_recommendation(age, height, weight, gender):
    bmi = calculate_bmi(weight, height)
    input_features = pd.DataFrame([[bmi, age, gender]], columns=['bmi', 'age', 'gender'])
    input_features = pd.get_dummies(input_features)
    input_features = input_features.reindex(columns = X_train.columns, fill_value=0)
    recommended_calories = model.predict(input_features)[0]
    return recommended_calories

# Example usage
# print(make_recommendation(30, 175, 70, 'male'))

# Fetch Data from USDA API
def fetch_usda_data(api_key, query, max_items=15):  # Increased number of items
    url = f"https://api.nal.usda.gov/fdc/v1/foods/search?api_key={api_key}&query={query}&pageSize={max_items}"
    response = requests.get(url)
    if response.status_code == 200:
        return response.json()
    else:
        return None

def extract_meals(data):
    meals = []
    if data and 'foods' in data:
        for item in data['foods']:
            name = item.get('description')
            calories = next((nutrient['value'] for nutrient in item['foodNutrients'] if nutrient['nutrientId'] == 1008), None)
            if name and calories:
                meals.append({'name': name, 'calories': calories})
    return meals

# Enhanced Meal Recommendation Function
def recommend_meal(api_key, time_of_day, current_calorie_intake, total_calorie_limit):
    meal_types = {'morning': 'breakfast', 'afternoon': 'lunch', 'evening': 'dinner', 'anytime': 'snack'}
    query = meal_types.get(time_of_day, 'snack')

    data = fetch_usda_data(api_key, query)
    available_meals = extract_meals(data)
    calorie_budget = total_calorie_limit - current_calorie_intake

    # Adding a flexibility margin for calorie budget
    margin = max(0.1 * total_calorie_limit, 100)
    suitable_meals = [meal for meal in available_meals if meal['calories'] <= calorie_budget + margin]

    default_meals = [
        {'name': 'Mixed Salad', 'calories': 150},
        {'name': 'Greek Yogurt with Berries', 'calories': 200},
        {'name': 'Vegetable Stir Fry', 'calories': 250},
        {'name': 'Grilled Chicken Breast', 'calories': 300},
        {'name': 'Whole Wheat Sandwich with Turkey and Avocado', 'calories': 350},
        {'name': 'Quinoa and Black Bean Salad', 'calories': 400},
        {'name': 'Baked Salmon with Steamed Broccoli', 'calories': 450},
        {'name': 'Tomato and Basil Pasta', 'calories': 300},
        {'name': 'Cauliflower Rice Bowl with Tofu', 'calories': 350},
        {'name': 'Spinach and Feta Omelette', 'calories': 200},
        {'name': 'Lentil Soup', 'calories': 250},
        {'name': 'Veggie Wrap with Hummus', 'calories': 300},
        {'name': 'Chicken Caesar Salad', 'calories': 350},
        {'name': 'Shrimp and Avocado Salad', 'calories': 400},
        {'name': 'Grilled Tuna Steak', 'calories': 450},
        {'name': 'Sweet Potato and Black Bean Chili', 'calories': 400},
        {'name': 'Zucchini Noodles with Pesto', 'calories': 350},
        {'name': 'Roasted Turkey and Cranberry Sandwich', 'calories': 300},
        {'name': 'Chickpea and Spinach Curry', 'calories': 350},
        {'name': 'Beef and Broccoli Stir Fry', 'calories': 400},
        {'name': 'Butternut Squash Soup', 'calories': 300},
        {'name': 'Caprese Salad with Balsamic Glaze', 'calories': 250},
        {'name': 'Eggplant Parmesan', 'calories': 350},
        {'name': 'Spicy Tuna Roll', 'calories': 300},
        {'name': 'Chicken Tikka Masala', 'calories': 450},
        {'name': 'Steak Salad with Blue Cheese', 'calories': 400},
        {'name': 'Mushroom Risotto', 'calories': 350},
        {'name': 'Stuffed Bell Peppers', 'calories': 300},
        {'name': 'Cobb Salad', 'calories': 400},
        {'name': 'Seared Scallops with Quinoa', 'calories': 350},
        {'name': 'Pan-Seared Duck Breast', 'calories': 400},
        {'name': 'Falafel with Tzatziki Sauce', 'calories': 300},
        {'name': 'Pulled Pork Sandwich', 'calories': 450},
        {'name': 'Kale and Quinoa Salad', 'calories': 350},
        {'name': 'Chicken Alfredo Pasta', 'calories': 450},
        {'name': 'Baked Cod with Lemon and Dill', 'calories': 300},
        {'name': 'Vegan Burrito Bowl', 'calories': 400},
        {'name': 'Pesto Chicken Wrap', 'calories': 350},
        {'name': 'Spaghetti Carbonara', 'calories': 450},
        {'name': 'Mediterranean Chickpea Salad', 'calories': 300},
        {'name': 'Teriyaki Chicken with Rice', 'calories': 400},
        {'name': 'Roasted Vegetable Tart', 'calories': 350},
        {'name': 'Sushi Bowl with Salmon and Avocado', 'calories': 400},
        {'name': 'Thai Green Curry with Chicken', 'calories': 450},
        {'name': 'Quiche Lorraine', 'calories': 350},
        {'name': 'Grilled Veggie Panini', 'calories': 300},
        {'name': 'Beef Stroganoff', 'calories': 450},
        {'name': 'Ratatouille', 'calories': 200},
        {'name': 'Pork Tenderloin with Roasted Vegetables', 'calories': 400},
        {'name': 'Tofu and Vegetable Stir Fry', 'calories': 350}
    ]


    if suitable_meals:
        return random.choice(suitable_meals)
    if not suitable_meals:
        if available_meals:
            # Find the closest meal to the calorie budget
            closest_meal = min(available_meals, key=lambda x: abs(x['calories'] - calorie_budget))
            return closest_meal
        else:
            # Recommend a random default meal when no meals are fetched
            return random.choice(default_meals)

    return random.choice(suitable_meals)


# Example Usage
# api_key = 'YOUR_API_KEY'  # Replace with your actual USDA API key
# total_calorie_limit = make_recommendation(30, 175, 70, 'male')
# current_calorie_intake = 500  # Example current intake
# time_of_day = 'afternoon'  # 'morning', 'afternoon', 'evening', 'anytime'

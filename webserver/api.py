from flask import Flask, request, jsonify
from exercise_model import recommend_exercise
from calorie_exercise_recommend import generate_exercise, calculate_calories_burned
import random

app = Flask(__name__)

def recommend_exercises(height, weight, previous_exercise, calories_goal):
    recommended_type_exercise = recommend_exercise(height, weight, previous_exercise, calories_goal)  
    recommended_exercise = generate_exercise(recommended_type_exercise)

    time = random.randint(20, 60)
    calories = calculate_calories_burned(recommended_exercise, weight, time)

    return recommended_exercise, time, calories


@app.route('/recommend_exercises', methods=['POST'])
def recommend_exercises_endpoint():
    data = request.json

    required_fields = ['height', 'weight', 'previous_exercise', 'calories_goal']

    # Check if all required fields are present in the request
    if not all(field in data for field in required_fields):
        return jsonify({'error': 'Missing required fields'}), 400

    data = recommend_exercises(data['height'], data['weight'], data['previous_exercise'], data['calories_goal'])

    return jsonify({
        'recommended_exercise': data[0],
        'time': data[1],
        'calories': data[2]
    })

if __name__ == '__main__':
    app.run(debug=True)

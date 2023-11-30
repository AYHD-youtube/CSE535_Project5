from flask import Flask, request, jsonify

app = Flask(__name__)

def recommend_exercises(height, weight, previous_exercise, calories_goal):
    # Replace this with your specific logic for recommending exercises
    recommended_exercises = []

    if previous_exercise == 'yes':
        recommended_exercises.append('Cardio exercises like running or cycling')
    else:
        recommended_exercises.append('Strength training exercises like weightlifting')

    if calories_goal < 2000:
        recommended_exercises.append('High-intensity interval training (HIIT) workouts')
    else:
        recommended_exercises.append('Low-intensity steady-state (LISS) cardio exercises')

    return recommended_exercises

@app.route('/recommend_exercises', methods=['POST'])
def recommend_exercises_endpoint():
    data = request.json

    required_fields = ['height', 'weight', 'previous_exercise', 'calories_goal']

    # Check if all required fields are present in the request
    if not all(field in data for field in required_fields):
        return jsonify({'error': 'Missing required fields'}), 400

    height = data['height']
    weight = data['weight']
    previous_exercise = data['previous_exercise']
    calories_goal = data['calories_goal']

    recommended_exercises = recommend_exercises(height, weight, previous_exercise, calories_goal)

    return jsonify({'recommended_exercises': recommended_exercises})

if __name__ == '__main__':
    app.run(debug=True)

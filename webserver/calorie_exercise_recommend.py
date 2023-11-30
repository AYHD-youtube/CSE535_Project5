import random

# Function to generate random exercise
def generate_exercise(exercise_category):
    exercises_by_category = {
        'Cardio': ['Running', 'Cycling', 'Jump Rope', 'Elliptical', 'Rowing'],
        'Strength': ['Weightlifting', 'Bodyweight Exercises', 'CrossFit'],
        'Yoga': ['Hatha Yoga', 'Vinyasa Yoga', 'Power Yoga', 'Ashtanga Yoga'],
        'HIIT': ['Tabata', 'Interval Sprints', 'Circuit Training'],
        'Running': ['Treadmill Running', 'Trail Running', 'Sprint Training'],
        'Cycling': ['Road Cycling', 'Mountain Biking', 'Indoor Cycling']
    }

    return random.choice(exercises_by_category[exercise_category])

# Function to calculate base calories burned based on exercise type, weight, and time (in minutes)
def calculate_calories_burned(exercise_type, weight, time):
    calories_per_minute = {
        'Running': 11.4,
        'Cycling': 8.0,
        'Jump Rope': 12.0,
        'Elliptical': 10.0,
        'Rowing': 9.0,
        'Weightlifting': 4.0,
        'Bodyweight Exercises': 6.0,
        'CrossFit': 8.5,
        'Hatha Yoga': 2.5,
        'Vinyasa Yoga': 4.0,
        'Power Yoga': 5.5,
        'Ashtanga Yoga': 6.0,
        'Tabata': 12.0,
        'Interval Sprints': 14.0,
        'Circuit Training': 10.0,
        'Treadmill Running': 11.5,
        'Trail Running': 12.5,
        'Sprint Training': 14.0,
        'Road Cycling': 9.0,
        'Mountain Biking': 10.5,
        'Indoor Cycling': 8.5,
    }

    return calories_per_minute.get(exercise_type, 0.0) * time * (weight / 70.0)  # Adjusting for weight

# Project README

## Prerequisites

Ensure that you have Python version 3.9.18 installed on your system. You can check your Python version by running:

```bash
python --version

Installation
To set up the project environment, run the following command to install dependencies from the requirements.txt file:

```bash
pip install -r requirements.txt

Running the Application
To launch the application, execute the following command:

```bash
python app.py

This will start the application, and it will be accessible at http://127.0.0.1:5000/.

Making Recommendations
To make exercise recommendations, use the following curl command in your terminal:

```bash
curl --location --request POST 'http://127.0.0.1:5000/recommend_exercises' \
--header 'Content-Type: application/json' \
--data-raw '{"height": 170, "weight": 70, "previous_exercise": "Running", "calories_goal": 2000}'

This curl command sends a POST request to the local server, providing user data (height, weight, previous exercise, and calories goal) in JSON format. Adjust the values accordingly for personalized recommendations.

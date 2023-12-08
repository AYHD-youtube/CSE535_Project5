# Project README

## Prerequisites

Ensure that you have Python version 3.9.18 installed on your system. You can check your Python version by running:

bash
python --version

Installation
To set up the project environment, run the following command to install dependencies from the requirements.txt file:

bash
pip install -r requirements.txt

Running the Application
To launch the application, execute the following command:

bash
python app.py

This will start the application, and it will be accessible at http://127.0.0.1:5000/.

Making Recommendations
To make diet recommendations, use the following curl command in your terminal:

bash
curl -X POST -H "Content-Type: application/json" -d '{"total_calorie_limit": 2000, "current_calorie_intake": 500, "time_of_day": "afternoon"}' http://127.0.0.1:5000/

This curl command sends a POST request to the local server, providing user data (height, weight, previous exercise, and calories goal) in JSON format. Adjust the values accordingly for personalized recommendations.
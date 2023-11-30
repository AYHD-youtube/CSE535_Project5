import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.preprocessing import OneHotEncoder

def create_model():
    data = pd.read_csv("exercise_dataset.csv")

    df = pd.DataFrame(data)

    df_encoded = pd.get_dummies(df, columns=['Previous_Exercise'], drop_first=True)

    X = df_encoded.drop('Recommended_Exercise', axis=1)
    y = df_encoded['Recommended_Exercise']

    # Split the data into training and testing sets
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

    # Train the machine learning model
    model = RandomForestClassifier(n_estimators=100, random_state=42)
    model.fit(X_train, y_train)
    print(X_test.columns)
    return model

def recommend_exercise(height, weight, previous_exercise, calories_goal):

    data_point = pd.DataFrame({
        'Height': [height],
        'Weight': [weight],
        'Previous_Exercise_Cycling': [0],
        'Previous_Exercise_HIIT': [0],
        'Previous_Exercise_Running': [0],
        'Previous_Exercise_Strength': [0],
        'Previous_Exercise_Yoga': [0],
        'Calories_Goal': [calories_goal]
    })

    if previous_exercise == 'Cycling':
        data_point['Previous_Exercise_Cycling'] = 1
    elif previous_exercise == 'HIIT':
        data_point['Previous_Exercise_HIIT'] = 1
    elif previous_exercise == 'Running':
        data_point['Previous_Exercise_Running'] = 1
    elif previous_exercise == 'Strength':
        data_point['Previous_Exercise_Strength'] = 1
    elif previous_exercise == 'Yoga':
        data_point['Previous_Exercise_Yoga'] = 1
   
    # Load the model and use it to predict on the data point
    model = create_model()
    prediction = model.predict(data_point)

    print(prediction)

    # Return the prediction
    return prediction[0]

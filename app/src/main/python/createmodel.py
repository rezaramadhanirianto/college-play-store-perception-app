import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC
import joblib  # For saving models
import os
from os.path import dirname, join

# Android-specific imports
from java import jclass

# Step 1: Load and clean the data
def init():
    # Get the Android context
    Python = jclass("com.chaquo.python.Python")
    context = Python.getPlatform().getApplication().getApplicationContext()

    # Set the path for the CSV file (assumes combined_reviews.csv is in the app's assets)
    asset_manager = context.getAssets()
    base_dir = dirname(__file__)
    path = join(base_dir, 'combined_reviews.csv')
    df = pd.read_csv(path)

    # Process the data
    df = df.drop_duplicates(subset='reviewId')
    df = df[df['perception'].notnull() & df['perception'].isin(["0", "1", "2"])]

    # Step 2: Feature Extraction
    tfidf = TfidfVectorizer(stop_words='english', max_features=1000)
    X = tfidf.fit_transform(df['content']).toarray()
    y = df['perception']

    # Step 3: Split the data into training and test sets
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Step 4: Train KNN Model
    knn = KNeighborsClassifier(n_neighbors=5)
    knn.fit(X_train, y_train)

    # Get the path to the app’s files directory
    files_dir = context.getFilesDir().getAbsolutePath()

    # Save the trained KNN model
    knn_model_path = os.path.join(files_dir, 'knn_model.joblib')
    joblib.dump(knn, knn_model_path)
    print(f"KNN model saved as '{knn_model_path}'")

    # Step 5: Train SVM Model
    svm = SVC(kernel='linear', random_state=42)
    svm.fit(X_train, y_train)

    # Save the trained SVM model
    svm_model_path = os.path.join(files_dir, 'svm_model.joblib')
    joblib.dump(svm, svm_model_path)
    print(f"SVM model saved as '{svm_model_path}'")

    # Save the TF-IDF vectorizer to transform new data consistently
    vectorizer_path = os.path.join(files_dir, 'tfidf_vectorizer.joblib')
    joblib.dump(tfidf, vectorizer_path)
    print(f"TF-IDF vectorizer saved as '{vectorizer_path}'")

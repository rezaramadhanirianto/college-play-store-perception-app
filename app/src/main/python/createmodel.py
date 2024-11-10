import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC
from sklearn.decomposition import PCA
from sklearn.metrics import accuracy_score, confusion_matrix, classification_report
import joblib
import numpy as np
from java import jclass
from os.path import join, dirname

def init_paths():
    # Access Android context for Chaquopy
    Python = jclass("com.chaquo.python.Python")
    context = Python.getPlatform().getApplication().getApplicationContext()

    # File paths (all files at the same directory level)
    base_dir = dirname(__file__)
    csv_path = join(base_dir, 'combined_reviews.csv')
    knn_model_path = join(base_dir, 'knn_model.joblib')
    svm_model_path = join(base_dir, 'svm_model.joblib')
    vectorizer_path = join(base_dir, 'tfidf_vectorizer.joblib')
    pca_path = join(base_dir, 'pca_transformer.joblib')

    return csv_path, knn_model_path, svm_model_path, vectorizer_path, pca_path

def init_and_preprocess(csv_path):
    # Load data
    df = pd.read_csv(csv_path)
    df = df.drop_duplicates(subset='reviewId')
    df = df[df['perception'].notnull() & df['perception'].isin(["0", "1", "2"])]

    # Balance classes
    class_counts = df['perception'].value_counts()
    min_count = class_counts.min()
    df_balanced = pd.concat([df[df['perception'] == label].sample(min_count, replace=True) for label in class_counts.index])

    # TF-IDF vectorization
    tfidf = TfidfVectorizer(stop_words='english', max_features=2000)
    X = tfidf.fit_transform(df_balanced['content']).toarray()
    y = df_balanced['perception'].astype(int)

    return X, y, tfidf

def apply_pca(X):
    # Set a fixed number of components for PCA
    n_components = 700
    n_samples, n_features = X.shape

    # Ensure n_components is within a valid range
    if n_components > min(n_samples, n_features):
        raise ValueError(f"n_components={n_components} is too high for the data with shape ({n_samples}, {n_features}).")

    print(f"Applying PCA with n_components={n_components}")

    # PCA transformation
    pca = PCA(n_components=n_components, random_state=42)
    X_reduced = pca.fit_transform(X)
    return X_reduced, pca

def train_knn_with_hyperparameter_tuning(X_train, y_train):
    param_grid = {'n_neighbors': [3, 5, 7, 9, 11, 13, 15, 19, 21, 25, 29, 31]}
    knn = KNeighborsClassifier()
    grid_search = GridSearchCV(knn, param_grid, cv=5, scoring='accuracy')
    grid_search.fit(X_train, y_train)
    print(f"Best KNN parameters: {grid_search.best_params_}")
    return grid_search.best_estimator_

def train_svm(X_train, y_train):
    svm = SVC(kernel='linear', random_state=42)
    svm.fit(X_train, y_train)
    return svm

def evaluate_model_with_counts(model, X_test, y_test, model_name="Model"):
    y_pred = model.predict(X_test)
    accuracy = accuracy_score(y_test, y_pred)
    print(f"{model_name} Accuracy: {accuracy:.4f}")
    cm = confusion_matrix(y_test, y_pred)
    print(f"{model_name} Confusion Matrix:\n{cm}")
    report = classification_report(y_test, y_pred, target_names=["Negative", "Neutral", "Positive"])
    print(f"{model_name} Classification Report:\n{report}")
    predictions_counts = pd.Series(y_pred).value_counts(sort=False).rename(index={0: "Negative", 1: "Neutral", 2: "Positive"})
    print(f"{model_name} Prediction Counts:\n{predictions_counts}")
    return accuracy, cm, report, predictions_counts

def init():
    # Initialize paths
    csv_path, knn_model_path, svm_model_path, vectorizer_path, pca_path = init_paths()
    X, y, tfidf = init_and_preprocess(csv_path)

    # Save TF-IDF vectorizer
    joblib.dump(tfidf, vectorizer_path)
    print(f"TF-IDF vectorizer saved as '{vectorizer_path}'")

    # Apply PCA with 700 components and save PCA
    X_reduced, pca = apply_pca(X)
    joblib.dump(pca, pca_path)
    print(f"PCA transformer saved as '{pca_path}' with n_components=700")

    # Split data
    X_train, X_test, y_train, y_test = train_test_split(X_reduced, y, test_size=0.2, random_state=42, stratify=y)

    # Train and save KNN model
    knn_model = train_knn_with_hyperparameter_tuning(X_train, y_train)
    joblib.dump(knn_model, knn_model_path)
    print(f"KNN model saved as '{knn_model_path}'")

    # Train and save SVM model
    svm_model = train_svm(X_train, y_train)
    joblib.dump(svm_model, svm_model_path)
    print(f"SVM model saved as '{svm_model_path}'")

    # Evaluate models
    print("\nEvaluating KNN Model:")
    evaluate_model_with_counts(knn_model, X_test, y_test, model_name="KNN")
    print("\nEvaluating SVM Model:")
    evaluate_model_with_counts(svm_model, X_test, y_test, model_name="SVM")

# Run the init function
init()

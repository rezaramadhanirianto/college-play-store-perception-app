from google_play_scraper import app, Sort, reviews
import pandas as pd
import ssl
import joblib
import numpy as np
from java import jclass
from os.path import join, dirname

# Fix SSL context issue
ssl._create_default_https_context = ssl._create_unverified_context

def fetch_and_analyze(app_package, review_limit, model_type='svm'):
    # Get the Android context
    Python = jclass("com.chaquo.python.Python")
    context = Python.getPlatform().getApplication().getApplicationContext()

    # Define paths for the vectorizer, PCA, and model files
    base_dir = dirname(__file__)
    vectorizer_path = join(base_dir, 'tfidf_vectorizer.joblib')
    pca_path = join(base_dir, 'pca_transformer.joblib')
    model_path = join(base_dir, f"{model_type}_model.joblib")

    # Fetch app details
    app_result = app(app_package, lang='id', country='id')

    # Fetch and process reviews
    reviews_all = []
    reviews_batch, continuation_token = reviews(
        app_package, lang='id', country='id', sort=Sort.NEWEST, count=int(review_limit / 10)
    )
    reviews_all.extend(reviews_batch)
    while len(reviews_all) < review_limit and continuation_token:
        reviews_batch, continuation_token = reviews(
            app_package, lang='id', country='id', sort=Sort.NEWEST, count=int(review_limit / 10), continuation_token=continuation_token
        )
        reviews_all.extend(reviews_batch)

    # Trim and clean reviews
    reviews_all = pd.DataFrame(reviews_all).drop_duplicates(subset="content")[:review_limit]
    if 'content' not in reviews_all.columns:
        raise ValueError("The DataFrame does not contain 'content' column with review text.")
    reviews_all['content'] = reviews_all['content'].astype(str)

    # Load TF-IDF vectorizer, PCA, and model
    vectorizer = joblib.load(vectorizer_path)
    X = vectorizer.transform(reviews_all['content']).toarray()
    pca = joblib.load(pca_path)
    X_reduced = pca.transform(X)
    model = joblib.load(model_path)

    # Predict sentiments
    predictions = model.predict(X_reduced)
    positive_count = np.sum(predictions == 2)
    neutral_count = np.sum(predictions == 1)
    negative_count = np.sum(predictions == 0)

    result = {
        "title": app_result['title'],
        "iconUrl": app_result['icon'],
        "total_reviews": len(reviews_all),
        "positive_reviews": positive_count,
        "neutral_reviews": neutral_count,
        "negative_reviews": negative_count,
        "positive_percentage": (positive_count / len(reviews_all)) * 100
    }
    return result

# Example usage
# result = fetch_and_analyze('com.gojek.app', 100, 'svm')
# print(result)

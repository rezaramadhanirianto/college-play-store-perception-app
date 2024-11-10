from google_play_scraper import app, Sort, reviews
import pandas as pd
import ssl
import joblib
import numpy as np
from os.path import dirname, join

# Fix SSL context issue
ssl._create_default_https_context = ssl._create_unverified_context

def fetch_and_analyze(app_package, review_limit, model_type='svm'):
    # Fetch app details
    app_result = app(
        app_package,
        lang='id',
        country='id'
    )

    # Initialize list for all reviews
    reviews_all = []

    # Get the initial batch of reviews
    reviews_batch, continuation_token = reviews(
        app_package,
        lang='id',  # Language is Indonesian
        country='id',  # Country is Indonesia
        sort=Sort.NEWEST,  # Sorting by newest reviews
        count=int(review_limit / 10)  # Adjusted to meet review limit
    )

    # Append the first batch of reviews to the list
    reviews_all.extend(reviews_batch)

    # Continue fetching reviews until reaching the review limit
    while len(reviews_all) < review_limit and continuation_token:
        reviews_batch, continuation_token = reviews(
            app_package,
            lang='id',
            country='id',
            sort=Sort.NEWEST,
            count=int(review_limit / 10),
            continuation_token=continuation_token  # Use continuation token for pagination
        )
        reviews_all.extend(reviews_batch)

    # Trim the reviews to the specified limit and remove duplicates
    reviews_all = pd.DataFrame(reviews_all).drop_duplicates(subset="content")
    reviews_all = reviews_all[:review_limit]
    total_unique_reviews = len(reviews_all)

    # Ensure the 'content' column is present and has only string values
    if 'content' not in reviews_all.columns:
        raise ValueError("The DataFrame does not contain 'content' column with review text.")

    # Convert any non-string content to string type to avoid issues
    reviews_all['content'] = reviews_all['content'].astype(str)

    # Define file paths using os.path for vectorizer and model files
    base_dir = dirname(__file__)
    vectorizer_path = join(base_dir, 'tfidf_vectorizer.joblib')
    if model_type == 'knn':
        model_path = join(base_dir, 'knn_model.joblib')
    else:
        model_path = join(base_dir, 'svm_model.joblib')

    # Load the pre-trained TfidfVectorizer and transform the review content
    vectorizer = joblib.load(vectorizer_path)
    X = vectorizer.transform(reviews_all['content'])

    # Convert the sparse matrix to dense
    X = X.toarray()

    # Load the pre-trained sentiment analysis model (e.g., SVM)
    model = joblib.load(model_path)

    # Predict sentiments (assumes 0 for negative, 1 for neutral, 2 for positive)
    predictions = model.predict(X)

    # Count the number of predictions for each sentiment
    positive_count = np.sum(predictions == '2')  # Count positives
    neutral_count = np.sum(predictions == '1')   # Count neutrals
    negative_count = np.sum(predictions == '0')  # Count negatives
    total_reviews = total_unique_reviews         # Use the unique review count as the total

    # Return flat dictionary with app details and review analysis results
    result = {
        "title": app_result['title'],
        "iconUrl": app_result['icon'],
        "total_reviews": total_reviews,
        "positive_reviews": positive_count,
        "neutral_reviews": neutral_count,
        "negative_reviews": negative_count,
        "positive_percentage": (positive_count / total_reviews) * 100
    }

    return result

# Example usage
# result = fetch_and_analyze('com.gojek.app', 100, 'svm')
# print(result)
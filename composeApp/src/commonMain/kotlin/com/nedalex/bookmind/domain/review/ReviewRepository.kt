package com.nedalex.bookmind.domain.review

import com.nedalex.bookmind.data.models.Review

interface ReviewRepository {
    /**
     * Get all reviews for a specific book
     */
    suspend fun getReviewsForBook(bookId: String): Result<List<Review>>

    /**
     * Get a user's review for a specific book
     */
    suspend fun getUserReviewForBook(userId: String, bookId: String): Result<Review?>

    /**
     * Get all reviews by a user
     */
    suspend fun getReviewsByUser(userId: String): Result<List<Review>>

    /**
     * Add a new review
     */
    suspend fun addReview(
        bookId: String,
        userId: String,
        rating: Int,
        reviewText: String?
    ): Result<Review>

    /**
     * Update an existing review
     */
    suspend fun updateReview(
        reviewId: String,
        rating: Int,
        reviewText: String?
    ): Result<Review>

    /**
     * Delete a review
     */
    suspend fun deleteReview(reviewId: String): Result<Unit>
}
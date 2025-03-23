package com.he172006.onlineclothesshop.DAO;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Review;


import java.util.ArrayList;
import java.util.List;


public class ReviewDAO {
    private static final String TABLE_REVIEWS = "Reviews";
    private static final String COLUMN_REVIEW_ID = "reviewId";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_REVIEW_CONTENT = "reviewContent";


    private DataBase dbHelper;
    private SQLiteDatabase db;


    public ReviewDAO(Context context) {
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
    }


    public long insertReview(Review review) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_ID, review.getProductId());
        values.put(COLUMN_RATING, review.getRating());
        values.put(COLUMN_REVIEW_CONTENT, review.getReviewText());
        return db.insert(TABLE_REVIEWS, null, values);
    }


    public List<Review> getReviewsByProduct(int productId) {
        List<Review> reviewList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_REVIEWS, null, COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)}, null, null, null);


        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_ID));
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING));
                String reviewText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_CONTENT));


                reviewList.add(new Review(productId, rating, reviewText));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reviewList;
    }




    public int updateReview(int id, float rating, String reviewText) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RATING, rating);
        values.put(COLUMN_REVIEW_CONTENT, reviewText);


        return db.update(TABLE_REVIEWS, values, COLUMN_REVIEW_ID + " = ?", new String[]{String.valueOf(id)});
    }


    public int deleteReview(int id) {
        return db.delete(TABLE_REVIEWS, COLUMN_REVIEW_ID + " = ?", new String[]{String.valueOf(id)});
    }
}





package com.he172006.onlineclothesshop.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private static final String TABLE_NAME = "Categories";
    private static final String COLUMN_CATEGORY_ID = "categoryId";
    private static final String COLUMN_CATEGORY_NAME = "categoryName";
    private static final String COLUMN_CATEGORY_IMAGE = "image";
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public CategoryDAO(Context context) {
        this.context = context;
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
        // Không gọi insertSampleCategories() ở đây, sẽ gọi từ nơi khác nếu cần
    }

    // Insert a new category
    public long insertCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());
        values.put(COLUMN_CATEGORY_IMAGE, category.getImage());
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("CategoryDAO", "Inserted category with name: " + category.getCategoryName() + ", Result: " + result);
        return result;
    }

    // Update an existing category
    public int updateCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());
        values.put(COLUMN_CATEGORY_IMAGE, category.getImage());
        String whereClause = COLUMN_CATEGORY_ID + " = ?";
        String[] whereArgs = {String.valueOf(category.getCategoryId())};
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.d("CategoryDAO", "Updated category with ID: " + category.getCategoryId() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }

    // Delete a category
    public boolean deleteCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUMN_CATEGORY_ID + " = ?";
        String[] whereArgs = {String.valueOf(categoryId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("CategoryDAO", "Deleted category with ID: " + categoryId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }

    // Get category by ID
    public Category getCategoryById(int categoryId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_CATEGORY_ID, COLUMN_CATEGORY_NAME, COLUMN_CATEGORY_IMAGE};
        String selection = COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Category category = cursorToCategory(cursor);
            cursor.close();
            return category;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get category name by ID
    public String getCategoryNameById(int categoryId) {
        Category category = getCategoryById(categoryId);
        return category != null ? category.getCategoryName() : null;
    }

    // Get all categories
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Category category = cursorToCategory(cursor);
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryList;
    }

    // Insert sample categories for testing
    public void insertSampleCategories() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data

        // Sample category 1: Women's Clothing
        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_CATEGORY_NAME, "Women's Clothing");
        values1.put(COLUMN_CATEGORY_IMAGE, "womens_clothing.jpg"); // Giả lập đường dẫn ảnh
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("CategoryDAO", "Inserted category Women's Clothing, result: " + result1);

        // Sample category 2: Men's Clothing
        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_CATEGORY_NAME, "Men's Clothing");
        values2.put(COLUMN_CATEGORY_IMAGE, "mens_clothing.jpg");
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("CategoryDAO", "Inserted category Men's Clothing, result: " + result2);

        // Sample category 3: Kids' Clothing
        ContentValues values3 = new ContentValues();
        values3.put(COLUMN_CATEGORY_NAME, "Kids' Clothing");
        values3.put(COLUMN_CATEGORY_IMAGE, "kids_clothing.jpg");
        long result3 = db.insert(TABLE_NAME, null, values3);
        Log.d("CategoryDAO", "Inserted category Kids' Clothing, result: " + result3);

        // Sample category 4: Sportswear
        ContentValues values4 = new ContentValues();
        values4.put(COLUMN_CATEGORY_NAME, "Sportswear");
        values4.put(COLUMN_CATEGORY_IMAGE, "sportswear.jpg");
        long result4 = db.insert(TABLE_NAME, null, values4);
        Log.d("CategoryDAO", "Inserted category Sportswear, result: " + result4);

        // Sample category 5: Accessories (liên quan đến Clothes)
        ContentValues values5 = new ContentValues();
        values5.put(COLUMN_CATEGORY_NAME, "Accessories");
        values5.put(COLUMN_CATEGORY_IMAGE, "accessories.jpg");
        long result5 = db.insert(TABLE_NAME, null, values5);
        Log.d("CategoryDAO", "Inserted category Accessories, result: " + result5);

        db.close();
    }

    // Convert cursor to Category object
    private Category cursorToCategory(Cursor cursor) {
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
        String image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_IMAGE));
        return new Category(categoryId, categoryName, image);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
package com.he172006.onlineclothesshop.DAO;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Product;


import java.util.ArrayList;
import java.util.List;


public class ProductDAO {
    private static final String TABLE_NAME = "Products";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_CATEGORY_ID = "categoryId";
    private static final String COLUMN_PRODUCT_NAME = "productName";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_STOCK = "stock";
    private static final String COLUMN_IMAGE = "image";
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private final Context context;


    public ProductDAO(Context context) {
        this.context = context;
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
    }


    // Insert a new product
    public long insertProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_ID, product.getCategoryId());
        values.put(COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_STOCK, product.getStock());
        values.put(COLUMN_IMAGE, product.getImage());
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("ProductDAO", "Inserted product with name: " + product.getProductName() + ", Result: " + result);
        return result;
    }
    public void insertProducts(List<Product> products) {
        db.beginTransaction();
        try {
            for (Product product : products) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_PRODUCT_NAME, product.getProductName());
                values.put(COLUMN_PRICE, product.getPrice());
                values.put(COLUMN_IMAGE, product.getImage());
                values.put(COLUMN_DESCRIPTION, product.getDescription());
                values.put(COLUMN_STOCK, product.getStock());
                values.put(COLUMN_CATEGORY_ID, product.getCategoryId());


                // Thực hiện chèn dữ liệu vào database
                long result = db.insert(TABLE_NAME, null, values);
                if (result == -1) {
                    Log.e("ProductDAO", "Failed to insert product: " + product.getProductName());
                } else {
                    Log.d("ProductDAO", "Inserted product with ID: " + result);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("ProductDAO", "Error inserting products", e);
        } finally {
            db.endTransaction();
        }
    }




    // Update an existing product
    public int updateProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_ID, product.getCategoryId());
        values.put(COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_STOCK, product.getStock());
        values.put(COLUMN_IMAGE, product.getImage());
        String whereClause = COLUMN_PRODUCT_ID + " = ?";
        String[] whereArgs = {String.valueOf(product.getProductId())};
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.d("ProductDAO", "Updated product with ID: " + product.getProductId() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }


    // Delete a product
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUMN_PRODUCT_ID + " = ?";
        String[] whereArgs = {String.valueOf(productId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("ProductDAO", "Deleted product with ID: " + productId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }


    // Delete all products in a category
    public void deleteProductsByCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUMN_CATEGORY_ID + " = ?";
        String[] whereArgs = {String.valueOf(categoryId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("ProductDAO", "Deleted products in category ID: " + categoryId + ", Rows affected: " + rowsDeleted);
    }


    // Get product by ID
    public Product getProductById(int productId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_PRODUCT_ID, COLUMN_CATEGORY_ID, COLUMN_PRODUCT_NAME, COLUMN_DESCRIPTION, COLUMN_PRICE, COLUMN_STOCK, COLUMN_IMAGE};
        String selection = COLUMN_PRODUCT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(productId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Product product = cursorToProduct(cursor);
            cursor.close();
            return product;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }


    // Get all products
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Product product = cursorToProduct(cursor);
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }


    // Get products by category ID
    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> productList = new ArrayList<>();
        String table = TABLE_NAME;
        String[] columns = {COLUMN_PRODUCT_ID, COLUMN_CATEGORY_ID, COLUMN_PRODUCT_NAME, COLUMN_DESCRIPTION, COLUMN_PRICE, COLUMN_STOCK, COLUMN_IMAGE};
        String selection = COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Product product = cursorToProduct(cursor);
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }


    // Get product count by category ID
    public int getProductCountByCategory(int categoryId) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }


    // Insert sample products for testing
    public void insertSampleProducts() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data


        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_CATEGORY_ID, 1); // Women's Clothing
        values1.put(COLUMN_PRODUCT_NAME, "Women's T-Shirt");
        values1.put(COLUMN_DESCRIPTION, "Comfortable cotton t-shirt for women");
        values1.put(COLUMN_PRICE, 29.99);
        values1.put(COLUMN_STOCK, 50);
        values1.put(COLUMN_IMAGE, "womens_tshirt.jpg");
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("ProductDAO", "Inserted product Women's T-Shirt, result: " + result1);


        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_CATEGORY_ID, 2); // Men's Clothing
        values2.put(COLUMN_PRODUCT_NAME, "Men's Jacket");
        values2.put(COLUMN_DESCRIPTION, "Warm jacket for men");
        values2.put(COLUMN_PRICE, 79.99);
        values2.put(COLUMN_STOCK, 30);
        values2.put(COLUMN_IMAGE, "mens_jacket.jpg");
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("ProductDAO", "Inserted product Men's Jacket, result: " + result2);


        ContentValues values3 = new ContentValues();
        values3.put(COLUMN_CATEGORY_ID, 3); // Kids' Clothing
        values3.put(COLUMN_PRODUCT_NAME, "Kids' Dress");
        values3.put(COLUMN_DESCRIPTION, "Cute dress for kids");
        values3.put(COLUMN_PRICE, 19.99);
        values3.put(COLUMN_STOCK, 40);
        values3.put(COLUMN_IMAGE, "kids_dress.jpg");
        long result3 = db.insert(TABLE_NAME, null, values3);
        Log.d("ProductDAO", "Inserted product Kids' Dress, result: " + result3);


    }


    public Product cursorToProduct(Cursor cursor) {
        int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
        int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK));
        String image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));


        Product product = new Product(categoryId, productName, description, price, stock, image);
        product.setProductId(productId);
        return product;
    }


    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}





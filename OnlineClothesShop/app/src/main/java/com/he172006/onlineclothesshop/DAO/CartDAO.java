package com.he172006.onlineclothesshop.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private static final String TABLE_NAME = "Cart";
    private static final String COLUMN_CART_ID = "cartId";
    private static final String COLUMN_ACCOUNT_ID = "accountId";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_DATE_ADDED = "dateAdded";
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public CartDAO(Context context) {
        this.context = context;
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert a new cart item
    public long insertCart(Cart cart) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_ID, cart.getAccountId());
        values.put(COLUMN_PRODUCT_ID, cart.getProductId());
        values.put(COLUMN_QUANTITY, cart.getQuantity());
        values.put(COLUMN_DATE_ADDED, cart.getDateAdded());
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("CartDAO", "Inserted cart item for account ID: " + cart.getAccountId() + ", Result: " + result);
        return result;
    }

    // Update an existing cart item
    public int updateCart(Cart cart) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_ID, cart.getAccountId());
        values.put(COLUMN_PRODUCT_ID, cart.getProductId());
        values.put(COLUMN_QUANTITY, cart.getQuantity());
        values.put(COLUMN_DATE_ADDED, cart.getDateAdded());
        String whereClause = COLUMN_CART_ID + " = ?";
        String[] whereArgs = {String.valueOf(cart.getCartId())};
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.d("CartDAO", "Updated cart item with ID: " + cart.getCartId() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }

    // Delete a cart item
    public boolean deleteCart(int cartId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUMN_CART_ID + " = ?";
        String[] whereArgs = {String.valueOf(cartId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("CartDAO", "Deleted cart item with ID: " + cartId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }

    // Get cart item by ID
    public Cart getCartById(int cartId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_CART_ID, COLUMN_ACCOUNT_ID, COLUMN_PRODUCT_ID, COLUMN_QUANTITY, COLUMN_DATE_ADDED};
        String selection = COLUMN_CART_ID + " = ?";
        String[] selectionArgs = {String.valueOf(cartId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Cart cart = cursorToCart(cursor);
            cursor.close();
            return cart;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get cart items by account ID
    public List<Cart> getCartItemsByAccountId(int accountId) {
        List<Cart> cartList = new ArrayList<>();
        String table = TABLE_NAME;
        String[] columns = {COLUMN_CART_ID, COLUMN_ACCOUNT_ID, COLUMN_PRODUCT_ID, COLUMN_QUANTITY, COLUMN_DATE_ADDED};
        String selection = COLUMN_ACCOUNT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(accountId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Cart cart = cursorToCart(cursor);
                cartList.add(cart);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cartList;
    }

    // Insert sample cart items for testing
    public void insertSampleCartItems() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data

        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_ACCOUNT_ID, 1);
        values1.put(COLUMN_PRODUCT_ID, 1);
        values1.put(COLUMN_QUANTITY, 2);
        values1.put(COLUMN_DATE_ADDED, "2025-03-21");
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("CartDAO", "Inserted cart item for account ID 1, result: " + result1);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_ACCOUNT_ID, 2);
        values2.put(COLUMN_PRODUCT_ID, 2);
        values2.put(COLUMN_QUANTITY, 3);
        values2.put(COLUMN_DATE_ADDED, "2025-03-21");
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("CartDAO", "Inserted cart item for account ID 2, result: " + result2);

        db.close();
    }

    // Convert cursor to Cart object
    private Cart cursorToCart(Cursor cursor) {
        int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_ID));
        int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID));
        int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
        String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_ADDED));
        return new Cart(cartId, accountId, productId, quantity, dateAdded);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}

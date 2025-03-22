package com.he172006.onlineclothesshop.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Shipping;

import java.util.ArrayList;
import java.util.List;

public class ShippingDAO {
    private static final String TABLE_NAME = "Shipping";
    private static final String COLUMN_SHIPPING_ID = "shippingId";
    private static final String COLUMN_ORDER_ID = "orderId";
    private static final String COLUMN_TRACKING_NUMBER = "trackingNumber";
    private static final String COLUMN_SHIPPING_STATUS = "shippingStatus";
    private static final String COLUMN_DELIVERY_DATE = "deliveryDate";
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public ShippingDAO(Context context) {
        this.context = context;
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert a new shipping record
    public long insertShipping(Shipping shipping) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, shipping.getOrderId());
        values.put(COLUMN_TRACKING_NUMBER, shipping.getTrackingNumber());
        values.put(COLUMN_SHIPPING_STATUS, shipping.getShippingStatus());
        values.put(COLUMN_DELIVERY_DATE, shipping.getDeliveryDate());
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("ShippingDAO", "Inserted shipping for order ID: " + shipping.getOrderId() + ", Result: " + result);
        return result;
    }

    // Update an existing shipping record
    public int updateShipping(Shipping shipping) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, shipping.getOrderId());
        values.put(COLUMN_TRACKING_NUMBER, shipping.getTrackingNumber());
        values.put(COLUMN_SHIPPING_STATUS, shipping.getShippingStatus());
        values.put(COLUMN_DELIVERY_DATE, shipping.getDeliveryDate());
        String whereClause = COLUMN_SHIPPING_ID + " = ?";
        String[] whereArgs = {String.valueOf(shipping.getShippingId())};
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.d("ShippingDAO", "Updated shipping with ID: " + shipping.getShippingId() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }

    // Delete a shipping record
    public boolean deleteShipping(int shippingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUMN_SHIPPING_ID + " = ?";
        String[] whereArgs = {String.valueOf(shippingId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("ShippingDAO", "Deleted shipping with ID: " + shippingId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }

    // Get shipping by ID
    public Shipping getShippingById(int shippingId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_SHIPPING_ID, COLUMN_ORDER_ID, COLUMN_TRACKING_NUMBER, COLUMN_SHIPPING_STATUS, COLUMN_DELIVERY_DATE};
        String selection = COLUMN_SHIPPING_ID + " = ?";
        String[] selectionArgs = {String.valueOf(shippingId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Shipping shipping = cursorToShipping(cursor);
            cursor.close();
            return shipping;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get shipping by order ID
    public Shipping getShippingByOrderId(int orderId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_SHIPPING_ID, COLUMN_ORDER_ID, COLUMN_TRACKING_NUMBER, COLUMN_SHIPPING_STATUS, COLUMN_DELIVERY_DATE};
        String selection = COLUMN_ORDER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Shipping shipping = cursorToShipping(cursor);
            cursor.close();
            return shipping;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Insert sample shipping records for testing
    public void insertSampleShipping() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data

        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_ORDER_ID, 1);
        values1.put(COLUMN_TRACKING_NUMBER, "TRACK123");
        values1.put(COLUMN_SHIPPING_STATUS, "Shipped");
        values1.put(COLUMN_DELIVERY_DATE, "2025-03-25");
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("ShippingDAO", "Inserted shipping for order ID 1, result: " + result1);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_ORDER_ID, 2);
        values2.put(COLUMN_TRACKING_NUMBER, "TRACK456");
        values2.put(COLUMN_SHIPPING_STATUS, "Delivered");
        values2.put(COLUMN_DELIVERY_DATE, "2025-03-23");
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("ShippingDAO", "Inserted shipping for order ID 2, result: " + result2);

        db.close();
    }

    // Convert cursor to Shipping object
    private Shipping cursorToShipping(Cursor cursor) {
        int shippingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SHIPPING_ID));
        int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
        String trackingNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRACKING_NUMBER));
        String shippingStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHIPPING_STATUS));
        String deliveryDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELIVERY_DATE));
        return new Shipping(shippingId, orderId, trackingNumber, shippingStatus, deliveryDate);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
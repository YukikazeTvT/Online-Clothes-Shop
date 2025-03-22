package com.he172006.onlineclothesshop.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderDAO {
    private static final String TABLE_NAME = "Orders";
    private static final String COLUMN_ORDER_ID = "orderId";
    private static final String COLUMN_ACCOUNT_ID = "accountId";
    private static final String COLUMN_ORDER_DATE = "orderDate";
    private static final String COLUMN_TOTAL_AMOUNT = "totalAmount";
    private static final String COLUMN_STATUS = "status";
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public OrderDAO(Context context) {
        this.context = context;
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert a new order
    public long insertOrder(Order order) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_ID, order.getAccountId());
        values.put(COLUMN_ORDER_DATE, order.getOrderDate());
        values.put(COLUMN_TOTAL_AMOUNT, order.getTotalAmount());
        values.put(COLUMN_STATUS, order.getStatus());
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("OrderDAO", "Inserted order for account ID: " + order.getAccountId() + ", Result: " + result);
        return result;
    }

    // Update an existing order
    public int updateOrder(Order order) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_ID, order.getAccountId());
        values.put(COLUMN_ORDER_DATE, order.getOrderDate());
        values.put(COLUMN_TOTAL_AMOUNT, order.getTotalAmount());
        values.put(COLUMN_STATUS, order.getStatus());
        String whereClause = COLUMN_ORDER_ID + " = ?";
        String[] whereArgs = {String.valueOf(order.getOrderId())};
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.d("OrderDAO", "Updated order with ID: " + order.getOrderId() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }

    // Delete an order
    public boolean deleteOrder(int orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUMN_ORDER_ID + " = ?";
        String[] whereArgs = {String.valueOf(orderId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("OrderDAO", "Deleted order with ID: " + orderId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }

    // Get order by ID
    public Order getOrderById(int orderId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ORDER_ID, COLUMN_ACCOUNT_ID, COLUMN_ORDER_DATE, COLUMN_TOTAL_AMOUNT, COLUMN_STATUS};
        String selection = COLUMN_ORDER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Order order = cursorToOrder(cursor);
            cursor.close();
            return order;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get orders by account ID
    public List<Order> getOrdersByAccountId(int accountId) {
        List<Order> orderList = new ArrayList<>();
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ORDER_ID, COLUMN_ACCOUNT_ID, COLUMN_ORDER_DATE, COLUMN_TOTAL_AMOUNT, COLUMN_STATUS};
        String selection = COLUMN_ACCOUNT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(accountId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Order order = cursorToOrder(cursor);
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }

    // Insert sample orders for testing
    public void insertSampleOrders() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data

        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_ACCOUNT_ID, 1);
        values1.put(COLUMN_ORDER_DATE, "2025-03-21");
        values1.put(COLUMN_TOTAL_AMOUNT, 1999.98);
        values1.put(COLUMN_STATUS, "Pending");
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("OrderDAO", "Inserted order for account ID 1, result: " + result1);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_ACCOUNT_ID, 2);
        values2.put(COLUMN_ORDER_DATE, "2025-03-21");
        values2.put(COLUMN_TOTAL_AMOUNT, 59.97);
        values2.put(COLUMN_STATUS, "Shipped");
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("OrderDAO", "Inserted order for account ID 2, result: " + result2);

        db.close();
    }

    // Get possible status values for Orders (and Products)
    public List<String> getStatusValues() {
        return Arrays.asList("Pending", "Accepted", "Declined");
    }

    // Convert cursor to Order object
    private Order cursorToOrder(Cursor cursor) {
        int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
        int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID));
        String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
        double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_AMOUNT));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
        return new Order(orderId, accountId, orderDate, totalAmount, status);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
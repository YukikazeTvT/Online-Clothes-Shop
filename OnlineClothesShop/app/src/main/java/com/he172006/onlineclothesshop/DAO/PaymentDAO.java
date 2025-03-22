package com.he172006.onlineclothesshop.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    private static final String TABLE_NAME = "Payment";
    private static final String COLUMN_PAYMENT_ID = "paymentId";
    private static final String COLUMN_ORDER_ID = "orderId";
    private static final String COLUMN_PAYMENT_METHOD = "paymentMethod";
    private static final String COLUMN_PAYMENT_STATUS = "paymentStatus";
    private static final String COLUMN_TRANSACTION_ID = "transactionId";
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public PaymentDAO(Context context) {
        this.context = context;
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert a new payment
    public long insertPayment(Payment payment) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, payment.getOrderId());
        values.put(COLUMN_PAYMENT_METHOD, payment.getPaymentMethod());
        values.put(COLUMN_PAYMENT_STATUS, payment.getPaymentStatus());
        values.put(COLUMN_TRANSACTION_ID, payment.getTransactionId());
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("PaymentDAO", "Inserted payment for order ID: " + payment.getOrderId() + ", Result: " + result);
        return result;
    }

    // Update an existing payment
    public int updatePayment(Payment payment) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, payment.getOrderId());
        values.put(COLUMN_PAYMENT_METHOD, payment.getPaymentMethod());
        values.put(COLUMN_PAYMENT_STATUS, payment.getPaymentStatus());
        values.put(COLUMN_TRANSACTION_ID, payment.getTransactionId());
        String whereClause = COLUMN_PAYMENT_ID + " = ?";
        String[] whereArgs = {String.valueOf(payment.getPaymentId())};
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.d("PaymentDAO", "Updated payment with ID: " + payment.getPaymentId() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }

    // Delete a payment
    public boolean deletePayment(int paymentId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUMN_PAYMENT_ID + " = ?";
        String[] whereArgs = {String.valueOf(paymentId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("PaymentDAO", "Deleted payment with ID: " + paymentId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }

    // Get payment by ID
    public Payment getPaymentById(int paymentId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_PAYMENT_ID, COLUMN_ORDER_ID, COLUMN_PAYMENT_METHOD, COLUMN_PAYMENT_STATUS, COLUMN_TRANSACTION_ID};
        String selection = COLUMN_PAYMENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(paymentId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Payment payment = cursorToPayment(cursor);
            cursor.close();
            return payment;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get payment by order ID
    public Payment getPaymentByOrderId(int orderId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_PAYMENT_ID, COLUMN_ORDER_ID, COLUMN_PAYMENT_METHOD, COLUMN_PAYMENT_STATUS, COLUMN_TRANSACTION_ID};
        String selection = COLUMN_ORDER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Payment payment = cursorToPayment(cursor);
            cursor.close();
            return payment;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Insert sample payments for testing
    public void insertSamplePayments() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data

        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_ORDER_ID, 1);
        values1.put(COLUMN_PAYMENT_METHOD, "Credit Card");
        values1.put(COLUMN_PAYMENT_STATUS, "Completed");
        values1.put(COLUMN_TRANSACTION_ID, "TXN12345");
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("PaymentDAO", "Inserted payment for order ID 1, result: " + result1);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_ORDER_ID, 2);
        values2.put(COLUMN_PAYMENT_METHOD, "PayPal");
        values2.put(COLUMN_PAYMENT_STATUS, "Pending");
        values2.put(COLUMN_TRANSACTION_ID, "TXN67890");
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("PaymentDAO", "Inserted payment for order ID 2, result: " + result2);

        db.close();
    }

    // Convert cursor to Payment object
    private Payment cursorToPayment(Cursor cursor) {
        int paymentId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_ID));
        int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
        String paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD));
        String paymentStatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_STATUS));
        String transactionId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID));
        return new Payment(paymentId, orderId, paymentMethod, paymentStatus, transactionId);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
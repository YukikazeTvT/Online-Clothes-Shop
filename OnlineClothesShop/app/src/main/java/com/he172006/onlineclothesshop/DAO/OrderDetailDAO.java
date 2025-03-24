package com.he172006.onlineclothesshop.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO {
    private static final String TABLE_NAME = "OrderDetails";
    private static final String COLUMN_ORDER_DETAIL_ID = "orderDetailId";
    private static final String COLUMN_ORDER_ID = "orderId";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_SUBTOTAL = "subtotal";
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public OrderDetailDAO(Context context) {
        this.context = context;
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert a new order detail
    public long insertOrderDetail(OrderDetail orderDetail) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderDetail.getOrderId());
        values.put(COLUMN_PRODUCT_ID, orderDetail.getProductId());
        values.put(COLUMN_QUANTITY, orderDetail.getQuantity());
        values.put(COLUMN_SUBTOTAL, orderDetail.getSubtotal());
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("OrderDetailDAO", "Inserted order detail for order ID: " + orderDetail.getOrderId() + ", Result: " + result);
        return result;
    }

    // Update an existing order detail
    public int updateOrderDetail(OrderDetail orderDetail) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderDetail.getOrderId());
        values.put(COLUMN_PRODUCT_ID, orderDetail.getProductId());
        values.put(COLUMN_QUANTITY, orderDetail.getQuantity());
        values.put(COLUMN_SUBTOTAL, orderDetail.getSubtotal());
        String whereClause = COLUMN_ORDER_DETAIL_ID + " = ?";
        String[] whereArgs = {String.valueOf(orderDetail.getOrderDetailId())};
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.d("OrderDetailDAO", "Updated order detail with ID: " + orderDetail.getOrderDetailId() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }

    // Delete an order detail
    public boolean deleteOrderDetail(int orderDetailId) {
        String whereClause = COLUMN_ORDER_DETAIL_ID + " = ?";
        String[] whereArgs = {String.valueOf(orderDetailId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("OrderDetailDAO", "Deleted order detail with ID: " + orderDetailId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }

    // Delete all order details by order ID
    public boolean deleteOrderDetailsByOrderId(int orderId) {
        String whereClause = COLUMN_ORDER_ID + " = ?";
        String[] whereArgs = {String.valueOf(orderId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("OrderDetailDAO", "Deleted order details for order ID: " + orderId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }

    // Get order detail by ID
    public OrderDetail getOrderDetailById(int orderDetailId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ORDER_DETAIL_ID, COLUMN_ORDER_ID, COLUMN_PRODUCT_ID, COLUMN_QUANTITY, COLUMN_SUBTOTAL};
        String selection = COLUMN_ORDER_DETAIL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderDetailId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            OrderDetail orderDetail = cursorToOrderDetail(cursor);
            cursor.close();
            return orderDetail;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get order details by order ID
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> orderDetailList = new ArrayList<>();
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ORDER_DETAIL_ID, COLUMN_ORDER_ID, COLUMN_PRODUCT_ID, COLUMN_QUANTITY, COLUMN_SUBTOTAL};
        String selection = COLUMN_ORDER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                OrderDetail orderDetail = cursorToOrderDetail(cursor);
                orderDetailList.add(orderDetail);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderDetailList;
    }

    // Insert sample order details for testing
    public void insertSampleOrderDetails() {
        db.delete(TABLE_NAME, null, null); // Clear existing data

        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_ORDER_ID, 1);
        values1.put(COLUMN_PRODUCT_ID, 1);
        values1.put(COLUMN_QUANTITY, 2);
        values1.put(COLUMN_SUBTOTAL, 59.98); // 2 * 29.99
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("OrderDetailDAO", "Inserted order detail for order ID 1, result: " + result1);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_ORDER_ID, 2);
        values2.put(COLUMN_PRODUCT_ID, 2);
        values2.put(COLUMN_QUANTITY, 1);
        values2.put(COLUMN_SUBTOTAL, 79.99); // 1 * 79.99
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("OrderDetailDAO", "Inserted order detail for order ID 2, result: " + result2);
    }

    // Convert cursor to OrderDetail object
    private OrderDetail cursorToOrderDetail(Cursor cursor) {
        int orderDetailId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DETAIL_ID));
        int orderId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID));
        int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
        double subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SUBTOTAL));
        return new OrderDetail(orderDetailId, orderId, productId, quantity, subtotal);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
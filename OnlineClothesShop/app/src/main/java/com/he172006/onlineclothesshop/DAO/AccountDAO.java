package com.he172006.onlineclothesshop.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private static final String TABLE_NAME = "Accounts";
    private static final String COLUMN_ACCOUNT_ID = "accountId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ROLE = "role";
    private DataBase dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public AccountDAO(Context context) {
        this.context = context;
        dbHelper = new DataBase(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert a new account
    public long insertAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, account.getName());
        values.put(COLUMN_EMAIL, account.getEmail());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_ADDRESS, account.getAddress());
        values.put(COLUMN_PHONE, account.getPhone());
        values.put(COLUMN_ROLE, account.getRole());
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("AccountDAO", "Inserted account with name: " + account.getName() + ", Result: " + result);
        return result;
    }

    // Update an existing account
    public int updateAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, account.getName());
        values.put(COLUMN_EMAIL, account.getEmail());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_ADDRESS, account.getAddress());
        values.put(COLUMN_PHONE, account.getPhone());
        values.put(COLUMN_ROLE, account.getRole());
        String whereClause = COLUMN_ACCOUNT_ID + " = ?";
        String[] whereArgs = {String.valueOf(account.getAccountId())};
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.d("AccountDAO", "Updated account with ID: " + account.getAccountId() + ", Rows affected: " + rowsUpdated);
        return rowsUpdated;
    }

    // Delete an account
    public boolean deleteAccount(int accountId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUMN_ACCOUNT_ID + " = ?";
        String[] whereArgs = {String.valueOf(accountId)};
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        Log.d("AccountDAO", "Deleted account with ID: " + accountId + ", Rows affected: " + rowsDeleted);
        return rowsDeleted > 0;
    }

    // Get account by ID
    public Account getAccountById(int accountId) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ACCOUNT_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_ADDRESS, COLUMN_PHONE, COLUMN_ROLE};
        String selection = COLUMN_ACCOUNT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(accountId)};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Account account = cursorToAccount(cursor);
            cursor.close();
            return account;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get account by email (replacing getAccountByUsername)
    public Account getAccountByEmail(String email) {
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ACCOUNT_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_ADDRESS, COLUMN_PHONE, COLUMN_ROLE};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Account account = cursorToAccount(cursor);
            cursor.close();
            return account;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Get all accounts
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Account account = cursorToAccount(cursor);
                accountList.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accountList;
    }

    // Login method
    public Account login(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String table = TABLE_NAME;
        String[] columns = {COLUMN_ACCOUNT_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_ADDRESS, COLUMN_PHONE, COLUMN_ROLE};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Log.d("AccountDAO", "Login query - Email: " + email + ", Password: " + password);
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        Log.d("AccountDAO", "Login query rows found: " + cursor.getCount());
        if (cursor != null && cursor.moveToFirst()) {
            Account account = cursorToAccount(cursor);
            Log.d("AccountDAO", "Login successful for email: " + email + ", Role: " + account.getRole());
            cursor.close();
            return account;
        }
        if (cursor != null) {
            Log.d("AccountDAO", "No matching account found for email: " + email);
            cursor.close();
        } else {
            Log.e("AccountDAO", "Cursor is null, query failed");
        }
        return null;
    }

    // Register method
    public long register(String name, String email, String password, String address, String phone, String role) {
        if (checkEmail(email)) {
            return -1; // Email already exists
        }
        Account account = new Account(0, name, email, password, address, phone, role);
        return insertAccount(account);
    }

    // Check if email exists
    private boolean checkEmail(String email) {
        String column = COLUMN_ACCOUNT_ID;
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_NAME, new String[]{column}, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Insert sample accounts for testing
    public void insertSampleAccounts() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Clear existing data

        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_NAME, "Admin User");
        values1.put(COLUMN_EMAIL, "admin@example.com");
        values1.put(COLUMN_PASSWORD, "admin123");
        values1.put(COLUMN_ADDRESS, "123 Admin St");
        values1.put(COLUMN_PHONE, "1234567890");
        values1.put(COLUMN_ROLE, "admin");
        long result1 = db.insert(TABLE_NAME, null, values1);
        Log.d("AccountDAO", "Inserted admin account, result: " + result1);

        ContentValues values2 = new ContentValues();
        values2.put(COLUMN_NAME, "Regular User");
        values2.put(COLUMN_EMAIL, "user@example.com");
        values2.put(COLUMN_PASSWORD, "user123");
        values2.put(COLUMN_ADDRESS, "456 User Rd");
        values2.put(COLUMN_PHONE, "0987654321");
        values2.put(COLUMN_ROLE, "user");
        long result2 = db.insert(TABLE_NAME, null, values2);
        Log.d("AccountDAO", "Inserted user account, result: " + result2);

        db.close();
    }

    // Convert cursor to Account object
    private Account cursorToAccount(Cursor cursor) {
        int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
        String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
        String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
        return new Account(accountId, name, email, password, address, phone, role);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
    public boolean updateAccounts(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, account.getName());
        values.put(COLUMN_EMAIL, account.getEmail());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_ADDRESS, account.getAddress());
        values.put(COLUMN_PHONE, account.getPhone());
        values.put(COLUMN_ROLE, account.getRole());

        String whereClause = COLUMN_ACCOUNT_ID + " = ?";
        String[] whereArgs = {String.valueOf(account.getAccountId())};

        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close(); // Đóng database sau khi cập nhật

        if (rowsUpdated > 0) {
            Log.d("AccountDAO", "Cập nhật thành công tài khoản ID: " + account.getAccountId());
            return true;
        } else {
            Log.e("AccountDAO", "Cập nhật thất bại tài khoản ID: " + account.getAccountId());
            return false;
        }
    }
}
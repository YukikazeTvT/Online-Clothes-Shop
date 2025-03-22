package com.he172006.onlineclothesshop.dtb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "OnlineClothesShop";
    private static final int DATABASE_VERSION = 2; // Tăng version để thêm cột image

    // Bảng Accounts (merged ADMIN and USER)
    private static final String TABLE_ACCOUNTS = "Accounts";
    private static final String COLUMN_ACCOUNT_ID = "accountId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ROLE = "role";

    // Bảng Categories
    private static final String TABLE_CATEGORIES = "Categories";
    private static final String COLUMN_CATEGORY_ID = "categoryId";
    private static final String COLUMN_CATEGORY_NAME = "categoryName";
    private static final String COLUMN_CATEGORY_IMAGE = "image"; // Thêm cột image

    // Bảng Products
    private static final String TABLE_PRODUCTS = "Products";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_PRODUCT_NAME = "productName";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_STOCK = "stock";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_PRODUCT_CATEGORY_ID = "categoryId";

    // Bảng Cart
    private static final String TABLE_CART = "Cart";
    private static final String COLUMN_CART_ID = "cartId";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_DATE_ADDED = "dateAdded";

    // Bảng Orders
    private static final String TABLE_ORDERS = "Orders";
    private static final String COLUMN_ORDER_ID = "orderId";
    private static final String COLUMN_ORDER_DATE = "orderDate";
    private static final String COLUMN_TOTAL_AMOUNT = "totalAmount";
    private static final String COLUMN_STATUS = "status";

    // Bảng OrderDetails
    private static final String TABLE_ORDER_DETAILS = "OrderDetails";
    private static final String COLUMN_ORDER_DETAIL_ID = "orderDetailId";
    private static final String COLUMN_SUBTOTAL = "subtotal";

    // Bảng Payment
    private static final String TABLE_PAYMENT = "Payment";
    private static final String COLUMN_PAYMENT_ID = "paymentId";
    private static final String COLUMN_PAYMENT_METHOD = "paymentMethod";
    private static final String COLUMN_PAYMENT_STATUS = "paymentStatus";
    private static final String COLUMN_TRANSACTION_ID = "transactionId";

    // Bảng Shipping
    private static final String TABLE_SHIPPING = "Shipping";
    private static final String COLUMN_SHIPPING_ID = "shippingId";
    private static final String COLUMN_TRACKING_NUMBER = "trackingNumber";
    private static final String COLUMN_SHIPPING_STATUS = "shippingStatus";
    private static final String COLUMN_DELIVERY_DATE = "deliveryDate";

    // SQL để tạo bảng Accounts (merged ADMIN and USER)
    private static final String SQL_CREATE_ACCOUNTS =
            "CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                    COLUMN_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_ADDRESS + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_ROLE + " TEXT NOT NULL)";

    // SQL để tạo bảng Categories
    private static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY_IMAGE + " TEXT)"; // Thêm cột image

    // SQL để tạo bảng Products
    private static final String SQL_CREATE_PRODUCTS =
            "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PRODUCT_CATEGORY_ID + " INTEGER NOT NULL, " +
                    COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_PRICE + " REAL NOT NULL, " +
                    COLUMN_STOCK + " INTEGER NOT NULL, " +
                    COLUMN_IMAGE + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_PRODUCT_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ") ON DELETE CASCADE)";

    // SQL để tạo bảng Cart
    private static final String SQL_CREATE_CART =
            "CREATE TABLE " + TABLE_CART + " (" +
                    COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ACCOUNT_ID + " INTEGER NOT NULL, " +
                    COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                    COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                    COLUMN_DATE_ADDED + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + COLUMN_ACCOUNT_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ") ON DELETE CASCADE)";

    // SQL để tạo bảng Orders
    private static final String SQL_CREATE_ORDERS =
            "CREATE TABLE " + TABLE_ORDERS + " (" +
                    COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ACCOUNT_ID + " INTEGER NOT NULL, " +
                    COLUMN_ORDER_DATE + " TEXT NOT NULL, " +
                    COLUMN_TOTAL_AMOUNT + " REAL NOT NULL, " +
                    COLUMN_STATUS + " TEXT NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + COLUMN_ACCOUNT_ID + ") ON DELETE CASCADE)";

    // SQL để tạo bảng OrderDetails
    private static final String SQL_CREATE_ORDER_DETAILS =
            "CREATE TABLE " + TABLE_ORDER_DETAILS + " (" +
                    COLUMN_ORDER_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER_ID + " INTEGER NOT NULL, " +
                    COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                    COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                    COLUMN_SUBTOTAL + " REAL NOT NULL, " +
                    "FOREIGN KEY (" + COLUMN_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + ") ON DELETE CASCADE)";

    // SQL để tạo bảng Payment
    private static final String SQL_CREATE_PAYMENT =
            "CREATE TABLE " + TABLE_PAYMENT + " (" +
                    COLUMN_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER_ID + " INTEGER NOT NULL, " +
                    COLUMN_PAYMENT_METHOD + " TEXT NOT NULL, " +
                    COLUMN_PAYMENT_STATUS + " TEXT NOT NULL, " +
                    COLUMN_TRANSACTION_ID + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + ") ON DELETE CASCADE)";

    // SQL để tạo bảng Shipping
    private static final String SQL_CREATE_SHIPPING =
            "CREATE TABLE " + TABLE_SHIPPING + " (" +
                    COLUMN_SHIPPING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER_ID + " INTEGER NOT NULL, " +
                    COLUMN_TRACKING_NUMBER + " TEXT, " +
                    COLUMN_SHIPPING_STATUS + " TEXT NOT NULL, " +
                    COLUMN_DELIVERY_DATE + " TEXT, " +
                    "FOREIGN KEY (" + COLUMN_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + ") ON DELETE CASCADE)";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACCOUNTS);
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_PRODUCTS);
        db.execSQL(SQL_CREATE_CART);
        db.execSQL(SQL_CREATE_ORDERS);
        db.execSQL(SQL_CREATE_ORDER_DETAILS);
        db.execSQL(SQL_CREATE_PAYMENT);
        db.execSQL(SQL_CREATE_SHIPPING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Thêm cột image vào bảng Categories nếu chưa có
            db.execSQL("ALTER TABLE " + TABLE_CATEGORIES + " ADD COLUMN " + COLUMN_CATEGORY_IMAGE + " TEXT");
        }
        // Thêm các nâng cấp khác trong tương lai nếu cần
    }

    // Get all accounts
    public Cursor getAllAccounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS, null);
    }

    // Get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
    }

    // Delete account
    public boolean deleteAccount(int accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ACCOUNTS, COLUMN_ACCOUNT_ID + "=?", new String[]{String.valueOf(accountId)}) > 0;
    }

    // Delete product
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + "=?", new String[]{String.valueOf(productId)}) > 0;
    }
}
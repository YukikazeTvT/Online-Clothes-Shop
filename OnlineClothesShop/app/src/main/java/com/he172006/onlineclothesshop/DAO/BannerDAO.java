package com.he172006.onlineclothesshop.DAO;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.he172006.onlineclothesshop.dtb.DataBase;
import com.he172006.onlineclothesshop.entity.Banner;


import java.util.ArrayList;
import java.util.List;


public class BannerDAO {
    private static final String TABLE_NAME = "Banners";
    private static final String COLUMN_BANNER_ID = "bannerId";
    private static final String COLUMN_BANNER_IMAGE = "image";


    private final DataBase dbHelper;
    private final SQLiteDatabase db;


    public BannerDAO(Context context) {
        this.dbHelper = new DataBase(context);
        this.db = dbHelper.getWritableDatabase();
    }


    public long insertBanner(String image) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BANNER_IMAGE, image);
        return db.insert(TABLE_NAME, null, values);
    }
    public void insertBanners(List<Banner> banners) {
        db.beginTransaction();
        try {
            for (Banner banner : banners) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_BANNER_IMAGE, banner.getImage());
                db.insert(TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }




    public List<Banner> getAllBanners() {
        List<Banner> bannerList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);


        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BANNER_ID));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BANNER_IMAGE));
                bannerList.add(new Banner(image));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bannerList;
    }
}





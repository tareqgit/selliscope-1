package com.humaclab.selliscope.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.humaclab.selliscope.dbmodel.Target;
import com.humaclab.selliscope.dbmodel.UserVisit;
import com.humaclab.selliscope.model.ProductResponse;
import com.humaclab.selliscope.model.Targets;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.PUT;

/**
 * Created by dipu_ on 4/21/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "selliscopedb";

    // Database Tables
    private static final String TABLE_TARGET = "targets";
    private static final String TABLE_USER_VISITS = "userVisits";
    private static final String TABLE_PRODUCT = "product";
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_BRAND = "brand";

    // Target Table Columns names
    private static final String KEY_TARGET_ID = "targetId";
    private static final String KEY_TARGET_TYPE = "type";
    private static final String KEY_TARGET_TIME_PERIOD = "targetTimePeriod";
    private static final String KEY_TARGET_COUNT = "targetCount";
    private static final String KEY_TARGET_ACHIEVED = "targetAchieved";

    //userVisits table columns names
    private static final String KEY_USER_VISIT_ID = "userVisitId";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TIMESTAMP = "timeStamp";
    private static final String KEY_IS_UPDATED = "isUpdated";

    //product table column names
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_PRODUCT_PRICE = "product_price";
    private static final String KEY_PRODUCT_IMAGE = "product_image";
    private static final String KEY_CATEGORY_ID = "cat_id";
    private static final String KEY_CATEGORY_NAME = "cat_name";
    private static final String KEY_BRAND_ID = "brand_id";
    private static final String KEY_BRAND_NAME = "brand_name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TARGET_TABLE = "CREATE TABLE " + TABLE_TARGET + "("
                + KEY_TARGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TARGET_TIME_PERIOD + " TEXT,"
                + KEY_TARGET_TYPE + " TEXT,"
                + KEY_TARGET_COUNT + " INTEGER,"
                + KEY_TARGET_ACHIEVED + " INTEGER" + ")";
        String CREATE_TARGET_USER_VISITS = "CREATE TABLE " + TABLE_USER_VISITS + "("
                + KEY_USER_VISIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL,"
                + KEY_TIMESTAMP + " TEXT,"
                + KEY_IS_UPDATED + " INTEGER" + ")";
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + KEY_PRODUCT_ID + " INTEGER PRIMARY KEY,"
                + KEY_PRODUCT_NAME + " TEXT,"
                + KEY_PRODUCT_PRICE + " TEXT,"
                + KEY_PRODUCT_IMAGE + " TEXT,"
                + KEY_CATEGORY_ID + " INTEGER,"
                + KEY_CATEGORY_NAME + " TEXT,"
                + KEY_BRAND_ID + " INTEGER,"
                + KEY_BRAND_NAME + " TEXT"
                + ")";
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CATEGORY_NAME + " TEXT"
                + ")";
        String CREATE_BRAND_TABLE = "CREATE TABLE " + TABLE_BRAND + "("
                + KEY_BRAND_ID + " INTEGER PRIMARY KEY,"
                + KEY_BRAND_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_TARGET_TABLE);
        db.execSQL(CREATE_TARGET_USER_VISITS);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_BRAND_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_VISITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRAND);
        // Create tables again
        onCreate(db);
    }

    //adding target
    public void addTargets(List<Target> targets) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Target target : targets) {
            values.put(KEY_TARGET_TIME_PERIOD, target.getTargetTimePeriod());
            values.put(KEY_TARGET_TYPE, target.getTargetType());
            values.put(KEY_TARGET_COUNT, target.getTargetCount());
            values.put(KEY_TARGET_ACHIEVED, target.getTargetAchieved());
            db.insert(TABLE_TARGET, null, values);
            values.clear();
        }
        db.close(); // Closing database connection
    }

    public void addUserVisits(UserVisit userVisit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, userVisit.getLatitude());
        values.put(KEY_LONGITUDE, userVisit.getLongitude());
        values.put(KEY_TIMESTAMP, userVisit.getTimeStamp());
        values.put(KEY_IS_UPDATED, 0);
        db.insert(TABLE_USER_VISITS, null, values);
        values.clear();
        db.close(); // Closing database connection
    }

    public List<UserVisit> getUSerVisits() {
        List<UserVisit> visitList = new ArrayList<UserVisit>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_VISITS
                + " WHERE " + KEY_IS_UPDATED + "= 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserVisit visit = new UserVisit(
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndex(KEY_USER_VISIT_ID))
                );
                visitList.add(visit);
            } while (cursor.moveToNext());
        }
        // return visit list
        return visitList;
    }

    public int updateUserVisit(int visitId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IS_UPDATED, 1);
        // updating row
        return db.update(TABLE_USER_VISITS, values, KEY_USER_VISIT_ID + " = ?",
                new String[]{String.valueOf(visitId)});
    }

    public void deleteUserVisit(int visitId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_VISITS, KEY_USER_VISIT_ID + " = ?",
                new String[]{String.valueOf(visitId)});
        db.close();
    }

    public void addProduct(int productID, String productName, String price, String image, int brandID, String brandName, int categoryID, String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_ID, productID);
        values.put(KEY_PRODUCT_NAME, productName);
        values.put(KEY_PRODUCT_PRICE, price);
        values.put(KEY_PRODUCT_IMAGE, image);
        values.put(KEY_CATEGORY_ID, categoryID);
        values.put(KEY_CATEGORY_NAME, categoryName);
        values.put(KEY_BRAND_ID, brandID);
        values.put(KEY_BRAND_NAME, brandName);
        try {
            db.insert(TABLE_PRODUCT, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        values.clear();
        db.close(); // Closing database connection
    }

    public void addCategory(int categoryID, String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_ID, categoryID);
        values.put(KEY_CATEGORY_NAME, categoryName);
        try {
            db.insert(TABLE_CATEGORY, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        values.clear();
        db.close(); // Closing database connection
    }

    public void addBrand(int brandID, String brandName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BRAND_ID, brandID);
        values.put(KEY_BRAND_NAME, brandName);
        try {
            db.insert(TABLE_BRAND, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        values.clear();
        db.close(); // Closing database connection
    }

    public void removeProductCategoryBrand() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, KEY_PRODUCT_ID + " > ?",
                new String[]{String.valueOf(-1)});
        db.delete(TABLE_CATEGORY, KEY_CATEGORY_ID + " > ?",
                new String[]{String.valueOf(-1)});
        db.delete(TABLE_BRAND, KEY_BRAND_ID + " > ?",
                new String[]{String.valueOf(-1)});
        db.close();
    }

    public List<ProductResponse.ProductResult> getProduct(int categoryID, int brandID) {
        List<ProductResponse.ProductResult> productList = new ArrayList<>();
        // Select All Query
        String selectQuery;
        if (categoryID != 0 && brandID != 0) {
            selectQuery = "SELECT  * FROM " + TABLE_PRODUCT
                    + " WHERE " + KEY_CATEGORY_ID + "=" + categoryID
                    + " AND " + KEY_BRAND_ID + "=" + brandID + " ORDER BY " + KEY_PRODUCT_NAME + " ASC";
        } else if (categoryID != 0 && brandID == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " WHERE "
                    + KEY_CATEGORY_ID + "=" + categoryID + " ORDER BY " + KEY_PRODUCT_NAME + " ASC";
        } else if (brandID != 0 && categoryID == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " WHERE "
                    + KEY_BRAND_ID + "=" + brandID + " ORDER BY " + KEY_PRODUCT_NAME + " ASC";
        } else {
            selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " ORDER BY " + KEY_PRODUCT_NAME + " ASC";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                ProductResponse.ProductResult products = new ProductResponse.ProductResult();
                products.category = new ProductResponse.Category();
                products.brand = new ProductResponse.Brand();

                products.id = cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_ID));
                products.name = cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_NAME));
                products.price = cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_PRICE));
                products.img = cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_IMAGE));
                products.category.id = cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID));
                products.category.name = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME));
                products.brand.id = cursor.getInt(cursor.getColumnIndex(KEY_BRAND_ID));
                products.brand.name = cursor.getString(cursor.getColumnIndex(KEY_BRAND_NAME));
                productList.add(products);
            } while (cursor.moveToNext());
        }
        return productList;
    }

    public List<ProductResponse.Category> getCategory() {
        List<ProductResponse.Category> categoryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        System.out.println("TEST = " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                ProductResponse.Category category = new ProductResponse.Category();
                category.id = cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID));
                category.name = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME));

                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        return categoryList;
    }

    public List<ProductResponse.Brand> getBrand() {
        List<ProductResponse.Brand> brandList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BRAND;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                ProductResponse.Brand brand = new ProductResponse.Brand();
                brand.id = cursor.getInt(cursor.getColumnIndex(KEY_BRAND_ID));
                brand.name = cursor.getString(cursor.getColumnIndex(KEY_BRAND_NAME));

                brandList.add(brand);
            } while (cursor.moveToNext());
        }
        return brandList;
    }
}

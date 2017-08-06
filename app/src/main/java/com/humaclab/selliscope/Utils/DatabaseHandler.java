package com.humaclab.selliscope.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.humaclab.selliscope.dbmodel.Target;
import com.humaclab.selliscope.dbmodel.UserVisit;
import com.humaclab.selliscope.model.DeliveryResponse;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.ProductResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String TABLE_DELIVERY = "delivery";
    private static final String TABLE_DELIVERY_PRODUCT = "delivery_product";
    private static final String TABLE_OUTLET = "outlet";

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

    //delivery table column names
    private static final String KEY_ORDER_ID = "order_id";
    private static final String KEY_OUTLET_ID = "outlet_id";
    private static final String KEY_OUTLET_NAME = "outlet_name";
    private static final String KEY_DISCOUNT = "discount";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_ORDER_DATE = "order_date";

    //delivery_product table column names
    private static final String KEY_DELIVERY_PRODUCT_ID = "delivery_product_id";
    private static final String KEY_DELIVERY_PRODUCT_NAME = "delivery_product_name";
    private static final String KEY_DELIVERY_PRODUCT_PRICE = "delivery_product_price";
    private static final String KEY_DELIVERY_PRODUCT_DISCOUNT = "delivery_product_discount";
    private static final String KEY_DELIVERY_PRODUCT_AMOUNT = "delivery_product_amount";
    private static final String KEY_DELIVERY_PRODUCT_QTY = "delivery_product_qty";

    //Outlet table
    //Outlet name and id are declared before
    private static final String KEY_OUTLET_TYPE = "outlet_type";
    private static final String KEY_OUTLET_OWNER_NAME = "outlet_owner_name";
    private static final String KEY_OUTLET_ADDRESS = "outlet_address";
    private static final String KEY_OUTLET_DISTRICT = "outlet_district";
    private static final String KEY_OUTLET_THANA = "outlet_thana";
    private static final String KEY_OUTLET_PHONE = "outlet_phone";
    private static final String KEY_OUTLET_IMAGE = "outlet_image";
    private static final String KEY_OUTLET_LONGITUDE = "outlet_longitude";
    private static final String KEY_OUTLET_LATITUDE = "outlet_latitude";
    private static final String KEY_OUTLET_DUE = "outlet_due";


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
        String CREATE_DELIVERY_TABLE = "CREATE TABLE " + TABLE_DELIVERY + "("
                + KEY_OUTLET_ID + " INTEGER,"
                + KEY_ORDER_ID + " INTEGER,"
                + KEY_OUTLET_NAME + " TEXT,"
                + KEY_DISCOUNT + " TEXT,"
                + KEY_AMOUNT + " TEXT,"
                + KEY_ORDER_DATE + " TEXT"
                + ")";
        String CREATE_DELIVERY_PRODUCT_TABLE = "CREATE TABLE " + TABLE_DELIVERY_PRODUCT + "("
                + KEY_DELIVERY_PRODUCT_ID + " INTEGER,"
                + KEY_ORDER_ID + " INTEGER,"
                + KEY_OUTLET_ID + " INTEGER,"
                + KEY_DELIVERY_PRODUCT_NAME + " TEXT,"
                + KEY_DELIVERY_PRODUCT_PRICE + " TEXT,"
                + KEY_DELIVERY_PRODUCT_DISCOUNT + " TEXT,"
                + KEY_DELIVERY_PRODUCT_AMOUNT + " TEXT,"
                + KEY_DELIVERY_PRODUCT_QTY + " INTEGER"
                + ")";

        String CREATE_OUTLET_TABLE = "CREATE TABLE " + TABLE_OUTLET + "("
                + KEY_OUTLET_ID + " INTEGER  PRIMARY KEY,"
                + KEY_OUTLET_NAME + " TEXT,"
                + KEY_OUTLET_TYPE + " TEXT,"
                + KEY_OUTLET_OWNER_NAME + " TEXT,"
                + KEY_OUTLET_ADDRESS + " TEXT,"
                + KEY_OUTLET_DISTRICT + " TEXT,"
                + KEY_OUTLET_THANA + " TEXT,"
                + KEY_OUTLET_PHONE + " TEXT,"
                + KEY_OUTLET_IMAGE + " TEXT,"
                + KEY_OUTLET_LATITUDE + " TEXT,"
                + KEY_OUTLET_LONGITUDE + " TEXT,"
                + KEY_OUTLET_DUE + " TEXT"
                + ")";
        db.execSQL(CREATE_TARGET_TABLE);
        db.execSQL(CREATE_TARGET_USER_VISITS);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_BRAND_TABLE);
        db.execSQL(CREATE_DELIVERY_TABLE);
        db.execSQL(CREATE_DELIVERY_PRODUCT_TABLE);
        db.execSQL(CREATE_OUTLET_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELIVERY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELIVERY_PRODUCT);
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

    public int getSizeOfProduct() {
        List<ProductResponse.ProductResult> productList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " ORDER BY " + KEY_PRODUCT_NAME + " ASC";

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
        return productList.size();
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

    public void removeDeliveryAndDeliveryProduct() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DELIVERY, KEY_ORDER_ID + " > ?",
                new String[]{String.valueOf(-1)});
        db.delete(TABLE_DELIVERY_PRODUCT, KEY_DELIVERY_PRODUCT_ID + " > ?",
                new String[]{String.valueOf(-1)});
        db.close();
    }

    public void addDeliveryList(List<DeliveryResponse.DeliveryList> delivers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues product_values = new ContentValues();

        try {
            for (DeliveryResponse.DeliveryList deliveryList : delivers) {
                values.clear();
                values.put(KEY_ORDER_ID, deliveryList.deliveryId);
                values.put(KEY_OUTLET_ID, deliveryList.outletId);
                values.put(KEY_OUTLET_NAME, deliveryList.outletName);
                values.put(KEY_DISCOUNT, deliveryList.discount);
                values.put(KEY_AMOUNT, deliveryList.amount);
                values.put(KEY_ORDER_DATE, deliveryList.deliveryDate);

                try {
                    db.insert(TABLE_DELIVERY, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (DeliveryResponse.Product product : deliveryList.productList) {
                    product_values.clear();
                    product_values.put(KEY_ORDER_ID, deliveryList.deliveryId);
                    product_values.put(KEY_OUTLET_ID, deliveryList.outletId);
                    product_values.put(KEY_DELIVERY_PRODUCT_ID, product.productId);
                    product_values.put(KEY_DELIVERY_PRODUCT_NAME, product.name);
                    product_values.put(KEY_DELIVERY_PRODUCT_PRICE, product.price);
                    product_values.put(KEY_DELIVERY_PRODUCT_QTY, product.qty - Integer.parseInt(product.dQty));
                    product_values.put(KEY_DELIVERY_PRODUCT_AMOUNT, product.amount);
                    product_values.put(KEY_DELIVERY_PRODUCT_DISCOUNT, product.discount);
                    try {
                        db.insert(TABLE_DELIVERY_PRODUCT, null, product_values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close(); // Closing database connection
    }

    public Map<String, List<String>> getOutlets() {
        Map<String, List<String>> outlets = new HashMap<>();
        List<String> outletID = new ArrayList<>();
        List<String> outletName = new ArrayList<>();
        outletID.add("0");
        outletName.add("Select outlet");

        // Select All Query
        String selectQuery = "SELECT distinct " + KEY_OUTLET_NAME + "," + KEY_OUTLET_ID + " FROM " + TABLE_DELIVERY + " ORDER BY " + KEY_OUTLET_NAME + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                outletID.add(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_OUTLET_ID))));
                outletName.add(cursor.getString(cursor.getColumnIndex(KEY_OUTLET_NAME)));
            } while (cursor.moveToNext());
        }
        outlets.put("outletID", outletID);
        outlets.put("outletName", outletName);
        return outlets;
    }

    public List<DeliveryResponse.DeliveryList> getDeliveries(int outletID) {
        // Select All Query
        List<DeliveryResponse.DeliveryList> deliveryList = new ArrayList<>();
        List<DeliveryResponse.Product> productList = new ArrayList<>();

        String deliveryQuery;
        String productQuery;
        if (outletID != 0) {
            deliveryQuery = "SELECT * FROM " + TABLE_DELIVERY + " WHERE " + KEY_OUTLET_ID + "=" + outletID;
            productQuery = "SELECT * FROM " + TABLE_DELIVERY_PRODUCT + " WHERE " + KEY_OUTLET_ID + "=" + outletID;
        } else {
            deliveryQuery = "SELECT * FROM " + TABLE_DELIVERY;
            productQuery = "SELECT * FROM " + TABLE_DELIVERY_PRODUCT;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor deliveryCursor = db.rawQuery(deliveryQuery, null);
        Cursor productCursor = db.rawQuery(productQuery, null);

        // looping through all rows and adding to list
        if (deliveryCursor.moveToFirst()) {
            do {
                DeliveryResponse.DeliveryList deliveryList1 = new DeliveryResponse.DeliveryList();
                deliveryList1.deliveryId = deliveryCursor.getInt(deliveryCursor.getColumnIndex(KEY_ORDER_ID));
                deliveryList1.outletId = deliveryCursor.getInt(deliveryCursor.getColumnIndex(KEY_OUTLET_ID));
                deliveryList1.outletName = deliveryCursor.getString(deliveryCursor.getColumnIndex(KEY_OUTLET_NAME));
                deliveryList1.amount = deliveryCursor.getString(deliveryCursor.getColumnIndex(KEY_AMOUNT));
                deliveryList1.deliveryDate = deliveryCursor.getString(deliveryCursor.getColumnIndex(KEY_ORDER_DATE));
                deliveryList1.discount = deliveryCursor.getString(deliveryCursor.getColumnIndex(KEY_DISCOUNT));

                if (productCursor.moveToFirst()) {
                    do {
                        DeliveryResponse.Product product = new DeliveryResponse.Product();
                        product.productId = productCursor.getInt(productCursor.getColumnIndex(KEY_DELIVERY_PRODUCT_ID));
                        product.name = productCursor.getString(productCursor.getColumnIndex(KEY_DELIVERY_PRODUCT_NAME));
                        product.amount = productCursor.getString(productCursor.getColumnIndex(KEY_DELIVERY_PRODUCT_AMOUNT));
                        product.discount = productCursor.getString(productCursor.getColumnIndex(KEY_DELIVERY_PRODUCT_DISCOUNT));
                        product.price = productCursor.getString(productCursor.getColumnIndex(KEY_DELIVERY_PRODUCT_PRICE));
                        product.qty = productCursor.getInt(productCursor.getColumnIndex(KEY_DELIVERY_PRODUCT_QTY));
                        productList.add(product);
                    } while (productCursor.moveToNext());
                }
                deliveryList1.productList = productList;
                deliveryList.add(deliveryList1);
            } while (deliveryCursor.moveToNext());
        }

        System.out.println("Searched order: " + deliveryQuery);
        System.out.println("Searched order: " + productQuery);
        System.out.println("Searched order: " + new Gson().toJson(deliveryList));

        return deliveryList;
    }

    //For outlet
    public void addOutlet(List<Outlets.Successful.Outlet> outletList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            for (Outlets.Successful.Outlet outlet : outletList) {
                values.put(KEY_OUTLET_ID, outlet.outletId);
                values.put(KEY_OUTLET_NAME, outlet.outletName);
                values.put(KEY_OUTLET_TYPE, outlet.outletType);
                values.put(KEY_OUTLET_OWNER_NAME, outlet.ownerName);
                values.put(KEY_OUTLET_ADDRESS, outlet.outletAddress);
                values.put(KEY_OUTLET_DISTRICT, outlet.district);
                values.put(KEY_OUTLET_THANA, outlet.thana);
                values.put(KEY_OUTLET_PHONE, outlet.phone);
                values.put(KEY_OUTLET_IMAGE, outlet.outletImgUrl);
                values.put(KEY_OUTLET_LONGITUDE, outlet.outletLatitude);
                values.put(KEY_OUTLET_LATITUDE, outlet.outletLongitude);
                values.put(KEY_OUTLET_DUE, outlet.outletDue);
            }

            try {
                db.insert(TABLE_OUTLET, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public List<Outlets.Successful.Outlet> getAllOutlet() {
        List<Outlets.Successful.Outlet> outletList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT *  FROM " + TABLE_OUTLET + " ORDER BY " + KEY_OUTLET_NAME + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                Outlets.Successful.Outlet outlet = new Outlets.Successful.Outlet();
                outlet.outletId = cursor.getInt(cursor.getColumnIndex(KEY_OUTLET_ID));
                outlet.outletName = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_NAME));
                outlet.outletType = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_TYPE));
                outlet.ownerName = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_OWNER_NAME));
                outlet.outletAddress = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_ADDRESS));
                outlet.district = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_DISTRICT));
                outlet.thana = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_THANA));
                outlet.phone = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_PHONE));
                outlet.outletImgUrl = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_IMAGE));
                outlet.outletLongitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_OUTLET_LONGITUDE)));
                outlet.outletLatitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_OUTLET_LATITUDE)));
                outlet.outletDue = cursor.getString(cursor.getColumnIndex(KEY_OUTLET_DUE));

                outletList.add(outlet);
            } while (cursor.moveToNext());
        }
        return outletList;
    }
    //For outlet
}

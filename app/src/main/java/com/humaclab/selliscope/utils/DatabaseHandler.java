package com.humaclab.selliscope.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.humaclab.selliscope.dbmodel.PriceVariationStartEnd;
import com.humaclab.selliscope.dbmodel.Target;
import com.humaclab.selliscope.dbmodel.TradePromoionData;
import com.humaclab.selliscope.dbmodel.UserVisit;
import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.model.DeliveryResponse;
import com.humaclab.selliscope.model.district.District;
import com.humaclab.selliscope.model.outlet_type.OutletType;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.reason.ReasonResponse;
import com.humaclab.selliscope.model.route_plan.RouteDetailsResponse;
import com.humaclab.selliscope.model.thana.Thana;
import com.humaclab.selliscope.model.trade_promotion.Promotion;
import com.humaclab.selliscope.model.trade_promotion.Result;
import com.humaclab.selliscope.model.variant_product.Brand;
import com.humaclab.selliscope.model.variant_product.Category;
import com.humaclab.selliscope.model.variant_product.GodownItem;
import com.humaclab.selliscope.model.price_variation.Product;
import com.humaclab.selliscope.model.price_variation.PriceVariation;
import com.humaclab.selliscope.model.variant_product.ProductsItem;
import com.humaclab.selliscope.model.variant_product.VariantDetailsItem;
import com.humaclab.selliscope.model.variant_product.VariantsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leon on 19/3/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 21;

    // Database Tables
    private static final String TABLE_TARGET = "targets";
    private static final String TABLE_USER_VISITS = "userVisits";
    private static final String TABLE_PRODUCT = "product";
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_BRAND = "brand";
    private static final String TABLE_DELIVERY = "delivery";
    private static final String TABLE_DELIVERY_PRODUCT = "delivery_product";
    private static final String TABLE_OUTLET = "outlet";
    private static final String TABLE_THANA = "thana";
    private static final String TABLE_DISTRICT = "district";
    private static final String TABLE_OUTLET_TYPE = "outlet_type";
    private static final String TABLE_ORDER = "order_table";
    private static final String TABLE_PRICE_VARIATION = "priceVariation";
    private static final String TABLE_TRADE_PROMTOIN = "tradePromotion";
    private static final String TABLE_SELLS_REASON = "reason";

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
    private static final String KEY_BATTERY_STATUS = "battery_status";

    //product table column names
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_PRODUCT_PRICE = "product_price";
    private static final String KEY_PRODUCT_IMAGE = "product_image";
    private static final String KEY_CATEGORY_ID = "cat_id";
    private static final String KEY_CATEGORY_NAME = "cat_name";
    private static final String KEY_BRAND_ID = "brand_id";
    private static final String KEY_BRAND_NAME = "brand_name";
    private static final String KEY_PRODUCT_STOCK = "stock";
    private static final String KEY_VARIANT_ROW = "variant_row";

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
    private static final String KEY_OUTLET_ROUTEPLAN = "outlet_routeplan";
    private static final String KEY_OUTLET_CLIENTID = "ClientID";

    //Thana table column name
    private static final String KEY_THANA_ID = "thana_id";
    private static final String KEY_THANA_NAME = "name";

    //District table column name
    private static final String KEY_DISTRICT_ID = "district_id";
    private static final String KEY_DISTRICT_NAME = "name";

    //Outlet type table column name
    private static final String KEY_TYPE_ID = "id";
    private static final String KEY_TYPE_NAME = "type";

    //Outlet type table column name
    private static final String KEY_SREASONID = "sid";
    private static final String KEY_REASON_ID = "id";
    private static final String KEY_REASON_NAME = "name";

    //Order table column name
    private static final String KEY_ORDER_OUTLET_ID = "outlet_id";
    private static final String KEY_ORDER_DISCOUNT = "order_discount";
    private static final String KEY_ORDER_PRODUCT_ID = "product_id";
    private static final String KEY_ORDER_PRODUCT_QUANTITY = "product_quantity";
    private static final String KEY_ORDER_PRODUCT_ROW = "product_row";
    private static final String KEY_ORDER_PRODUCT_PRICE = "product_price";
    private static final String KEY_ORDER_PRODUCT_DISCOUNT = "product_discount";
    private static final String KEY_ORDER_PRODUCT_TP_DISCOUNT = "product_tp_discount";
    private static final String KEY_ORDER_Return_Relation_ID = "order_return_id";

    //price variation table column name
    private static final String KEY_PV_ID = "pv_id";
    private static final String KEY_PV_PRODUCT_ID = "product_id";
    private static final String KEY_PV_VARIANT_ROW = "variant_row";
    private static final String KEY_PV_OUTLET_TYPE_ID = "outlet_type_id";
    private static final String KEY_PV_OUTLET_TYPE = "outlet_type";
    private static final String KEY_PV_STARTE_RANGE = "start_range";
    private static final String KEY_PV_END_RANGE = "end_range";
    private static final String KEY_PV_PRICE = "price";

    //price variation table column name
    private static final String KEY_TP_ID = "tp_id";
    private static final String KEY_TP_PRODUCT_ID = "product_id";
    private static final String KEY_TP_PROMOTIONAL_TITLE = "promotion_title";
    private static final String KEY_TP_PROMOTION_TYPE = "promotion_type";
    private static final String KEY_TP_PROMOTION_VALUE = "promotion_value";
    private static final String KEY_TP_OFFER_TYPE = "offer_type";
    private static final String KEY_TP_OFFER_VALUE = "offer_value";

    private static final String KEY_TP_OFFER_PRODUCT_NAME = "free_product_name";
    private static final String KEY_TP_OFFER_PRODUCT_QTY = "free_product_qty";
    private static final String KEY_TP_OFFER_PRODUCT_ID = "free_product_id";

    public DatabaseHandler(Context context) {
        super(context, com.humaclab.selliscope.utils.Constants.databaseName, null, DATABASE_VERSION);
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
                + KEY_IS_UPDATED + " INTEGER,"
                + KEY_BATTERY_STATUS + " TEXT"           + ")";

        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + "ID INTEGER PRIMARY KEY,"
                + KEY_PRODUCT_ID + " INTEGER,"
                + KEY_PRODUCT_NAME + " TEXT,"
                + KEY_PRODUCT_PRICE + " TEXT,"
                + KEY_PRODUCT_IMAGE + " TEXT,"
                + KEY_CATEGORY_ID + " INTEGER,"
                + KEY_CATEGORY_NAME + " TEXT,"
                + KEY_BRAND_ID + " INTEGER,"
                + KEY_BRAND_NAME + " TEXT,"
                + KEY_PRODUCT_STOCK + " TEXT,"
                + KEY_VARIANT_ROW + " TEXT"
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
                + KEY_OUTLET_DUE + " TEXT,"
                + KEY_OUTLET_ROUTEPLAN + " TEXT,"
                + KEY_OUTLET_CLIENTID + " TEXT"
                + ")";

        String CREATE_DISTRICT_TABLE = "CREATE TABLE " + TABLE_DISTRICT + "("
                + KEY_DISTRICT_ID + " INTEGER  PRIMARY KEY,"
                + KEY_DISTRICT_NAME + " TEXT"
                + ")";

        String CREATE_THANA_TABLE = "CREATE TABLE " + TABLE_THANA + "("
                + KEY_THANA_ID + " INTEGER  PRIMARY KEY,"
                + KEY_THANA_NAME + " TEXT,"
                + KEY_DISTRICT_ID + " INTEGER"
                + ")";

        String CREATE_OUTELT_TYPE_TABLE = "CREATE TABLE " + TABLE_OUTLET_TYPE + "("
                + KEY_TYPE_ID + " INTEGER  PRIMARY KEY,"
                + KEY_TYPE_NAME + " TEXT"
                + ")";

        String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDER + "("
                + KEY_ORDER_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT,"
                + KEY_ORDER_OUTLET_ID + " INTEGER,"
                + KEY_ORDER_DISCOUNT + " TEXT,"
                + KEY_ORDER_PRODUCT_ID + " INTEGER,"
                + KEY_ORDER_PRODUCT_QUANTITY + " INTEGER,"
                + KEY_ORDER_PRODUCT_ROW + " INTEGER,"
                + KEY_ORDER_PRODUCT_PRICE + " TEXT,"
                + KEY_ORDER_PRODUCT_DISCOUNT + " TEXT,"
                + KEY_ORDER_PRODUCT_TP_DISCOUNT + " TEXT,"
                + KEY_ORDER_Return_Relation_ID + " INTEGER"

                + ")";

        String CREATE_PRICE_VARIATION_TABLE = "CREATE TABLE " + TABLE_PRICE_VARIATION + "("
                + KEY_PV_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT,"
                + KEY_PV_PRODUCT_ID + " INTEGER,"
                + KEY_PV_VARIANT_ROW + " INTEGER,"
                + KEY_PV_OUTLET_TYPE_ID + " INTEGER,"
                + KEY_PV_OUTLET_TYPE + " TEXT,"
                + KEY_PV_STARTE_RANGE + " INTEGER,"
                + KEY_PV_END_RANGE + " INTEGER,"
                + KEY_PV_PRICE + " INTEGER"
                + ")";
        String CREATE_PRODUCT_PROMOTION_TABLE = "CREATE TABLE " + TABLE_TRADE_PROMTOIN + "("
                + KEY_TP_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT,"
                + KEY_TP_PRODUCT_ID + " INTEGER,"
                + KEY_TP_PROMOTIONAL_TITLE + " TEXT,"
                + KEY_TP_OFFER_TYPE + " TEXT,"
                + KEY_TP_PROMOTION_VALUE + " DOUBLE,"
                + KEY_TP_PROMOTION_TYPE + " TEXT,"
                + KEY_TP_OFFER_VALUE + " DOUBLE,"
                + KEY_TP_OFFER_PRODUCT_NAME + " TEXT,"
                + KEY_TP_OFFER_PRODUCT_QTY + " INTEGER,"
                + KEY_TP_OFFER_PRODUCT_ID + " INTEGER"
                + ")";
        String CREATE_TABLE_REASON = "CREATE TABLE " + TABLE_SELLS_REASON + "("
                + KEY_SREASONID + " INTEGER  PRIMARY KEY AUTOINCREMENT,"
                + KEY_REASON_ID + " INTEGER,"
                + KEY_REASON_NAME + " TEXT"
                + ")";

        db.execSQL(CREATE_TARGET_TABLE);
        db.execSQL(CREATE_TARGET_USER_VISITS);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_BRAND_TABLE);
        db.execSQL(CREATE_DELIVERY_TABLE);
        db.execSQL(CREATE_DELIVERY_PRODUCT_TABLE);
        db.execSQL(CREATE_OUTLET_TABLE);
        db.execSQL(CREATE_THANA_TABLE);
        db.execSQL(CREATE_DISTRICT_TABLE);
        db.execSQL(CREATE_OUTELT_TYPE_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_PRICE_VARIATION_TABLE);
        db.execSQL(CREATE_PRODUCT_PROMOTION_TABLE);
        db.execSQL(CREATE_TABLE_REASON);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTLET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THANA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRICT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTLET_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICE_VARIATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRADE_PROMTOIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELLS_REASON);
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
        values.put(KEY_BATTERY_STATUS, userVisit.getBattery_status());
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

        try {


            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    UserVisit visit = new UserVisit(
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                            cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP)),
                            cursor.getInt(cursor.getColumnIndex(KEY_USER_VISIT_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_BATTERY_STATUS))
                    );
                    visitList.add(visit);
                } while (cursor.moveToNext());
            }
        }finally {
            cursor.close();
        }
        // return visit list
        db.close();
        return visitList;
    }

    public void deleteUserVisit(int visitId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_VISITS, KEY_USER_VISIT_ID + " = ?",
                new String[]{String.valueOf(visitId)});
        db.close();
    }

    public void deleteUserVisit() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_VISITS, null, null);
        db.close();

    }


    public void addProduct(List<ProductsItem> products) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        List<ProductsItem> productsItems = getAllProduct();
        for (ProductsItem product : products) {
            if (!productsItems.contains(product)) {

                ContentValues values = new ContentValues();
                String productName = product.getName();
                values.put(KEY_PRODUCT_ID, product.getId());
                values.put(KEY_PRODUCT_IMAGE, product.getImg());
                values.put(KEY_CATEGORY_ID, product.getCategory().getId());
                values.put(KEY_CATEGORY_NAME, product.getCategory().getName());
                values.put(KEY_BRAND_ID, product.getBrand().getId());
                values.put(KEY_BRAND_NAME, product.getBrand().getName());
                if (product.getVariants().isEmpty()) {
                    values.put(KEY_PRODUCT_NAME, product.getName().toLowerCase());
                    values.put(KEY_PRODUCT_PRICE, product.getPrice());
                    int stock = 0;
                    for (GodownItem godownItem : product.getGodown()) {
                        stock += Integer.parseInt(godownItem.getStock());
                    }
                    values.put(KEY_PRODUCT_STOCK, String.valueOf(stock));

                    try {
                        if (!db.isOpen()) db = this.getWritableDatabase();
                        db.insert(TABLE_PRODUCT, null, values);
                        values.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("tareq_test", "addProduct: " + e.getMessage());
                    }
                } else {
                    for (VariantsItem variants : product.getVariants()) {
                        for (VariantDetailsItem variantDetailsItem : variants.getVariantDetailsItems()) {
                            if (variantDetailsItem.getRow() != null) {
                                values.put(KEY_VARIANT_ROW, variantDetailsItem.getRow());
                            } else if (variantDetailsItem.getStock() != null) {
                                values.put(KEY_PRODUCT_STOCK, variantDetailsItem.getStock());
                            } else {
                                if (variantDetailsItem.getVariantCatName().toLowerCase().equals("price")) {
                                    values.put(KEY_PRODUCT_PRICE, variantDetailsItem.getName());
                                    productName += "-" + variantDetailsItem.getName();
                                } else
                                    productName += "-" + variantDetailsItem.getName();
                            }
                        }
                        values.put(KEY_PRODUCT_NAME, productName.toLowerCase().replace(" ", ""));

                        try {
                            if (!db.isOpen()) db = this.getWritableDatabase();
                            db.insert(TABLE_PRODUCT, null, values);
                            productName = product.getName();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("tareq_test", "addProduct: " + e.getMessage());

                        }
                    }
                }
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close(); // Closing database connection
    }

    public void addCategory(int categoryID, String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!getCategory().contains(new Category(categoryName, String.valueOf(categoryID)))) {
            values.put(KEY_CATEGORY_ID, categoryID);
            values.put(KEY_CATEGORY_NAME, categoryName);
            try {
                if (!db.isOpen()) db = this.getWritableDatabase();
                db.insert(TABLE_CATEGORY, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        values.clear();
        db.close(); // Closing database connection
    }

    public void addBrand(int brandID, String brandName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!getBrand().contains(new Brand(brandName, String.valueOf(brandID)))) {
            values.put(KEY_BRAND_ID, brandID);
            values.put(KEY_BRAND_NAME, brandName);
            try {
                if (!db.isOpen()) db = this.getWritableDatabase();
                db.insert(TABLE_BRAND, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        values.clear();

        db.close(); // Closing database connection
    }

    public void removeProductCategoryBrand() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, KEY_PRODUCT_ID + " > ?", new String[]{String.valueOf(-1)});
        db.delete(TABLE_CATEGORY, KEY_CATEGORY_ID + " > ?", new String[]{String.valueOf(-1)});
        db.delete(TABLE_BRAND, KEY_BRAND_ID + " > ?", new String[]{String.valueOf(-1)});
        db.delete(TABLE_PRICE_VARIATION, KEY_PV_ID + " > ?", new String[]{String.valueOf(-1)});
        db.delete(TABLE_TRADE_PROMTOIN, KEY_TP_ID + " > ?", new String[]{String.valueOf(-1)});
        db.close();
    }

    public List<ProductsItem> getAllProduct() {
        List<ProductsItem> productList = new ArrayList<>();
        // Select All Query
        String selectQuery;

        selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " ORDER BY " + KEY_PRODUCT_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                ProductsItem productsItem = new ProductsItem();
                Category category = new Category();
                Brand brand = new Brand();

                productsItem.setId(cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_ID)));
                productsItem.setName(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_NAME)));
                productsItem.setPrice(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_PRICE)));
                productsItem.setImg(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_IMAGE)));
                productsItem.setStock(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_STOCK)));
                productsItem.setVariantRow(cursor.getString(cursor.getColumnIndex(KEY_VARIANT_ROW)) == null ? "0" : cursor.getString(cursor.getColumnIndex(KEY_VARIANT_ROW)));

                category.setName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME)));
                category.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID))));
                productsItem.setCategory(category);

                brand.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_BRAND_ID))));
                brand.setName(cursor.getString(cursor.getColumnIndex(KEY_BRAND_NAME)));
                productsItem.setBrand(brand);

                productList.add(productsItem);
            } while (cursor.moveToNext());
        }
        return productList;
    }

    public List<ProductsItem> getProduct(int categoryID, int brandID) {
        List<ProductsItem> productList = new ArrayList<>();
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
                ProductsItem productsItem = new ProductsItem();
                Category category = new Category();
                Brand brand = new Brand();

                productsItem.setId(cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_ID)));
                productsItem.setName(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_NAME)));
                productsItem.setPrice(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_PRICE)));
                productsItem.setImg(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_IMAGE)));
                productsItem.setStock(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_STOCK)));
                productsItem.setVariantRow(cursor.getString(cursor.getColumnIndex(KEY_VARIANT_ROW)) == null ? "0" : cursor.getString(cursor.getColumnIndex(KEY_VARIANT_ROW)));

                category.setName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME)));
                category.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID))));
                productsItem.setCategory(category);

                brand.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_BRAND_ID))));
                brand.setName(cursor.getString(cursor.getColumnIndex(KEY_BRAND_NAME)));
                productsItem.setBrand(brand);

                productList.add(productsItem);
            } while (cursor.moveToNext());
        }
        return productList;
    }

    public List<ProductsItem> getProduct(int categoryID, int brandID, String searchProduct) {
        List<ProductsItem> productList = new ArrayList<>();
        // Select All Query
        String selectQuery;
        if (categoryID != 0 && brandID != 0) {
            selectQuery = "SELECT * FROM " + TABLE_PRODUCT
                    + " WHERE " + KEY_CATEGORY_ID + "=" + categoryID
                    + " AND " + KEY_BRAND_ID + "=" + brandID + " AND " + KEY_PRODUCT_NAME + " LIKE '%" + searchProduct.toLowerCase() + "%' ORDER BY " + KEY_PRODUCT_NAME + " ASC";
        } else if (categoryID != 0 && brandID == 0) {
            selectQuery = "SELECT * FROM " + TABLE_PRODUCT + " WHERE "
                    + KEY_CATEGORY_ID + "=" + categoryID + " AND " + KEY_PRODUCT_NAME + " LIKE '%" + searchProduct.toLowerCase() + "%' ORDER BY " + KEY_PRODUCT_NAME + " ASC";
        } else if (brandID != 0 && categoryID == 0) {
            selectQuery = "SELECT * FROM " + TABLE_PRODUCT + " WHERE "
                    + KEY_BRAND_ID + "=" + brandID + " AND " + KEY_PRODUCT_NAME + " LIKE '%" + searchProduct.toLowerCase() + "%' ORDER BY " + KEY_PRODUCT_NAME + " ASC";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + KEY_PRODUCT_NAME + " LIKE '%" + searchProduct.toLowerCase() + "%' ORDER BY " + KEY_PRODUCT_NAME + " ASC";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                ProductsItem productsItem = new ProductsItem();
                Category category = new Category();
                Brand brand = new Brand();

                productsItem.setId(cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_ID)));
                productsItem.setName(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_NAME)));
                productsItem.setPrice(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_PRICE)));
                productsItem.setImg(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_IMAGE)));
                productsItem.setStock(cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_STOCK)));
                productsItem.setVariantRow(cursor.getString(cursor.getColumnIndex(KEY_VARIANT_ROW)) == null ? "0" : cursor.getString(cursor.getColumnIndex(KEY_VARIANT_ROW)));

                category.setName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME)));
                category.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID))));
                productsItem.setCategory(category);

                brand.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_BRAND_ID))));
                brand.setName(cursor.getString(cursor.getColumnIndex(KEY_BRAND_NAME)));
                productsItem.setBrand(brand);

                productList.add(productsItem);
            } while (cursor.moveToNext());
        }
        return productList;
    }

    public List<Category> getCategory() {
        List<Category> categoryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                Category category = new Category();
                category.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID))));
                category.setName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME)));

                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        return categoryList;
    }

    public List<Brand> getBrand() {
        List<Brand> brandList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BRAND;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                Brand brand = new Brand();
                brand.setId(String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_BRAND_ID))));
                brand.setName(cursor.getString(cursor.getColumnIndex(KEY_BRAND_NAME)));

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
    public void removeOutlet() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OUTLET, KEY_OUTLET_ID + " > ?", new String[]{String.valueOf(-1)});
        db.close();
    }

    //For outlet
    public void removeOutletWithId(int outletId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OUTLET, KEY_OUTLET_ID + " = ?", new String[]{String.valueOf(outletId)});
        db.close();
    }

    public void addOutlet(List<Outlets.Outlet> outletList) {
        SQLiteDatabase db = this.getWritableDatabase();
        /*   db.beginTransaction();*/
        ContentValues values = new ContentValues();

        try {
            List<Outlets.Outlet> outlets = getAllOutlet().outlets;
            for (Outlets.Outlet outlet : outletList) {
                if (!outlets.contains(outlet)) {
                    values.put(KEY_OUTLET_ID, outlet.outletId);
                    values.put(KEY_OUTLET_NAME, outlet.outletName);
                    values.put(KEY_OUTLET_TYPE, outlet.outletType);
                    values.put(KEY_OUTLET_OWNER_NAME, outlet.ownerName);
                    values.put(KEY_OUTLET_ADDRESS, outlet.outletAddress);
                    values.put(KEY_OUTLET_DISTRICT, outlet.district);
                    values.put(KEY_OUTLET_THANA, outlet.thana);
                    values.put(KEY_OUTLET_PHONE, outlet.phone);
                    values.put(KEY_OUTLET_IMAGE, outlet.outletImgUrl);
                    values.put(KEY_OUTLET_LONGITUDE, outlet.outletLongitude);
                    values.put(KEY_OUTLET_LATITUDE, outlet.outletLatitude);
                    values.put(KEY_OUTLET_DUE, outlet.outletDue);
                    values.put(KEY_OUTLET_ROUTEPLAN, outlet.getOutlet_routeplan());
                    values.put(KEY_OUTLET_CLIENTID, outlet.getClientID());
                    try {
                   /*     db.setTransactionSuccessful();
                        db.endTransaction();*/
                        db.close();
                        removeOutletWithId(outlet.outletId);
                        if (!db.isOpen()) db = this.getWritableDatabase();
                        db.insert(TABLE_OUTLET, null, values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("tareq_test", "can't update outlet " + e.getMessage());
        }
      /*if(db.inTransaction()) {
          db.setTransactionSuccessful();
          db.endTransaction();
      }*/
        db.close();
    }

    /**
     * For adding extra temporary outlet which is added into from plan but not in the personal
     * outlet list
     *
     * @param outlet RouteDetailsResponse.OutletItem
     */
    private void addExtraTempOutlet(RouteDetailsResponse.OutletItem outlet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (outlet.getCheckIn().equals("uncheck")) {

            try {
                values.put(KEY_OUTLET_ID, outlet.getId());
                values.put(KEY_OUTLET_NAME, outlet.getName());
                values.put(KEY_OUTLET_TYPE, outlet.getType());
                values.put(KEY_OUTLET_OWNER_NAME, outlet.getOwner());
                values.put(KEY_OUTLET_ADDRESS, outlet.getAddress());
                values.put(KEY_OUTLET_DISTRICT, outlet.getDistrict());
                values.put(KEY_OUTLET_THANA, outlet.getThana());
                values.put(KEY_OUTLET_PHONE, outlet.getPhone());
                values.put(KEY_OUTLET_IMAGE, outlet.getImg());
                values.put(KEY_OUTLET_LONGITUDE, outlet.getLongitude());
                values.put(KEY_OUTLET_LATITUDE, outlet.getLatitude());
                values.put(KEY_OUTLET_DUE, outlet.getDue());
                values.put(KEY_OUTLET_ROUTEPLAN, "1");
                values.put(KEY_OUTLET_CLIENTID, outlet.getClientID());
                try {
                    db.insert(TABLE_OUTLET, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                values.put(KEY_OUTLET_ID, outlet.getId());
                values.put(KEY_OUTLET_NAME, outlet.getName());
                values.put(KEY_OUTLET_TYPE, outlet.getType());
                values.put(KEY_OUTLET_OWNER_NAME, outlet.getOwner());
                values.put(KEY_OUTLET_ADDRESS, outlet.getAddress());
                values.put(KEY_OUTLET_DISTRICT, outlet.getDistrict());
                values.put(KEY_OUTLET_THANA, outlet.getThana());
                values.put(KEY_OUTLET_PHONE, outlet.getPhone());
                values.put(KEY_OUTLET_IMAGE, outlet.getImg());
                values.put(KEY_OUTLET_LONGITUDE, outlet.getLongitude());
                values.put(KEY_OUTLET_LATITUDE, outlet.getLatitude());
                values.put(KEY_OUTLET_DUE, outlet.getDue());
                values.put(KEY_OUTLET_ROUTEPLAN, "0");
                values.put(KEY_OUTLET_CLIENTID, outlet.getClientID());
                try {
                    db.insert(TABLE_OUTLET, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //db.close();
    }

    /**
     * For checking if the provided outlet in the route plan is in the database
     *
     * @param outlet RouteDetailsResponse.OutletItem
     * @return
     */
    private boolean checkExtraOutlet(RouteDetailsResponse.OutletItem outlet) {
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_OUTLET + " WHERE " + KEY_OUTLET_ID + " = " + outlet.getId();
        Cursor cursor = db.rawQuery(checkQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }

    //for route plan update the portion
    public void updateOutletRoutePlan(List<RouteDetailsResponse.OutletItem> outletItemList) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            for (RouteDetailsResponse.OutletItem outlet : outletItemList) {
                if (checkExtraOutlet(outlet)) {
                    addExtraTempOutlet(outlet);
                } else {
                    if (outlet.getCheckIn().equals("uncheck")) {
                        String selectQuery = "UPDATE " + TABLE_OUTLET + " SET " + KEY_OUTLET_ROUTEPLAN + "= 1" + " WHERE " + KEY_OUTLET_ID + " = " + outlet.getId();
                        db.execSQL(selectQuery);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    //After Checking Update route Paln

    public void afterCheckinUpdateOutletRoutePlan(int id) {
        try {

            String selectQuery = "UPDATE " + TABLE_OUTLET + " SET " + KEY_OUTLET_ROUTEPLAN + "= 0" + " WHERE " + KEY_OUTLET_ID + " = " + id;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(selectQuery);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Outlets.OutletsResult getAllOutlet() {
        Outlets.OutletsResult outletsResult = new Outlets.OutletsResult();
        List<Outlets.Outlet> outletList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_OUTLET + " ORDER BY " + KEY_OUTLET_ROUTEPLAN + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                Outlets.Outlet outlet = new Outlets.Outlet();
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
                outlet.setOutlet_routeplan(cursor.getString(cursor.getColumnIndex(KEY_OUTLET_ROUTEPLAN)));
                outlet.setClientID(cursor.getString(cursor.getColumnIndex(KEY_OUTLET_CLIENTID)));

                outletList.add(outlet);
            } while (cursor.moveToNext());
        }
        outletsResult.outlets = outletList;
        return outletsResult;
    }

    public int getSizeOfOutlet() {
        List<Outlets.Outlet> outletList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_OUTLET + " ORDER BY " + KEY_OUTLET_NAME + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                Outlets.Outlet outlet = new Outlets.Outlet();
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
        return outletList.size();
    }

    public Outlets.OutletsResult getSearchedOutlet(String outletName) {
        Outlets.OutletsResult outletsResult = new Outlets.OutletsResult();
        List<Outlets.Outlet> outletList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_OUTLET + " WHERE " + KEY_OUTLET_NAME + " LIKE  '%" + outletName + "%' OR " + KEY_OUTLET_CLIENTID + " LIKE '%" + outletName + "%' ORDER BY " + KEY_OUTLET_NAME + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                Outlets.Outlet outlet = new Outlets.Outlet();
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
                outlet.setOutlet_routeplan(cursor.getString(cursor.getColumnIndex(KEY_OUTLET_ROUTEPLAN)));
                outlet.setClientID(cursor.getString(cursor.getColumnIndex(KEY_OUTLET_CLIENTID)));
                outletList.add(outlet);
            } while (cursor.moveToNext());
        }
        outletsResult.outlets = outletList;
        return outletsResult;
    }
    //For outlet

    public List<District> getDistrict() {
        List<District> districtList = new ArrayList<>();

        /*District district1 = new District();
        district1.setId(0);
        district1.setName("Select district");
        districtList.add(district1);*/

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DISTRICT;

        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                District district = new District();
                district.setId(cursor.getInt(cursor.getColumnIndex(KEY_DISTRICT_ID)));
                district.setName(cursor.getString(cursor.getColumnIndex(KEY_DISTRICT_NAME)));
                districtList.add(district);

            } while (cursor.moveToNext());
        }
        db.close();
        return districtList;
    }

    public void setDistrict(List<District> districtList) {
        List<District> districts = getDistrict();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            for (District district : districtList) {
                if (!districts.contains(district)) {
                    values.put(KEY_DISTRICT_ID, district.getId());
                    values.put(KEY_DISTRICT_NAME, district.getName());
                    try {
                        if (!db.isOpen()) db = this.getWritableDatabase();
                        db.insert(TABLE_DISTRICT, null, values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public List<Thana> getThana(int districtId) {
        List<Thana> thanaList = new ArrayList<>();

        Thana thana1 = new Thana();
        thana1.setId(0);
        thana1.setName("Select Thana");
        thana1.setDistrictId(0);
        thanaList.add(thana1);

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_THANA
                + " WHERE " + KEY_DISTRICT_ID + "=" + districtId;

        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                Thana thana = new Thana();
                thana.setId(cursor.getInt(cursor.getColumnIndex(KEY_THANA_ID)));
                thana.setName(cursor.getString(cursor.getColumnIndex(KEY_THANA_NAME)));
                thana.setDistrictId(cursor.getInt(cursor.getColumnIndex(KEY_DISTRICT_ID)));
                thanaList.add(thana);

            } while (cursor.moveToNext());
        }
        db.close();
        return thanaList;
    }

    public void setThana(List<Thana> thanaList) {

        List<Thana> thanas = getAllThanas();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            for (Thana thana : thanaList) {
                if (!thanas.contains(thana)) {
                    values.put(KEY_THANA_ID, thana.getId());
                    values.put(KEY_THANA_NAME, thana.getName());
                    values.put(KEY_DISTRICT_ID, thana.getDistrictId());
                    try {
                        if (!db.isOpen()) db = this.getWritableDatabase();
                        db.insert(TABLE_THANA, null, values);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    private List<Thana> getAllThanas() {
        List<Thana> thanaList = new ArrayList<>();


        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_THANA;

        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                Thana thana = new Thana();
                thana.setId(cursor.getInt(cursor.getColumnIndex(KEY_THANA_ID)));
                thana.setName(cursor.getString(cursor.getColumnIndex(KEY_THANA_NAME)));
                thana.setDistrictId(cursor.getInt(cursor.getColumnIndex(KEY_DISTRICT_ID)));
                thanaList.add(thana);

            } while (cursor.moveToNext());
        }
        db.close();
        return thanaList;
    }


    public List<OutletType> getOutletType() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_OUTLET_TYPE;
        List<OutletType> outletTypeList = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                OutletType outletType = new OutletType();
                outletType.setId(cursor.getInt(cursor.getColumnIndex(KEY_TYPE_ID)));
                outletType.setName(cursor.getString(cursor.getColumnIndex(KEY_TYPE_NAME)));
                outletTypeList.add(outletType);
            } while (cursor.moveToNext());
        }
        db.close();
        return outletTypeList;
    }

    public void setOutletType(List<OutletType> outletTypeList) {

        List<OutletType> outletTypes = getOutletType();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            for (OutletType outletType : outletTypeList) {
                if (!outletTypes.contains(outletType)) {
                    values.put(KEY_TYPE_ID, outletType.getId());
                    values.put(KEY_TYPE_NAME, outletType.getName());
                    try {
                        if (!db.isOpen()) db = this.getWritableDatabase();
                        db.insert(TABLE_OUTLET_TYPE, null, values);
                        Log.d("tareq_test", "" + new Gson().toJson(getOutletType()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    //Offline order taking
    public List<AddNewOrder> getOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        String outletIds = "SELECT distinct " + KEY_ORDER_OUTLET_ID + "," + KEY_ORDER_DISCOUNT + " FROM " + TABLE_ORDER;

        List<AddNewOrder> addNewOrderList = new ArrayList<>();

        Cursor cursorOutletID = db.rawQuery(outletIds, null);

        // looping through all rows and adding to list
        if (cursorOutletID.moveToFirst()) {
            do {
                AddNewOrder addNewOrder = new AddNewOrder();
                AddNewOrder.NewOrder newOrder = new AddNewOrder.NewOrder();
                List<AddNewOrder.NewOrder.Product> productList = new ArrayList<>();
                cursorOutletID.getColumnNames();
                newOrder.outletId = cursorOutletID.getInt(cursorOutletID.getColumnIndex(KEY_ORDER_OUTLET_ID));
                newOrder.discount = Double.valueOf(cursorOutletID.getString(cursorOutletID.getColumnIndex(KEY_ORDER_DISCOUNT)));


                String orders = "SELECT * FROM " + TABLE_ORDER + " WHERE " + KEY_ORDER_OUTLET_ID + "=" + newOrder.outletId;
                Cursor cursorOrders = db.rawQuery(orders, null);
                if (cursorOrders.moveToFirst()) {
                    do {
                        AddNewOrder.NewOrder.Product product = new AddNewOrder.NewOrder.Product();
                        product.id = cursorOrders.getInt(cursorOrders.getColumnIndex(KEY_ORDER_PRODUCT_ID));
                        product.qty = cursorOrders.getInt(cursorOrders.getColumnIndex(KEY_ORDER_PRODUCT_QUANTITY));
                        product.row = cursorOrders.getInt(cursorOrders.getColumnIndex(KEY_ORDER_PRODUCT_ROW));
                        product.price = cursorOrders.getString(cursorOrders.getColumnIndex(KEY_ORDER_PRODUCT_PRICE));
                        product.discount = Double.valueOf(cursorOrders.getString(cursorOrders.getColumnIndex(KEY_ORDER_PRODUCT_DISCOUNT)));
                        product.tpDiscount = Double.valueOf(cursorOrders.getString(cursorOrders.getColumnIndex(KEY_ORDER_PRODUCT_TP_DISCOUNT)));
                        product.order_return_id = cursorOrders.getInt(cursorOrders.getColumnIndex(KEY_ORDER_Return_Relation_ID));
                        productList.add(product);
                    } while (cursorOrders.moveToNext());
                    newOrder.products = productList;
                    addNewOrder.newOrder = newOrder;
                }
                addNewOrderList.add(addNewOrder);
            } while (cursorOutletID.moveToNext());
        }
        db.close();
        return addNewOrderList;
    }

    public void setOrder(AddNewOrder addNewOrder, int order_return_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ORDER_OUTLET_ID, addNewOrder.newOrder.outletId);
        values.put(KEY_ORDER_DISCOUNT, addNewOrder.newOrder.discount);
        values.put(KEY_ORDER_Return_Relation_ID, order_return_id);
        try {
            for (AddNewOrder.NewOrder.Product product : addNewOrder.newOrder.products) {
                values.put(KEY_ORDER_PRODUCT_ID, product.id);
                values.put(KEY_ORDER_PRODUCT_QUANTITY, product.qty);
                values.put(KEY_ORDER_PRODUCT_ROW, product.row);
                values.put(KEY_ORDER_PRODUCT_PRICE, product.price);
                values.put(KEY_ORDER_PRODUCT_DISCOUNT, product.discount);
                values.put(KEY_ORDER_PRODUCT_TP_DISCOUNT, product.tpDiscount);

                try {
                    db.insert(TABLE_ORDER, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BRAND, null, null);
        db.delete(TABLE_CATEGORY, null, null);
        db.delete(TABLE_DELIVERY, null, null);
        db.delete(TABLE_DELIVERY_PRODUCT, null, null);
        db.delete(TABLE_DISTRICT, null, null);
        db.delete(TABLE_ORDER, null, null);
        db.delete(TABLE_OUTLET, null, null);
        db.delete(TABLE_OUTLET_TYPE, null, null);
        db.delete(TABLE_PRICE_VARIATION, null, null);
        db.delete(TABLE_PRODUCT, null, null);
        db.delete(TABLE_SELLS_REASON, null, null);
        db.delete(TABLE_TARGET, null, null);
        db.delete(TABLE_THANA, null, null);
        db.delete(TABLE_TRADE_PROMTOIN, null, null);
        db.delete(TABLE_USER_VISITS, null, null);
        db.close();
    }

    public void removeOrder(int outletId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ORDER, KEY_OUTLET_ID + " = ?", new String[]{String.valueOf(outletId)});
        db.close();
    }
    //Offline order taking

    //Price Variation
    public void add_price_Variation(List<Product> products) {

        SQLiteDatabase db = this.getWritableDatabase();
        for (Product priceVariatiomItem : products) {
            ContentValues values = new ContentValues();
            //String productName = product.getName();
            values.put(KEY_PV_PRODUCT_ID, priceVariatiomItem.getId());
            for (PriceVariation variationModel : priceVariatiomItem.getPriceVariation()) {
                values.put(KEY_PV_VARIANT_ROW, variationModel.getVariantRow());
                values.put(KEY_PV_OUTLET_TYPE_ID, variationModel.getOutletTypeId());
                values.put(KEY_PV_OUTLET_TYPE, variationModel.getOutletType());
                values.put(KEY_PV_STARTE_RANGE, variationModel.getStartRange());
                values.put(KEY_PV_END_RANGE, variationModel.getEndRange());
                values.put(KEY_PV_PRICE, variationModel.getPrice());
                try {
                    db.insert(TABLE_PRICE_VARIATION, null, values);
                    //productName = product.getName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        db.close(); // Closing database connection
    }

    public List<PriceVariationStartEnd> getpriceVariationStartEndEprice(int productID, String outletType, int varianrRow) {
        List<PriceVariationStartEnd> priceVariationStartEndList = new ArrayList<PriceVariationStartEnd>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRICE_VARIATION
                + " WHERE " + KEY_PV_PRODUCT_ID + "= " + productID + " AND " + KEY_PV_OUTLET_TYPE + "= " + "'" + outletType + "'" + "AND " + KEY_VARIANT_ROW + "= " + varianrRow;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PriceVariationStartEnd priceVariationStartEnd = new PriceVariationStartEnd(
                        cursor.getDouble(cursor.getColumnIndex(KEY_PV_STARTE_RANGE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_PV_END_RANGE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_PV_PRICE))
                );
                priceVariationStartEndList.add(priceVariationStartEnd);
            } while (cursor.moveToNext());
        }
        // return visit list
        return priceVariationStartEndList;
    }


    //Price Variation
    public void add_trade_promotion(List<Result> results) {

        SQLiteDatabase db = this.getWritableDatabase();
        for (Result result : results) {
            ContentValues values = new ContentValues();
            //String productName = product.getName();
            values.put(KEY_TP_PRODUCT_ID, result.getProductId());
            for (Promotion promotion : result.getPromotion()) {
                values.put(KEY_TP_PROMOTIONAL_TITLE, promotion.getPromotionTitle());
                values.put(KEY_TP_PROMOTION_TYPE, promotion.getPromotionType());
                values.put(KEY_TP_PROMOTION_VALUE, promotion.getPromotionValue());
                values.put(KEY_TP_OFFER_TYPE, promotion.getOfferType());
                values.put(KEY_TP_OFFER_VALUE, promotion.getOfferValue());
                if (!(promotion.getOfferProduct() == null)) {
                    values.put(KEY_TP_OFFER_PRODUCT_NAME, promotion.getOfferProduct().get(0).getProductName());
                    values.put(KEY_TP_OFFER_PRODUCT_QTY, promotion.getOfferProduct().get(0).getProductQty());
                    values.put(KEY_TP_OFFER_PRODUCT_ID, promotion.getOfferProduct().get(0).getProductId());
                }
                try {
                    db.insert(TABLE_TRADE_PROMTOIN, null, values);
                    //productName = product.getName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        db.close(); // Closing database connection
    }

    public List<TradePromoionData> getTradePromoion(int productID) {
        List<TradePromoionData> tradePromoionData = new ArrayList<TradePromoionData>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRADE_PROMTOIN
                + " WHERE " + KEY_TP_PRODUCT_ID + "= " + productID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TradePromoionData tradePromoionData1 = new TradePromoionData(
                        cursor.getString(cursor.getColumnIndex(KEY_TP_PROMOTION_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TP_PROMOTIONAL_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TP_PROMOTION_VALUE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TP_OFFER_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TP_OFFER_VALUE)),
                        cursor.getString(cursor.getColumnIndex(KEY_TP_OFFER_PRODUCT_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_TP_OFFER_PRODUCT_QTY)),
                        cursor.getString(cursor.getColumnIndex(KEY_TP_OFFER_PRODUCT_ID))
                );
                tradePromoionData.add(tradePromoionData1);
            } while (cursor.moveToNext());
        }
        // return visit list
        return tradePromoionData;
    }

    public void setSellsResponseReason(List<ReasonResponse.Result> responseReason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            for (ReasonResponse.Result reason : responseReason) {
                values.put(KEY_REASON_ID, reason.getId());
                values.put(KEY_REASON_NAME, reason.getName());
                try {
                    db.insert(TABLE_SELLS_REASON, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public List<ReasonResponse.Result> getSellsResponseReason() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SELLS_REASON;
        List<ReasonResponse.Result> resultList = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                cursor.getColumnNames();
                ReasonResponse.Result result = new ReasonResponse.Result();
                result.setId(cursor.getInt(cursor.getColumnIndex(KEY_REASON_ID)));
                result.setName(cursor.getString(cursor.getColumnIndex(KEY_REASON_NAME)));
                resultList.add(result);

            }
            while (cursor.moveToNext());
        }
        db.close();
        return resultList;
    }

}
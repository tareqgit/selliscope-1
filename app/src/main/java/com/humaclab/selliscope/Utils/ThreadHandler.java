package com.humaclab.selliscope.Utils;

import android.content.Context;

import com.humaclab.selliscope.model.ProductResponse;

import java.util.List;

/**
 * Created by leon on 7/27/17.
 */

public class ThreadHandler {
    private static Thread addOutletThread;
    private static Thread addProductThread;

    public static void startAddOutletThread() {

    }

    public static void stopAddOutletThread() {
        addOutletThread.interrupt();
    }

    public static void startAddProductThread(Context context, final List<ProductResponse.ProductResult> products) {
        final DatabaseHandler databaseHandler = new DatabaseHandler(context);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                databaseHandler.removeProductCategoryBrand();
                for (ProductResponse.ProductResult result : products) {
                    databaseHandler.addProduct(result.id, result.name, result.price, result.img, result.brand.id, result.brand.name, result.category.id, result.category.name);
                }
                stopAddProductThread();
            }
        };
        addProductThread = new Thread(runnable);
        addProductThread.start();
        addProductThread.interrupt();
    }

    public static void stopAddProductThread() {
        addProductThread.interrupt();
    }

}

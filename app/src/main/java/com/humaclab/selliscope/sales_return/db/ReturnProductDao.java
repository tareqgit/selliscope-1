package com.humaclab.selliscope.sales_return.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/***
 * Created by mtita on 11,July,2019.
 */
@Dao
public interface ReturnProductDao {

    @Query("SELECT * FROM return_product_entity")
    List<ReturnProductEntity> getAllReturnProduct();

    @Query("SELECT * FROM return_product_entity WHERE order_return_id = :order_return_id")
    List<ReturnProductEntity> getAllReturnProduct(int order_return_id);

    @Query("DELETE  FROM return_product_entity")
    void deleteAllReturnProduct();

    @Query("DELETE  FROM return_product_entity  WHERE order_return_id = :order_return_id")
    void deleteAllReturnProduct(int order_return_id);


    @Insert
    void insertReturnProduct(ReturnProductEntity returnProductEntity);


    @Delete
    void deleteReturnProduct(ReturnProductEntity returnProductEntity);
}

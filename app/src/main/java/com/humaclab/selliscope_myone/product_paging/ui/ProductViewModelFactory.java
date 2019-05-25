/*
 * Created by Tareq Islam on 5/23/19 2:09 PM
 *
 *  Last modified 5/22/19 3:04 PM
 */

/*
 * Created by Tareq Islam on 5/22/19 2:57 PM
 *
 *  Last modified 5/22/19 2:57 PM
 */

package com.humaclab.selliscope_myone.product_paging.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.humaclab.selliscope_myone.outlet_paging.data.OutletRepository;
import com.humaclab.selliscope_myone.outlet_paging.ui.OutletSearchViewModel;
import com.humaclab.selliscope_myone.product_paging.data.ProductRepository;

import java.lang.reflect.InvocationTargetException;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductViewModelFactory implements ViewModelProvider.Factory {

    private ProductRepository mProductRepository;

    public ProductViewModelFactory(ProductRepository productRepository) {
        this.mProductRepository = productRepository;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (ProductSearchViewModel.class.isAssignableFrom(modelClass)) {
            try {
                return modelClass.getConstructor(ProductRepository.class).newInstance(mProductRepository);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class " + modelClass);
    }
}
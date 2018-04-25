package com.humaclab.selliscope.interfaces;

import com.humaclab.selliscope.helper.SelectedProductHelper;

/**
 * Created by leon on 20/3/18.
 */

public interface OnSelectProduct {
    /**
     * Update UI when product select for order
     *
     * @param selectedProduct SelectedProductHelper selected product details
     */
    void onSetSelectedProduct(SelectedProductHelper selectedProduct);

    /**
     * Update UI when product is removed from order
     *
     * @param selectedProduct SelectedProductHelper removed product details
     */
    void onRemoveSelectedProduct(SelectedProductHelper selectedProduct);
}

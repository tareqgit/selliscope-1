package com.sokrio.sokrio_classic.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/***
 * Created by mtita on 22,September,2019.
 */
public class MyMath {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

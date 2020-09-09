package com.sokrio.sokrio_classic.utils;


import java.util.ArrayList;

public class ImportentFunction {

    public static String convertToEnglishDigits(String value) {
        String newValue = value.replace("১", "1").replace("২", "2").replace("৩", "3").replace("৪", "4").replace("৫", "5")
                .replace("৬", "6").replace("৭", "7").replace("৮", "8").replace("৯", "9").replace("০", "0");

        return newValue;
    }
    public static ArrayList<Integer> deliveryArrayList = new ArrayList<>();
}

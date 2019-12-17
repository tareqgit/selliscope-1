package com.easyopstech.easyops.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

/**
 * Created by leon on 8/20/17.
 */

public class GetAddressFromLatLang {
    /**
     * @param context   {@link Context}
     * @param latitude  String
     * @param longitude String
     * @return address String
     */
    public static String getAddressFromLatLan(Context context, double latitude, double longitude) {
        String address = "";
        String city = "";
        String state = "";
        String country = "";
        String postalCode = "";
        String knownName = "";
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();

            System.out.println("Location address: " + addresses.get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*System.out.println("Location address: " + address + ", " + city + ", " + state + ", " + country + ", " + postalCode + ", " + knownName);
        System.out.println("Location address: " + address + ", " + city + ", " + state + ", " + country);
        System.out.println("Location address: " + address + ", " + city);
        System.out.println("Location address: " + address);*/

        return address;
    }
}

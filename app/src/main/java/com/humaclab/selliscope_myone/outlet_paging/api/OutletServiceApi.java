/*
 * Created by Tareq Islam on 5/23/19 1:32 PM
 *
 *  Last modified 5/23/19 1:32 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.outlet_paging.model.OutletItem;
import com.humaclab.selliscope_myone.outlet_paging.api.response_model.OutletSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Created by mtita on 23,May,2019.
 */
public class OutletServiceApi {


    public static void searchOutlets(SelliscopeApiEndpointInterface service, String query, int page, int itemsPerPage, final ApiCallback apiCallback) {
        Log.d("tareq_test", String.format("searchRepos: query: %s, page: %s, itemsPerPage: %s", query, page, itemsPerPage));

        //Framing the query to be searched only in the Name and description fields of the
        //Github repositories
        String apiQuery = query ;

        //Executing the API asynchronously
        service.searchOutlets(apiQuery, page).enqueue(new Callback<OutletSearchResponse>() {
            //Called when the response is received
            @Override
            public void onResponse(@Nullable Call<OutletSearchResponse> call, @NonNull Response<OutletSearchResponse> response) {
                Log.d("tareq_test", "onResponse: Got response: " + response);
                if (response.isSuccessful()) {
                    //Retrieving the Repo Items when the response is successful
                    List<OutletItem> items;
                    if (response.body() != null) {
                        items = response.body().getResult().getOutlet();
                    } else {
                        items = new ArrayList<>();
                    }
                    //Pass the result to the callback
                    apiCallback.onSuccess(items);
                } else {
                    //When the response is unsuccessful
                    try {
                        //Pass the error to the callback
                        apiCallback.onError(response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error");
                    } catch (IOException e) {
                        Log.e("tareq_test", "onResponse: Failed while reading errorBody: ", e);
                    }
                }
            }

            //Called on Failure of the request
            @Override
            public void onFailure(@Nullable Call<OutletSearchResponse> call, @NonNull Throwable t) {
                Log.d("tareq_test", "onFailure: Failed to get data");
                //Pass the error to the callback
                apiCallback.onError(t.getMessage() != null ?
                        t.getMessage() : "Unknown error");
            }
        });
    }





    public interface ApiCallback {
        /**
         * Callback invoked when the Search Repo API Call
         * completed successfully
         *
         * @param items The List of Repos retrieved for the Search done
         */
        void onSuccess(List<OutletItem> items);

        /**
         * Callback invoked when the Search Repo API Call failed
         *
         * @param errorMessage The Error message captured for the API Call failed
         */
        void onError(String errorMessage);
    }
}

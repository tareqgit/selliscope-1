package com.sokrio.sokrio_classic.utils;

import android.content.Context;
import android.util.Log;

/***
 * Created by mtita on 13,October,2019.
 */
public class ReloadDataService {


    DatabaseHandler databaseHandler;
    LoadLocalIntoBackground mLoadLocalIntoBackground;

    public ReloadDataService(Context context) {
        this.databaseHandler =  new DatabaseHandler(context);

       this. mLoadLocalIntoBackground = new LoadLocalIntoBackground(context);

        databaseHandler.deleteAllData();

    }


    public void reloadData(ReloadDataListener reloadDataListener){
        mLoadLocalIntoBackground.loadAll(new LoadLocalIntoBackground.LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                Log.d("tareq_test", "Data refresh complete");
               reloadDataListener.onComplete();
            }

            @Override
            public void onLoadFailed(String msg) {
              reloadDataListener.onFailed(msg);

            }
        });
    }


   public interface ReloadDataListener{
        void onComplete();

        void onFailed(String reason);
    }
}

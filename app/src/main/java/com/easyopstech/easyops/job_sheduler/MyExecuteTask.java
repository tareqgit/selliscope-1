package com.easyopstech.easyops.job_sheduler;

import android.content.Context;
import android.os.AsyncTask;


public class MyExecuteTask extends AsyncTask<Void,Void,String> {
    private Context context;


    public MyExecuteTask(Context context) {
        this.context = context;

    }

    @Override
    protected String doInBackground(Void... voids) {

        return " Start ";
    }
}

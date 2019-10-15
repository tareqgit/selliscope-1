package com.humaclab.selliscope.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.humaclab.selliscope.utils.ReloadDataService;
import com.humaclab.selliscope.utils.UpLoadDataService;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
        Log.d("tareq_test", "ActionReceiver #15: onReceive:  receive");
        String action=intent.getStringExtra("action");
        if(action.equals("actionUpload")){
            performActionUpload(context,intent);
        }
        else if(action.equals("actionReload")){
            performActionReload(context,intent);

        }
      /*  //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
*/

    }

    public void performActionUpload(Context context, Intent intent){

       UpLoadDataService upLoadDataService = new UpLoadDataService(context);
        upLoadDataService.uploadOrder_and_ReturnData(new UpLoadDataService.UploadCompleteListener() {
            @Override
            public void uploadComplete() {
                Log.d("tareq_test", "Upload complete");
                Toast.makeText(context, "Data Upload Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void uploadFailed(String reason) {
                Log.d("tareq_test", "" + reason);
                Toast.makeText(context, "Data laod failed" + reason, Toast.LENGTH_SHORT).show();
            }
        });

        // if you want cancel notification
        int notificationId = intent.getIntExtra("notificationId", 0);
        Log.d("tareq_test", "ActionReceiver #31: onReceive:  "+ notificationId);


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);

    }

    public void performActionReload(Context context, Intent intent){
        Log.d("tareq_test", "ActionReceiver #34: performActionReload:  called");

        new ReloadDataService(context).reloadData(new ReloadDataService.ReloadDataListener() {
            @Override
            public void onComplete() {

                Log.d("tareq_test", "ActionReceiver #71: onComplete:  ");
                Toast.makeText(context, "Data ReLoad Complete", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailed(String reason) {
                Log.d("tareq_test", "ActionReceiver #78: onFailed:  "+ reason);
                Toast.makeText(context, "Data Reload failed; "+ reason, Toast.LENGTH_SHORT).show();
            }
        });


        // if you want cancel notification
        int notificationId = intent.getIntExtra("notificationId", 0);
        Log.d("tareq_test", "ActionReceiver #31: onReceive:  "+ notificationId);


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

}

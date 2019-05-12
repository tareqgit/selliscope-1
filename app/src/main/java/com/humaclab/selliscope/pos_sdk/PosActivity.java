package com.humaclab.selliscope.pos_sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.ActivityPosBinding;
import com.humaclab.selliscope.pos_sdk.command.sdk.PrinterCommand;


public class PosActivity extends AppCompatActivity {
    /******************************************************************************************************/
    // Debugging
    private static final String TAG = "Main_Activity";
    private static final boolean DEBUG = true;
    /******************************************************************************************************/
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    /*******************************************************************************************************/
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "activity_pos_device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CHOSE_BMP = 3;
    private static final int REQUEST_CAMER = 4;

    /******************************************************************************************************/
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;
    /*******************************************************************************************************/
    private static final String CHINESE = "GBK";


    ActivityPosBinding mMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       mMainBinding= DataBindingUtil.setContentView(this, R.layout.activity_pos);


        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // If Bluetooth is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else {
            if (mService == null)
                KeyListenerInit();
        }
    }



    @Override
    public synchronized void onResume() {
        super.onResume();

        if (mService != null) {

            if (mService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                mService.start();
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (DEBUG)
            Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (mService != null)
            mService.stop();
        if (DEBUG)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    private void KeyListenerInit() {
        mMainBinding.buttonScan.setOnClickListener(v -> {
            Intent serverIntent = new Intent(PosActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

        });

        mMainBinding.closeButton.setOnClickListener(v->{
            mService.stop();
            mMainBinding.buttonScan.setText("Search for and connect a Bluetooth printer");
            mMainBinding.buttonScan.setEnabled(true);
        });

        mMainBinding.printButton.setOnClickListener(v->{
            Print_Test();
        });

        mService = new BluetoothService(this, mHandler);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_STATE_CHANGE:
                    if (DEBUG)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                          mMainBinding.buttonScan.setText("Connected");
                            //	Print_Test();//
                            mMainBinding.buttonScan.setEnabled(false);
                           /* editText.setEnabled(true);
                            imageViewPicture.setEnabled(true);
                            width_58mm.setEnabled(true);
                            width_80.setEnabled(true);
                            hexBox.setEnabled(true);
                            sendButton.setEnabled(true);
                            testButton.setEnabled(true);
                            printbmpButton.setEnabled(true);
                            btnClose.setEnabled(true);
                            btn_BMP.setEnabled(true);
                            btn_ChoseCommand.setEnabled(true);
                            btn_prtcodeButton.setEnabled(true);
                            btn_prtsma.setEnabled(true);
                            btn_prttableButton.setEnabled(true);
                            btn_camer.setEnabled(true);
                            btn_scqrcode.setEnabled(true);
                            Simplified.setEnabled(true);
                            Korean.setEnabled(true);
                            big5.setEnabled(true);
                            thai.setEnabled(true);*/
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            mMainBinding.buttonScan.setText("Connecting");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            mMainBinding.buttonScan.setText("Connect");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_READ:

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_CONNECTION_LOST:
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                   /* editText.setEnabled(false);
                    imageViewPicture.setEnabled(false);
                    width_58mm.setEnabled(false);
                    width_80.setEnabled(false);
                    hexBox.setEnabled(false);
                    sendButton.setEnabled(false);
                    testButton.setEnabled(false);
                    printbmpButton.setEnabled(false);
                    btnClose.setEnabled(false);
                    btn_BMP.setEnabled(false);
                    btn_ChoseCommand.setEnabled(false);
                    btn_prtcodeButton.setEnabled(false);
                    btn_prtsma.setEnabled(false);
                    btn_prttableButton.setEnabled(false);
                    btn_camer.setEnabled(false);
                    btn_scqrcode.setEnabled(false);
                    Simplified.setEnabled(false);
                    Korean.setEnabled(false);
                    big5.setEnabled(false);
                    thai.setEnabled(false);*/
                    break;
                case MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };


    private void Print_Test(){

            String msg = "Congratulations!\n\n";
            String data = "You have sucessfully created communications between your device and our bluetooth printer.\n"
                    +"  the company is a high-tech enterprise which specializes" +
                    " in R&D,manufacturing,marketing of thermal printers and barcode scanners.\n\n";
            SendDataByte(PrinterCommand.POS_Print_Text(msg, CHINESE, 0, 1, 1, 0));
            SendDataByte(PrinterCommand.POS_Print_Text(data, CHINESE, 0, 0, 0, 0));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());

    }

    /*
     *SendDataByte
     */
    private void SendDataByte(byte[] data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:{
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter
                                .getRemoteDevice(address);
                        // Attempt to connect to the device
                        mService.connect(device);
                    }
                }
                break;
            }
            case REQUEST_ENABLE_BT:{
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    KeyListenerInit();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Bluetooth does not start, Quit the program",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }


        }
    }

}

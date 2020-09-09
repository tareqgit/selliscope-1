package com.sokrio.sokrio_classic.pos_sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.sokrio.sokrio_classic.R;
import com.sokrio.sokrio_classic.activity.ActivityCart;
import com.sokrio.sokrio_classic.databinding.ActivityPosBinding;
import com.sokrio.sokrio_classic.pos_sdk.command.sdk.Command;
import com.sokrio.sokrio_classic.pos_sdk.command.sdk.PrinterCommand;
import com.sokrio.sokrio_classic.pos_sdk.model.PosModel;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.allegro.finance.tradukisto.MoneyConverters;
import zj.com.customize.sdk.Other;


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
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_pos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Printer");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        //For starting service by Tareq
        Intent serverIntent = new Intent(PosActivity.this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
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

        mMainBinding.closeButton.setOnClickListener(v -> {
            mService.stop();
            mMainBinding.buttonScan.setText("Search for and connect a Bluetooth printer");
            mMainBinding.buttonScan.setEnabled(true);
        });

        mMainBinding.printButton.setOnClickListener(v -> {
            //   Print_Test();
            Print_Ex();
            //  Print_Table();
        });

        mService = new BluetoothService(this, mHandler);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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


    private void Print_Test() {

        String msg = "Congratulations!\n\n";
        String data = "You have sucessfully created communications between your device and our bluetooth printer.\n"
                + "  the company is a high-tech enterprise which specializes" +
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
            case REQUEST_CONNECT_DEVICE: {
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
            case REQUEST_ENABLE_BT: {
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

    /*****************************************************************************************************/
    /*
     * SendDataString
     */
    private void SendDataString(String data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "Not Conected", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void Print_Ex() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String date = str;

        try {
            //for center
					/*Command.ESC_Align[2] = 0x01;
					SendDataByte(Command.ESC_Align);
					SendDataByte("NIKE Shop\n".getBytes("GBK"));*/

            //right Align
					/*Command.ESC_Align[2] = 0x02;
					SendDataByte(Command.ESC_Align);
					SendDataByte("NIKE Shop\n".getBytes("GBK"));*/

            //left Align
					/*Command.ESC_Align[2] = 0x00;
					SendDataByte(Command.ESC_Align);
					SendDataByte("NIKE Shop\n".getBytes("GBK"));
					*/

            //For big text
					/*Command.GS_ExclamationMark[2] = 0x11;
					SendDataByte(Command.GS_ExclamationMark);
					SendDataByte("NIKE Shop\n".getBytes("GBK"));*/

            //for regular text
					/*Command.GS_ExclamationMark[2] = 0x00;
					SendDataByte(Command.GS_ExclamationMark);
					SendDataByte("NIKE Shop\n".getBytes("GBK"));
*/
//with format
				/*
					Command.GS_ExclamationMark[2] = 0x00;
					//total 32 char
					//SendDataByte(String.format("%-8s%-8s%-8s%-8s","NIKEeeee","Shopeeee","helloooo","55555555") .getBytes("GBK"));
				*/

            //proper format with space
					/*Command.GS_ExclamationMark[2] = 0x00;
					SendDataByte(Command.GS_ExclamationMark);
					SendDataByte(String.format("%-6s %-8s %-7s %-8s","NIKEee","Shopeeee","hellooo","55555555") .getBytes("GBK"));
					*/

            //for space at the end
					/*	SendDataByte("NIKE Shop\n".getBytes("GBK"));
					SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(48));
					SendDataByte(Command.GS_V_m_n);
*/

               byte[] qrcode = PrinterCommand.getBarCommand("Selliscope Receipt Printer! \n", 0, 3, 6);//
                Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                SendDataByte(qrcode);


                SendDataByte(String.format("--------------------------------").getBytes("GBK"));


                SendDataByte(Command.ESC_Align);
                Command.GS_ExclamationMark[2] = 0x11;//by this line starting  bold text
                SendDataByte(Command.GS_ExclamationMark); //by this line starting bold text
                SendDataByte(String.format("%s\n", ActivityCart.sPosModel.getOutletName()).getBytes("GBK"));

                Command.ESC_Align[2] = 0x00;
                SendDataByte(Command.ESC_Align);
                Command.GS_ExclamationMark[2] = 0x00; //by this line closing bold text
                SendDataByte(Command.GS_ExclamationMark); //by this line closing bold text
            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));
                SendDataByte(String.format("%-10s %-1s %-19s","Number",":",ActivityCart.sPosModel.getNumber()).getBytes("GBK"));
               SendDataByte(String.format("%-10s %-1s %-19s","Receipt",":",ActivityCart.sPosModel.getReceipt()).getBytes("GBK"));
                SendDataByte(String.format("%-10s %-1s %-19s","Cashier",":",ActivityCart.sPosModel.getCashier()).getBytes("GBK"));
                SendDataByte(String.format("%-10s %-1s %-19s","Print Time",":",date).getBytes("GBK"));

                Command.ESC_Align[2] = 0x00;
                SendDataByte(Command.ESC_Align);
                SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));


                Command.ESC_Align[2] = 0x00;
                SendDataByte(Command.ESC_Align);

                printString("Customer", ":",ActivityCart.sPosModel.getCustomerName(), 9,1,20, "%-9s %-1s %-20s\n");

               printString("Cust Addr", ":",ActivityCart.sPosModel.getCustomerAddr(), 9,1,20, "%-9s %-1s %-20s\n");

            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));

            printString("Product", "Rate","Qty", "C.Amt", "net",10, 5,3,5, 5, "%-10s %-5s %-3s %-5s %-5s\n");

            for (PosModel.Product product: ActivityCart.sPosModel.getProducts()) {
                printString(product.p_Name ==null ?" ": product.p_Name, String.valueOf(product.p_Rate),String.valueOf(Math.round( product.p_Quantity)), String.valueOf(product.p_C_Amount), String.valueOf(product.p_Net),10, 5,3,5, 5, "%-10s %-5s %-3s %-5s %-5s\n");
            }
        //    printString("Lux 60 mg white", "40","12", "20", "460",10, 4,4,5, 5, "%-10s %-4s %-4s %-5s %-5s\n");
        //    printString("Lux 60 mg pink", "40","12", "20", "460",10, 4,4,5, 5, "%-10s %-4s %-4s %-5s %-5s\n");


            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));

            printString("Total Qty",":", String.valueOf(ActivityCart.sPosModel.getTotal_quantity()),11,1,18,"%-11s %-1s %-18s");
            printString("Inv. Total",":", String.valueOf(ActivityCart.sPosModel.getInvTotal()),11,1,18,"%-11s %-1s %-18s");
            printString("Total C.Amt",":", String.valueOf(ActivityCart.sPosModel.getTotal_C_Amount()),11,1,18,"%-11s %-1s %-18s");
            printString("Net Amt",":", String.valueOf(ActivityCart.sPosModel.getNetAmount()),11,1,18,"%-11s %-1s %-18s");

            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));
            MoneyConverters converter = MoneyConverters.ENGLISH_BANKING_MONEY_VALUE;
            Double amt = ActivityCart.sPosModel.getNetAmount();
            BigDecimal bd = BigDecimal.valueOf(amt);

            String t= converter.asWords(bd);
            printString("In Word", ":",t.substring(0,t.length()-9) +" taka only",7,1,22,"%-7s %-1s %-22s\n");

            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));

         /*   SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x11;//by this line starting  bold text
            SendDataByte(Command.GS_ExclamationMark); //by this line starting bold text*/
            SendDataByte("Payment Info:\n".getBytes("GBK"));
        /*    Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00; //by this line closing bold text
            SendDataByte(Command.GS_ExclamationMark); //by this line closing bold text*/

            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));

            printString("PayType","PayOrder#","Amount",9,12,9,"%-9s %-12s %-9s\n");
            printString(ActivityCart.sPosModel.getPaytype().toString(),ActivityCart.sPosModel.getPayOrder(), String.valueOf(ActivityCart.sPosModel.getAmount()),9,12,9,"%-9s %-12s %-9s\n");

            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));
            Command.ESC_Align[2] = 0x02;
            SendDataByte(Command.ESC_Align);
            SendDataByte(String.format("%-12s %-1s %-17s\n",  "Total Paid",":",ActivityCart.sPosModel.getTotalPaid()).getBytes("GBK"));
            SendDataByte(String.format("%-12s %-1s %-17s\n",  "Due",":",ActivityCart.sPosModel.getDue()).getBytes("GBK"));
            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));
            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            SendDataByte(String.format("%-32s\n","Instruction:").getBytes("GBK"));
            SendDataByte(String.format("--------------------------------\n").getBytes("GBK"));
            SendDataByte("Please preserve this cash memo for any future exchange/claim\n\n".getBytes("GBK"));
            SendDataByte("Above pieces are inc. of VAT.\n\n".getBytes("GBK"));

            //  printString("Address of dhaka in Uttara is defined by tareq", ":","tareq", "123 by A Uttara Dhaka Sector 4 Dhaka 1230", 9,1,5, 14, "%-9s %-1s %-5s %-14s\n");
           // printString("Address of dhaka in Uttara is defined by tareq", "123 by A Uttara Dhaka Sector 4 Dhaka 1230", 9, 22, "%-9s %-22s\n");


            //   SendDataByte("Receipt  S00003333\nCashier：1001\nDate：xxxx-xx-xx\nPrint Time：xxxx-xx-xx  xx:xx:xx\n".getBytes("GBK"));

            //    SendDataByte("Name    Quantity    price  Money\nShoes   10.00       899     8990\nBall    10.00       1599    15990\n".getBytes("GBK"));

            //  SendDataByte("Quantity：             20.00\ntotal：                16889.00\npayment：              17000.00\nKeep the change：      111.00\n".getBytes("GBK"));

            //  SendDataByte("company name：NIKE\nSite：www.xxx.xxx\naddress：ShenzhenxxAreaxxnumber\nphone number：0755-11111111\nHelpline：400-xxx-xxxx\n================================\n".getBytes("GBK"));
                /*Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                Command.GS_ExclamationMark[2] = 0x11;
                SendDataByte(Command.GS_ExclamationMark);
                SendDataByte("Welcome again!\n".getBytes("GBK"));
                Command.ESC_Align[2] = 0x00;
                SendDataByte(Command.ESC_Align);
                Command.GS_ExclamationMark[2] = 0x00;
                SendDataByte(Command.GS_ExclamationMark);

                SendDataByte("(The above information is for testing template, if agree, is purely coincidental!)\n".getBytes("GBK"));
                Command.ESC_Align[2] = 0x02;
                SendDataByte(Command.ESC_Align);
                SendDataString(date);
                SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(48));
                SendDataByte(Command.GS_V_m_n);*/
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    void Print_Table() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String date = str + "\n\n\n\n\n\n";


        Command.ESC_Align[2] = 0x02;
        byte[][] allbuf;
        try {
            allbuf = new byte[][]{
//here %4 means 4 char space
//%4s means 4 char string
//%4d means 4 digit space left alignment
//%-4s means 4 digit space right alignment
                    Command.ESC_Init, Command.ESC_Three,
                    String.format("┏━━┳━━━┳━━┳━━━━┓\n").getBytes("GBK"),
                    String.format("┃%4s┃%-6s┃XXXX┃%-8s┃\n", "A", "Tareq", "Islam").getBytes("GBK"),
                    String.format("┣━━╋━━━╋━━╋━━━━┫\n").getBytes("GBK"),
                    String.format("┃XXXX┃%2d/%-3d┃XXXX┃%-8d┃\n", 1, 222, 555).getBytes("GBK"),
                    String.format("┣━━┻┳━━┻━━┻━━━━┫\n").getBytes("GBK"),
                    String.format("┃XXXXXX┃%-18s┃\n", "【XX】XXXX/XXXXXX").getBytes("GBK"),
                    String.format("┣━━━╋━━┳━━┳━━━━┫\n").getBytes("GBK"),
                    String.format("┃XXXXXX┃%-2s┃XXXX┃%-8s┃\n", "XXXX", "XXXX").getBytes("GBK"),
                    String.format("┗━━━┻━━┻━━┻━━━━┛\n").getBytes("GBK"),
                    Command.ESC_Align, "\n".getBytes("GBK")
            };
            byte[] buf = Other.byteArraysToBytes(allbuf);
            SendDataByte(buf);
            SendDataString(date);
            SendDataByte(Command.GS_V_m_n);
        } catch (UnsupportedEncodingException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }


    void printString(String text1, String text2, int text1Length,int text2Length, String format) throws UnsupportedEncodingException {
        List<String> text1List = subStringListGenerator(text1, text1Length);
        List<String> text2List = subStringListGenerator(text2, text2Length);


        int maxNoOfLine = text1List.size() >= text2List.size() ? text1List.size() : text2List.size();


        Command.ESC_Align[2] = 0x00;
        SendDataByte(Command.ESC_Align);
        for (int i = 0; i < maxNoOfLine; i++) {

            //       System.out.println(i<text1List.size()? text1List.get(i): " ");
            SendDataByte(String.format(format,
                    i < text1List.size() ? text1List.get(i) : " ",
                    i < text2List.size() ? text2List.get(i) : " "

            ).getBytes("GBK"));
        }
    }


    void printString(String text1, String text2, String text3, int text1Length,int text2Length, int text3Length, String format) throws UnsupportedEncodingException {
        List<String> text1List = subStringListGenerator(text1, text1Length);
        List<String> text2List = subStringListGenerator(text2, text2Length);
        List<String> text3List = subStringListGenerator(text3, text3Length);


        int maxNoOfLine = text1List.size() >= text2List.size() ? text1List.size() : text2List.size();
        maxNoOfLine = maxNoOfLine >= text3List.size() ? maxNoOfLine : text3List.size();


        Command.ESC_Align[2] = 0x00;
        SendDataByte(Command.ESC_Align);
        for (int i = 0; i < maxNoOfLine; i++) {


            SendDataByte(String.format(format,
                    i < text1List.size() ? text1List.get(i) : " ",
                    i < text2List.size() ? text2List.get(i) : " ",
                    i < text3List.size() ? text3List.get(i) : " "
            ).getBytes("GBK"));
        }


    /*    if(text.length() >max_length){
            int fraction = (text.length() % max_length) + 1;


            String part_1= text.substring(0,max_length);
            String part_2= text.substring(max_length+1,text.length());
            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            SendDataByte(String.format(format,"Cust Addr", ":", part_1 ).getBytes("GBK"));
            Command.ESC_Align[2] = 0x02;
            SendDataByte(Command.ESC_Align);
            SendDataByte(String.format(format," ", " ", part_2 ).getBytes("GBK"));

        }*/
    }

    void printString(String text1, String text2, String text3, String text4, int text1Length,int text2Length,int text3Length,int text4Length, String format) throws UnsupportedEncodingException {
        List<String> text1List = subStringListGenerator(text1, text1Length);
        List<String> text2List = subStringListGenerator(text2, text2Length);
        List<String> text3List = subStringListGenerator(text3, text3Length);
        List<String> text4List = subStringListGenerator(text4, text4Length);


        int maxNoOfLine = text1List.size() >= text2List.size() ? text1List.size() : text2List.size();
        maxNoOfLine = maxNoOfLine >= text3List.size() ? maxNoOfLine : text3List.size();
        maxNoOfLine = maxNoOfLine >= text4List.size() ? maxNoOfLine : text4List.size();


        Command.ESC_Align[2] = 0x00;
        SendDataByte(Command.ESC_Align);
        for (int i = 0; i < maxNoOfLine; i++) {

            //       System.out.println(i<text1List.size()? text1List.get(i): " ");
            SendDataByte(String.format(format,
                    i < text1List.size() ? text1List.get(i) : " ",
                    i < text2List.size() ? text2List.get(i) : " ",
                    i < text3List.size() ? text3List.get(i) : " ",
                    i < text4List.size() ? text4List.get(i) : " "
            ).getBytes("GBK"));
        }

    }

    void printString(String text1, String text2, String text3, String text4,String text5, int text1Length,int text2Length,int text3Length,int text4Length,int text5Length, String format) throws UnsupportedEncodingException {
        List<String> text1List = subStringListGenerator(text1, text1Length);
        List<String> text2List = subStringListGenerator(text2, text2Length);
        List<String> text3List = subStringListGenerator(text3, text3Length);
        List<String> text4List = subStringListGenerator(text4, text4Length);
        List<String> text5List = subStringListGenerator(text5, text5Length);


        int maxNoOfLine = text1List.size() >= text2List.size() ? text1List.size() : text2List.size();
        maxNoOfLine = maxNoOfLine >= text3List.size() ? maxNoOfLine : text3List.size();
        maxNoOfLine = maxNoOfLine >= text4List.size() ? maxNoOfLine : text4List.size();
        maxNoOfLine = maxNoOfLine >= text5List.size() ? maxNoOfLine : text5List.size();


        Command.ESC_Align[2] = 0x00;
        SendDataByte(Command.ESC_Align);
        for (int i = 0; i < maxNoOfLine; i++) {

            //       System.out.println(i<text1List.size()? text1List.get(i): " ");
        if(i==maxNoOfLine-1){
            SendDataByte(String.format(format+"\n",
                    i < text1List.size() ? text1List.get(i) : " ",
                    i < text2List.size() ? text2List.get(i) : " ",
                    i < text3List.size() ? text3List.get(i) : " ",
                    i < text4List.size() ? text4List.get(i) : " ",
                    i < text5List.size() ? text5List.get(i) : " "
            ).getBytes("GBK"));
        }else {
            SendDataByte(String.format(format,
                    i < text1List.size() ? text1List.get(i) : " ",
                    i < text2List.size() ? text2List.get(i) : " ",
                    i < text3List.size() ? text3List.get(i) : " ",
                    i < text4List.size() ? text4List.get(i) : " ",
                    i < text5List.size() ? text5List.get(i) : " "
            ).getBytes("GBK"));
        }
        }

    }

    List<String> subStringListGenerator(String text, int size) {
        List<String> subStringArray = new ArrayList<>();
        for (int i = 0; i < text.length(); i = i + size) {
            int j = i + size > text.length() ? text.length() : (i + size);
            String s = text.substring(i, j);
            subStringArray.add(s);
        }
        return subStringArray;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

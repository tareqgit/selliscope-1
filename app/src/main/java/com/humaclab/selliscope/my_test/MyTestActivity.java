package com.humaclab.selliscope.my_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.dbmodel.UserVisit;
import com.humaclab.selliscope.utils.DatabaseHandler;

import java.util.List;

public class MyTestActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_test);

        DatabaseHandler databaseHandler= new DatabaseHandler(this);

      List<UserVisit> userVisits= databaseHandler.getUSerVisits();

        mTextView = findViewById(R.id.testText);

        String fullText="";



        for ( UserVisit userVisit:  userVisits  ) {

            fullText += userVisit.getTimeStamp() +"\n ";
        }


        mTextView.setText( fullText);
    }
}

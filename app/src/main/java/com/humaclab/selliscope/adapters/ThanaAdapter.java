package com.humaclab.selliscope.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.model.Districts;
import com.humaclab.selliscope.model.Thanas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dipu_ on 3/25/2017.
 */

public class ThanaAdapter extends ArrayAdapter<Thanas.Successful.Thana> {
    private Activity activity;
    private List<Thanas.Successful.Thana> thanas;
    LayoutInflater layoutInflater;

    public ThanaAdapter(Activity activity, List<Thanas.Successful.Thana>
            thanas) {
        super(activity, R.layout.spinner_item, thanas);
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.thanas = thanas;
    }

    public int getCount() {
        return thanas.size();
    }

    public Thanas.Successful.Thana getItem(int position) {
        return thanas.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = layoutInflater
                .inflate(R.layout.spinner_item, parent, false);
        TextView label = (TextView) row.findViewById(R.id.tv_spinner_item_name);
        label.setTextColor(Color.BLACK);
        label.setText(thanas.get(position).thanaName);
        return row;
    }
}

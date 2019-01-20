package com.humaclab.akij_selliscope.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.model.OutletType.OutletType;

import java.util.List;

/**
 * Created by leon on 3/25/2017.
 */

public class OutletTypeAdapter extends ArrayAdapter<OutletType> {
    LayoutInflater layoutInflater;
    private Activity activity;
    private List<OutletType> outletTypeList;

    public OutletTypeAdapter(Activity activity, List<OutletType>
            outletTypeList) {
        super(activity, R.layout.spinner_item, outletTypeList);
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.outletTypeList = outletTypeList;
    }

    public int getCount() {
        return outletTypeList.size();
    }

    public OutletType getItem(int position) {
        return outletTypeList.get(position);
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
        TextView label = row.findViewById(R.id.tv_spinner_item_name);
        label.setTextColor(Color.BLACK);
        label.setText(outletTypeList.get(position).getName());
        return row;
    }
}
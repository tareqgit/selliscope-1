package com.humaclab.selliscope_malaysia.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.humaclab.selliscope_malaysia.R;
import com.humaclab.selliscope_malaysia.model.OutletTypes;

import java.util.List;

/**
 * Created by dipu_ on 3/25/2017.
 */

public class OutletTypeAdapter extends ArrayAdapter<OutletTypes.Successful.OutletType> {
    LayoutInflater layoutInflater;
    private Activity activity;
    private List<OutletTypes.Successful.OutletType> outletTypes;

    public OutletTypeAdapter(Activity activity, List<OutletTypes.Successful.OutletType>
            outletTypes) {
        super(activity, R.layout.spinner_item, outletTypes);
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.outletTypes = outletTypes;
    }

    public int getCount() {
        return outletTypes.size();
    }

    public OutletTypes.Successful.OutletType getItem(int position) {
        return outletTypes.get(position);
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
        label.setText(outletTypes.get(position).outletTypeName);
        return row;
    }
}
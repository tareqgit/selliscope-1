package com.humaclab.akij_selliscope.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.model.District.District;

import java.util.List;

/**
 * Created by leon on 11/25/2017.
 */

public class DistrictAdapter extends ArrayAdapter<District> {
    LayoutInflater layoutInflater;
    private Activity activity;
    private List<District> districts;

    public DistrictAdapter(Activity activity, List<District> districts) {
        super(activity, R.layout.spinner_item, districts);
        layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.districts = districts;
    }

    public int getCount() {
        return districts.size();
    }

    public District getItem(int position) {
        return districts.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = layoutInflater.inflate(R.layout.spinner_item, parent, false);
        TextView label = row.findViewById(R.id.tv_spinner_item_name);
        label.setTextColor(Color.BLACK);
        label.setText(districts.get(position).getName());
        return row;
    }

}

package com.humaclab.akij_selliscope.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.model.Reason.ReasonResponse;

import java.util.List;

/**
 * Created by leon on 3/25/2017.
 */

public class ReasonTypeAdapter extends ArrayAdapter<ReasonResponse.Result> {
    LayoutInflater layoutInflater;
    private Context context;
    private List<ReasonResponse.Result> reasonResponses;

    public ReasonTypeAdapter(Context context, List<ReasonResponse.Result>
            reasonResponses) {
        super(context, R.layout.spinner_item, reasonResponses);
        layoutInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.reasonResponses = reasonResponses;
    }

    public int getCount() {
        return reasonResponses.size();
    }

    public ReasonResponse.Result getItem(int position) {
        return reasonResponses.get(position);
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
        label.setText(reasonResponses.get(position).getName());
        return row;
    }
}
package com.humaclab.selliscope.adapters;

/**
 * Created by Miaki on 2/27/17.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.humaclab.selliscope.DailyTargetFragment;
import com.humaclab.selliscope.MonthlyTargetFragment;
import com.humaclab.selliscope.WeeklyTargetFragment;

public class TargetFragmentAdapter extends FragmentPagerAdapter {
    Context ctxt = null;

    public TargetFragmentAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt = ctxt;
    }

    @Override
    public int getCount() {
        return (3);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return (DailyTargetFragment.newInstance(position));
            case 1:
                return (WeeklyTargetFragment.newInstance(position));
            case 2:
                return (MonthlyTargetFragment.newInstance(position));
        }
        return null;

    }

    @Override
    public String getPageTitle(int position) {
        //return(TargetFragment.getTitle(ctxt, position));
        switch (position) {
            case 0:
                return "Daily";
            case 1:
                return "Weekly";
            case 2:
                return "Monthly";
            default:
                return "Nothing";
        }
    }
}

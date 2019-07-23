package com.humaclab.lalteer.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.lalteer.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by dipu_ on 2/25/2017.
 * Updated by Leon on 7/14/2017.
 * Updated by Anam on 10/06/2018.
 */

public class TargetFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_target, container, false);
        ViewPager pager = (ViewPager) result.findViewById(R.id.viewpager);

        pager.setAdapter(buildAdapter());

        SmartTabLayout viewPagerTab = (SmartTabLayout) result.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(pager);

        return (result);
    }

    private PagerAdapter buildAdapter() {
        return (new TargetFragmentAdapter(getActivity(), getChildFragmentManager()));
    }

    public class TargetFragmentAdapter extends FragmentPagerAdapter {
        Context ctxt = null;

        public TargetFragmentAdapter(Context ctxt, FragmentManager mgr) {
            super(mgr);
            this.ctxt = ctxt;
        }

        @Override
        public int getCount() {
            return (1);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return (DailyTargetFragment.newInstance(position));
//                case 1:
//                    return (WeeklyTargetFragment.newInstance(position));
//                case 2:
//                    return (MonthlyTargetFragment.newInstance(position));
            }
            return null;

        }

        @Override
        public String getPageTitle(int position) {
            //return(TargetFragment.getTitle(ctxt, position));
            switch (position) {
                case 0:
                    return getString(R.string.myPerformance);
//                case 1:
//                    return "Weekly";
//                case 2:
//                    return "Monthly";
                default:
                    return "Nothing";
            }
        }
    }

}

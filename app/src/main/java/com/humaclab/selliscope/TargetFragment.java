package com.humaclab.selliscope;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.adapters.TargetFragmentAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by dipu_ on 2/25/2017.
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
}

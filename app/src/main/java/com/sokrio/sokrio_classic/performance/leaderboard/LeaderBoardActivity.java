package com.sokrio.sokrio_classic.performance.leaderboard;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.sokrio.sokrio_classic.R;
import com.sokrio.sokrio_classic.performance.leaderboard.fragments.TopCheckerFragment;
import com.sokrio.sokrio_classic.performance.leaderboard.fragments.TopCollectionerFragment;
import com.sokrio.sokrio_classic.performance.leaderboard.fragments.TopSellerFragment;

import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {
    public enum FRAGMENT_TAGS {
        Top_Seller, Top_Collectioner, Top_Checker
    }

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_leaderboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.tabLayout = findViewById(R.id.tabLayout);
        getFragment(TopSellerFragment.newInstance(), FRAGMENT_TAGS.Top_Seller);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getFragment(TopSellerFragment.newInstance(), FRAGMENT_TAGS.Top_Seller);
                        break;
                    case 1:
                        getFragment(TopCollectionerFragment.newInstance(), FRAGMENT_TAGS.Top_Collectioner);
                        break;
                    case 2:
                        getFragment(TopCheckerFragment.newInstance(), FRAGMENT_TAGS.Top_Checker);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void getFragment(Fragment fragment, FRAGMENT_TAGS fragTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragTag) {

            case Top_Checker:
                if (getVisibleFragment() != null && !getVisibleFragment().getTag().equalsIgnoreCase(FRAGMENT_TAGS.Top_Checker.toString()))

                    fragmentTransaction.setCustomAnimations(R.anim.right_to_left, R.anim.right_out_left);
                break;
            case Top_Seller:
                if (getVisibleFragment() != null && !getVisibleFragment().getTag().equalsIgnoreCase(FRAGMENT_TAGS.Top_Seller.toString()))

                    fragmentTransaction.setCustomAnimations(R.anim.left_to_right, R.anim.left_out_right);
                break;
            case Top_Collectioner:
                if (getVisibleFragment() != null) {
                    if (getVisibleFragment().getTag().equalsIgnoreCase(FRAGMENT_TAGS.Top_Seller.toString()))
                        fragmentTransaction.setCustomAnimations(R.anim.right_to_left, R.anim.right_out_left);
                    else if (getVisibleFragment().getTag().equalsIgnoreCase(FRAGMENT_TAGS.Top_Checker.toString()))
                        fragmentTransaction.setCustomAnimations(R.anim.left_to_right, R.anim.left_out_right);
                }

                break;
        }

        fragmentTransaction.replace(R.id.content_fragment, fragment, fragTag.toString())
                .commit();

    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) {

                    return fragment;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_performance_leaderboard_menu, menu);

        return true;

    }

 // public  static boolean sort_icon = true;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }



}

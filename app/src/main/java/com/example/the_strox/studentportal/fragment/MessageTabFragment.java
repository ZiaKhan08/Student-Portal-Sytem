package com.example.the_strox.studentportal.fragment;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.the_strox.studentportal.NewMessageActivity;
import com.example.the_strox.studentportal.NewTopicActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.the_strox.studentportal.R;

import java.util.ArrayList;
import java.util.List;

public class MessageTabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2 ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x =  inflater.inflate(R.layout.tab_layout,container,false);
        Toolbar toolbar = (Toolbar) x.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar supportActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        supportActionBar.setTitle("Messages");
        FloatingActionMenu fabm = (FloatingActionMenu) x.findViewById(R.id.tabfab);
        FloatingActionButton fabgroup =(FloatingActionButton) x.findViewById(R.id.fab_group) ;
        FloatingActionButton fabtopic =(FloatingActionButton) x.findViewById(R.id.fab_topic);
        FloatingActionButton fabmessage =(FloatingActionButton) x.findViewById(R.id.fab_message);
        fabm.setVisibility(View.GONE);
        fabgroup.setVisibility(View.GONE);
        fabtopic.setVisibility(View.GONE);
        fabmessage.setVisibility(View.VISIBLE);

        fabmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, NewMessageActivity.class);
                context.startActivity(intent);
            }
        });

        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        setupViewPager(viewPager);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);

        return x;

    }
    @Override
    public void onStart() {
        super.onStart();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    // Add Fragments to Tab
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new InboxFragment(), "INBOX");
        adapter.addFragment(new SentFragment(), "SENT ITEMS ");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

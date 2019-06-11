package com.luca.genlike;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.luca.genlike.Utils.Utils;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.net.MalformedURLException;

public class DetailActivity extends AppCompatActivity {

    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Bundle bundle;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Users INFOS
        String hUserID = getIntent().getExtras().getString("userID");
        String hUserFirstName = getIntent().getExtras().getString("userFirstName");
        String hUserBirthday = getIntent().getExtras().getString("userBirthday");
        String hUserLatitude = getIntent().getExtras().getString("userLatitude");
        String hUserLongitude= getIntent().getExtras().getString("userLongitude");
        String hUserDescription = getIntent().getExtras().getString("userDescription");
        String hUserProfileImage = getIntent().getExtras().getString("userProfileImage");
        String hUserIdFacebook = getIntent().getExtras().getString("userIDFacebook");
        String mLatitude = getIntent().getExtras().getString("myLatitude");
        String mLongitude = getIntent().getExtras().getString("myLongitude");
        String mUserID = getIntent().getExtras().getString("myUserID");
        bundle = new Bundle();
        bundle.putString("userID", hUserID);
        bundle.putString("userProfileImage", hUserProfileImage);
        bundle.putString("userIdFacebook", hUserIdFacebook);
        bundle.putString("userDescription", hUserDescription);
        bundle.putString("userLatitude", hUserLatitude);
        bundle.putString("userLongitude", hUserLongitude);
        bundle.putString("userBirthday", hUserBirthday);
        bundle.putString("userFirstName", hUserFirstName);
        bundle.putString("myLongitude", mLongitude);
        bundle.putString("myLatitude", mLatitude);
        bundle.putString("myUserID", mUserID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(hUserFirstName);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.changeActivity(DetailActivity.this, MainActivity.class);
            }
        });
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0: {
                    fragment = new FragmentInfoUser();
                    fragment.setArguments(bundle);
                }
                break;
                case 1:{
                    fragment = new FragmentPictureUser();
                    fragment.setArguments(bundle);
                }
                break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}

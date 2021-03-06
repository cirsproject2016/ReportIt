package com.cirs.reportit.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cirs.R;
import com.cirs.gcm.GcmUtils;
import com.cirs.gcm.TokenRegistrationService;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.db.dbhelpers.QueryHelper;
import com.cirs.reportit.ui.adapters.ViewPagerAdapter;
import com.cirs.reportit.ui.fragments.TabBookmarkedFragment;
import com.cirs.reportit.ui.fragments.TabMyReportsFragment;
import com.cirs.reportit.ui.fragments.TabRecentFragment;
import com.cirs.reportit.utils.Constants;
import com.github.clans.fab.FloatingActionButton;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.FileInputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private DrawerLayout drawer;

    private ActionBarDrawerToggle toggle;

    private NavigationView navigationView;

    private ViewPager viewPager;

    private TabLayout tabLayout;

    private View header;

    private CircleImageView imgProfile;

    private TextView txtUsername;

    private Context context = this;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeViews();
        setListeners();
        setProfilePicAndUsername();
        setSupportActionBar(toolbar);
        createTabs();
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        ReportItApplication.fetchCategories();
        ReportItApplication.fetchUpvotedComplaintsByThisUser();

        if (GcmUtils.checkPlayServices(this)) {
            Intent intent = new Intent(this, TokenRegistrationService.class);
            startService(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.nav_recent:
                tabLayout.getTabAt(0).select();
                break;

            case R.id.nav_my_reports:
                tabLayout.getTabAt(1).select();
                break;

            case R.id.nav_bookmarked:
                tabLayout.getTabAt(2).select();
                break;

            case R.id.nav_new_complaint:
                startActivity(new Intent(HomeActivity.this, NewComplaintActivity.class));
                break;

            case R.id.nav_my_profile:
                startActivity(new Intent(HomeActivity.this, ViewProfileActivity.class));
                finish();
                break;

            case R.id.nav_logout:
                GcmUtils.invalidateToken(this);
                Prefs.clear();
                new QueryHelper(this).emptyAllTables();
                Toast.makeText(context, "Successfully logged out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createTabs() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabRecentFragment(), getResources().getString(R.string.home_tab_recent));
        adapter.addFragment(new TabMyReportsFragment(), getResources().getString(R.string.home_tab_my_reports));
        adapter.addFragment(new TabBookmarkedFragment(), getResources().getString(R.string.home_tab_bookmarked));
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_recent);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_my_reports);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_bookmark);
    }

    private void setProfilePicAndUsername() {
        if (Prefs.getBoolean(Constants.SPUD_IS_IMAGE_SET, false)) {
            try {
                FileInputStream fis = context.openFileInput(Constants.FILE_PATH_PROFILE_PIC);
                Bitmap b = BitmapFactory.decodeStream(fis);
                fis.close();
                imgProfile.setImageBitmap(b);
            } catch (Exception e) {
            }
        }
        txtUsername.setText(Prefs.getString(Constants.SPUD_FIRSTNAME, null) + " " + Prefs.getString(Constants.SPUD_LASTNAME, null));
    }

    private void initializeViews() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        imgProfile = (CircleImageView) header.findViewById(R.id.imageView);
        txtUsername = (TextView) header.findViewById(R.id.txt_user_full_name);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void setListeners() {
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setDrawerListener(toggle);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NewComplaintActivity.class));
                finish();
            }
        });
    }
}

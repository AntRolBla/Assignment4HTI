package a2id40.thermostatapp.activities.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.wdullaer.materialdatetimepicker.time.Timepoint;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.activities.base.util.ActivityUtils;
import a2id40.thermostatapp.fragments.Utils.SnackBarHelper;
import a2id40.thermostatapp.fragments.help.HelpFragment;
import a2id40.thermostatapp.fragments.main.MainFragment;
import a2id40.thermostatapp.fragments.settings.SettingsFragment;
import a2id40.thermostatapp.fragments.viewweekly.ViewWeeklyDayFragment;
import a2id40.thermostatapp.fragments.viewweekly.ViewWeeklyFragment;
import a2id40.thermostatapp.fragments.weekly.AddTimeslotFragment;
import a2id40.thermostatapp.fragments.weekly.Models.TimeslotModel;
import a2id40.thermostatapp.fragments.weekly.WeeklyDayFragment;
import a2id40.thermostatapp.fragments.weekly.WeeklyFragment;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by rafael on 6/4/16.
 */

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WeeklyDayFragment mWeeklyDayFragment;

    @BindView(R.id.activity_base_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_base_drawerLayout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.activity_base_navView)
    NavigationView mNavigationView;

    @BindView(R.id.activity_base_loading)
    FrameLayout mLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivity();
        setupDrawer();
        setupInitialView();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActivity() {
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    private void setupDrawer() {
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimary);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.base_drawer_main);
    }

    private void setupInitialView() {
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), MainFragment.newInstance(), R.id.activity_base_container);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.base_drawer_main:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), MainFragment.newInstance(), R.id.activity_base_container);
                break;
            case R.id.base_drawer_viewWeekly:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), ViewWeeklyFragment.newInstance(), R.id.activity_base_container);
                break;
            case R.id.base_drawer_help:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), HelpFragment.newInstance(), R.id.activity_base_container);
                break;
            case R.id.base_drawer_weekly:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), WeeklyFragment.newInstance(), R.id.activity_base_container);
                break;
            case R.id.base_drawer_settings:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), SettingsFragment.newInstance(), R.id.activity_base_container);
                break;
            default:
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showLoadingScreen(){
        mLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen(){
        mLoading.setVisibility(View.GONE);
    }


    public void openWeeklyDay(int day) {
        Bundle weekDayBundle = new Bundle();
        weekDayBundle.putInt(WeeklyDayFragment.WEEK_DAY_BUNDLE, day);

        mWeeklyDayFragment = new WeeklyDayFragment();
        mWeeklyDayFragment.setArguments(weekDayBundle);

        FragmentTransaction weekDayTransaction = getSupportFragmentManager().beginTransaction();
        weekDayTransaction.replace(R.id.activity_base_container, mWeeklyDayFragment);
        weekDayTransaction.addToBackStack("Week Day Transaction");
        weekDayTransaction.commit();
    }

    public void openViewWeeklyDay(int day){
        Bundle weekDayBundle = new Bundle();
        weekDayBundle.putInt(WeeklyDayFragment.WEEK_DAY_BUNDLE, day);

        ViewWeeklyDayFragment viewWeeklyDayFragment = new ViewWeeklyDayFragment();
        viewWeeklyDayFragment.setArguments(weekDayBundle);

        FragmentTransaction weekDayTransaction = getSupportFragmentManager().beginTransaction();
        weekDayTransaction.replace(R.id.activity_base_container, viewWeeklyDayFragment);
        weekDayTransaction.addToBackStack("View Week Day Transaction");
        weekDayTransaction.commit();
    }

    public void openAddTimeslots(int day, int sunLeft, Timepoint[] timepointsInitial, Timepoint[] timepointsEnd){
        Bundle addTimeslotsBundle = new Bundle();
        addTimeslotsBundle.putInt(WeeklyDayFragment.WEEK_DAY_BUNDLE, day);
        addTimeslotsBundle.putInt(AddTimeslotFragment.ADD_TIMESLOT_SUN_LEFT_BUNDLE, sunLeft);
        addTimeslotsBundle.putParcelableArray(AddTimeslotFragment.ADD_TIMESLOT_TIMEPOINTS_INITIAL_BUNDLE, timepointsInitial);
        addTimeslotsBundle.putParcelableArray(AddTimeslotFragment.ADD_TIMESLOT_TIMEPOINTS_END_BUNDLE, timepointsEnd);

        AddTimeslotFragment addTimeslotFragment = new AddTimeslotFragment();
        addTimeslotFragment.setArguments(addTimeslotsBundle);

        FragmentTransaction addTimeslotTransaction = getSupportFragmentManager().beginTransaction();
        addTimeslotTransaction.replace(R.id.activity_base_container, addTimeslotFragment);
        addTimeslotTransaction.addToBackStack("Add Timeslots Transaction");
        addTimeslotTransaction.commit();
    }

    public void addTimeslotToWeeklyDay(TimeslotModel timeslotModel){
        getSupportFragmentManager().popBackStack();
        mWeeklyDayFragment.getTimeslotFromAddTimeslot(timeslotModel);
    }

    public void goBackAddCallErrorSnackBar(){
        getSupportFragmentManager().popBackStack();
        SnackBarHelper.showErrorSnackBar(mDrawerLayout);
    }
}

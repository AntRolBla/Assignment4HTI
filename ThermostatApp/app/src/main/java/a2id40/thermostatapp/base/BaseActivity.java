package a2id40.thermostatapp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import a2id40.thermostatapp.R;
import a2id40.thermostatapp.base.util.ActivityUtils;
import a2id40.thermostatapp.daynight.DayNightFragment;
import a2id40.thermostatapp.help.HelpFragment;
import a2id40.thermostatapp.main.MainFragment;
import a2id40.thermostatapp.vacation.VacationFragment;
import a2id40.thermostatapp.weekly.WeeklyFragment;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by rafael on 6/4/16.
 */

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.activity_base_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_base_drawerLayout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.activity_base_navView)
    NavigationView mNavigationView;

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
            case R.id.base_drawer_daynight:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), DayNightFragment.newInstance(), R.id.activity_base_container);
                break;
            case R.id.base_drawer_help:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), HelpFragment.newInstance(), R.id.activity_base_container);
                break;
            case R.id.base_drawer_vacation:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), VacationFragment.newInstance(), R.id.activity_base_container);
                break;
            case R.id.base_drawer_weekly:
                ActivityUtils.replaceFragment(getSupportFragmentManager(), WeeklyFragment.newInstance(), R.id.activity_base_container);
                break;
            default:
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

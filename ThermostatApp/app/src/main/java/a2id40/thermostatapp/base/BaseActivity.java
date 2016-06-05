package a2id40.thermostatapp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import a2id40.thermostatapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by rafael on 6/4/16.
 */

public class BaseActivity extends AppCompatActivity {

    @BindView(R.id.activity_base_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_base_drawerLayout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivity();
        setupDrawer();

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
    }
}

package a2id40.thermostatapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Variables for texViews and buttons
    TextView tv, tempBig;
    Button button1, button2, button3, button4;
    String s;

    double temperature = 20.1; // Initialized to 20.1
    // When complete, should take the value from the server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // TextViews ID and text value
        tv = new TextView (this);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("Current temperature: " + temperature + "ºC");
        tempBig =  new TextView (this);
        tempBig = (TextView) findViewById(R.id.tempBig);
        tempBig.setText(temperature + "ºC");

        // Adding the functions to the buttons
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperature = round(temperature - 1.0, 1);
                if (!checkTemperatureRange()){  // Means we have exceeded the MIN
                    temperature = 5.0;
                }
                if (temperature < 10.0){ s = "0"; } else { s = ""; }    // Visual effect
                tv.setText("Current temperature: " + s + temperature + "ºC");
                tempBig.setText(s + temperature + "ºC");
            }
        });
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperature = round(temperature - 0.1, 1);
                if (!checkTemperatureRange()){  // Means we have exceeded the MIN
                    temperature = 5.0;
                }
                if (temperature < 10.0){ s = "0"; } else { s = ""; }
                tv.setText("Current temperature: " + s + temperature + "ºC");
                tempBig.setText(s + temperature + "ºC");
            }
        });
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperature = round(temperature + 0.1, 1);
                if (!checkTemperatureRange()){  // Means we have exceeded the MAX
                    temperature = 30.0;
                }
                if (temperature < 10.0){ s = "0"; } else { s = ""; }    // Visual effect
                tv.setText("Current temperature: " + s + temperature + "ºC");
                tempBig.setText(s + temperature + "ºC");
            }
        });
        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperature = round(temperature + 1.0, 1);
                if (!checkTemperatureRange()){  // Means we have exceeded the MAX
                    temperature = 30.0;
                }
                if (temperature < 10.0){ s = "0"; } else { s = ""; }    // Visual effect
                tv.setText("Current temperature: " + s + temperature + "ºC");
                tempBig.setText(s + temperature + "ºC");
            }
        });
    }

    /*
    // Original onClick function

    public void onClick(View v) {
        if (v == button1) { // - 1 degree button
            temperature = temperature - 1.0;
        } else if (v == button2){ // - 0.1 degrees
            temperature = temperature - 0.1;
        } else if (v == button3){ // + 0.1 degrees
            temperature = temperature + 0.1;
        } else if (v == button4){ // + 1 degree
            temperature = temperature + 1.0;
        }
        // Update texts for temperature
        tv.setText("Current temperature: " + temperature + "ºC");
        tempBig.setText(temperature + "ºC");
    }
    */

    // Auxiliary method for rounding temperature to 'places' decimal (used for 1 decimal)
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // Auxiliary method for checking if the temperature is between 5 and 30
    public boolean checkTemperatureRange (){
        boolean result = true;
        if (temperature > 30.0 || temperature < 5.0){
            result = false;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

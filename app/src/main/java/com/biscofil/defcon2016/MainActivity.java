package com.biscofil.defcon2016;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.biscofil.defcon2016.fragments.Credits_fragment;
import com.biscofil.defcon2016.fragments.GuidaCalcoloIndice_fragment;
import com.biscofil.defcon2016.fragments.Guida_fragment;
import com.biscofil.defcon2016.fragments.Licenze_fragment;
import com.biscofil.defcon2016.fragments.Map_fragment;
import com.biscofil.defcon2016.fragments.Menu_fragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    public Fragment setFragmentContent(Class fragmentClass, boolean first) {
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (first) {
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();
            }
            return fragment;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        // Setup drawer view
        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });

        Menu menuNav = nvDrawer.getMenu();
        MenuItem nav_item2 = menuNav.findItem(R.id.menu_map);
        nav_item2.setEnabled(!((EcoMe) getApplication()).offlineMode);

        Fragment f = setFragmentContent(Menu_fragment.class, true);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_web) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.web_url)));
            startActivity(i);
        } else {
            switch (menuItem.getItemId()) {
                case R.id.menu_map:
                    setFragmentContent(Map_fragment.class, false);
                    break;
                case R.id.menu_guida:
                    setFragmentContent(Guida_fragment.class, false);
                    break;
                case R.id.menu_calc:
                    setFragmentContent(GuidaCalcoloIndice_fragment.class, false);
                    break;
                case R.id.menu_license:
                    setFragmentContent(Licenze_fragment.class, false);
                    break;
                case R.id.menu_credits:
                    setFragmentContent(Credits_fragment.class, false);
                    break;
                default:
                    setFragmentContent(Map_fragment.class, false);
            }
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
            mDrawer.closeDrawers();
        }
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

    // `onPostCreate` called when activity start-up is complete after `onStart()`

    // NOTE 1: Make sure to override the method with only a single `Bundle` argument

    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.

    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);

    }

}

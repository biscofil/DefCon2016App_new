package com.biscofil.defcon2016;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import com.biscofil.defcon2016.fragments.Meter_fragment;

import static com.biscofil.defcon2016.lib.Utils.setFragmentContent;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
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


        if (null == savedInstanceState) {
            setFragmentContent(this, Menu_fragment.class, true);
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_web) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.web_url)));
            startActivity(i);
        } else if (!menuItem.isChecked()) {
            switch (menuItem.getItemId()) {
                case R.id.menu_menu:
                    setFragmentContent(this, Menu_fragment.class, false);
                    break;
                case R.id.menu_meter:
                    setFragmentContent(this, Meter_fragment.class, false);
                    break;
                case R.id.menu_map:
                    setFragmentContent(this, Map_fragment.class, false);
                    break;
                case R.id.menu_guida:
                    setFragmentContent(this, Guida_fragment.class, false);
                    break;
                case R.id.menu_calc:
                    setFragmentContent(this, GuidaCalcoloIndice_fragment.class, false);
                    break;
                case R.id.menu_license:
                    setFragmentContent(this, Licenze_fragment.class, false);
                    break;
                case R.id.menu_credits:
                    setFragmentContent(this, Credits_fragment.class, false);
                    break;
                default:
                    setFragmentContent(this, Map_fragment.class, false);
            }
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);

    }

}

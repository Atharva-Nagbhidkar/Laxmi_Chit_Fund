package com.example.bankappp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bankappp.Model.Profile;
import com.example.bankappp.Model.db.ApplicationDB;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;


public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public enum manualNavID {
        DASHBOARD_ID,
        ACCOUNTS_ID
    }

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private SharedPreferences userPreferences;
    private Gson gson;
    private String json;

    private Profile userProfile;


    public void manualNavigation(manualNavID id, Bundle bundle) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (id == manualNavID.DASHBOARD_ID) {
            ft.replace(R.id.flContent, new DashboardFragment()).commit();
            navView.setCheckedItem(R.id.nav_dashboard);
            setTitle("Dashboard");
        } else if (id == manualNavID.ACCOUNTS_ID) {
            AccountOverviewFragment accountOverviewFragment = new AccountOverviewFragment();
            if (bundle != null) {
                accountOverviewFragment.setArguments(bundle);
            }
            ft.replace(R.id.flContent, accountOverviewFragment).commit();
            setTitle("Accounts");
        }

        drawerLayout.closeDrawers();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        drawerLayout = findViewById(R.id.drawer_layout1);

        toolbar = findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
       // drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        userPreferences = this.getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        loadFromDB();

        SharedPreferences.Editor prefsEditor = userPreferences.edit();
        json = gson.toJson(userProfile);
        prefsEditor.putString("LastProfileUsed", json).apply();

        setupHeader();

        manualNavigation(manualNavID.DASHBOARD_ID, null);
    }

    private void setupHeader() {

        View headerView = navView.getHeaderView(0);

        TextView txtName = headerView.findViewById(R.id.txt_name);
        TextView txtUsername = headerView.findViewById(R.id.txt_username);

        String name = userProfile.getFirstName() + " " + userProfile.getLastName();
        txtName.setText(name);

        txtUsername.setText(userProfile.getUsername());
    }

    private void loadFromDB() {
        ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());

        userProfile.setPayeesFromDB(applicationDb.getPayeesFromCurrentProfile(userProfile.getDbId()));
        userProfile.setAccountsFromDB(applicationDb.getAccountsFromCurrentProfile(userProfile.getDbId()));

        for (int iAccount = 0; iAccount < userProfile.getAccounts().size(); iAccount++) {
            userProfile.getAccounts().get(iAccount).setTransactions(applicationDb.getTransactionsFromCurrentAccount(userProfile.getDbId(), userProfile.getAccounts().get(iAccount).getAccountNo()));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void showDrawerButton() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.syncState();
    }

    public void showUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        FragmentManager fragmentManager = getSupportFragmentManager();

        // Handle navigation view item clicks here.
        Class fragmentClass = null;
        String title = item.getTitle().toString();

        switch(item.getItemId()) {
            case R.id.nav_dashboard:
                fragmentClass = DashboardFragment.class;
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginAcitviy.class);
                startActivity(intent);
                finish();
                break;
            default:
                fragmentClass = DashboardFragment.class;
        }

        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            item.setChecked(true);
            setTitle(title);
            drawerLayout.closeDrawers();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


}

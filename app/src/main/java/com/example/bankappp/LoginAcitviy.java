package com.example.bankappp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class LoginAcitviy extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitviy);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_frm_content, new LoginFragment()).commit();
    }

    public void removeUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void showUpButton() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public void login() {
        Intent intent = new Intent(getApplicationContext(), DrawerActivity.class);
        startActivity(intent);
        finish();
    }

    public void profileCreated(Bundle bundle) {

        Toast.makeText(this, R.string.account_success, Toast.LENGTH_SHORT).show();

        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.login_frm_content, loginFragment).commit();
    }
}

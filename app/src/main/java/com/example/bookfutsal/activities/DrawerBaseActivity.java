package com.example.bookfutsal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.bookfutsal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    MenuItem menuLogin, menuProfile, menuLogout;
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // check si utilisateur est connecté
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            disconnected();
        } else {
            connected();
        }
    }
    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar =  drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        menuLogin = navigationView.getMenu().findItem(R.id.menuLogin);
        menuLogout = navigationView.getMenu().findItem(R.id.menuLogout);
        menuProfile = navigationView.getMenu().findItem(R.id.menuProfile);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()){
            case R.id.menuMyReservations:
                startActivity(new Intent(this, ReservationActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.menuHome:
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.menuLogin:
                startActivity(new Intent(this, SignInActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.menuLogout:
                mAuth.signOut();
                disconnected();
                showToast("disconnected");
                finish();
                break;
        }
        return false;
    }
    public void connected() {
        menuProfile.setVisible(true);
        menuLogin.setVisible(false);
        menuLogout.setVisible(true);
    }

    public void disconnected() {
        menuLogin.setVisible(true);
        menuLogout.setVisible(false);
        menuProfile.setVisible(false);
    }


    private void showToast(String message) {
        Toast.makeText(this,  message, Toast.LENGTH_SHORT).show();
    }

    protected void allocateActivityTitle(String title){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }
}
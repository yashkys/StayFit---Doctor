package com.example.stayfit_doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.stayfit_doctor.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        selectedFragment = new HomeFragment();
        binding.bottomNavigationBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home :
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_browse :
                    selectedFragment = new BrowseFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment  = new ProfileFragment();
                    break;
                case R.id.nav_feed:
                    selectedFragment  = new FeedFragment();
                    break;
                default:
                    selectedFragment = new HomeFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, (selectedFragment != null) ? selectedFragment : new HomeFragment())
                    .commit();

            return true;
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, (selectedFragment != null) ? selectedFragment : new HomeFragment())
                .commit();


    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home :
                selectedFragment = new HomeFragment();
                break;
            case R.id.nav_browse :
                selectedFragment = new BrowseFragment();
                break;
            case R.id.nav_profile:
                selectedFragment  = new ProfileFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, (selectedFragment != null) ? selectedFragment : new HomeFragment())
                .commit();

        return true;
    }

//        @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//    }


}
package thick2.truongthimylinh.mywallet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // mặc định Home
        loadFragment(new HomeFragment());

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();

            } else if (id == R.id.nav_chart) {
                selectedFragment = new ChartFragment();

            } else if (id == R.id.nav_add) {
                selectedFragment = new AddFragment();

            } else if (id == R.id.nav_report) {
                selectedFragment = new ReportFragment();

            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}
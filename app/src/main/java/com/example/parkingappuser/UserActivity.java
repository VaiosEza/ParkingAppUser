package com.example.parkingappuser;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stripe.android.PaymentConfiguration;


public class UserActivity extends AppCompatActivity {
    private int lastSelectedItemId = R.id.userPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        PaymentConfiguration.init(
                getApplicationContext(),
                getString(R.string.Stripe_Public_Key)
        );

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int newSelectedItemId = item.getItemId();

            // Αποτρέπουμε την αλλαγή του ίδιου fragment
            if(lastSelectedItemId == newSelectedItemId){
                return false;
            }

            boolean isGoingForward = getItemOrder(newSelectedItemId) > getItemOrder(lastSelectedItemId);

            if (newSelectedItemId == R.id.userPage) {
                showActivityContent();
            } else {
                if (newSelectedItemId == R.id.walletPage) {
                    showFragment(WalletFragment.newInstance(), isGoingForward);
                } else if (newSelectedItemId == R.id.parkingPage) {
                    showFragment(ParkingFragment.newInstance(), isGoingForward);
                }
            }
            lastSelectedItemId = newSelectedItemId;
            return true;
        });
    }

    private int getItemOrder(int itemId) {
        if (itemId == R.id.userPage) return 0;
        if (itemId == R.id.walletPage) return 1;
        if (itemId == R.id.parkingPage) return 2;
        return -1;
    }

    private void showFragment(Fragment fragment, boolean isForward) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isForward) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        transaction.replace(R.id.fragment_container, fragment).commit();
    }

    private void showActivityContent() {

        Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        //Χωρίς αυτόν τον έλεγχο εαν επιλέξουμε ξανά το user page χωρίς να έχουμε επιλέξει προηγουμένως κάποιο
        //fragment , θα υπάρξει σφάλμα.
        if (oldFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).remove(oldFragment).commit();

        }
    }
}
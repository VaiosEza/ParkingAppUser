package com.example.parkingappuser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class WalletFragment extends Fragment {

    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;

    // UI Elements
    private RadioGroup radioGroup;
    private Button buttonDebitCard;
    private Button buttonPaySafePayPal;
    private Integer selectedAmount = null; // Το επιλεγμένο ποσό σε ΣΕΝΤΣ

    // Αυτό πρέπει να το πάρεις από την Activity/SharedPreferences κτλ.
    // Είναι το email του συνδεδεμένου χρήστη.
    private String currentUserEmail = "user1@gmail.com"; // <<-- ΠΑΡΑΔΕΙΓΜΑ! ΑΛΛΑΞΕ ΤΟ

    public WalletFragment() {
        // Required empty public constructor
    }

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Αρχικοποίηση του PaymentSheet. Γίνεται μία φορά.
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Σύνδεση των UI elements με τον κώδικα
        radioGroup = view.findViewById(R.id.radio_group);
        buttonDebitCard = view.findViewById(R.id.buttonDebidCard);
        buttonPaySafePayPal = view.findViewById(R.id.buttonPaySafe_PayPal);

        // 2. Αρχικά, τα κουμπιά πληρωμής είναι απενεργοποιημένα
        setPaymentButtonsEnabled(false);

        // 3. Προσθήκη listener στο RadioGroup για να ξέρουμε πότε ο χρήστης επιλέγει ποσό
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.ten_euro) {
                selectedAmount = 1000; // 10€ σε σεντς
            } else if (checkedId == R.id.twenty_euro) {
                selectedAmount = 2000; // 20€ σε σεντς
            } else if (checkedId == R.id.fifty_euro) {
                selectedAmount = 5000; // 50€ σε σεντς
            } else {
                selectedAmount = null;
            }

            // Ενεργοποίησε τα κουμπιά ΜΟΝΟ αν έχει επιλεγεί ποσό
            setPaymentButtonsEnabled(selectedAmount != null);
        });

        // 4. Προσθήκη listener στο κουμπί πληρωμής
        View.OnClickListener paymentClickListener = v -> {
            if (selectedAmount != null) {
                setPaymentButtonsEnabled(false); // Απενεργοποίησε τα κουμπιά για να μην πατηθούν ξανά
                fetchPaymentIntent();
            }
        };

        buttonDebitCard.setOnClickListener(paymentClickListener);
        buttonPaySafePayPal.setOnClickListener(paymentClickListener); // Το ίδιο sheet χειρίζεται όλες τις μεθόδους
    }

    private void fetchPaymentIntent() {
        // Εδώ γίνεται η κλήση δικτύου στο backend για να πάρουμε το clientSecret
        // Χρησιμοποιούμε background thread για να μην "παγώσει" το UI
        new Thread(() -> {
            try {
                // Υποθέτουμε ότι έχεις μια κλάση ApiHelper με τη μέθοδο createPaymentIntent
                ApiHelper apiHelper = new ApiHelper();
                String url = getString(R.string.CreatePaymentIntent_URL);

                // Η μέθοδος επιστρέφει το clientSecret ή null αν αποτύχει
                paymentIntentClientSecret = apiHelper.createPaymentIntent(url, currentUserEmail, selectedAmount);

                // Η ενημέρωση του UI πρέπει να γίνει στον main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (paymentIntentClientSecret != null) {
                            // Αν όλα πήγαν καλά, εμφάνισε το παράθυρο του Stripe
                            presentPaymentSheet();
                        } else {
                            // Αν υπήρξε σφάλμα, ενημέρωσε τον χρήστη και ενεργοποίησε ξανά τα κουμπιά
                            Toast.makeText(getContext(), "Σφάλμα δικτύου. Προσπαθήστε ξανά.", Toast.LENGTH_SHORT).show();
                            setPaymentButtonsEnabled(true);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("WalletFragment", "Error fetching payment intent", e);
            }
        }).start();
    }

    private void presentPaymentSheet() {
        if (paymentIntentClientSecret != null) {
            paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret);
        }
    }

    // Αυτή η μέθοδος καλείται αυτόματα όταν η πληρωμή ολοκληρωθεί, ακυρωθεί ή αποτύχει
    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Η πληρωμή πέτυχε!
            Toast.makeText(getContext(), "Η πληρωμή ολοκληρώθηκε!", Toast.LENGTH_LONG).show();
            // Τώρα καλούμε το backend για να ενημερώσει το υπόλοιπο στη βάση
            updateUserBalanceOnServer();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(getContext(), "Η πληρωμή ακυρώθηκε.", Toast.LENGTH_SHORT).show();
            setPaymentButtonsEnabled(true); // Ενεργοποίησε ξανά τα κουμπιά
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed failedResult = (PaymentSheetResult.Failed) paymentSheetResult;
            Toast.makeText(getContext(), "Η πληρωμή απέτυχε: " + failedResult.getError().getLocalizedMessage(), Toast.LENGTH_LONG).show();
            setPaymentButtonsEnabled(true); // Ενεργοποίησε ξανά τα κουμπιά
        }
    }

    private void updateUserBalanceOnServer() {
        if (selectedAmount == null) return;

        // Μετατροπή από σεντς σε ευρώ για το script μας
        double amountInEuros = selectedAmount / 100.0;

        new Thread(() -> {
            try {
                ApiHelper apiHelper = new ApiHelper();
                String url = getString(R.string.UpdateBalance_URL);

                // Η μέθοδος επιστρέφει το νέο υπόλοιπο ή null
                Double newBalance = apiHelper.updateUserBalance(url, currentUserEmail, amountInEuros);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (newBalance != null) {
                            Toast.makeText(getContext(), "Το υπόλοιπο ενημερώθηκε: "+newBalance, Toast.LENGTH_LONG).show();
                            // Ενημέρωσε το TextView με το νέο υπόλοιπο
//                            tvCurrentBalance.setText(String.format("Υπόλοιπο: %.2f€", newBalance));
                        } else {
                            Toast.makeText(getContext(), "Σφάλμα κατά την ενημέρωση του υπολοίπου.", Toast.LENGTH_LONG).show();
                        }
                        // Σε κάθε περίπτωση, ενεργοποίησε ξανά τα κουμπιά
                        setPaymentButtonsEnabled(true);
                        radioGroup.clearCheck(); // Καθάρισε την επιλογή
                    });
                }
            } catch (Exception e) {
                Log.e("WalletFragment", "Error updating user balance", e);
            }
        }).start();
    }

    // Βοηθητική μέθοδος για να (απ)ενεργοποιούμε και τα δύο κουμπιά μαζί
    private void setPaymentButtonsEnabled(boolean isEnabled) {
        buttonDebitCard.setEnabled(isEnabled);
        buttonPaySafePayPal.setEnabled(isEnabled);
    }
}
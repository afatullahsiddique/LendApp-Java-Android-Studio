package com.example.lendapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

public class ApplyLoan extends AppCompatActivity {

    private static final String TAG = "ApplyLoan2";

    private TextView loanAmountTextView, amountReceivedTextView, totalFeeTextView, totalAmountTextView, repaidAmountTextView;
    private EditText paymentAccountNumberEditText, confirmPaymentAccountNumberEditText;
    private Spinner paymentMethodSpinner;
    private CheckBox agreementCheckbox;
    private Button confirmLoanButton;
    private ImageView backArrow;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @SuppressLint({"DefaultLocale", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.apply_loan2);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            // Handle unauthenticated state, e.g., redirect to login
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return;
        }

        databaseReference = firebaseDatabase.getReference("users").child(currentUser.getUid());

        confirmLoanButton = findViewById(R.id.confirmLoan_Button);
        loanAmountTextView = findViewById(R.id.loanAmount);
        amountReceivedTextView = findViewById(R.id.amountReceived);
        totalFeeTextView = findViewById(R.id.totalFee);
        totalAmountTextView = findViewById(R.id.totalAmount);
        repaidAmountTextView = findViewById(R.id.repaidAmount);
        paymentAccountNumberEditText = findViewById(R.id.paymentAccountNumber);
        confirmPaymentAccountNumberEditText = findViewById(R.id.confirmPaymentAccountNumber);
        paymentMethodSpinner = findViewById(R.id.paymentMethod);
        agreementCheckbox = findViewById(R.id.agreementCheckbox);
        backArrow = findViewById(R.id.back_arrow);

        // Set up the spinner with payment methods
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        // Retrieve the loan amount passed from ApplyLoan1 activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("loanAmount")) {
            int loanAmount = intent.getIntExtra("loanAmount", 10000);
            loanAmountTextView.setText(String.format("৳%,d", loanAmount));
        }

        // Assuming values for amountReceived and totalFee are set elsewhere in your code
        // Set dummy values for demonstration purposes
        amountReceivedTextView.setText("10000");
        totalFeeTextView.setText("1000");

        // Calculate and display totalAmount and repaidAmount
        int amountReceived = Integer.parseInt(amountReceivedTextView.getText().toString().replace("৳", "").trim());
        int totalFee = Integer.parseInt(totalFeeTextView.getText().toString().replace("৳", "").trim());
        int totalAmount = amountReceived + totalFee;
        totalAmountTextView.setText(String.format("৳%,d", totalAmount));
        repaidAmountTextView.setText(String.format("৳%,d", totalAmount));

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });

        confirmLoanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Confirm loan button clicked");
                if (validateInputs()) {
                    Log.d(TAG, "Inputs validated successfully");
                    // Store data in Firebase
                    storeLoanDataInFirebase(amountReceived, totalAmount, paymentMethodSpinner.getSelectedItem().toString(), paymentAccountNumberEditText.getText().toString());
                } else {
                    Log.d(TAG, "Input validation failed");
                }
            }
        });
    }

    private boolean validateInputs() {
        String paymentAccountNumber = paymentAccountNumberEditText.getText().toString().trim();
        String confirmPaymentAccountNumber = confirmPaymentAccountNumberEditText.getText().toString().trim();
        String paymentMethod = paymentMethodSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(paymentMethod) || paymentMethod.equals("Select Payment Method")) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(paymentAccountNumber)) {
            paymentAccountNumberEditText.setError("Payment account number is required");
            return false;
        }
        if (TextUtils.isEmpty(confirmPaymentAccountNumber)) {
            confirmPaymentAccountNumberEditText.setError("Confirm your payment account number");
            return false;
        }
        if (!paymentAccountNumber.equals(confirmPaymentAccountNumber)) {
            confirmPaymentAccountNumberEditText.setError("Payment account numbers do not match");
            return false;
        }
        if (!agreementCheckbox.isChecked()) {
            Toast.makeText(this, "You must agree to the loan service and privacy agreement", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void storeLoanDataInFirebase(int amountReceived, int totalAmount, String paymentMethod, String paymentAccountNumber) {
        LoanDetails loanDetails = new LoanDetails(amountReceived, totalAmount, paymentMethod, paymentAccountNumber);
        Log.d(TAG, "Storing loan data to Firebase: " + loanDetails.toString());
        databaseReference.child("loanDetails").setValue(loanDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    Log.e(TAG, "Failed to store data: " + error.getMessage());
                    Toast.makeText(ApplyLoan.this, "Failed to store data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Loan confirmed successfully");
                    Toast.makeText(ApplyLoan.this, "Loan confirmed successfully", Toast.LENGTH_SHORT).show();
                    // You can add further actions here, such as navigating to another activity
                }
            }
        });
    }

    public static class LoanDetails {
        public int amountReceived;
        public int totalAmount;
        public String paymentMethod;
        public String paymentAccountNumber;

        public LoanDetails(int amountReceived, int totalAmount, String paymentMethod, String paymentAccountNumber) {
            this.amountReceived = amountReceived;
            this.totalAmount = totalAmount;
            this.paymentMethod = paymentMethod;
            this.paymentAccountNumber = paymentAccountNumber;
        }

        @Override
        public String toString() {
            return "LoanDetails{" +
                    "amountReceived=" + amountReceived +
                    ", totalAmount=" + totalAmount +
                    ", paymentMethod='" + paymentMethod + '\'' +
                    ", paymentAccountNumber='" + paymentAccountNumber + '\'' +
                    '}';
        }
    }
}

package com.rvkt.swift_upi;

import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.Manifest;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Map;
import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;


public class SwiftUpiActivity extends Activity {

    static final int uniqueReqCode = 512079;

    SwiftUpiPlugin swiftUpiPlugin = new SwiftUpiPlugin();
    // Declare scaleAnimation here at the class level
    private Animation scaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swift_upi);

        String app = getIntent().getStringExtra("app");
        String receiverUpiId = getIntent().getStringExtra("receiverUpiId");
        String receiverName = getIntent().getStringExtra("receiverName");
        String transactionRefId = getIntent().getStringExtra("transactionRefId");
        String transactionNote = getIntent().getStringExtra("transactionNote");
        String amount = getIntent().getStringExtra("amount");
        String currency = getIntent().getStringExtra("currency");
        String url = getIntent().getStringExtra("url");
        String merchantId = getIntent().getStringExtra("merchantId");

        // Find the TextView elements
//        TextView textApp = findViewById(R.id.text_app);
//        TextView textReceiverUpiId = findViewById(R.id.text_receiver_upi_id);
//        TextView textReceiverName = findViewById(R.id.text_receiver_name);
//        TextView textTransactionRefId = findViewById(R.id.text_transaction_ref_id);
//        TextView textTransactionNote = findViewById(R.id.text_transaction_note);
//        TextView textAmount = findViewById(R.id.text_amount);
//        TextView textCurrency = findViewById(R.id.text_currency);
//        TextView textUrl = findViewById(R.id.text_url);
//        TextView textMerchantId = findViewById(R.id.text_merchant_id);

        // Set the text in the TextViews
//        if (app != null) {
//            textApp.setText("App: " + app);
//        } else {
//            textApp.setText("App: com.google.android.apps.nbu.paisa.user");
//        }
//
//
//        if (receiverUpiId != null) {
//            textReceiverUpiId.setText("Receiver UPI ID: " + receiverUpiId);
//        } else {
//            textReceiverUpiId.setText("Receiver UPI ID: 8860733786@ybl");
//        }
//
//        if (receiverName != null) {
//            textReceiverName.setText("Receiver Name: " + receiverName);
//        } else {
//            textReceiverName.setText("Receiver Name: Ravi Kant");
//        }
//
//        if (transactionRefId != null) {
//            textTransactionRefId.setText("Transaction Ref ID: " + transactionRefId);
//        } else {
//            textTransactionRefId.setText("Transaction Ref ID: Not Provided");
//        }
//
//        if (transactionNote != null) {
//            textTransactionNote.setText("Transaction Note: " + transactionNote);
//        } else {
//            textTransactionNote.setText("Transaction Note: No Note");
//        }
//
//        if (amount != null) {
//            textAmount.setText("Amount: " + amount);
//        } else {
//            textAmount.setText("Amount: 10.00");
//        }
//
//        if (currency != null) {
//            textCurrency.setText("Currency: " + currency);
//        } else {
//            textCurrency.setText("Currency: INR");
//        }
//
//        if (url != null) {
//            textUrl.setText("URL: " + url);
//        } else {
//            textUrl.setText("URL: Not Available");
//        }
//
//        if (merchantId != null) {
//            textMerchantId.setText("Merchant ID: " + merchantId);
//        } else {
//            textMerchantId.setText("Merchant ID: Not Assigned");
//        }



        TextView transactionStatus = findViewById(R.id.transaction_status);
//        Button closeButton = findViewById(R.id.close_button);
        Button openPhonepeBtn = findViewById(R.id.open_phonepe);
        Button openGpayBtn = findViewById(R.id.open_gpay);
        Button openPaytmBtn = findViewById(R.id.open_paytm);


        // Initialize the scale animation
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_button);

        // Set a message (you can pass data through Intent extras)
        transactionStatus.setText("Transaction Successful");

        // Close the activity when the button is clicked
//        closeButton.setOnClickListener(v -> finish());

        // Set OnClickListeners for UPI app buttons
        setUpUpiButton(openPhonepeBtn, app, receiverUpiId, receiverName, transactionRefId, transactionNote, amount, currency, url, merchantId);
        setUpUpiButton(openGpayBtn, app, receiverUpiId, receiverName, transactionRefId, transactionNote, amount, currency, url, merchantId);
        setUpUpiButton(openPaytmBtn, app, receiverUpiId, receiverName, transactionRefId, transactionNote, amount, currency, url, merchantId);
    }

    // Helper method to set up the button listeners and call the appropriate UPI app
    private void setUpUpiButton(Button button, String packageId, String receiverUpiId,
                                String receiverName,
                                String transactionRefId,
                                String transactionNote,
                                String amount,
                                String currency,
                                String url,
                                String merchantId) {
        button.setOnClickListener(v -> {
            // Apply scale animation to the clicked button
            button.startAnimation(scaleAnimation);

            // Collect parameters from the intent or defaults
//            String receiverUpiId = getIntent().getStringExtra("receiverUpiId") != null
//                    ? getIntent().getStringExtra("receiverUpiId") : "8860733786@ybl";
//            String receiverName = getIntent().getStringExtra("receiverName") != null
//                    ? getIntent().getStringExtra("receiverName") : "Ravi Kant";
//            String transactionRefId = getIntent().getStringExtra("transactionRefId");
//            String transactionNote = getIntent().getStringExtra("transactionNote") != null
//                    ? getIntent().getStringExtra("transactionNote") : "No Note";
//            String amount = getIntent().getStringExtra("amount") != null
//                    ? getIntent().getStringExtra("amount") : "10.00";
//            String currency = getIntent().getStringExtra("currency") != null
//                    ? getIntent().getStringExtra("currency") : "INR";
//            String url = getIntent().getStringExtra("url");
//            String merchantId = getIntent().getStringExtra("merchantId");

            // Launch the UPI app through the plugin with dynamic values
            startTxn(packageId, receiverUpiId, receiverName, transactionRefId, transactionNote, amount, currency, url, merchantId);
        });
    }


    // Method to launch UPI app via the plugin
    private void launchUpiApp(String packageId) {
        swiftUpiPlugin.launchUpiApp(packageId, new MethodChannel.Result() {
            @Override
            public void success(Object result) {
                Toast.makeText(SwiftUpiActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(String errorCode, String errorMessage, Object errorDetails) {
                Toast.makeText(SwiftUpiActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notImplemented() {
                Toast.makeText(SwiftUpiActivity.this, "Not implemented", Toast.LENGTH_SHORT).show();
            }
        }, this);
    }

    // Method to launch UPI app via the plugin
    private void startTxn(String packageId,
                          String receiverUpiId,
                          String receiverName,
                          String transactionRefId,
                          String transactionNote,
                          String amount,
                          String currency,
                          String url,
                          String merchantId) {
        swiftUpiPlugin.startTxn(
                this,
                packageId,
                receiverUpiId,
                receiverName,
                transactionRefId,
                transactionNote,
                amount,
                currency,
                url,
                merchantId,
                new MethodChannel.Result() { // Result callback
                    @Override
                    public void success(Object result) {
                        Toast.makeText(SwiftUpiActivity.this, "Transaction Successful: " + result, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void error(String errorCode, String errorMessage, Object errorDetails) {
                        Toast.makeText(SwiftUpiActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
//                        SwiftUpiActivity.this.finish();
                    }

                    @Override
                    public void notImplemented() {
                        Toast.makeText(SwiftUpiActivity.this, "Not implemented", Toast.LENGTH_SHORT).show();
//                        SwiftUpiActivity.this.finish();
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check the requestCode to ensure it matches the one used for launching the UPI apps
        if (requestCode == uniqueReqCode) { // Replace 1234 with your requestCode if any
            if (resultCode == RESULT_OK) {
                // Handle the UPI transaction result if successful
                String transactionResponse = data != null ? data.getStringExtra("response") : null;
                if (transactionResponse != null) {
                    // Display the successful transaction response
                    Toast.makeText(SwiftUpiActivity.this, "Transaction Successful: " + transactionResponse, Toast.LENGTH_LONG).show();
                    SwiftUpiActivity.this.finish();
                } else {
                    // Handle null response if no transaction response is received
                    Toast.makeText(SwiftUpiActivity.this, "No response from UPI app.", Toast.LENGTH_SHORT).show();
                    SwiftUpiActivity.this.finish();
                }
            } else {
                // Handle the failure case
                String error = data != null ? data.getStringExtra("error") : "Unknown error";
                Toast.makeText(SwiftUpiActivity.this, "Transaction Failed: " + error, Toast.LENGTH_SHORT).show();
                SwiftUpiActivity.this.finish();
            }
        }
    }


}
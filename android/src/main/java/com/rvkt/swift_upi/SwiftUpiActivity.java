package com.rvkt.swift_upi;

import android.app.Activity;
import android.widget.Button;
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

    private FlutterEngine flutterEngine;
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
        TextView textApp = findViewById(R.id.text_app);
        TextView textReceiverUpiId = findViewById(R.id.text_receiver_upi_id);
        TextView textReceiverName = findViewById(R.id.text_receiver_name);
        TextView textTransactionRefId = findViewById(R.id.text_transaction_ref_id);
        TextView textTransactionNote = findViewById(R.id.text_transaction_note);
        TextView textAmount = findViewById(R.id.text_amount);
        TextView textCurrency = findViewById(R.id.text_currency);
        TextView textUrl = findViewById(R.id.text_url);
        TextView textMerchantId = findViewById(R.id.text_merchant_id);

        // Set the text in the TextViews
        if (app != null) {
            textApp.setText("App: " + app);
        }

        if (receiverUpiId != null) {
            textReceiverUpiId.setText("Receiver UPI ID: " + receiverUpiId);
        }

        if (receiverName != null) {
            textReceiverName.setText("Receiver Name: " + receiverName);
        }

        if (transactionRefId != null) {
            textTransactionRefId.setText("Transaction Ref ID: " + transactionRefId);
        }

        if (transactionNote != null) {
            textTransactionNote.setText("Transaction Note: " + transactionNote);
        }

        if (amount != null) {
            textAmount.setText("Amount: " + amount);
        }

        if (currency != null) {
            textCurrency.setText("Currency: " + currency);
        }

        if (url != null) {
            textUrl.setText("URL: " + url);
        }

        if (merchantId != null) {
            textMerchantId.setText("Merchant ID: " + merchantId);
        }


        TextView transactionStatus = findViewById(R.id.transaction_status);
        Button closeButton = findViewById(R.id.close_button);
        Button openPhonepeBtn = findViewById(R.id.open_phonepe);
        Button openGpayBtn = findViewById(R.id.open_gpay);
        Button openPaytmBtn = findViewById(R.id.open_paytm);


        // Initialize the scale animation
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_button);

        // Set a message (you can pass data through Intent extras)
        transactionStatus.setText("Transaction Successful");

        // Close the activity when the button is clicked
        closeButton.setOnClickListener(v -> finish());

        // Set OnClickListeners for UPI app buttons
        setUpUpiButton(openPhonepeBtn, "com.phonepe.app");
        setUpUpiButton(openGpayBtn, "com.google.android.apps.nbu.paisa.user");
        setUpUpiButton(openPaytmBtn, "net.one97.paytm");
    }

    // Helper method to set up the button listeners and call the appropriate UPI app
    private void setUpUpiButton(Button button, String packageId) {
        button.setOnClickListener(v -> {
            // Apply scale animation to the clicked button
            button.startAnimation(scaleAnimation);

            // Launch the UPI app through the plugin
            launchUpiApp(packageId);
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

}
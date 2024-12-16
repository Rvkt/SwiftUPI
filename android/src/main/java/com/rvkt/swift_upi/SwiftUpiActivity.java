package com.rvkt.swift_upi;

import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.Manifest;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.view.animation.Animation;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.view.animation.AnimationUtils;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import android.widget.LinearLayout;
import android.widget.GridLayout;
import android.animation.ObjectAnimator;


public class SwiftUpiActivity extends Activity {

    static final int uniqueReqCode = 512079;

    SwiftUpiPlugin swiftUpiPlugin = new SwiftUpiPlugin();
    // Declare scaleAnimation here at the class level
    private Animation scaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swift_upi);

        // Link buttons from layout
        Button btnPaytm = findViewById(R.id.btnPaytm);
        Button btnPhonePe = findViewById(R.id.btnPhonePe);
        Button btnGooglePay = findViewById(R.id.btnGooglePay);
        Button btnAmazonPay = findViewById(R.id.btnAmazonPay);
        Button btnAxis = findViewById(R.id.btnAxis);
        Button btnBob = findViewById(R.id.btnBob);
        Button btnBhimUpi = findViewById(R.id.btnBhimUpi);

        Button btnCentralBank = findViewById(R.id.btnCentralBank);
        Button btnBOI = findViewById(R.id.btnBOI);
        Button btnCSB = findViewById(R.id.btnCSB);
        Button btnCUB = findViewById(R.id.btnCUB);
        Button btnDBS = findViewById(R.id.btnDBS);
        Button btnEquitas = findViewById(R.id.btnEquitas);


        // List of buttons and UPI packages
        List<Button> buttons = Arrays.asList(btnPaytm, btnPhonePe, btnAmazonPay, btnGooglePay, btnAxis, btnBob, btnBhimUpi, btnCentralBank, btnBOI, btnCSB, btnCUB, btnDBS, btnEquitas);
        List<String> upiPackages = Arrays.asList(
                "net.one97.paytm",
                "com.phonepe.app",
                "in.amazon.mShop.android.shopping",
                "com.google.android.apps.nbu.paisa.user",
                "com.upi.axispay",
                "com.bankofbaroda.upi",
                "in.org.npci.upiapp",
                "com.infrasofttech.centralbankupi",
                "com.boi.ua.android",
                "com.lcode.csbupi",
                "com.cub.wallet.gui",
                "com.dbs.in.digitalbank",
                "com.equitasbank.upi",
                "com.freecharge.android",
                "com.mgs.hsbcupi",
                "com.csam.icici.bank.imobile",
                "com.mgs.induspsp",
                "com.lcode.smartz",
                "com.khaalijeb.inkdrops",
                "com.msf.kbank.mobile",
                "com.mobikwik_new",
                "net.one97.paytm",
                "com.phonepe.app",
                "com.sbi.upi",
                "com.YesBank",
                "com.yesbank.yespaynext",
                "com.naviapp",
                "com.dreamplug.androidapp",
                "com.phonepe.app.business",
                "money.super.payments",
                "com.sbi.lotusintouch",
                "com.snapwork.hdfc",
                "com.hdfcbank.payzapp",
                "com.idfcfirstbank.optimus",
                "com.bankofbaroda.mconnect",
                "com.axis.mobile",
                "com.nextbillion.groww",
                "com.myairtelapp",
                "com.fss.indus",
                "org.altruist.BajajExperia");

//        String app = getIntent().getStringExtra("app");
        String receiverUpiId = getIntent().getStringExtra("receiverUpiId");
        String receiverName = getIntent().getStringExtra("receiverName");
        String transactionRefId = getIntent().getStringExtra("transactionRefId");
        String transactionNote = getIntent().getStringExtra("transactionNote");
        String amount = getIntent().getStringExtra("amount");
        String currency = getIntent().getStringExtra("currency");
        String url = getIntent().getStringExtra("url");
        String merchantId = getIntent().getStringExtra("merchantId");


        TextView expandableTitle = findViewById(R.id.expandable_title);
//        LinearLayout expandableContent = findViewById(R.id.expandable_content);
        GridLayout  expandableContent = findViewById(R.id.expandable_content);

//        expandableTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (expandableContent.getVisibility() == View.GONE) {
//                    // Expand with animation
//                    expandableContent.setVisibility(View.VISIBLE);
//                    ObjectAnimator.ofFloat(expandableContent, "translationY", 0f, expandableContent.getHeight()).start();
//                } else {
//                    // Collapse with animation
//                    ObjectAnimator.ofFloat(expandableContent, "translationY", expandableContent.getHeight(), 0f).start();
//                    expandableContent.setVisibility(View.GONE);
//                }
//            }
//        });

        expandableTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableContent.getVisibility() == View.GONE) {
                    // Expand with animation
                    expandableContent.setVisibility(View.VISIBLE); // Make the content visible first
                    ObjectAnimator animator = ObjectAnimator.ofFloat(expandableContent, "translationY", expandableContent.getHeight(), 0f);
                    animator.setDuration(300); // Set the animation duration
                    animator.start();
                } else {
                    // Collapse with animation
                    ObjectAnimator animator = ObjectAnimator.ofFloat(expandableContent, "translationY", 0f, expandableContent.getHeight());
                    animator.setDuration(100); // Set the animation duration
                    animator.start();

                    // Set visibility to GONE after the animation completes
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Hide the content only after the collapse animation finishes
                            expandableContent.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });







//        Button closeButton = findViewById(R.id.close_button);
//        Button openPhonepeBtn = findViewById(R.id.open_phonepe);
//        Button openGpayBtn = findViewById(R.id.open_gpay);
//        Button openPaytmBtn = findViewById(R.id.open_paytm);


        // Initialize the scale animation
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_button);


        setupAllUpiButtons(buttons, upiPackages, receiverUpiId, receiverName, transactionRefId, transactionNote, amount, currency, url, merchantId);



        // Close the activity when the button is clicked
//        closeButton.setOnClickListener(v -> finish());

        // Set OnClickListeners for UPI app buttons
        //        setUpUpiButton(openPhonepeBtn, "com.phonepe.app", receiverUpiId, receiverName, transactionRefId, transactionNote, amount, currency, url, merchantId);
        //        setUpUpiButton(openGpayBtn, "com.google.android.apps.nbu.paisa.user", receiverUpiId, receiverName, transactionRefId, transactionNote, amount, currency, url, merchantId);
        //        setUpUpiButton(openPaytmBtn, "net.one97.paytm", receiverUpiId, receiverName, transactionRefId, transactionNote, amount, currency, url, merchantId);
    }


    private void setupAllUpiButtons(
            List<Button> buttons, // List of buttons for UPI apps
            List<String> packageIds, // List of package names
            String receiverUpiId,
            String receiverName,
            String transactionRefId,
            String transactionNote,
            String amount,
            String currency,
            String url,
            String merchantId
    ) {
        for (int i = 0; i < buttons.size(); i++) {
            if (i < packageIds.size()) {
                setUpUpiButton(
                        buttons.get(i),
                        packageIds.get(i),
                        receiverUpiId,
                        receiverName,
                        transactionRefId,
                        transactionNote,
                        amount,
                        currency,
                        url,
                        merchantId
                );
            }
        }
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
            Log.d("SwiftUpiActivity", "App Name: " + packageId);
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
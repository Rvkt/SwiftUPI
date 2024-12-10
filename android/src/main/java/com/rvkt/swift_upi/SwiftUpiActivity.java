package com.rvkt.swift_upi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class SwiftUpiActivity extends Activity {

    SwiftUpiPlugin swiftUpiPlugin = new SwiftUpiPlugin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swift_upi);

        TextView transactionStatus = findViewById(R.id.transaction_status);
        Button closeButton = findViewById(R.id.close_button);
        Button openAppButton = findViewById(R.id.open_app);

        // Set a message (you can pass data through Intent extras)
        transactionStatus.setText("Transaction Successful");

        // Close the activity when the button is clicked
        closeButton.setOnClickListener(v -> finish());


        openAppButton.setOnClickListener(v -> {
            // Call the launchUpiApp method with the PhonePe package name
            String phonePePackage = "com.phonepe.app"; // PhonePe's package name
            swiftUpiPlugin.launchUpiApp(phonePePackage, new MethodChannel.Result() {
                @Override
                public void success(Object result) {
                    Toast.makeText(SwiftUpiActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void error(String errorCode, String errorMessage, Object errorDetails) {
                    Toast.makeText(SwiftUpiActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void notImplemented() {
                    Toast.makeText(SwiftUpiActivity.this, "Not implemented", Toast.LENGTH_SHORT).show();
                }
            }, this);
        });

    }
}

package com.rvkt.swift_upi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** SwiftUpiPlugin */
public class SwiftUpiPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private MethodChannel channel;
  private Activity activity; // To hold the activity reference
  private Result finalResult; // To hold the result reference for async response

  static final String TAG = "UPI INDIA";
  static final int uniqueRequestCode = 512078;
  private boolean resultReturned;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "swift_upi");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    finalResult = result; // Save the result reference for async calls
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("getAllUpiApps")) {
      getAllUpiApps(); // Call the method to get UPI apps
    } else if (call.method.equals("launchUpiApp")) {
      String packageName = call.argument("packageName");
      launchUpiApp(packageName, result);
    } else if (call.method.equals("startTransaction")) {
      startTransaction(call, result); // Handle the start transaction request
    } else {
      result.notImplemented();
    }
  }

  // Method to get all apps on the device that can handle UPI Intent.
  private void getAllUpiApps() {
    List<Map<String, Object>> packages = new ArrayList<>();

    Intent intent = new Intent(Intent.ACTION_VIEW);
    String uriString = "upi://pay?pa=test@upi&pn=Test&tn=GetAllApps&am=10.00&cu=INR&mode=04";
    Uri uri = Uri.parse(uriString);
    intent.setData(uri);

    if (activity == null) {
      finalResult.error("activity_missing", "No attached activity found!", null);
      return;
    }

    PackageManager pm = activity.getPackageManager();
    List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);

    for (ResolveInfo resolveInfo : resolveInfoList) {
      try {
        String packageName = resolveInfo.activityInfo.packageName;
        String name = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        Drawable dIcon = pm.getApplicationIcon(packageName);
        Bitmap bIcon = getBitmapFromDrawable(dIcon);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bIcon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] icon = stream.toByteArray();

        Map<String, Object> m = new HashMap<>();
        m.put("packageName", packageName);
        m.put("name", name);
        m.put("icon", icon);

        packages.add(m);
      } catch (Exception e) {
        e.printStackTrace();
        finalResult.error("package_get_failed", "Failed to get list of installed UPI apps", null);
        return;
      }
    }

    finalResult.success(packages);
  }

  // Helper method to convert Drawable to Bitmap
  private Bitmap getBitmapFromDrawable(Drawable drawable) {
    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }

  // Method to launch the UPI app
  private void launchUpiApp(String packageName, Result result) {
    PackageManager pm = activity.getPackageManager();
    Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
    if (launchIntent != null) {
      activity.startActivity(launchIntent);
      result.success("App launched successfully");
    } else {
      result.error("PACKAGE_NOT_FOUND", "UPI app not found", null);
    }
  }

  private void startTransaction(MethodCall call, Result finalResult) {
    resultReturned = false;
    String app;

    // Extract the arguments.
    if (call.argument("app") == null) {
      app = "in.org.npci.upiapp"; // Default UPI app
    } else {
      app = call.argument("app");
    }

    String receiverUpiId = call.argument("receiverUpiId");
    String receiverName = call.argument("receiverName");
    String transactionRefId = call.argument("transactionRefId");
    String transactionNote = call.argument("transactionNote");
    String amount = call.argument("amount");
    String currency = call.argument("currency");
    String url = call.argument("url");
    String merchantId = call.argument("merchantId");

    // Build the UPI URI
    try {
      String uriString = "upi://pay?pa=" + receiverUpiId +
              "&pn=" + Uri.encode(receiverName) +
              "&am=" + Uri.encode(amount);

      if (transactionNote != null) uriString += "&tn=" + Uri.encode(transactionNote);
      if (transactionRefId != null) uriString += "&tr=" + Uri.encode(transactionRefId);
      if (currency == null) uriString += "&cu=INR"; // Default currency
      else uriString += "&cu=" + Uri.encode(currency);
      if (url != null) uriString += "&url=" + Uri.encode(url);
      if (merchantId != null) uriString += "&mc=" + Uri.encode(merchantId);
      uriString += "&mode=04"; // Optional mode

      Uri uri = Uri.parse(uriString);

      // Create an intent to launch the UPI app
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(uri);
      intent.setPackage(app); // Set the package for the specified app

      // Check if the app is installed and start the activity
      if (isAppInstalled(app)) {
        activity.startActivityForResult(intent, uniqueRequestCode);
      } else {
        Log.d(TAG, app + " not installed on the device.");
        finalResult.error("app_not_installed", "Requested app not installed", null);
      }
    } catch (Exception ex) {
      Log.d(TAG, "Error: " + ex);
      finalResult.error("invalid_parameters", "Transaction parameters are invalid", null);
    }
  }

  private boolean isAppInstalled(String packageName) {
    PackageManager packageManager = activity.getPackageManager();
    try {
      packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
      return true; // App is installed
    } catch (PackageManager.NameNotFoundException e) {
      return false; // App not found
    }
  }






  public void onAttachedToActivity(ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
    binding.addActivityResultListener((requestCode, resultCode, data) -> {
      if (requestCode == uniqueRequestCode) {
        if (resultCode == Activity.RESULT_OK) {
          if (data != null) {
            String response = data.getStringExtra("response");
            // Send this response back to Flutter if necessary
            finalResult.success(response);
          } else {
            finalResult.error("no_response", "No response from UPI app", null);
          }
        } else {
          finalResult.error("transaction_failed", "Transaction failed or cancelled", null);
        }
        return true; // Indicate that you handled the result
      }
      return false; // Pass through for other request codes
    });
  }


  @Override
  public void onDetachedFromActivityForConfigChanges() {
    this.activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    this.activity = null;
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }



//  @Override
//  public void onActivityResult(int requestCode, int resultCode, Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
//    if (requestCode == uniqueRequestCode) {
//      if (data != null) {
//        String response = data.getStringExtra("response");
//        // Process the response here (you may want to parse it)
//        // Send this response back to Flutter if necessary
//        // You might call a method here to send the result back to Flutter
//      } else {
//        // Handle cases where no response was received
//        finalResult.error("no_response", "No response from UPI app", null);
//      }
//    }
//  }
}

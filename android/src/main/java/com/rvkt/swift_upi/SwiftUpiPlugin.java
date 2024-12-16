package com.rvkt.swift_upi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;

import android.widget.LinearLayout;
import android.widget.Button;
import android.content.Context;


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

import android.Manifest;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/** SwiftUpiPlugin */
public class SwiftUpiPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  private MethodChannel channel;
  private Activity activity; // To hold the activity reference
  private Result finalResult; // To hold the result reference for async response

  static final String TAG = "SWIFT UPI";
  static final int uniqueRequestCode = 512078;
  static final int uniqueReqCode = 512079;
  private boolean resultReturned;

  private static final int PERMISSION_REQUEST_CODE = 100;




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
      launchUpiApp(packageName, result, activity);
    } else if (call.method.equals("startTransaction")) {
      startTransaction(call, result); // Handle the start transaction request
    }

    else if (call.method.equals("showCustomUi")) {
      showCustomUi(call, result);
      result.success("Custom UI displayed");
    }
    else {
      result.notImplemented();
    }
  }


  private void getAllUpiApps() {
    if (activity == null) {
      if (finalResult != null) {
        finalResult.error("activity_missing", "No attached activity found!", null);
      }
      return;
    }

    List<Map<String, Object>> packages = new ArrayList<>();
    Intent intent = new Intent(Intent.ACTION_VIEW);
    String uriString = "upi://pay?pa=test@upi&pn=Test&tn=GetAllApps&am=10.00&cu=INR&mode=04";
    Uri uri = Uri.parse(uriString);
    intent.setData(uri);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    PackageManager pm = activity.getPackageManager();
    List<ResolveInfo> resolveInfoList;

    // Check the Android version
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
      // Only check for permission if the device is Android 11 or higher
      if (ContextCompat.checkSelfPermission(activity, Manifest.permission.QUERY_ALL_PACKAGES)
              != PackageManager.PERMISSION_GRANTED) {
        // Request the permission
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.QUERY_ALL_PACKAGES},
                PERMISSION_REQUEST_CODE);
        return; // Don't continue if permission is not granted
      }

      // If permission is granted, proceed to query apps
      resolveInfoList = pm.queryIntentActivities(intent, 0);
    } else {
      // For older Android versions, proceed without checking for the permission
      resolveInfoList = pm.queryIntentActivities(intent, 0);

      // Log the apps
    }

    if (resolveInfoList.isEmpty()) {
      Log.d(TAG, "No apps found for UPI intent.");
    } else {
      Log.d(TAG, "UPI apps found: " + resolveInfoList.size());
    }

    for (ResolveInfo resolveInfo : resolveInfoList) {
      try {
        String packageName = resolveInfo.activityInfo.packageName;
        String name = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        Drawable dIcon = pm.getApplicationIcon(packageName);
        Bitmap bIcon = getBitmapFromDrawable(dIcon);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bIcon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] icon = stream.toByteArray();

        Log.d(TAG, "UPI App Found: " + name + " (" + packageName + ")");
        Log.d(TAG, "Activity Info: " + resolveInfo.activityInfo.toString());

        Map<String, Object> m = new HashMap<>();
        m.put("packageName", packageName);
        m.put("name", name);
        m.put("icon", icon);

        packages.add(m);
      } catch (Exception e) {
        Log.e(TAG, "Failed to get package details", e);
      }
    }

    // Return the results
    if (finalResult != null) {
      finalResult.success(packages);
    } else {
      Log.e(TAG, "Final result callback is null.");
    }
  }

  private void showCustomUi(MethodCall call, Result finalResult) {
    String app = call.argument("app");
    Log.d(TAG, app + " is clicked");
    if (activity == null) {
      if (finalResult != null) {
        finalResult.error("activity_missing", "No attached activity found!", null);
      }
      return;
    }

    // Extract the arguments from the MethodCall
//    String app = call.argument("app") != null ? call.argument("app") : "in.org.npci.upiapp"; // Default UPI app
    String receiverUpiId = call.argument("receiverUpiId");
    String receiverName = call.argument("receiverName");
    String transactionRefId = call.argument("transactionRefId");
    String transactionNote = call.argument("transactionNote");
    String amount = call.argument("amount");
    String currency = call.argument("currency");
    String url = call.argument("url");
    String merchantId = call.argument("merchantId");

    // Create an intent to start SwiftUpiActivity
    Intent intent = new Intent(activity, SwiftUpiActivity.class);

    // Pass the extracted parameters to the intent
//    intent.putExtra("app", app);
    intent.putExtra("receiverUpiId", receiverUpiId);
    intent.putExtra("receiverName", receiverName);
    intent.putExtra("transactionRefId", transactionRefId);
    intent.putExtra("transactionNote", transactionNote);
    intent.putExtra("amount", amount);
    intent.putExtra("currency", currency);
    intent.putExtra("url", url);
    intent.putExtra("merchantId", merchantId);

    // Start the activity
    activity.startActivity(intent);
  }



  // Helper method to convert Drawable to Bitmap
  private Bitmap getBitmapFromDrawable(Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }

    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap;
  }

  // Method to launch the UPI app
  public void launchUpiApp(String packageName, Result result, Activity launnchActivity) {
    PackageManager pm = launnchActivity.getPackageManager();
    Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
    if (launchIntent != null) {
      launnchActivity.startActivity(launchIntent);
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
      if (isAppInstalled(app, activity)) {
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

  public void startTxn(
          Activity activity,
          String app,
          String receiverUpiId,
          String receiverName,
          String transactionRefId,
          String transactionNote,
          String amount,
          String currency,
          String url,
          String merchantId,
          Result finalResult
  ) {
    resultReturned = false;

    // Set default app if not provided
    if (app == null || app.isEmpty()) {
      app = "in.org.npci.upiapp"; // Default UPI app
    }

    // Build the UPI URI
    try {
      String uriString = "upi://pay?pa=" + receiverUpiId +
              "&pn=" + Uri.encode(receiverName) +
              "&am=" + Uri.encode(amount);

      if (transactionNote != null && !transactionNote.isEmpty()) {
        uriString += "&tn=" + Uri.encode(transactionNote);
      }
      if (transactionRefId != null && !transactionRefId.isEmpty()) {
        uriString += "&tr=" + Uri.encode(transactionRefId);
      }
      if (currency == null || currency.isEmpty()) {
        uriString += "&cu=INR"; // Default currency
      } else {
        uriString += "&cu=" + Uri.encode(currency);
      }
      if (url != null && !url.isEmpty()) {
        uriString += "&url=" + Uri.encode(url);
      }
      if (merchantId != null && !merchantId.isEmpty()) {
        uriString += "&mc=" + Uri.encode(merchantId);
      }
      uriString += "&mode=04"; // Optional mode

      Uri uri = Uri.parse(uriString);

      // Create an intent to launch the UPI app
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(uri);
      intent.setPackage(app); // Set the package for the specified app

      // Check if the app is installed and start the activity
      if (isAppInstalled(app, activity)) {
        activity.startActivityForResult(intent, uniqueReqCode);
      } else {
        Log.d(TAG, app + " not installed on the device.");
        finalResult.error("app_not_installed", "Requested app not installed", null);
      }
    } catch (Exception ex) {
      Log.d(TAG, "Error: " + ex);
      finalResult.error("invalid_parameters", "Transaction parameters are invalid", null);
    }
  }


  private boolean isAppInstalled(String packageName, Activity activity) {
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

//    binding.addActivityResultListener((requestCode, resultCode, data) -> {
//      if (requestCode == uniqueReqCode) {
//        if (resultCode == Activity.RESULT_OK) {
//          if (data != null) {
//            String response = data.getStringExtra("response");
//            // Send this response back to Flutter if necessary
//            finalResult.success(response);
//          } else {
//            finalResult.error("no_response", "No response from UPI app", null);
//          }
//        } else {
//          finalResult.error("transaction_failed", "Transaction failed or cancelled", null);
//        }
//        return true; // Indicate that you handled the result
//      }
//      return false; // Pass through for other request codes
//    });


    binding.addRequestPermissionsResultListener((requestCode, permissions, grantResults) -> {
      if (requestCode == PERMISSION_REQUEST_CODE) { // Directly compare with the int constant
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (finalResult != null) {
            getAllUpiApps(); // Call this if permission granted
          }
        } else {
          if (finalResult != null) {
            finalResult.error("permission_denied", "Permission denied to query installed apps", null);
          }
        }
        return true; // Handle this permission result
      }
      return false; // Pass through for other request codes
    });

    // Check for permissions here
    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.QUERY_ALL_PACKAGES)
            != PackageManager.PERMISSION_GRANTED) {
      // Request the permission
      ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.QUERY_ALL_PACKAGES},
              PERMISSION_REQUEST_CODE);
    }

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
}














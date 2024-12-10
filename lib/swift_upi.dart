import 'swift_upi_platform_interface.dart';

class SwiftUpi {
  Future<String?> getPlatformVersion() {
    return SwiftUpiPlatform.instance.getPlatformVersion();
  }

  // New method to get UPI apps
  Future<List<dynamic>> getAllUpiApps() {
    return SwiftUpiPlatform.instance.getAllUpiApps();
  }

  // New method to launch UPI app
  Future<String?> launchUpiApp(String packageName) {
    return SwiftUpiPlatform.instance.launchUpiApp(packageName);
  }

  Future<String?> startTransaction({
    required String receiverUpiId,
    required String receiverName,
    required String amount,
    required String app,
    String? transactionRefId,
    String? transactionNote,
    String? currency,
    String? url,
    String? merchantId,
  }) {
    return SwiftUpiPlatform.instance.startTransaction({
      'receiverUpiId': receiverUpiId,
      'receiverName': receiverName,
      'transactionRefId': transactionRefId,
      'transactionNote': transactionNote,
      'amount': amount,
      'currency': currency,
      'url': url,
      'merchantId': merchantId,
      'app': app,
    });
  }

  // Function to show the custom UI
  Future<void> showCustomUi() async {
    await SwiftUpiPlatform.instance.showCustomUi();
  }
}

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'swift_upi_platform_interface.dart';

/// An implementation of [SwiftUpiPlatform] that uses method channels.
class MethodChannelSwiftUpi extends SwiftUpiPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('swift_upi');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<List<dynamic>> getAllUpiApps() async {
    final List<dynamic> apps = await methodChannel.invokeMethod('getAllUpiApps');
    return apps;
  }

  @override
  Future<String?> launchUpiApp(String packageName) async {
    final String? result = await methodChannel.invokeMethod('launchUpiApp', {'packageName': packageName});
    return result;
  }

  @override
  @override
  Future<String?> startTransaction(Map<String, String?> transactionDetails) async {
    final String? result = await methodChannel.invokeMethod('startTransaction', transactionDetails);
    return result;
  }

}

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'swift_upi_method_channel.dart';

abstract class SwiftUpiPlatform extends PlatformInterface {
  /// Constructs a SwiftUpiPlatform.
  SwiftUpiPlatform() : super(token: _token);

  static final Object _token = Object();

  static SwiftUpiPlatform _instance = MethodChannelSwiftUpi();

  /// The default instance of [SwiftUpiPlatform] to use.
  ///
  /// Defaults to [MethodChannelSwiftUpi].
  static SwiftUpiPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SwiftUpiPlatform] when
  /// they register themselves.
  static set instance(SwiftUpiPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  // Add this method declaration
  Future<List<dynamic>> getAllUpiApps() {
    throw UnimplementedError('getAllUpiApps() has not been implemented.');
  }

  // New method to launch UPI app
  Future<String?> launchUpiApp(String packageName);

  /// Starts a UPI transaction with the provided parameters.
  Future<String?> startTransaction(Map<String, String?> transactionDetails);
}

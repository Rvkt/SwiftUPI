import 'package:flutter_test/flutter_test.dart';
import 'package:swift_upi/swift_upi.dart';
import 'package:swift_upi/swift_upi_platform_interface.dart';
import 'package:swift_upi/swift_upi_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockSwiftUpiPlatform
    with MockPlatformInterfaceMixin
    implements SwiftUpiPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final SwiftUpiPlatform initialPlatform = SwiftUpiPlatform.instance;

  test('$MethodChannelSwiftUpi is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelSwiftUpi>());
  });

  test('getPlatformVersion', () async {
    SwiftUpi swiftUpiPlugin = SwiftUpi();
    MockSwiftUpiPlatform fakePlatform = MockSwiftUpiPlatform();
    SwiftUpiPlatform.instance = fakePlatform;

    expect(await swiftUpiPlugin.getPlatformVersion(), '42');
  });
}

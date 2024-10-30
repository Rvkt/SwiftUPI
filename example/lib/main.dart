import 'dart:developer';
import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:swift_upi/swift_upi.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _platformVersionCode = '0';
  final _swiftUpiPlugin = SwiftUpi();
  static const platform = MethodChannel('swift_upi');
  List<dynamic> upiApps = [];
  bool isLoading = true;

  String txnId = 'N/A';
  String responseCode = 'N/A';
  String txnRef = 'N/A';
  String status = 'N/A';
  String recUpiId = '9988776655@ybl';

  @override
  void initState() {
    super.initState();
    initPlatformState();
    // fetchUpiApps();
    _getInstalledUpiApps();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _swiftUpiPlugin.getPlatformVersion() ?? 'Unknown platform version';
      setState(() {
        _platformVersionCode = platformVersion.split(' ')[1].split('.')[0];
      });
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  // Future<void> fetchUpiApps() async {
  //   try {
  //     final List<dynamic> apps = await _swiftUpiPlugin.getAllUpiApps();
  //     // Process the list of UPI apps
  //     setState(() {
  //       upiApps = apps; // Update the state with the list of apps
  //       isLoading = false; // Set loading to false
  //     });
  //     for (var app in apps) {
  //       log('App Name: ${app['name']}, Package Name: ${app['packageName']}', name: 'UPI App');
  //     }
  //   } on PlatformException catch (e) {
  //     // log("Failed to get UPI apps: '${e.message}'.", name: 'Permission');
  //     log("Failed to get UPI apps: '${e.message}', code: '${e.code}'.", name: 'Permission');
  //     setState(() {
  //       isLoading = false; // Update loading state even on error
  //     });
  //   }
  // }

  Future<void> _getInstalledUpiApps() async {
    final List<dynamic> apps = await _swiftUpiPlugin.getAllUpiApps();
    setState(() {
      upiApps = apps;
      isLoading = false;
    });
    for (var app in apps) {
      log('App Name: ${app['name']}, Package Name: ${app['packageName']}', name: 'UPI App');
    }
  }

  void _openUpiApp(String packageName) async {
    try {
      final String? result = await _swiftUpiPlugin.launchUpiApp(packageName);
      print(result); // Handle success message
    } on PlatformException catch (e) {
      print("Failed to launch app: '${e.message}'.");
    }
  }

  // New method to start a transaction
  void _startTransaction({required String recUpiId, required String recName, required String txnRefId, required String txnNote, required String amt, required String app}) async {
    Map<String, String> resultMap = {};

    try {
      final result = await _swiftUpiPlugin.startTransaction(
        merchantId: '123',
        currency: 'INR',
        url: 'https://domain.in',

        receiverUpiId: recUpiId,
        // Replace with actual receiver UPI ID
        receiverName: recName,
        // Replace with actual receiver name
        transactionRefId: txnRefId,
        // Replace with a unique transaction reference ID (optional)
        transactionNote: txnNote,
        // Replace with a transaction note (optional)
        amount: amt,
        // Amount in INR (required)
        app: app, // Currency (default INR, optional)
      );

      if (result != null) {
        log(result, name: 'Transaction Result');
        // String modifiedResult = result!.replaceAll('&', '\t');
        // log(modifiedResult, name: 'Transaction Result');

        // Split the result string into key-value pairs
        List<String> params = result.split('&');
        for (String param in params) {
          List<String> keyValue = param.split('=');
          if (keyValue.length == 2) {
            resultMap[keyValue[0]] = keyValue[1];
          }
        }

        // // Extract values from the map
        // String txnId = resultMap['txnId'] ?? 'N/A';
        // String responseCode = resultMap['responseCode'] ?? 'N/A';
        // String txnRef = resultMap['txnRef'] ?? 'N/A';
        // String status = resultMap['Status'] ?? 'N/A';

        // Extract values from the map
        setState(() {
          txnId = resultMap['txnId'] ?? 'N/A';
          responseCode = resultMap['responseCode'] ?? 'N/A';
          txnRef = resultMap['txnRef'] ?? 'N/A';
          status = resultMap['Status'] ?? 'N/A';
        });

        // Log the values
        log('Transaction ID: $txnId', name: 'Transaction Details');
        log('Response Code: $responseCode', name: 'Transaction Details');
        log('Transaction Reference: $txnRef', name: 'Transaction Details');
        log('Status: $status', name: 'Transaction Details');
      }
    } on PlatformException catch (e) {
      print("Failed to start transaction: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          centerTitle: true,
          title: const Text(
            'Swift UPI Plugin - Example App',
            style: TextStyle(
              fontSize: 16,
              fontFamily: 'monospace',
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
        body: Column(
          children: [
            Text('Running on: $_platformVersion\n'),
            Text('Paying on: $recUpiId\n'),
            isLoading
                ? const Center(
                    child: CircularProgressIndicator(),
                  )
                : SizedBox(
                    height: MediaQuery.of(context).size.height * 0.125,
                    child: ListView.builder(
                      itemCount: upiApps.length,
                      shrinkWrap: true,
                      scrollDirection: Axis.horizontal,
                      itemBuilder: (context, index) {
                        return SizedBox(
                          width: MediaQuery.of(context).size.width * 0.25,
                          child: Column(
                            children: [
                              InkWell(
                                onTap: () {
                                  _startTransaction(
                                    recUpiId: recUpiId,
                                    recName: 'UPI TEST',
                                    txnRefId: 'TXN123QWER',
                                    txnNote: 'Check',
                                    amt: '1.0',
                                    app: upiApps[index]['packageName'],
                                  );
                                },
                                child: _buildAppIcon(upiApps[index]['icon']),
                              ),
                              const SizedBox(height: 8),
                              Text(upiApps[index]['name'])
                            ],
                          ),
                        );
                      },
                    ),
                  ),
            const Spacer(),
            Card(
              elevation: 4,
              margin: const EdgeInsets.all(24),
              child: Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Column(
                      children: [
                        const Text(
                          'Transaction ID',
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 8),
                        Text(txnId),
                        const SizedBox(height: 8),
                        const Divider(),
                      ],
                    ),
                    Column(
                      children: [
                        const Text(
                          'Response Code',
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 8),
                        Text(responseCode),
                        const SizedBox(height: 8),
                        const Divider(),
                      ],
                    ),
                    Column(
                      children: [
                        const Text(
                          'Transaction Reference',
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 8),
                        Text(txnRef),
                        const SizedBox(height: 8),
                        const Divider(),
                      ],
                    ),
                    Column(
                      children: [
                        const Text(
                          'Status',
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 8),
                        Text(status),
                        const SizedBox(height: 8),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// Helper function to build the app icon from byte array
Widget _buildAppIcon(dynamic iconData) {
  return Image.memory(
    Uint8List.fromList(iconData), // Convert byte array to Uint8List
    width: 40, // Set desired width
    height: 40, // Set desired height
    fit: BoxFit.cover, // Set the box fit
  );
}

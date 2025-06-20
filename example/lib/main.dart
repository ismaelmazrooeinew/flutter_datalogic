import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_datalogic/flutter_datalogic.dart';

void main() => runApp(MaterialApp(home: const ExampleApp()));

class ExampleApp extends StatefulWidget {
  const ExampleApp({super.key});

  @override
  State<ExampleApp> createState() => _ExampleAppState();
}

class _ExampleAppState extends State<ExampleApp> {
  late FlutterDatalogic fdl;
  late StreamSubscription onSubscription;

  var scannerStatus = ScannerStatusType.IDLE;
  var scannedBarcode = 'Press Scan button on device';
  var scannedBarcodeId = '';
  bool _hasBeenPressed = false;

  @override
  void initState() {
    initScanner();
    super.initState();
  }

  Future<void> initScanner() async {
    if (Platform.isAndroid) {
      fdl = FlutterDatalogic();
      onSubscription = fdl.onScannerInfo.listen((event) {
        setState(() {
          scannerStatus = event.status;
          scannedBarcode = event.data;
          scannedBarcodeId = event.dataId;
        });
      });
    }
  }

  void startScanning() async {
    fdl.startScanning();

    setState(() {
      _hasBeenPressed = true;
    });
  }

  void stopScanning() async {
    fdl.stopScanning();

    setState(() {
      _hasBeenPressed = false;
    });
  }

  @override
  void dispose() {
    onSubscription.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Datalogic demo')),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            Text(
              'Status : ${scannerStatus.name}',
              textAlign: TextAlign.center,
            ),
            SizedBox(
              height: 16.0,
            ),
            Text(
              scannedBarcode,
              textAlign: TextAlign.center,
            ),
            SizedBox(
              height: 16.0,
            ),
            Text(
              scannedBarcodeId,
              textAlign: TextAlign.center,
            ),
            SizedBox(
              height: 16.0,
            ),
            GestureDetector(
              onTapDown: (_) {
                startScanning();
              },
              onTapUp: (_) {
                stopScanning();
              },
              onPanEnd: (_) {
                stopScanning();
              },
              child: Container(
                padding: const EdgeInsets.all(40.0),
                decoration: BoxDecoration(
                  color: _hasBeenPressed ? Colors.amber : Colors.lightBlue,
                  borderRadius: BorderRadius.circular(8.0),
                  border: Border.all(width: 5),
                ),
                child: const Text(
                  'Scan',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 18.0,
                  ),
                ),
              ),
            )
            // TextButton(onPressed: _scan, child: Text('Scan')),
          ],
        ),
      ),
    );
  }
}

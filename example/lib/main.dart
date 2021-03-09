import 'dart:async';

import 'package:flutter/material.dart';
import 'package:nfc_card_reader/nfc_card_reader.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String event = 'Ready to read';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      await NfcCardReader.initialize();
      NfcCardReader.readerStream.listen((value) {
        setState(() {
          event = value;
        });
      });
    } on Exception {
      setState(() {
        event = 'Failed to get platform version.';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Current Event : $event\n'),
        ),
      ),
    );
  }
}

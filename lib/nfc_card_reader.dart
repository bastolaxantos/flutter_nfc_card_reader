
import 'dart:async';

import 'package:flutter/services.dart';

class NfcCardReader {
  static const MethodChannel _channel =
      const MethodChannel('nfc_card_reader');

  static const EventChannel _eventChannel =
      const EventChannel('com.paymentology.card_event');

  static Stream<String> _readerStream;

  static Future initialize() async {
     await _channel.invokeMethod('initialize');
  }

  static Stream<String> get readerStream {
    _readerStream = _eventChannel.receiveBroadcastStream().map<String>((event) => event);
    return _readerStream;
  }
}

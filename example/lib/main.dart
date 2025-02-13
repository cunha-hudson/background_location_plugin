import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:get_background_location/get_background_location.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _getBackgroundLocationPlugin = GetBackgroundLocation();

  @override
  void initState() {
    super.initState();
    _getBackgroundLocationPlugin.startServiceLocation();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {

    try {
       _getBackgroundLocationPlugin.getLocation().listen((location){
         print("Localização Atualizada: $location");
       });
    } on PlatformException {
      print('error');
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;


  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running !!! olha o Logcat'),
        ),
      ),
    );
  }
}

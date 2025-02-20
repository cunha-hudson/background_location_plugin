import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'get_background_location_platform_interface.dart';

/// An implementation of [GetBackgroundLocationPlatform] that uses method channels.
class MethodChannelGetBackgroundLocation extends GetBackgroundLocationPlatform {
  /// The method channel used to interact with the native platform.
  static const MethodChannel _methodChannel = MethodChannel('get_background_location');

  static const EventChannel _eventChannel = EventChannel('location_stream');

  @override
  Stream<Map<String, double>> get locationStream {
    return _eventChannel.receiveBroadcastStream().map((event) {
      final location = event as Map<dynamic, dynamic>;
      return {
        'latitude': location['latitude'] as double,
        'longitude': location['longitude'] as double,
      };
    });
  }


  @override
   Future<void> startService() async {
    try {
      await _methodChannel.invokeMethod('startService', {
        "CHECK_INTERVAL": 10000, // Passa o intervalo escolhido pelo usuário
      });
    } catch (e) {
      if (kDebugMode) {
        print("Erro ao iniciar serviço: $e");
      }
    }
  }

  @override
   Future<void> stopService() async {
    try {
      await _methodChannel.invokeMethod('stopService');
    } catch (e) {
      if (kDebugMode) {
        print("Erro ao parar serviço: $e");
      }
    }
  }

}

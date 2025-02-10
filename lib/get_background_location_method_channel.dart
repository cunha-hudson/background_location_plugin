import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'get_background_location_platform_interface.dart';

/// An implementation of [GetBackgroundLocationPlatform] that uses method channels.
class MethodChannelGetBackgroundLocation extends GetBackgroundLocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('get_background_location');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('startLocationService');
    return version;
  }
}

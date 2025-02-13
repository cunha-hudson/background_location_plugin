import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'get_background_location_method_channel.dart';

abstract class GetBackgroundLocationPlatform extends PlatformInterface {
  /// Constructs a GetBackgroundLocationPlatform.
  GetBackgroundLocationPlatform() : super(token: _token);

  static final Object _token = Object();

  static GetBackgroundLocationPlatform _instance = MethodChannelGetBackgroundLocation();

  /// The default instance of [GetBackgroundLocationPlatform] to use.
  ///
  /// Defaults to [MethodChannelGetBackgroundLocation].
  static GetBackgroundLocationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [GetBackgroundLocationPlatform] when
  /// they register themselves.
  static set instance(GetBackgroundLocationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

   Stream<Map<String, double>> get locationStream;
   Future<void> startService();
  Future<void> stopService();

}

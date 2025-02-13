import 'package:flutter_test/flutter_test.dart';
import 'package:get_background_location/get_background_location_platform_interface.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockGetBackgroundLocationPlatform
    with MockPlatformInterfaceMixin
    implements GetBackgroundLocationPlatform {

  Future<String?> getLocationBackground() => Future.value('42');

  @override
  // TODO: implement locationStream
  Stream<Map<String, double>> get locationStream => throw UnimplementedError();

  @override
  Future<void> startService() {
    // TODO: implement startService
    throw UnimplementedError();
  }

  @override
  Future<void> stopService() {
    // TODO: implement stopService
    throw UnimplementedError();
  }
}

void main() {
  /*final GetBackgroundLocationPlatform initialPlatform = GetBackgroundLocationPlatform.instance;

  test('$MethodChannelGetBackgroundLocation is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelGetBackgroundLocation>());
  });

  test('getPlatformVersion', () async {
    GetBackgroundLocation getBackgroundLocationPlugin = GetBackgroundLocation();
    MockGetBackgroundLocationPlatform fakePlatform = MockGetBackgroundLocationPlatform();
    GetBackgroundLocationPlatform.instance = fakePlatform;

    expect(await getBackgroundLocationPlugin.getPlatformVersion(), '42');
  });*/
}

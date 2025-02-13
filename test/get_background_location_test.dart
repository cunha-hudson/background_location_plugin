import 'package:flutter_test/flutter_test.dart';
import 'package:get_background_location/get_background_location.dart';
import 'package:get_background_location/get_background_location_platform_interface.dart';
import 'package:get_background_location/get_background_location_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockGetBackgroundLocationPlatform
    with MockPlatformInterfaceMixin
    implements GetBackgroundLocationPlatform {

  @override
  Future<String?> getLocationBackground() => Future.value('42');
}

void main() {
  final GetBackgroundLocationPlatform initialPlatform = GetBackgroundLocationPlatform.instance;

  test('$MethodChannelGetBackgroundLocation is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelGetBackgroundLocation>());
  });

  test('getPlatformVersion', () async {
    GetBackgroundLocation getBackgroundLocationPlugin = GetBackgroundLocation();
    MockGetBackgroundLocationPlatform fakePlatform = MockGetBackgroundLocationPlatform();
    GetBackgroundLocationPlatform.instance = fakePlatform;

    expect(await getBackgroundLocationPlugin.getPlatformVersion(), '42');
  });
}

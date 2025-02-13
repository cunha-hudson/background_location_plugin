import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get_background_location/get_background_location_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelGetBackgroundLocation platform = MethodChannelGetBackgroundLocation();
  const MethodChannel channel = MethodChannel('get_background_location');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getLocationBackground(), '42');
  });
}

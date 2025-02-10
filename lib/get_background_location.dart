
import 'get_background_location_platform_interface.dart';

class GetBackgroundLocation {
  Future<String?> getPlatformVersion() {
    return GetBackgroundLocationPlatform.instance.getPlatformVersion();
  }
}

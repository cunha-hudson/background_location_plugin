
import 'get_background_location_platform_interface.dart';

class GetBackgroundLocation {
   final _interface = GetBackgroundLocationPlatform.instance;

  Stream<Map<String, double>> getLocation() {
    return _interface.locationStream;
  }

  Future<void> startServiceLocation()async {
     await _interface.startService();
  }

   void finishServiceLocation(){
     _interface.stopService();
   }
}


import 'dwlib_platform_interface.dart';

class Dwlib {
  Future<String?> getPlatformVersion() {
    return DwlibPlatform.instance.getPlatformVersion();
  }
}

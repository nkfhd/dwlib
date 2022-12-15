
import 'dwlib_platform_interface.dart';

class Dwlib {
  Future<dynamic> getList() {
    return DwlibPlatform.instance.getList();
  }

  Future<dynamic> start() {
    return DwlibPlatform.instance.start();
  }
}

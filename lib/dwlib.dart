import 'dwlib_platform_interface.dart';

class Dwlib {
  Future<dynamic> getList() {
    return DwlibPlatform.instance.getList();
  }

  Future<dynamic> start(Map<String, dynamic> data) {
    return DwlibPlatform.instance.start(data);
  }

  Future<dynamic> pause(Map<String, dynamic> data) {
    return DwlibPlatform.instance.pause(data);
  }

  Future<dynamic> resume(Map<String, dynamic> data) {
    return DwlibPlatform.instance.resume(data);
  }

  Future<dynamic> cancel(Map<String, dynamic> data) {
    return DwlibPlatform.instance.cancel(data);
  }

  Future<dynamic> delete(Map<String, dynamic> data) {
    return DwlibPlatform.instance.delete(data);
  }

  Future<dynamic> retry(Map<String, dynamic> data) {
    return DwlibPlatform.instance.retry(data);
  }
}

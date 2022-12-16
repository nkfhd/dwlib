import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'dwlib_method_channel.dart';

abstract class DwlibPlatform extends PlatformInterface {
  /// Constructs a DwlibPlatform.
  DwlibPlatform() : super(token: _token);

  static final Object _token = Object();

  static DwlibPlatform _instance = MethodChannelDwlib();

  /// The default instance of [DwlibPlatform] to use.
  ///
  /// Defaults to [MethodChannelDwlib].
  static DwlibPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [DwlibPlatform] when
  /// they register themselves.
  static set instance(DwlibPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<dynamic> getList() {
    throw UnimplementedError('getList() has not been implemented.');
  }

  Future<dynamic> start(Map<String, dynamic> data) {
    throw UnimplementedError('start() has not been implemented.');
  }

  Future<dynamic> pause(Map<String, dynamic> data) {
    throw UnimplementedError('pause() has not been implemented.');
  }

  Future<dynamic> resume(Map<String, dynamic> data) {
    throw UnimplementedError('resume() has not been implemented.');
  }

  Future<dynamic> cancel(Map<String, dynamic> data) {
    throw UnimplementedError('cancel() has not been implemented.');
  }

  Future<dynamic> delete(Map<String, dynamic> data) {
    throw UnimplementedError('delete() has not been implemented.');
  }

  Future<dynamic> retry(Map<String, dynamic> data) {
    throw UnimplementedError('retry() has not been implemented.');
  }

  Future<dynamic> deleteLocal(Map<String, dynamic> data) {
    throw UnimplementedError('deleteLocal() has not been implemented.');
  }
}

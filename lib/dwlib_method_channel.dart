import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'dwlib_platform_interface.dart';

/// An implementation of [DwlibPlatform] that uses method channels.
class MethodChannelDwlib extends DwlibPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('dwlib');

  @override
  Future<dynamic> getList() async {
    return await methodChannel.invokeMethod('get_list');
  }

  @override
  Future<dynamic> start() async {
    return await methodChannel.invokeMethod('start');
  }
}

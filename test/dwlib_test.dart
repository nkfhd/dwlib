import 'package:flutter_test/flutter_test.dart';
import 'package:dwlib/dwlib.dart';
import 'package:dwlib/dwlib_platform_interface.dart';
import 'package:dwlib/dwlib_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockDwlibPlatform
    with MockPlatformInterfaceMixin
    implements DwlibPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final DwlibPlatform initialPlatform = DwlibPlatform.instance;

  test('$MethodChannelDwlib is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelDwlib>());
  });

  test('getPlatformVersion', () async {
    Dwlib dwlibPlugin = Dwlib();
    MockDwlibPlatform fakePlatform = MockDwlibPlatform();
    DwlibPlatform.instance = fakePlatform;

    expect(await dwlibPlugin.getPlatformVersion(), '42');
  });
}

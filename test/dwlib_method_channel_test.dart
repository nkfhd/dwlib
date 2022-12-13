import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dwlib/dwlib_method_channel.dart';

void main() {
  MethodChannelDwlib platform = MethodChannelDwlib();
  const MethodChannel channel = MethodChannel('dwlib');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}

// import 'package:flutter/services.dart';
// import 'package:flutter_test/flutter_test.dart';
// import 'package:google_map_location_picker/google_map_location_picker_method_channel.dart';

// void main() {
//   MethodChannelGoogleMapLocationPicker platform = MethodChannelGoogleMapLocationPicker();
//   const MethodChannel channel = MethodChannel('google_map_location_picker');

//   TestWidgetsFlutterBinding.ensureInitialized();

//   setUp(() {
//     channel.setMockMethodCallHandler((MethodCall methodCall) async {
//       return '42';
//     });
//   });

//   tearDown(() {
//     channel.setMockMethodCallHandler(null);
//   });

//   test('getPlatformVersion', () async {
//     expect(await platform.getPlatformVersion(), '42');
//   });
// }

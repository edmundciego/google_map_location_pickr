// import 'package:flutter_test/flutter_test.dart';
// import 'package:google_map_location_picker/google_map_location_picker.dart';
// import 'package:google_map_location_picker/google_map_location_picker_platform_interface.dart';
// import 'package:google_map_location_picker/google_map_location_picker_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockGoogleMapLocationPickerPlatform
//     with MockPlatformInterfaceMixin
//     implements GoogleMapLocationPickerPlatform {

//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }

// void main() {
//   final GoogleMapLocationPickerPlatform initialPlatform = GoogleMapLocationPickerPlatform.instance;

//   test('$MethodChannelGoogleMapLocationPicker is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelGoogleMapLocationPicker>());
//   });

//   test('getPlatformVersion', () async {
//     GoogleMapLocationPicker googleMapLocationPickerPlugin = GoogleMapLocationPicker();
//     MockGoogleMapLocationPickerPlatform fakePlatform = MockGoogleMapLocationPickerPlatform();
//     GoogleMapLocationPickerPlatform.instance = fakePlatform;

//     expect(await googleMapLocationPickerPlugin.getPlatformVersion(), '42');
//   });
// }

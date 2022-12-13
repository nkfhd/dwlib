#import "DwlibPlugin.h"
#if __has_include(<dwlib/dwlib-Swift.h>)
#import <dwlib/dwlib-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "dwlib-Swift.h"
#endif

@implementation DwlibPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftDwlibPlugin registerWithRegistrar:registrar];
}
@end

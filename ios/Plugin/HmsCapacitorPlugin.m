#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(HmsCapacitorPlugin, "HmsCapacitor",
           CAP_PLUGIN_METHOD(echo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(initialize, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(joinRoom, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(muteAudio, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(unmuteAudio, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(muteVideo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(unmuteVideo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(enableSpeaker, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(disableSpeaker, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(leaveRoom, CAPPluginReturnPromise);
)

package com.hms.capacitor.io;

import android.Manifest;
import android.app.Application;

import androidx.annotation.NonNull;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.google.gson.Gson;

import live.hms.video.audio.HMSAudioManager;
import live.hms.video.error.HMSException;
import live.hms.video.media.tracks.HMSLocalAudioTrack;
import live.hms.video.media.tracks.HMSLocalVideoTrack;
import live.hms.video.media.tracks.HMSTrack;
import live.hms.video.sdk.HMSActionResultListener;
import live.hms.video.sdk.HMSSDK;
import live.hms.video.sdk.HMSUpdateListener;
import live.hms.video.sdk.models.HMSConfig;
import live.hms.video.sdk.models.HMSLocalPeer;
import live.hms.video.sdk.models.HMSMessage;
import live.hms.video.sdk.models.HMSPeer;
import live.hms.video.sdk.models.HMSRemovedFromRoom;
import live.hms.video.sdk.models.HMSRoleChangeRequest;
import live.hms.video.sdk.models.HMSRoom;
import live.hms.video.sdk.models.enums.HMSPeerUpdate;
import live.hms.video.sdk.models.enums.HMSRoomUpdate;
import live.hms.video.sdk.models.enums.HMSTrackUpdate;
import live.hms.video.sdk.models.trackchangerequest.HMSChangeTrackStateRequest;

@CapacitorPlugin(name = "HmsCapacitor",  permissions = {
        @Permission(
                alias = "camera",
                strings = { Manifest.permission.CAMERA }
        ),
        @Permission(
                alias = "record_audio",
                strings = { Manifest.permission.RECORD_AUDIO }
        )
})
public class HmsCapacitorPlugin extends Plugin {

    private HmsCapacitor implementation = new HmsCapacitor();

    HMSSDK hmssdk;
    HMSConfig hmsConfig;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    class MyHmsUpdateListener implements HMSUpdateListener {

        @Override public void onJoin(@NonNull HMSRoom hmsRoom) {
            JSObject eventData = new JSObject();
            Gson gson = new Gson();
            eventData.put("room", gson.toJson(hmsRoom));
            notifyListeners("onJoin", eventData);
        }
        @Override public void onMessageReceived(@NonNull HMSMessage hmsMessage) {
            JSObject eventData = new JSObject();
            Gson gson = new Gson();
            eventData.put("message", gson.toJson(hmsMessage));
            notifyListeners("onMessageReceived", eventData);
        }
        @Override public void onPeerUpdate(@NonNull HMSPeerUpdate hmsPeerUpdate, @NonNull HMSPeer hmsPeer) {
            JSObject eventData = new JSObject();
            Gson gson = new Gson();
            eventData.put("hmsPeerUpdate", gson.toJson(hmsPeerUpdate));
            eventData.put("hmsPeer", gson.toJson(hmsPeer));
            notifyListeners("onPeerUpdate", eventData);
        }
        @Override public void onReconnected() {
            JSObject eventData = new JSObject();
            eventData.put("status", "reconnected");
            notifyListeners("onReconnected", eventData);
        }
        @Override public void onReconnecting(@NonNull HMSException e) {
            JSObject eventData = new JSObject();
            eventData.put("status", "reconnecting");
            notifyListeners("onReconnecting", eventData);
        }
        @Override public void onRoleChangeRequest(@NonNull HMSRoleChangeRequest hmsRoleChangeRequest) {
            JSObject eventData = new JSObject();
            Gson gson = new Gson();
            eventData.put("peer", gson.toJson(hmsRoleChangeRequest.getRequestedBy()));
            notifyListeners("onRoleChange", eventData);
        }
        @Override public void onRoomUpdate(@NonNull HMSRoomUpdate hmsRoomUpdate, @NonNull HMSRoom hmsRoom) {}
        @Override public void onTrackUpdate(@NonNull HMSTrackUpdate hmsTrackUpdate, @NonNull HMSTrack hmsTrack, @NonNull HMSPeer hmsPeer) {
            JSObject eventData = new JSObject();
            Gson gson = new Gson();
            if(hmsTrackUpdate == HMSTrackUpdate.TRACK_ADDED) {
                eventData.put("hmsTrackUpdate", HMSTrackUpdate.TRACK_ADDED);
            } else if (hmsTrackUpdate == HMSTrackUpdate.TRACK_REMOVED) {
                eventData.put("hmsTrackUpdate", HMSTrackUpdate.TRACK_REMOVED);
            } else if (hmsTrackUpdate == HMSTrackUpdate.TRACK_DEGRADED) {
                eventData.put("hmsTrackUpdate", HMSTrackUpdate.TRACK_DEGRADED);
            } else if (hmsTrackUpdate == HMSTrackUpdate.TRACK_MUTED) {
                eventData.put("hmsTrackUpdate", HMSTrackUpdate.TRACK_MUTED);
            } else if (hmsTrackUpdate == HMSTrackUpdate.TRACK_DESCRIPTION_CHANGED) {
                eventData.put("hmsTrackUpdate", HMSTrackUpdate.TRACK_DESCRIPTION_CHANGED);
            } else if (hmsTrackUpdate == HMSTrackUpdate.TRACK_UNMUTED) {
                eventData.put("hmsTrackUpdate", HMSTrackUpdate.TRACK_UNMUTED);
            }
            eventData.put("hmsTrack", gson.toJson(hmsTrack));
            eventData.put("hmsPeer", gson.toJson(hmsPeer));
            notifyListeners("onTrackUpdate", eventData);
        }
        @Override public void onError(@NonNull HMSException e) {
            JSObject eventData = new JSObject();
            Gson gson = new Gson();
            eventData.put("error", gson.toJson(e));
            notifyListeners("onError", eventData);
        }

        @Override
        public void onChangeTrackStateRequest(@NonNull HMSChangeTrackStateRequest hmsChangeTrackStateRequest) {

        }

        @Override
        public void onRemovedFromRoom(@NonNull HMSRemovedFromRoom hmsRemovedFromRoom) {

        }
    }


    @PluginMethod()
    public void initialize(PluginCall call) {
        this.hmssdk = new HMSSDK.Builder(this.getContext()).build();
        call.resolve();
    }

    @PluginMethod()
    public void joinRoom(PluginCall call) {
        String userName = call.getString("userName");
        String authToken = call.getString("authToken");
        String metaData = call.getString("metaData");

        if(!call.getData().has("userName")) {
            call.reject("UserName is required");
        }

        if(!call.getData().has("authToken")) {
            call.reject("authToken is required");
        }


        this.hmsConfig = new HMSConfig(userName, authToken, metaData);

        PermissionState audioPermissionState = getPermissionState("record_audio");

        if(audioPermissionState == PermissionState.GRANTED) {
            this.hmssdk.join(this.hmsConfig, new MyHmsUpdateListener());
        } else {
            requestPermissionForAlias("record_audio", call, "recordAudioPermsCallback");
        }
    }

    @PluginMethod()
    public void muteAudio(PluginCall call) {
        HMSLocalPeer myPeer = this.hmssdk.getLocalPeer();
        HMSLocalAudioTrack myAudioTrack = myPeer.getAudioTrack();
        if(myAudioTrack != null) {
            myAudioTrack.setMute(true);
        }
        call.resolve();
    }

    @PluginMethod()
    public void unmuteAudio(PluginCall call) {
        HMSLocalPeer myPeer = this.hmssdk.getLocalPeer();
        HMSLocalAudioTrack myAudioTrack = myPeer.getAudioTrack();
        if(myAudioTrack != null) {
            myAudioTrack.setMute(false);
        }
        call.resolve();
    }

    @PluginMethod()
    public void muteVideo(PluginCall call) {
        HMSLocalPeer myPeer = this.hmssdk.getLocalPeer();
        HMSLocalVideoTrack myVideoTrack = myPeer.getVideoTrack();
        if(myVideoTrack != null) {
            myVideoTrack.setMute(true);
        }
        call.resolve();
    }


    @PluginMethod()
    public void unmuteVideo(PluginCall call) {
        HMSLocalPeer myPeer = this.hmssdk.getLocalPeer();
        HMSLocalVideoTrack myVideoTrack = myPeer.getVideoTrack();
        if(myVideoTrack != null) {
            myVideoTrack.setMute(false);
        }
        call.resolve();
    }


    @PluginMethod()
    public void enableSpeaker(PluginCall call) {
        this.hmssdk.switchAudioOutput(HMSAudioManager.AudioDevice.SPEAKER_PHONE);
        call.resolve();
    }

    @PluginMethod()
    public void disableSpeaker(PluginCall call) {
        this.hmssdk.switchAudioOutput(HMSAudioManager.AudioDevice.AUTOMATIC);
        call.resolve();
    }


    class hmsActionResultListener implements HMSActionResultListener {

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(@NonNull HMSException e) {

        }
    }

    @PluginMethod()
    public void leaveRoom(PluginCall call) {
        this.hmssdk.leave(new hmsActionResultListener());
        call.resolve();
    }



    @PermissionCallback
    private void recordAudioPermsCallback(PluginCall call) {
        this.hmssdk.join(this.hmsConfig, new MyHmsUpdateListener());
    }
}

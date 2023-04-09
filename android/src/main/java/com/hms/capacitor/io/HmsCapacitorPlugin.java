package com.hms.capacitor.io;

import android.Manifest;
import android.app.Application;

import androidx.annotation.NonNull;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
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

        private JSObject RoomInterface (HMSRoom hmsRoom) {
            JSObject data = new JSObject();

            data.put("id", hmsRoom.getRoomId());
            data.put("name", hmsRoom.getName());
            data.put("isConnected", true);
            data.put("peers", hmsRoom.getPeerList().toArray());
            data.put("localPeer", hmsRoom.getLocalPeer().getPeerID());
            data.put("roomState", hmsRoom.getRtmpHMSRtmpStreamingState());
            data.put("sessionId", hmsRoom.getSessionId());
            data.put("startedAt", hmsRoom.getStartedAt());
            data.put("joinedAt", hmsRoom.getLocalPeer().getJoinedAt());
            data.put("peerCount", hmsRoom.getPeerCount());

            return data;
        }

        private JSObject MessageInterface (HMSMessage hmsMessage) {
            JSObject data = new JSObject();

            data.put("id", hmsMessage.getServerReceiveTime());
            data.put("sender", hmsMessage.getSender().getPeerID());
            data.put("senderName", hmsMessage.getSender().getName());
            data.put("senderUserId", hmsMessage.getSender().getCustomerUserID());
            data.put("senderRole", hmsMessage.getSender().getHmsRole().getName());
            data.put("recipientPeer", hmsMessage.getRecipient().getRecipientPeer().getPeerID());
            data.put("time", hmsMessage.getServerReceiveTime());
            data.put("read", false);
            data.put("type", hmsMessage.getType());
            data.put("message", hmsMessage.getMessage());
            data.put("ignored", false);

            return data;
        }

        private JSObject PeerInterface (HMSPeer hmsPeer) {
            JSObject data = new JSObject();

            data.put("id", hmsPeer.getPeerID());
            data.put("name", hmsPeer.getName());
            data.put("roleName", hmsPeer.getHmsRole().getName());
            data.put("isLocal", hmsPeer.isLocal());
            data.put("isStarred", false);
//            data.put("videoTrack", hmsPeer.getVideoTrack().getTrackId());
//            data.put("audioTrack", hmsPeer.getAudioTrack().getTrackId());
            data.put("auxiliaryTracks", hmsPeer.getAuxiliaryTracks());
            data.put("customerUserId", hmsPeer.getCustomerUserID());
            data.put("metadata", hmsPeer.getMetadata());
            data.put("joinedAt", hmsPeer.getJoinedAt());

            return data;
        }

        private JSObject ErrorInterface (HMSException e) {
            JSObject data = new JSObject();

            data.put("code", e.getCode());
            data.put("action", e.getAction());
            data.put("name", e.getName());
            data.put("message", e.getMessage());
            data.put("description", e.getDescription());
            data.put("isTerminal", e.isTerminal());

            return data;
        }


        @Override public void onJoin(@NonNull HMSRoom hmsRoom) {
            JSObject eventData = new JSObject();
            eventData.put("room", this.RoomInterface(hmsRoom));
            notifyListeners("onJoin", eventData);
        }
        @Override public void onMessageReceived(@NonNull HMSMessage hmsMessage) {
            JSObject eventData = new JSObject();
            eventData.put("message", this.MessageInterface(hmsMessage));
            notifyListeners("onMessageReceived", eventData);
        }
        @Override public void onPeerUpdate(@NonNull HMSPeerUpdate hmsPeerUpdate, @NonNull HMSPeer hmsPeer) {
            JSObject eventData = new JSObject();

            eventData.put("hmsPeerUpdate", hmsPeerUpdate);
            eventData.put("hmsPeer",  this.PeerInterface(hmsPeer));

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
            eventData.put("peer",  hmsRoleChangeRequest.getRequestedBy());
            notifyListeners("onRoleChange", eventData);
        }
        @Override public void onRoomUpdate(@NonNull HMSRoomUpdate hmsRoomUpdate, @NonNull HMSRoom hmsRoom) {}
        @Override public void onTrackUpdate(@NonNull HMSTrackUpdate hmsTrackUpdate, @NonNull HMSTrack hmsTrack, @NonNull HMSPeer hmsPeer) {
            JSObject eventData = new JSObject();

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

            eventData.put("hmsTrack",  hmsTrack);
            eventData.put("hmsPeer", this.PeerInterface(hmsPeer));

            notifyListeners("onTrackUpdate", eventData);
        }
        @Override public void onError(@NonNull HMSException e) {
            JSObject eventData = new JSObject();

            eventData.put("error",  this.ErrorInterface(e));

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

        call.resolve();
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

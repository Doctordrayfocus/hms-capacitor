import Foundation
import Capacitor
import HMSSDK

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(HmsCapacitorPlugin)
public class HmsCapacitorPlugin: CAPPlugin {
    private let implementation = HmsCapacitor()
    
    var hmsSDK: HMSSDK!;
    
    var hmsConfig: HMSConfig!;
    
    @objc override public func checkPermissions(_ call: CAPPluginCall) {
        let audioState: String
        
        switch AVAudioSession.sharedInstance().recordPermission{
        case AVAudioSession.RecordPermission.granted:
            audioState = "granted"
        case AVAudioSession.RecordPermission.denied:
            audioState = "denied"
        case AVAudioSession.RecordPermission.undetermined:
            audioState = "undetermined"
        
        default:
            audioState = "prompt"
        }
        
        call.resolve(["record_audio": audioState])
    }
    
    @objc override public func requestPermissions(_ call: CAPPluginCall) {
        AVAudioSession.sharedInstance().requestRecordPermission { granted in
            if granted {
                // The user granted access. Present recording interface.
                self.checkPermissions(call)
            } else {
                call.reject("Record audio permission")
            }
        }

    }

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
    
    @objc func initialize(_ call: CAPPluginCall) {
        hmsSDK = HMSSDK.build();
        call.resolve();
    }
    
    @objc func joinRoom(_ call: CAPPluginCall) {
        
        guard let id = call.options["userName"] as? String else {
           call.reject("Must provide a userName")
           return
         }
        
        guard let id = call.options["authToken"] as? String else {
           call.reject("Must provide an authToken")
           return
         }
        
        let userName = call.getString("userName") ?? "hello"
        let authToken = call.getString("authToken") ?? "token"
        let metaData = call.getString("metaData") ?? ""
        
        hmsConfig = HMSConfig(userName: userName, authToken: authToken, metadata: metaData)
        
        hmsSDK.join(config: hmsConfig, delegate: self)
        
        call.resolve();
    }
    
    @objc func muteAudio(_ call: CAPPluginCall) {
        hmsSDK.localPeer?.localAudioTrack()?.setMute(true);
        call.resolve();
    }
    
    @objc func unmuteAudio(_ call: CAPPluginCall) {
        hmsSDK.localPeer?.localAudioTrack()?.setMute(false);
        call.resolve();
    }
    
    @objc func muteVideo(_ call: CAPPluginCall) {
        hmsSDK.localPeer?.localVideoTrack()?.setMute(true);
        call.resolve();
    }
    
    @objc func unmuteVideo(_ call: CAPPluginCall) {
        hmsSDK.localPeer?.localVideoTrack()?.setMute(false);
        call.resolve();
    }
    
    @objc func enableSpeaker(_ call: CAPPluginCall) {
        try? hmsSDK.switchAudioOutput(to: .speaker);
        call.resolve();
    }
    
    @objc func disableSpeaker(_ call: CAPPluginCall) {
        try? hmsSDK.switchAudioOutput(to: .earpiece);
        call.resolve();
    }
    
    @objc func leaveRoom(_ call: CAPPluginCall) {
        hmsSDK.leave();
        call.resolve();
    }
    
}

extension HmsCapacitorPlugin: HMSUpdateListener {
    
    private func peerInterface(peer: HMSPeer) -> [String : Any] {
        let data = [
            "id": peer.peerID,
            "name": peer.name,
            "roleName": peer.role?.name ?? "",
            "isLocal": peer.isLocal,
            "isStarred": false,
            "auxiliaryTracks": peer.auxiliaryTracks ?? [],
            "customerUserId": peer.customerUserID ?? "",
            "metadata": peer.metadata ?? "",
            "joinedAt": peer.joinedAt
        ] as [String : Any]
        
        return data;
    }
    
    private func messageInterface(message: HMSMessage) -> [String : Any] {
        let data = [
            "id": message.time,
            "sender": message.sender?.peerID ?? "",
            "senderName": message.sender?.name ?? "",
            "senderUserId": message.sender?.customerUserID ?? "",
            "senderRole": message.sender?.role?.name ?? "",
            "recipientPeer": message.recipient.peerRecipient?.peerID ?? "",
            "time": message.time,
            "read": false,
            "type": message.type,
            "message": message.message,
            "ignored": false
        ] as [String : Any]
        
        return data;
    }

    
    public func on(join room: HMSRoom) {
        let roomData = [
            "id": room.roomID ?? "",
            "name": room.name ?? "",
            "isConnected": true,
            "peers": room.peers,
            "localPeer": room.peers[0],
            "roomState": room.rtmpStreamingState,
            "sessionId": room.sessionID ?? "",
            "startedAt": room.sessionStartedAt ?? "",
            "joinedAt": room.peers[0].joinedAt,
            "peerCount": room.peerCount ?? 0
        ] as [String : Any]
        self.notifyListeners("onJoin", data: [
            "room" : roomData
        ])
    }
    
    public func on(room: HMSRoom, update: HMSRoomUpdate) {
        //
    }
    
    public func on(peer: HMSPeer, update: HMSPeerUpdate) {
        self.notifyListeners("onPeerUpdate", data: [
            "hmsPeerUpdate": update,
            "hmsPeer": peerInterface(peer: peer)
        ])
    }
    
    public func on(track: HMSTrack, update: HMSTrackUpdate, for peer: HMSPeer) {
        self.notifyListeners("onTrackUpdate", data: [
            "hmsTrackUpdate": update,
            "hmsTrack": track,
            "hmsPeer": peerInterface(peer: peer)
        ])
    }
    
    public func on(error: Error) {
        self.notifyListeners("onError", data: [
            "error": error
        ])
    }
    
    public func on(message: HMSMessage) {
        self.notifyListeners("onMessageReceived", data: [
            "message": messageInterface(message: message)
        ])
    }
    
    public func on(updated speakers: [HMSSpeaker]) {
        self.notifyListeners("listenForDominantSpeaker", data: [
            "peer": peerInterface(peer: speakers[0].peer)
        ])
    }
    
    public func onReconnecting() {
        self.notifyListeners("onReconnecting", data: [
            "status": "reconnecting"
        ])
    }
    
    public func onReconnected() {
        self.notifyListeners("onReconnected", data: [
            "status": "reconnected"
        ])
    }
    
}

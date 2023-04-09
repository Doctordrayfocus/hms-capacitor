import type { HMSTrackUpdate, HMSPeerUpdate } from '@100mslive/hms-video';
import type {
  HMSConfig,
  HMSException,
  HMSLeaveRoomRequest,
  HMSMessage,
  HMSPeer,
  HMSRoom,
  HMSTrack,
} from '@100mslive/hms-video-store';
import type { PermissionState, Plugin } from '@capacitor/core';

export interface HmsCapacitorPlugin extends Plugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  initialize(): Promise<void>;
  joinRoom(config: HMSConfig): Promise<void>;
  muteAudio(): Promise<void>;
  unmuteAudio(): Promise<void>;
  muteVideo(): Promise<void>;
  unmuteVideo(): Promise<void>;
  enableSpeaker(): Promise<void>;
  disableSpeaker(): Promise<void>;
  leaveRoom(): Promise<void>;
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;
}

export interface EventCallbacks {
  listenForDominantSpeaker?(peer: HMSPeer);
  onJoin?(room: HMSRoom);
  onMessageReceived?(message: HMSMessage);
  onPeerUpdate?(hmsPeerUpdate: HMSPeerUpdate, hmsPeer: HMSPeer);
  onReconnected?(status: 'reconnected');
  onReconnecting?(status: 'reconnecting');
  onRoleChange?(peer: HMSPeer);
  onRoomEnded?(leaveRequest: HMSLeaveRoomRequest);
  onTrackUpdate?(
    hmsTrackUpdate: HMSTrackUpdate,
    hmsTrack: HMSTrack,
    hmsPeer: HMSPeer,
  );
  onError?(error: HMSException);
}

export interface PermissionStatus {
  camera: PermissionState;
  record_audio: PermissionState;
}

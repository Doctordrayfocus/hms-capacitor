import { HMSTrackUpdate, HMSPeerUpdate } from '@100mslive/hms-video';
import type { HMSConfig, IHMSNotifications } from '@100mslive/hms-video-store';
import {
  HMSNotificationTypes,
  HMSReactiveStore,
  selectDominantSpeaker,
  selectLocalPeer,
} from '@100mslive/hms-video-store';
import type { IHMSActions } from '@100mslive/hms-video-store/dist/core/IHMSActions';
import type {
  IHMSStoreReadOnly,
  IHMSStatsStoreReadOnly,
} from '@100mslive/hms-video-store/dist/core/IHMSStore';
import { WebPlugin } from '@capacitor/core';

import type { HmsCapacitorPlugin, PermissionStatus } from './definitions';

export class HmsCapacitorWeb extends WebPlugin implements HmsCapacitorPlugin {
  public hms: HMSReactiveStore | undefined = undefined;

  public hmsActions: IHMSActions | undefined = undefined;

  public hmsStore: IHMSStoreReadOnly | undefined = undefined;

  public hmsNotifications: IHMSNotifications | undefined = undefined;

  public hmsStat: IHMSStatsStoreReadOnly | undefined = undefined;

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async initialize(): Promise<void> {
    this.hms = new HMSReactiveStore();
    // by default subscriber is notified about store changes post subscription only, this can be
    // changed to call it right after subscribing too using this function.
    this.hms.triggerOnSubscribe(); // optional, recommended

    this.hmsActions = this.hms.getActions();
    this.hmsStore = this.hms.getStore();
    this.hmsNotifications = this.hms.getNotifications();
    this.hmsStat = this.hms.getStats();

    // listen for domainant speaker

    this.hmsStore?.subscribe(peer => {
      if (peer) {
        this.notifyListeners('listenForDominantSpeaker', { peer });
      }
    }, selectDominantSpeaker);

    // listen for other events

    this.hmsNotifications?.onNotification(notification => {
      if (notification.type == HMSNotificationTypes.NEW_MESSAGE) {
        this.notifyListeners('onMessageReceived', {
          message: notification.data,
        });
      }

      if (notification.type == HMSNotificationTypes.PEER_LIST) {
        this.notifyListeners('onPeerUpdate', {
          hmsPeerUpdate: HMSPeerUpdate.PEER_LIST,
          hmsPeer: notification.data[0],
        });
      }

      if (notification.type == HMSNotificationTypes.PEER_JOINED) {
        this.notifyListeners('onPeerUpdate', {
          hmsPeerUpdate: HMSPeerUpdate.PEER_JOINED,
          hmsPeer: notification.data,
        });
      }

      if (notification.type == HMSNotificationTypes.PEER_LEFT) {
        this.notifyListeners('onPeerUpdate', {
          hmsPeerUpdate: HMSPeerUpdate.PEER_LEFT,
          hmsPeer: notification.data,
        });
      }

      if (notification.type == HMSNotificationTypes.RECONNECTED) {
        this.notifyListeners('onReconnected', {
          status: 'reconnected',
        });
      }

      if (notification.type == HMSNotificationTypes.RECONNECTING) {
        this.notifyListeners('onReconnecting', {
          status: 'reconnecting',
        });
      }

      if (notification.type == HMSNotificationTypes.ROLE_UPDATED) {
        this.notifyListeners('onRoleChange', {
          peer: notification.data,
        });
      }

      if (notification.type == HMSNotificationTypes.ROOM_ENDED) {
        this.notifyListeners('onRoomEnded', {
          leaveRequest: notification.data,
        });
      }

      if (notification.type == HMSNotificationTypes.TRACK_ADDED) {
        const currentPeer = this.hmsStore?.getState(selectLocalPeer);
        if (currentPeer) {
          this.notifyListeners('onTrackUpdate', {
            hmsTrackUpdate: HMSTrackUpdate.TRACK_ADDED,
            hmsTrack: notification.data,
            hmsPeer: currentPeer,
          });
        }
      }

      if (notification.type == HMSNotificationTypes.TRACK_DEGRADED) {
        const currentPeer = this.hmsStore?.getState(selectLocalPeer);
        if (currentPeer) {
          this.notifyListeners('onTrackUpdate', {
            hmsTrackUpdate: HMSTrackUpdate.TRACK_DEGRADED,
            hmsTrack: notification.data,
            hmsPeer: currentPeer,
          });
        }
      }

      if (notification.type == HMSNotificationTypes.TRACK_DESCRIPTION_CHANGED) {
        const currentPeer = this.hmsStore?.getState(selectLocalPeer);
        if (currentPeer) {
          this.notifyListeners('onTrackUpdate', {
            hmsTrackUpdate: HMSTrackUpdate.TRACK_DESCRIPTION_CHANGED,
            hmsTrack: notification.data,
            hmsPeer: currentPeer,
          });
        }
      }

      if (notification.type == HMSNotificationTypes.TRACK_MUTED) {
        const currentPeer = this.hmsStore?.getState(selectLocalPeer);
        if (currentPeer) {
          this.notifyListeners('onTrackUpdate', {
            hmsTrackUpdate: HMSTrackUpdate.TRACK_MUTED,
            hmsTrack: notification.data,
            hmsPeer: currentPeer,
          });
        }
      }

      if (notification.type == HMSNotificationTypes.TRACK_REMOVED) {
        const currentPeer = this.hmsStore?.getState(selectLocalPeer);
        if (currentPeer) {
          this.notifyListeners('onTrackUpdate', {
            hmsTrackUpdate: HMSTrackUpdate.TRACK_REMOVED,
            hmsTrack: notification.data,
            hmsPeer: currentPeer,
          });
        }
      }

      if (notification.type == HMSNotificationTypes.TRACK_RESTORED) {
        const currentPeer = this.hmsStore?.getState(selectLocalPeer);
        if (currentPeer) {
          this.notifyListeners('onTrackUpdate', {
            hmsTrackUpdate: HMSTrackUpdate.TRACK_REMOVED,
            hmsTrack: notification.data,
            hmsPeer: currentPeer,
          });
        }
      }

      if (notification.type == HMSNotificationTypes.TRACK_UNMUTED) {
        const currentPeer = this.hmsStore?.getState(selectLocalPeer);
        if (currentPeer) {
          this.notifyListeners('onTrackUpdate', {
            hmsTrackUpdate: HMSTrackUpdate.TRACK_UNMUTED,
            hmsTrack: notification.data,
            hmsPeer: currentPeer,
          });
        }
      }

      if (notification.type == HMSNotificationTypes.ERROR) {
        this.notifyListeners('onError', {
          error: notification.data,
        });
      }
    });
  }

  async joinRoom(config: HMSConfig): Promise<void> {
    await this.hmsActions?.join(config).then(() => {
      const currentRoom = this.hmsStore?.getState().room;
      if (currentRoom) {
        this.notifyListeners('onJoin', { room: currentRoom });
      }
    });
  }

  async muteAudio(): Promise<void> {
    await this.hmsActions?.setLocalAudioEnabled(false);
  }

  async unmuteAudio(): Promise<void> {
    await this.hmsActions?.setLocalAudioEnabled(true);
  }

  async muteVideo(): Promise<void> {
    await this.hmsActions?.setLocalVideoEnabled(false);
  }

  async unmuteVideo(): Promise<void> {
    await this.hmsActions?.setLocalVideoEnabled(true);
  }

  async enableSpeaker(): Promise<void> {
    if (this.hmsStore) {
      const devices = this.hmsStore?.getState().devices;

      const lastAudioDevice =
        devices.audioOutput[devices.audioOutput.length - 1];

      await this.hmsActions?.setAudioOutputDevice(lastAudioDevice.deviceId);
    }
  }

  async disableSpeaker(): Promise<void> {
    if (this.hmsStore) {
      const devices = this.hmsStore?.getState().devices;

      const defaultAudioDevice = devices.audioOutput[0];
      await this.hmsActions?.setAudioOutputDevice(defaultAudioDevice.deviceId);
    }
  }

  async leaveRoom(): Promise<void> {
    await this.hmsActions?.leave();
  }

  async checkPermissions(): Promise<PermissionStatus> {
    return {
      camera: 'granted',
      record_audio: 'granted',
    };
  }

  async requestPermissions(): Promise<PermissionStatus> {
    return {
      camera: 'granted',
      record_audio: 'granted',
    };
  }
}

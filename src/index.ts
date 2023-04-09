import { registerPlugin } from '@capacitor/core';

import type { HmsCapacitorPlugin, EventCallbacks } from './definitions';

const HmsCapacitor = registerPlugin<HmsCapacitorPlugin>('HmsCapacitor', {
  web: () => import('./web').then(m => new m.HmsCapacitorWeb()),
});

// register events listeners

const HmsEventsCallbacks: EventCallbacks = {};

HmsEventsCallbacks.onJoin = () => {
  console.log('I just joined');
};

HmsCapacitor.addListener('listenForDominantSpeaker', (eventData: any) => {
  if (HmsEventsCallbacks?.listenForDominantSpeaker) {
    HmsEventsCallbacks?.listenForDominantSpeaker(eventData.peer);
  }
});

HmsCapacitor.addListener('onJoin', (eventData: any) => {
  if (HmsEventsCallbacks?.onJoin) {
    HmsEventsCallbacks?.onJoin(eventData.room);
  }
});

HmsCapacitor.addListener('onMessageReceived', (eventData: any) => {
  if (HmsEventsCallbacks?.onMessageReceived) {
    HmsEventsCallbacks?.onMessageReceived(eventData.message);
  }
});

HmsCapacitor.addListener('onPeerUpdate', (eventData: any) => {
  if (HmsEventsCallbacks?.onPeerUpdate) {
    HmsEventsCallbacks?.onPeerUpdate(
      eventData.hmsPeerUpdate,
      eventData.hmsPeer,
    );
  }
});

HmsCapacitor.addListener('onReconnected', (eventData: any) => {
  if (HmsEventsCallbacks?.onReconnected) {
    HmsEventsCallbacks?.onReconnected(eventData.status);
  }
});

HmsCapacitor.addListener('onReconnecting', (eventData: any) => {
  if (HmsEventsCallbacks?.onReconnecting) {
    HmsEventsCallbacks?.onReconnecting(eventData.status);
  }
});

HmsCapacitor.addListener('onRoleChange', (eventData: any) => {
  if (HmsEventsCallbacks?.onRoleChange) {
    HmsEventsCallbacks?.onRoleChange(eventData.peer);
  }
});

HmsCapacitor.addListener('onRoomEnded', (eventData: any) => {
  if (HmsEventsCallbacks?.onRoomEnded) {
    HmsEventsCallbacks?.onRoomEnded(eventData.leaveRequest);
  }
});

HmsCapacitor.addListener('onTrackUpdate', (eventData: any) => {
  if (HmsEventsCallbacks?.onTrackUpdate) {
    HmsEventsCallbacks?.onTrackUpdate(
      eventData.hmsTrackUpdate,
      eventData.hmsTrack,
      eventData.hmsPeer,
    );
  }
});

HmsCapacitor.addListener('onError', (eventData: any) => {
  if (HmsEventsCallbacks?.onError) {
    HmsEventsCallbacks?.onError(eventData.error);
  }
});

export * from './definitions';
export { HmsCapacitor, HmsEventsCallbacks };

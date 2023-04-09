# hms-capacitor

A capacitor plugin for 100ms mobile SDK

## Install

```bash
npm install hms-capacitor
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`initialize()`](#initialize)
* [`joinRoom(...)`](#joinroom)
* [`muteAudio()`](#muteaudio)
* [`unmuteAudio()`](#unmuteaudio)
* [`muteVideo()`](#mutevideo)
* [`unmuteVideo()`](#unmutevideo)
* [`enableSpeaker()`](#enablespeaker)
* [`disableSpeaker()`](#disablespeaker)
* [`leaveRoom()`](#leaveroom)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### initialize()

```typescript
initialize() => Promise<void>
```

--------------------


### joinRoom(...)

```typescript
joinRoom(config: HMSConfig) => Promise<void>
```

| Param        | Type                                            |
| ------------ | ----------------------------------------------- |
| **`config`** | <code><a href="#hmsconfig">HMSConfig</a></code> |

--------------------


### muteAudio()

```typescript
muteAudio() => Promise<void>
```

--------------------


### unmuteAudio()

```typescript
unmuteAudio() => Promise<void>
```

--------------------


### muteVideo()

```typescript
muteVideo() => Promise<void>
```

--------------------


### unmuteVideo()

```typescript
unmuteVideo() => Promise<void>
```

--------------------


### enableSpeaker()

```typescript
enableSpeaker() => Promise<void>
```

--------------------


### disableSpeaker()

```typescript
disableSpeaker() => Promise<void>
```

--------------------


### leaveRoom()

```typescript
leaveRoom() => Promise<void>
```

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### Interfaces


#### HMSConfig

the config object tells the SDK options you want to join with

| Prop                                 | Type                                                        | Description                                                                                                                                                                                                                                                                 |
| ------------------------------------ | ----------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`userName`**                       | <code>string</code>                                         | the name of the peer, can be later accessed via peer.name and can also be changed mid call.                                                                                                                                                                                 |
| **`authToken`**                      | <code>string</code>                                         | client token which encodes room id and role to join with                                                                                                                                                                                                                    |
| **`metaData`**                       | <code>string</code>                                         | optional metadata which can be attached with a peer. This can also be changed mid call.                                                                                                                                                                                     |
| **`settings`**                       | <code><a href="#initialsettings">InitialSettings</a></code> | initial settings for audio/video and device to be used. Please don't pass this field while joining if you're using preview, the state changes in preview will be remembered across to join.                                                                                 |
| **`rememberDeviceSelection`**        | <code>boolean</code>                                        | highly recommended to pass this as true, this will make SDK use the local storage to remember any manual device selection for future joins.                                                                                                                                 |
| **`audioSinkElementId`**             | <code>string</code>                                         |                                                                                                                                                                                                                                                                             |
| **`autoVideoSubscribe`**             | <code>boolean</code>                                        |                                                                                                                                                                                                                                                                             |
| **`initEndpoint`**                   | <code>string</code>                                         |                                                                                                                                                                                                                                                                             |
| **`alwaysRequestPermissions`**       | <code>boolean</code>                                        | Request Camera/Mic permissions irrespective of role to avoid delay in getting device list                                                                                                                                                                                   |
| **`captureNetworkQualityInPreview`** | <code>boolean</code>                                        | Enable to get a network quality score while in preview. The score ranges from -1 to 5. -1 when we are not able to connect to 100ms servers within an expected time limit 0 when there is a timeout/failure when measuring the quality 1-5 ranges from poor to good quality. |
| **`autoManageVideo`**                | <code>boolean</code>                                        | if this flag is enabled, the sdk takes care of unsubscribing to the video when it goes out of view. Additionally if simulcast is enabled, it takes care of auto managing simulcast layers based on the dimensions of the video element to conserve bandwidth.               |


#### InitialSettings

| Prop                                | Type                           | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| ----------------------------------- | ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`isAudioMuted`**                  | <code>boolean</code>           |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **`isVideoMuted`**                  | <code>boolean</code>           |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **`audioInputDeviceId`**            | <code>string</code>            |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **`audioOutputDeviceId`**           | <code>string</code>            |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **`videoDeviceId`**                 | <code>string</code>            |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **`speakerAutoSelectionBlacklist`** | <code>string[] \| 'all'</code> | When a peer joins the room for the first time or when a device change happens, after selecting the mic for audio input, we try to find the matching output device for selecting the speaker(on browsers where speaker selection is possible). For e.g. if the headset mic is selected, the headset speaker will also be selected, if the laptop default mix is selected, the corresponding laptop speaker will be selected. This is useful because if a non-matching pair is selected, it might lead to an echo in the room. This field can be used to override the above behavior, and always go for the default device selection as given by the browser. There are two ways to use this, you can pass in 'all' which will disable the above behaviour for all devices. Or you can pass in an array of labels which will be string matched to disable the behavior for specific devices. For e.g. ["Yeti Stereo Microphone"], as Yeti shows up often in audio output even when no device is plugged into its headphone jack. |


#### PermissionStatus

| Prop               | Type                                                        |
| ------------------ | ----------------------------------------------------------- |
| **`camera`**       | <code><a href="#permissionstate">PermissionState</a></code> |
| **`record_audio`** | <code><a href="#permissionstate">PermissionState</a></code> |


### Type Aliases


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>

</docgen-api>

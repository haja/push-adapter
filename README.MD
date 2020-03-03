# Push Adapter
Service and Library to replace Googles FCM service for push notifications.

This service is self-contained, it does not depend on any third-party service.
It is split up in serveral components:
- two Android apps
- an Android library
- a Java backend
- shared code (pinning-tls-lib)
- sample app and -backend

I am still on the lookout for a better name ;)

# Android

## push-adapter (Android app)
app, manages clients, handles registrations.
relays push notifications from mqtt-client to registered apps.

## push-integration-lib
A library for apps using this service.
Receives notifications from push-adapter.
- provides clients the `FirebaseMessagingService` class to receive notifications.
- provides an API compatible to `FirebaseInstanceId` to register for push notifications, receiving a push token and connection details for pushing to a Push Relay.

## mqtt-client
Android app to handle low level MQTT connections.
Receives push notification from push-relay and provides them to Push Adapter.

### License: EPL
this part uses a different license, required by the mqtt-library used.

# push-relay: Java backend
REST API; backend for push-adapter;
handles app registration tokens and client certificates.
receives notifications to be pushed to devices from app backends (e.g. app-backend)
hosts an MQTT broker internally to push to mqtt-client on devices.

# pinning-tls-lib
plain java library for setting up a TLS connection with specific server certificates and client keys.
used in all components.

# Sample applications

## app
sample app which uses push-integration-lib

## app-backend (sample)
backend for the sample app

# Android O and up:

some settings to get this to work with doze mode:

## exceptions from doze mode needed:
- mqtt client
- push adapter
- `<app using push-integration-lib>`

# Acknowledgements
This project was created as part of a master thesis at [SBA Research](https://www.sba-research.org/).


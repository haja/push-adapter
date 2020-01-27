# push library - GCM / MicroG compatible (TODO codename?)

# Android:

## push-adapter
app, manages clients, handles registrations.
relays push notifications from mqtt-client to registered apps.

## push-integration-lib: library for apps using this service
library; receives notifications from push-adapter;
- provides clients GcmListenerService class to receive GCM-compatible notifications.

## mqtt-client
app, provides push notifications from push-relay.

### License: EPL
this part uses a different license, required by the mqtt-library used.

# Java backend:

## push-relay
REST API; backend for push-adapter;
handles app registration tokens and client certificates.
receives notifications to be pushed to devices from app backends (e.g. app-backend)
hosts an mqtt-broker internally to push to mqtt-client on devices.

# Common code

## pinning-tls-lib
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
- <app using push-integration-lib

# Credits
This project was created as part of a master thesis at [SBA Research](https://www.sba-research.org/).

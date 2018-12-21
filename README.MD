# push library - GCM / MicroG compatible (TODO codename?)

## push-notifier -> push-adapter
app, manages clients, relays push notifications from mqtt backend

## mqtt-adapter -> library for apps using this service (push-integration-lib)
library; receives notifications from push-adapter;
- provides clients GcmListenerService class to receive GCM-compatible notifications

## registration-backend -> push-relay
REST API; backend for push-adapter;
receives notifications to be pushed to devices from app-backend (or other apps backends)
hosts an mqtt-broker internally to push to mqtt-client on devices

## MQTT-backend -> mqtt-client
app, provides push notifications from mqtt backend.

### License: EPL
this part uses a different license, required by the mqtt-library used.

## app (sample)
sample app which uses mqtt-adapter library

## sample-backend -> app-backend (sample)
backend for the sample app

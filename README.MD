# push library - GCM / MicroG compatible (TODO codename?)

## push-notifier -> push-adapter
app, manages clients, relays push notifications from mqtt backend

## mqtt-adapter -> library for apps using this service (push-integration-library)
library; receives notifications from push-notifier;
- provides clients GcmListenerService class to receive GCM-compatible notifications

## MQTT-backend -> mqtt-client
app, provides push notifications from mqtt backend.

### License: EPL
this part uses a different license, required by the mqtt-library used.

## app (sample)
sample app which uses mqtt-adapter library

## sample-backend -> app-backend (sample)
backend for the sample app

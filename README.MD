# push library - GCM / MicroG compatible

## push-notifier
app, manages clients, relays push notifications from mqtt backend

## mqtt-adapter
library; receives notifications from push-notifier;
- provides clients GcmListenerService class to receive GCM-compatible notifications

## app
sample app which uses mqtt-adapter library

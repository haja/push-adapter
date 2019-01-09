# Next steps for implementation

## overall
- handle device registration
- generate client certs and mqtt-topic for client
- actually relay messages from REST API to mqtt-topic

## mqtt
- register client certs for mqtt-topic, use this as auth mechanism
    - how to handle tls client certs for auth on relay?
- expose/advertise mqtt connection settings (host:port) through REST API


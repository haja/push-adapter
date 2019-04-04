# Next steps for implementation

TESTS: some integration test?
    start relay backend with some application config.
    use bishbosh as mqtt client.
    do http calls from bash script, then subscribe with returned keys.
    then http call from backend-sample to deliver message
    assert if message gets delivered

## overall
- handle device registration
- generate client certs and mqtt-topic for client
- actually relay messages from REST API to mqtt-topic

## mqtt
- register client certs for mqtt-topic, use this as auth mechanism
    - how to handle tls client certs for auth on relay?
- expose/advertise mqtt connection settings (host:port) through REST API

# crypto setup
write script to do the following:
- generate server CA
- generate server key
- setup server keystore with server key
- setup server truststore with server CA (to trust this CA)
- setup mqtt-client truststore to trust server key or CA

furthermore, mqtt-client needs to use client keys properly (WIP)


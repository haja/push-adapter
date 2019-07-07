# Next steps for implementation

TESTS: some complete integration test?
    start relay backend with some application config.
    use the sample android apps as clients
    assert if message gets delivered

## overall
- cleanup TODOs in code
- do we need to verify senderId for pushToken?
    - in FCM docs, senderId is never referenced when pushing, only projectId somehow... how is authorization working in FCM?
    - is this a security issue at all?

# crypto setup
write script to do the following:
- generate server CA
- generate server key
- setup server keystore with server key
- setup server truststore with server CA cert only (to trust this CA); tomcat requires that the keystore entry is a certificate ONLY (without key)
- setup server castore with server CA keys (to generate client keys)
- setup mqtt-client truststore to trust server key or CA



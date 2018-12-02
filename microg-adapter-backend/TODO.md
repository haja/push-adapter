# TODOs
## configuration
- configure backend and push-relay (host:port)
- initial setup? register on push-relay?
- "test setup"-button

## security: handling verification/certificates
- configure (allow verification) of push-relay-certificate
- on registration of a new app, give push-relay-certificate to app, so app server can store it for verification of push-relay when pushing
- configure client certificate, so relay can verify adapter-backend for mqtt topic subscription

    adapter-relay setup workflow idea:
     - adapter requests registration on relay
     - relay returns client certificate and generates new topic for client
     - if dev already registered -> error? generate new topic and cert?
     
     app registration workflow idea:
     - adapter/backend (through InstanceID/GoogleCloudMessaging API) generates new token/id for app
     - sends token to relay/backend
     - gives token to app
        - app can now send token to app service. app service can push with token as auth for this specific app via the relay.
        - relay handles routing to topic, adds identifier/token to push message
        - adapter routes push notifications via identifier/token to correct app

- generating a token for each client app? hash(appId + 'some secret for this device')?

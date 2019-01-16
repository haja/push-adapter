# push-adapter TODOs

- start and configure mqtt-client via intents
- remove activity from mqtt-client, start only based on intents from push-notifier

## code-issues
- do proper JSON-parsing of payload
- cleanup unnecessary complex registration process
- remove not needed app infos / legacy GCM compatibility on registration requests

## configuration
- configure backend and push-relay (host:port)
  - now, that mqtt is integrated in register-backend, only backend config needed?
  - -> get relay port / hostname from backend?
- initial setup:
  - generate random UUID as device ID
  - register on push-relay
- "test setup"-button

## android integration
- run on startup and try to establish connection
- connect on wifi/4g connectivity (does paho handle this for us?)
- respect disabled background data? (needed on newer android versions?)

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

- document this workflow (+ analysis?) for thesis


# push-adapter TODOs

- remove activity from mqtt-client, start only based on intents from push-notifier

## code-issues
- do proper JSON-parsing of payload
- cleanup unnecessary complex registration process
- remove not needed app infos / legacy GCM compatibility on registration requests
    - almost done?

## configuration
- initial setup:
  - externalize relay-config
- "test setup"-button

## android integration
- connect on wifi/4g connectivity (does paho handle this for us?)
- respect disabled background data? (needed on newer android versions?)

## security: handling verification/certificates
- document workflows (+ analysis?) for thesis

# evaluation TODOs
- analysis: our 1 connection to relay vs. every app has its own webSocket/... connection


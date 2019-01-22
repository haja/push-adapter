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
- implement workflows for registration of relay and apps
- document workflows (+ analysis?) for thesis


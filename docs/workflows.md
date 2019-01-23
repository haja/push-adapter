# security: handling verification/certificates

## registration on relay

- configure (allow verification) of push-relay-certificate
- adapter requests registration on relay
- relay returns client certificate and generates new topic for client
- client certificate is used on relay and mqtt-backend to verify adapter and restrict access to mqtt-topics

### discussion
- if dev already registered -> error? generate new topic and cert?


## registration of apps

- on registration of a new app, give push-relay-certificate to app, so app server can store it for verification of push-relay when pushing

- adapter/backend (through InstanceID/GoogleCloudMessaging API) generates new token/id for app
- sends token to relay/backend
- gives token to app
   - app can now send token to app service. app service can push with token as auth for this specific app via the relay.
   - relay handles routing to topic, adds identifier/token to push message
   - adapter routes push notifications via identifier/token to correct app

### discussion
- generating a token for each client app? hash(appId + 'some secret for this device')?

## cert handling (request/revoke)
- revoke via email-reset?
- register via email OTP code?
- -> draw diagrams for this
- whitelisting via email-domains?

# TODO
- integrate sequence-diagrams here
- gibts schon papers zu push? zu GCM? vergleich architekturen apple/google/firefox push?
- activity-pub? (mastadon)
- matrix
- vor implementierung: state of the art abchecken und vergleichen
- -> threat model kann dadurch weiterentwickelt werden
    - z.B. honest but curios
    - threat model definieren
- berücksichtigen: neues relay (app updaten, da connection anders is), neues client cert

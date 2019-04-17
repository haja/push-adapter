# TODOs on android impl

# firebase compatibility - needed by wire:
`FirebaseInstanceID`

```
FirebaseApp.initializeApp(context, new com.google.firebase.FirebaseOptions.Builder()
    .setApplicationId(options.appId)
    .setApiKey(options.apiKey)
    .setGcmSenderId(options.pushSenderId)
    .build())
}.toOption
```

# diagrams
- more workflows/seq diagrams:
    - actual push

# cert handling / bootstrap
bootstrap via url parameter / open app with intent from browser
this should transmit:
    - relay connection settings
    - relay certificate
    - and open settings page of push adapter, to verify settings (nice-to-have?)
    - settings could be displayed, but not modified (protect against social engineering?)
        - but then again, another link could be provided
    - ask for confirmation with big disclaimer, not to add connection settings you do not trust - are not instructed by your relay hoster?
        -> find some paper for useable security, how to handle this case
        -> or: disable self-configuration, and allow app to get packaged with connection settings and certificate
            -> relay providers would need to provide the apk specifically built for their relay
            - is this our usecase?
            - allow building of adopted apk automatically during relay-setup process (after generating relay cert/CA)

# upgrade to android 7.1?
-> would include native support for cert pinning

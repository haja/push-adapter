# Registration Backend

## setup
this uses maven and embedded tomcat

### IDE config
this project uses lombok. You might need to enable support for it in your IDE.
see https://projectlombok.org/setup/overview

## certificate generation
generate certificates (e.g. in the folder 'cert-stores'):
```
keytool -genkey -keyalg EC -alias server -validity 360 -storetype pkcs12 -keystore keystore.jks
```
store the password and keystore location in application properties under
```
ssl.keyStorePassword
ssl.keyStore
```
when asked for first- and lastname, enter your domain CN e.g. your hostname

to verify the generated certificate, run
```
keytool -list -v -keystore keystore.jks
```

TODO for now, we use the same keystore as a truststore for issuing client certificates
set `ssl.trustStore` and `ssl.trustStorePassword` to the same values as above.

## remove other CAs
TODO document `cacerts` file; see https://docs.oracle.com/javase/1.5.0/docs/tooldocs/solaris/keytool.html#cacerts
should we remove other cacerts from the machine of the relay host?

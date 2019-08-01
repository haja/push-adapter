# Registration Backend - push-relay

## setup
this uses maven and embedded tomcat

can be run natively or via docker

### build and run with docker

build
```
sudo make docker # sudo needed if user is not in docker group
```

run
```
run-docker.sh
```


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

to export a certificate (to use in client app for auth/pinning), run
```
keytool -export -alias server -keystore keystore.jks -file server.cer
```
TODO this should be done automatically on build or solved differently

for now, do the same for `ssl.trustStore` and `ssl.trustStorePassword` (client certs are still hardcoded)

## remove other CAs
TODO document `cacerts` file; see https://docs.oracle.com/javase/1.5.0/docs/tooldocs/solaris/keytool.html#cacerts
should we remove other cacerts from the machine of the relay host?

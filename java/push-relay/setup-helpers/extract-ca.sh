#!/bin/bash

# TODO automate complete stores setup


EXPECTED_ARGS=1

if [ $# -ne $EXPECTED_ARGS ]
then
	echo "Usage: `basename $0` <ca alias>"
	exit 1
fi

KEY_ALIAS=$1

echo "ca alias: $KEY_ALIAS"

echo "enter key password:"
read PWD
echo "pwd: $PWD"

CERT_PEM=${KEY_ALIAS}_cert.pem
CERT_CRT=${KEY_ALIAS}_cert.der

KEYSTORE=castore
TRUSTSTORE=truststore

echo pem files &&
openssl pkcs12 -in $KEYSTORE.p12 -passin pass:$PWD -nokeys -out $CERT_PEM &&

echo DER files &&
openssl x509 -outform der -in $CERT_PEM -out $CERT_CRT &&

echo import to truststore &&
keytool -v -importcert \
    -file $CERT_CRT \
    -storetype PKCS12 \
    -keystore $TRUSTSTORE.p12 \
    -storepass $PWD &&

echo cleanup .pem and .crt &&
rm $CERT_PEM &&
rm $CERT_CRT &&

echo success


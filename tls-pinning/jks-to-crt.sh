#!/bin/bash


EXPECTED_ARGS=1

if [ $# -ne $EXPECTED_ARGS ]
then
	echo "Usage: `basename $0` <key alias>"
	exit 1
fi

KEY_ALIAS=$1

echo "key alias: $KEY_ALIAS"

echo "enter key password:"
read PWD
echo "pwd: $PWD"

CERT_PEM=${KEY_ALIAS}_cert.pem
CERT_CRT=${KEY_ALIAS}_cert.der

KEY_PEM=${KEY_ALIAS}_key.pem
KEY_CRT=${KEY_ALIAS}_key.key

KEYSTORE=keystore_clients

echo keytool &&
keytool -importkeystore \
    -srckeystore $KEYSTORE.jks \
    -destkeystore $KEYSTORE.p12 \
    -deststoretype PKCS12 \
    -srcalias $KEY_ALIAS \
    -deststorepass $PWD \
    -srcstorepass $PWD &&

echo pem files &&
openssl pkcs12 -in $KEYSTORE.p12 -passin pass:$PWD -nodes -nocerts -out $KEY_PEM &&
openssl pkcs12 -in $KEYSTORE.p12 -passin pass:$PWD -nokeys -out $CERT_PEM &&

echo key &&
openssl pkcs8 -topk8 -inform PEM -outform DER -in $KEY_PEM -out $KEY_CRT -nocrypt &&
echo cert &&
openssl x509 -outform der -in $CERT_PEM -out $CERT_CRT &&

echo cleanup .pem and .p12 &&
rm $CERT_PEM &&
rm $KEY_PEM &&
rm $KEYSTORE.p12 &&

echo success


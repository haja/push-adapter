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
KEYSTORE=keystore

echo pem files &&
openssl pkcs12 -in $KEYSTORE.p12 -passin pass:$PWD -nokeys -out $CERT_PEM &&

echo success


#!/bin/bash

# TODO this is unfinished
keytool -genkey -keyalg EC -alias server -validity 360 -keystore cert-stores/keystore.p12 -storetype pkcs12
keytool -genkey -keyalg EC -alias ca -validity 360 -keystore cert-stores/castore.p12 -storetype pkcs12 -ext BC=ca:true

DELETE FROM APP_REGISTRATIONS;

INSERT INTO APP_REGISTRATIONS(token, device_id, app, app_signature)
VALUES ('registrationToken1', 'testTopic1', 'app1', 'app1signature');

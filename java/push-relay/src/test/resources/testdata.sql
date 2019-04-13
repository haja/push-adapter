DELETE FROM APP_REGISTRATIONS;

INSERT INTO APP_REGISTRATIONS(token, device_id, app, app_signature)
VALUES ('registrationToken1', 'device1', 'app1', 'app1signature'),
('registrationToken2', 'device2', 'app1', 'app1signature');

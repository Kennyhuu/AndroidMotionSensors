here is how to setup the username*password pair for mosquitto broker
passwords.txt contains the username:password pair:
	phone:sensor
	"phone" is the username, "sensor" is the pwd

in mosquitto.conf i set
	allow_anonymous false
	password_file passwords.txt

copy this two file to the mosquitto directroy, where mosquitto.exe is. (paths should not be a problems)

to tell the mosquitto broker to use this conf file:
	mosquitto.exe -c mosquitto.conf

when a client tries to connect to the broker, it has to send the usr:pwd pair to the broker
this is already implemented
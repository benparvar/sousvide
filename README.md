# sousvide
Bluetooth Sousvide project


** Commands **

XXX:Y:000:111111

XXX -> Header
Y 	-> Command 
000 -> Number
111111 -> Value

** HEADER **
Should always be "PAN"

** COMMAND **
V Input 
S Output 

** NUMBER **
000 Turn off the heater
001 Turn on the heater
002 Set the heater timmer
003 Set the heater temperature in degrees Celsius (minimum is 30.00 -> 3000 and maximum is 60.00 -> 6000)
004 Heater timer target in minutes (minimum is 0.50 -> 050 and maximum is 1440.00 -> 144000)
005 Heater temperature target
006 Current heater timer
007 Current heater temperature

** VALUE **

* Input Examples *

PAN:V:000		Turn off the heater
PAN:V:001		Turn on the heater
PAN:V:002:0000	Set the heater timmer
PAN:V:003:0000	Set the heater temperature 

* Output Examples *

PAN:S:007:3906:7000	Current temperature is 39.06 (C), the target is 70.00 (C)


* Examples
PAN:V:000
PAN:V:003:4000
PAN:V:002:600
PAN:V:001





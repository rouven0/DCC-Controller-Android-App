# ModelleisenbahnController
DCC Controller for Model-train boards controlled via C-Bus
For more information, read the [*Developers Guide for CBUS*](https://www.merg.org.uk/merg_wiki/lib/exe/fetch.php?media=public:cbuspublic:developer_6b.pdf)

## How it works
*Your train-controlling unit and the android device must be connected with the same Network*

Via a local network, the app is sending and receiving CBUS frames
The console automatically builds frames using the *CBUS computer interface protocol.*

## About the Analog DCC Mode
In this mode, all analog controllers have a loco address so the app will se them as virtual trains that it can control
All track sections can be controlled seperately with the app.
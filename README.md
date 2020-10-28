# ModelleisenbahnController
DCC Controller for Model-train boards controlled with C-Bus
For more information, read the [*Developers Guide for CBUS*](https://www.merg.org.uk/merg_wiki/lib/exe/fetch.php?media=public:cbuspublic:developer_6b.pdf)

## Important notes
The interactive Layout and menu buttons are still hardcoded.

## How it works
*Your train-controlling unit and the android device must be connected with the same Network*

Via a local network, the app is sending and receiving CBUS frames that will be processed later.
The console automatically builds frames using the *CBUS computer interface protocol.*

# Current Cost (EnviR) forwarder

This project allows to bridge a [Current Cost EnviR](http://www.currentcost.com/) to a MQTT broker. Each data read from the Current Cost are forwarded to MQTT topics.
This allows to have :

- a **small computer box** (like a [Raspberry PI B+](http://www.raspberrypi.org/) or on my side a [Cubox-i2Ultra](http://www.raspberrypi.org/) mini-computer) (running [Debian](https://www.debian.org/) or [Archlinux](https://www.archlinux.org/) for example)
- the **Current Cost EnviR**, connected through USB to that linux box 
- a **MQTT broker** (on my side [Mosquitto 3.1](http://mosquitto.org/)) running somewhere (may be on the same mini-computer)
- a **MQTT consumer**, for example a domotic system (on my side, [Openhab 1.6](http://www.openhab.org/) 

![Current Cost Forwarder schema](https://github.com/SR-G/current-cost-forwarder/raw/master/schema-current-cost-forwarder.png)

## How to run (from release)

### Distribution

Grab down the release : TODO(serge) add link

### Start

Just use the provided current-cost-forwarder.sh

> wget <distribution>
> unzip <distribution>
> ./current-cost-forwarder.sh <options>

Options are : 

<pre>
Usage: <main class> [options]
  Options:
        --broker-auth
       Is the broker auth (true|false)
       Default: false
        --broker-data-dir
       The MQTT broker data dir (for lock files)
       Default: /var/tmp/
        --broker-password
       The MQTT broker password (if authed)
  *     --broker-topic
       The broker topic to publish on
  *     --broker-url
       The MQTT broker URL to publish on
        --broker-username
       The MQTT broker username (if authed)
        --debug
       Debug mode
       Default: false
    --device, -d
       Device name to use, e.g., /dev/hidraw0. If not provided, the first
       /dev/hidraw will be used
        --pid
       The PID filename. Default is current directory, file
       current-cost-forwarder.pid
       Default: current-cost-forwarder.pid
        --reconnection-timeout
       When expected device is not found (or was found previously but not
       anymore), we'll wait this timeout before trying to reconnect. In milliseconds.
       Default 2000.
       Default: 2000
    -h, --usage, --help
       Shows available commands
       Default: false
</pre>

By default the program will try to read something like /dev/ttyUSB0 or /dev/ttyUSB1, ... You have to change the device name (through the --device parameter if you have several USB devices). If you are not running as root, the device has be readable by the user starting this program (chmod a+r,a+x /dev/ttyUSB7 for example). 

## How to build (from sources)

Have [Gradle](https://www.gradle.org). Just git clone the repository and build.
> git clone  
> gradle build

## How to deploy

From the cloned repository (you have to adjust your SSH settings (hostname, login, home) in the build.gradle file) :
> gradle ssh 

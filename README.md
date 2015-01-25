# Current Cost (EnviR) forwarder

This project allows to bridge a [Current Cost EnviR](http://www.currentcost.com/) to a MQTT broker. Each data read from the Current Cost are forwarded to MQTT topics.
This allows to have :

- a **small computer box** (like a [Raspberry PI B+](http://www.raspberrypi.org/) or on my side a [Cubox-i2Ultra](http://www.raspberrypi.org/) mini-computer) (running [Debian](https://www.debian.org/) or [Archlinux](https://www.archlinux.org/) for example)
- the **Current Cost EnviR**, connected through USB to that linux box 
- a **MQTT broker** (on my side [Mosquitto 3.1](http://mosquitto.org/)) running somewhere (may be on the same mini-computer, or may be installed elsewhere)
- a **MQTT consumer**, for example a domotic system (on my side, [Openhab 1.6](http://www.openhab.org/) 

![Current Cost Forwarder schema](https://github.com/SR-G/current-cost-forwarder/raw/master/schema-current-cost-forwarder.png)

There are right now two ouput topics, one for temperature and one for watts. Base topic names are customizable, and you may have :

- metrics/current-cost/watts
- metrics/current-cost/temperature

## How to configure OpenHab

[Openhab](http://www.openhab.org/) is a very promising domotic system. It can read values from MQTT and (for example) store them in a RRD file.

### Acquiring values from MQTT

You have to activate the [MQTT binding](https://github.com/openhab/openhab/wiki/MQTT-Binding).

#### MQTT Broker configuration

In your configuration/openhab.cfg file. 

<pre>################################### MQTT Transport #########################################
#
# Define your MQTT broker connections here for use in the MQTT Binding or MQTT
# Persistence bundles. Replace <broker> with a id you choose.

# URL to the MQTT broker, e.g. tcp://localhost:1883 or ssl://localhost:8883
mqtt:mqtt-broker-home.url=tcp://192.168.8.40:1883

# Optional. Client id (max 23 chars) to use when connecting to the broker.
# If not provided a default one is generated.
#mqtt:<broker>.clientId=<clientId>
mqtt:mqtt-broker-home.clientId=openhabmqttclient

# Optional. User id to authenticate with the broker.
mqtt:mqtt-broker-home.user=USERNAME

# Optional. Password to authenticate with the broker.
mqtt:mqtt-broker-home.pwd=PASSWORD
</pre>

#### Items configuration

<pre>Number CurrentCostWatts {mqtt="&lt;[mqtt-broker-home:metrics/current-cost/watts:state:default]"} 
Number CurrentCostTemperature {mqtt="&lt;[mqtt-broker-home:metrics/current-cost/temperature:state:default]"}
</pre>

This will declare two variables on your MQTT broker that will be constantly filled with the values published in these two topics. 

#### Storing in RRD4J

Activate the [RRD4J binding](https://github.com/openhab/openhab/wiki/rrd4j-Persistence). Then in your configuration/persistence/rrd4.persist file :

<pre>// persistence strategies have a name and a definition and are referred to in the "Items" section
Strategies {
	// for rrd charts, we need a cron strategy
	everyMinute : "0 * * * * ?"
}
Items {
	CurrentCostWatts,CurrentCostTemperature : strategy = everyMinute, restoreOnStartup
}
</pre>

#### Display graphs on web/android clients 

In your configuration/sitemap/ sitemap file, add something like : 

<pre>Text item=CurrentCost icon="chart" {
	Frame {
		Chart item=CurrentCostWatts period=4h refresh=3600 visibility=[Weather_Chart_Period==2]
		Chart item=CurrentCostWatts period=3D refresh=20000 visibility=[Weather_Chart_Period==2]
	}
}
</pre>

## How to run (from release)

### Distribution

Grab down the release : TODO(serge) add link

### Start manually

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

### Start through monit

[Monit](http://mmonit.com/monit/) allows to easily start/stop/restart processes.

Just drop the provided monit configuration file in /etc/monit/conf.d and adjust your paths

<pre>
check process current-cost with pidfile /home/applications/currentcost/current-cost-forwarder.pid
  start program = "/home/bin/monitw.sh /home/applications/currentcost/current-cost-forwarder.sh"
    as uid root and gid root
    with timeout 15 seconds
 stop program = "/bin/bash -c 'kill -s SIGTERM `cat /home/applications/currentcost/current-cost-forwarder.pid`'"
    as uid root and gid root
</pre> 

Then you may use
> monit start current-cost
> monit stop current-cost
> monit restart current-cost	

## How to build (from sources)

Have [Gradle](https://www.gradle.org). Just git clone the repository and build.
> git clone  
> gradle build

## How to deploy

From the cloned repository (you have to adjust your SSH settings (hostname, login, home) in the build.gradle file) :
> gradle ssh 

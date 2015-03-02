# Current Cost (EnviR) forwarder

This project allows to bridge a [Current Cost EnviR](http://www.currentcost.com/) to a MQTT broker. Each data read from the Current Cost are then forwarded to separated MQTT topics.
This allows to have :

- a **small computer box** (like a [Raspberry PI B+](http://www.raspberrypi.org/) or on my side a [Cubox-i2Ultra](http://www.raspberrypi.org/) mini-computer) (running [Debian](https://www.debian.org/) or [Archlinux](https://www.archlinux.org/) for example)
- the **Current Cost EnviR**, connected through USB to that linux box 
- a **MQTT broker** (on my side [Mosquitto 3.1](http://mosquitto.org/)) running somewhere (may be on the same host, or may be installed elsewhere)
- a **MQTT consumer**, for example a domotic system (on my side, [Openhab 1.6](http://www.openhab.org/)) (may be on the same host, or also located elsewhere)

![Current Cost Forwarder schema](https://github.com/SR-G/current-cost-forwarder/raw/master/schema-current-cost-forwarder.png)

There are right now two ouput topics, one for temperature and one for watts. Base topic names are customizable, and you may have :

- metrics/current-cost/watts
- metrics/current-cost/temperature

## How to run (from release)

### Distribution

Grab down the latest release : [1.0.0-SNAPSHOT](https://github.com/SR-G/current-cost-forwarder/releases/download/1.0.0-SNAPSHOT/current-cost-forwarder-1.0.0-SNAPSHOT.zip)

### Start manually

Just use the provided current-cost-forwarder.sh

<pre>wget https://github.com/SR-G/current-cost-forwarder/releases/download/1.0.0-SNAPSHOT/current-cost-forwarder-1.0.0-SNAPSHOT.zip
unzip -o current-cost-forwarder-1.0.0-SNAPSHOT.zip
./current-cost-forwarder.sh
</pre>

Options are : 

<pre>Usage: <main class> [options]
  Options:
        --broker-auth
       Is the broker auth (true|false)
       Default: false
        --broker-data-dir
       The MQTT broker data dir (for lock files)
       Default: /var/tmp/
        --broker-password
       The MQTT broker password (if authed)
        --broker-reconnect-timeout
       The timeout between each reconnect on the broker. Example values : '30s',
       '1m', '500ms', ...
       Default: 5000ms
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
       Device name to use, e.g., /dev/ttyUSB0. If not provided, the first
       /dev/ttyUSB* will be used
        --device-reconnect-timeout
       When expected device is not found (or was found previously but not
       anymore), we'll wait this timeout before trying to reconnect. In milliseconds.
       Default: 2000ms
        --pid
       The PID filename. Default is current directory, file
       current-cost-forwarder.pid
       Default: current-cost-forwarder.pid
        --timeout
       Start/stop timeout. Example values : '30s', '1m', '500ms', ...
       Default: 60000ms
    -h, --usage, --help
       Shows available commands
       Default: true
</pre>

By default the program will try to read something like /dev/ttyUSB0 or /dev/ttyUSB1, aso (the first one will be used). You have to change the device name (through the --device parameter) if you have several USB devices.

If you are not running as root, the device has to be readable by the user starting this program ("sudo chmod a+r,a+x /dev/ttyUSB7" for example). 

Example of valid device :

<pre>cubox-i# ll /dev/tty*USB*
crw-rw---T 1 root dialout 188, 0 Jan 1 1970 /dev/ttyUSB0
</pre>

To check if the current cost is correctly found under linux (the Current Cost will be shown as a "Prolific Technology" entry) :
<pre>cubox-i# lsusb
(...)
Bus 002 Device 002: ID 067b:2303 Prolific Technology, Inc. PL2303 Serial Port
(...)
</pre> 

To check the mapping between your /dev/ttyUSBx device and the Bus ID / Device ID, use the following command (just put the right device under the --name parameter) : 

<pre>cubox-i# echo /dev/bus/usb/`udevadm info --name=/dev/ttyUSB0 --attribute-walk | sed -n 's/\s*ATTRS{\(\(devnum\)\|\(busnum\)\)}==\"\([^\"]\+\)\"/\4/p' | head -n 2 | awk '{$1 = sprintf("%03d", $1); print}'` | tr " " "/"
/dev/bus/usb/002/002
</pre>

To have additionnal informations about your device :
<pre>cubox-i# lsusb -D /dev/bus/usb/002/002
Device: ID 067b:2303 Prolific Technology, Inc. PL2303 Serial Port
Device Descriptor:
  bLength                18
  bDescriptorType         1
  bcdUSB               1.10
  bDeviceClass            0 (Defined at Interface level)
  bDeviceSubClass         0
  bDeviceProtocol         0
  bMaxPacketSize0        64
  idVendor           0x067b Prolific Technology, Inc.
  idProduct          0x2303 PL2303 Serial Port
  bcdDevice            3.00
  iManufacturer           1 Prolific Technology Inc.
  iProduct                2 USB-Serial Controller
  (...)
</pre>

### Start through monit

[Monit](http://mmonit.com/monit/) allows to easily start/stop/restart processes.

Just drop the provided monit configuration file in /etc/monit/conf.d/ and adjust your paths.

<pre>check process current-cost with pidfile /home/applications/currentcost/current-cost-forwarder.pid
  start program = "/home/bin/monitw.sh /home/applications/currentcost/current-cost-forwarder.sh"
    as uid root and gid root
    with timeout 15 seconds
 stop program = "/bin/bash -c 'kill -s SIGTERM `cat /home/applications/currentcost/current-cost-forwarder.pid`'"
    as uid root and gid root
</pre> 

Then you may use
<pre>monit start current-cost
monit stop current-cost
monit restart current-cost
</pre>	

## How to build (from sources)

Have [Gradle](https://www.gradle.org) installed. Just git clone the repository and build.

<pre>git clone https://github.com/SR-G/current-cost-forwarder
cd current-cost-forwarder
gradle build
</pre>

## How to deploy

You have to adjust your SSH settings (hostname, login, home) in the build.gradle file. Then, from the cloned repository, use :
<pre>gradle deploy
</pre> 

This action should generate the zip, upload the whole distribution and unzip it. You just have then to (re)start the program.  

## How to configure OpenHab

[Openhab](http://www.openhab.org/) is a very promising domotic system. It can read values from MQTT and (for example) store them in a RRD file. This forwarder may of course be used with anything else that is MQTT compliant, Openhab is just an example.

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
#mqtt:&lt;broker&gt;.clientId=&lt;clientId&gt;
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

![Chart Screenshot](https://github.com/SR-G/current-cost-forwarder/raw/master/src/site/resources/images/screenshot-current-cost.png)


### Logs

#### Changing log levels

In addition to the --debug parameter, you can override the log level definitions with your own log4j2.xml.

The main "event" log are "reduced" once the program is started in order to not have too much logs generated. After a few minutes, only one forwaring log event will be written down each 100 values. 

#### Example of expected start log

<pre>0 [main] INFO org.tensin.ccf.boot.CurrentCostForwarder - Classpath :
     initial classpath=
      - /home/applications/currentcost/lib/current-cost-forwarder-1.0.0-SNAPSHOT.jar
     dynamic classpath=
      - /home/applications/currentcost/lib/commons-collections-3.2.1.jar
      - /home/applications/currentcost/lib/commons-io-2.4.jar
      - /home/applications/currentcost/lib/commons-lang3-3.1.jar
      - /home/applications/currentcost/lib/commons-logging-1.2.jar
      - /home/applications/currentcost/lib/commons-net-3.2.jar
      - /home/applications/currentcost/lib/dom4j-1.6.1.jar
      - /home/applications/currentcost/lib/guava-17.0.jar
      - /home/applications/currentcost/lib/javassist-3.12.1.GA.jar
      - /home/applications/currentcost/lib/jcommander-1.30.jar
      - /home/applications/currentcost/lib/log4j-1.2-api-2.1.jar
      - /home/applications/currentcost/lib/log4j-api-2.1.jar
      - /home/applications/currentcost/lib/log4j-core-2.1.jar
      - /home/applications/currentcost/lib/log4j-jcl-2.1.jar
      - /home/applications/currentcost/lib/log4j-jul-2.1.jar
      - /home/applications/currentcost/lib/log4j-slf4j-impl-2.1.jar
      - /home/applications/currentcost/lib/mqtt-client-0.4.0.jar
      - /home/applications/currentcost/lib/reflections-0.9.8.jar
      - /home/applications/currentcost/lib/simple-xml-2.7.1.jar
      - /home/applications/currentcost/lib/slf4j-api-1.7.7.jar
      - /home/applications/currentcost/lib/xml-apis-1.0.b2.jar

0 [main] INFO org.tensin.ccf.boot.CurrentCostForwarder - Manifest :
     Manifest-Version: 1.0
     Implementation-Title: current-cost-forwarder
     Implementation-Version: 1.0.0-SNAPSHOT
     Main-Class: org.tensin.ccf.boot.CurrentCostForwarder
     Source-Compatibility: 1.6
     Target-Compatibility: 1.6
     Built-JDK: 1.6.0_45
     Built-Date: Fri Feb 06 14:15:17 CET 2015

14:14:09.656 [main] INFO  org.tensin.ccf.CurrentCostForwarder - Writing retrieved PID [1007] in PID file [/home/applications/currentcost/current-cost-forwarder.pid]
14:14:09.703 [main] INFO  org.tensin.ccf.CurrentCostForwarder - Now starting CurrentCostForwarder
14:14:09.755 [main] INFO  org.tensin.ccf.forwarder.mqtt.ForwarderMQTT - Starting MQTT forwarder with topic base name [metrics/current-cost], mqtt broker MQTTBrokerDefinition : broker-auth [false], broker-url [tcp://192.168.8.40:1883], broker-password []
14:14:09.786 [main] INFO  org.tensin.ccf.forwarder.mqtt.ForwarderMQTT - Now starting MQTT client on broker url [tcp://192.168.8.40:1883], client ID is [root.1423228449783], without authentification
14:14:09.934 [main] INFO  org.tensin.ccf.CurrentCostForwarder - Activated forwarders are [FORWARDER-MQTT]
14:14:09.935 [main] INFO  org.tensin.ccf.CurrentCostForwarder - Now starting reader
14:14:09.941 [main] INFO  org.tensin.ccf.reader.CurrentCostReader - Trying to autodect mirror4j device in [/dev/] with pattern [ttyUSB.*]
14:14:10.008 [main] INFO  org.tensin.ccf.reader.CurrentCostReader - Auto-detected current cost device [/dev/ttyUSB0]
14:14:10.078 [main] INFO  org.tensin.ccf.reader.CurrentCostReader - Starting CurrentCostForwarder reader thread on device [/dev/ttyUSB0]
14:14:10.079 [main] INFO  org.tensin.ccf.CurrentCostForwarder - CurrentCostForwarder started in [375ms]
14:14:10.086 [THREAD-CURRENT-COST-FORWARDER-READER] INFO  org.tensin.ccf.reader.CurrentCostReader - Now connected on specified device [/dev/ttyUSB0]
14:14:11.717 [THREAD-CURRENT-COST-FORWARDER-READER] INFO  org.tensin.ccf.forwarder.mqtt.ForwarderMQTT - Forwarding event #0 EventTemperature : temperature [20.6] on topic [metrics/current-cost/temperature]
14:14:11.761 [THREAD-CURRENT-COST-FORWARDER-READER] INFO  org.tensin.ccf.forwarder.mqtt.ForwarderMQTT - Forwarding event #1 EventWatts : watts [632] on topic [metrics/current-cost/watts]
14:14:17.253 [THREAD-CURRENT-COST-FORWARDER-READER] INFO  org.tensin.ccf.forwarder.mqtt.ForwarderMQTT - Forwarding event #2 EventTemperature : temperature [20.6] on topic [metrics/current-cost/temperature]
14:14:17.284 [THREAD-CURRENT-COST-FORWARDER-READER] INFO  org.tensin.ccf.forwarder.mqtt.ForwarderMQTT - Forwarding event #3 EventWatts : watts [636] on topic [metrics/current-cost/watts]
14:14:29.088 [THREAD-CURRENT-COST-FORWARDER-READER] INFO  org.tensin.ccf.forwarder.mqtt.ForwarderMQTT - Forwarding event #4 EventTemperature : temperature [20.8] on topic [metrics/current-cost/temperature]
14:14:29.117 [THREAD-CURRENT-COST-FORWARDER-READER] INFO  org.tensin.ccf.forwarder.mqtt.ForwarderMQTT - Forwarding event #5 EventWatts : watts [638] on topic [metrics/current-cost/watts]
</pre>
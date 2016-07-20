# sone-iot
The goal of this project is to demo [Spring Cloud Data Flow](https://cloud.spring.io/spring-cloud-dataflow/) as a backend for IOT applications. We're using the [photon from particle.io](https://www.particle.io/prototype#photon).

The particle.io platform provides great IOT edge capabilities. We won't be getting into the detail of it's platform beyond what's required to integrate with Spring Cloud Data Flow.

The idea is to use photon with some sensors to demonstrate the capturing and processing of events. 

## Bill of Materials
The list below provides the parts for building this sample project. We start with the photon kit from particle and add a temperature sensor and button. 

* [photon kit](https://store.particle.io/?product=particle-photon&utm_source=Proto&utm_medium=Button&utm_content=Photon&utm_campaign=Buy) - This get you started with a led and photosensor. 
* [Wifi Antennas](https://store.particle.io/collections/accessories) - **Optional**: I bought I flex but never had to use it
* [Temperature Sensor](https://www.sparkfun.com/products/10988)
* [Mini Push Button](https://www.sparkfun.com/products/97) - We simply use to generate events manually
* [Jumper Wire Kit](https://www.sparkfun.com/products/124) - So you can wire components

The components are pretty cheap, so you may want to explore more the photon capabilities when buying them.

## Firmware
The firmware code need to be flashed to your photon. The wiring needs to follow the PIN constants in the firmware code. 

**Wiring**

We start from the basic wiring from the [particle.io Getting Started](https://docs.particle.io/guide/getting-started/start/photon/). Then we wire the temperature sensor and pushdown button switch.

**TODO** - Wiring diagram

**Particle Dashboard**

Once you flashed the firmware you should be able to go to the Particle Dashboard and see the events for your Photon device.

**Congratulations!!!** you have your edge sensor up and running. 

## Applications
* Standalone - This application talks directly to the particle.io cloud API. 
* firmware - This is the firmware code for you photon.
* Spring Cloud Data Flow Source module - this provides the ability to receive events from particle.io cloud API

### Running the standalone application
This application is a single page that can be run locally. Simply open the index.html page on your browser and the application will connect directly to the particle.io cloud using javascript. 

Before running the standalone application you'll have to change the deviceID and accessToken variables in the index.html file. 

```javascript
// PARTICLE API
var accessToken = "459b6cd6b7d6c32fa728ec9ceaf941bfdfcfb5f1";
var deviceID = "3c0023000a47353138383138"
var url = "https://api.spark.io/v1/devices/" + deviceID;
```

**Running on Pivotal Web Services**

There are 2 files that help you deployed on PWS. The *Staticfile* is a marker file to tell PCF that you want to use the static buildpack. The *manifest.yml* provides the options for the cf push command. 

You may have to change the manifest.yml to avoid route conflicts before issuing the *cf push* command. 

### Running the backend application
This application uses Spring Cloud Data Flow to process the events from the particle.io cloud. Like the particle.io cloud, it acts as a Server-Sent Event server therefore we can leverage the existing standalone application code. 

This is a Spring Boot Web application that uses RabbitMQ to receive events from Spring Cloud Data Flow. It runs on PWS much like the standlone application. 

## Spring Cloud Data Flow
Let's infer you have a Spring Cloud Data Flow environment ready. 

The first step is to register the particle.io source. You can then verify with *app list** command. 

**TODO: Change to Bintray repo**
```
app register --name particle --type source --uri file:///Users/mborges/Tools/PARTICLE/scdf/particlesource/target/particle-source-0.0.1-SNAPSHOT.jar
```

Then you can create a stream to consume particle.io Cloud events. The particle source takes deviceId, accessToken and regex for eventName. 

```
stream create --definition "particle --particle.deviceId=3c0023000a47353138383138 --particle.accessToken=459b6cd6b7d6c32fa728ec9ceaf941bfdfcfb5f1 --particle.eventName=.* | log" --name cf-iot --deploy
```

All events are bring logged now. You can use the power of Spring Cloud Data Flow to process the events. The example below we use a **tap** with **processor** to extract just the data part of the event message.

```
stream create cf-iot-value --definition ":cf-iot.particle > transform --expression=#jsonPath(payload,'$.data') | log" --deploy
```




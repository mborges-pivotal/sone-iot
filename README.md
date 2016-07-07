# sone-iot
The goal of this project is to demo [Spring Cloud Data Flow](https://cloud.spring.io/spring-cloud-dataflow/) as a backend for IOT applications. We're using the [photon from particle.io](https://www.particle.io/prototype#photon).

The particle.io platform provides great IOT edge capabilities. We won't be getting into the detail of it's platform beyond what's required to integrate with Spring Cloud Data Flow.

The idea is to use photon with some sensors to demonstrate the capturing and processing of events. 

## Bill of Materials
The list below provides the parts for building this sample project. We start with the photon kit from particle and add a temperature sensor and button. 

* [photon kit](https://store.particle.io/?product=particle-photon&utm_source=Proto&utm_medium=Button&utm_content=Photon&utm_campaign=Buy) - This get you started with a led and photosensor. 
* [Temperature Sensor](https://www.sparkfun.com/products/10988)
* [Mini Push Button](https://www.sparkfun.com/products/97) - We simply use to generate events manually
* [Jumper Wire Kit](https://www.sparkfun.com/products/124)

The components are pretty cheap, so you may want to explore more the photon capabilities when buying them.

## Applications
* Standalone - This application talks directly to the particle.io cloud API. 
* Spring Cloud Data Flow Source module - this provides the ability to receive events from particle.io cloud API


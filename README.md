# FunnyDroids

Joke telling Android Application with Java Library, Android Library and Google Cloud Endpoints Implementation

##Project Overview
In this project, created an app with multiple flavors that uses multiple libraries and Google Cloud Endpoints. The finished app consists of four modules:

A Java library that provides jokes
A Google Cloud Endpoints (GCE) project that serves those jokes
An Android Library containing an activity for displaying jokes
An Android app that fetches jokes from the GCE module and passes them to the Android Library for display

##Why this Project?
As Android projects grow in complexity, it becomes necessary to customize the behavior of the Gradle build tool, allowing automation of repetitive tasks. Particularly, factoring functionality into libraries and creating product flavors allow for much bigger projects with minimal added complexity.

##Installation
* Clone this repository.
```
git clone https://github.com/josemontiel/FunnyDroids.git
```
* Import it into Android Studio.
* Start GCE Instance.
* Run it or Generate a Signed APK to install into your Android Device.

##Features
* Designed both JUnit and Android Connected Tests.
* Developed custom Gradle Tasks for enhanced testing.
* Developed and Implemented different dependencies: Java Libraries and Android Libraries.
* Developed and Implemented GCE as back-end

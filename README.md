
# EliteBoy [![Build Status](https://travis-ci.com/mluigi/EliteBoy.svg?branch=develop)](https://travis-ci.com/mluigi/EliteBoy)

A companion app to Elite Dangerous.

## Features
### Done
- Show Profile from ED Companion Api.
- Show last visited system with stations e bodies
- Show station with informations, market prices, ships and modules sold.
- Search nearest service
- Search for a station or a system with filters
- Show news from Galnet

## Screenshots
### System

<img src="/imgs/system.png" width="250" /> 

### Station
<img src="/imgs/station.png" width="250" /> 

### Search system
<img src="/imgs/searchsystem.png" width="250" /> 

### Search station
<img src="/imgs/searchstation.png" width="250" /> 

### Nearest
<img src="/imgs/nearest.png" width="250" /> 

## Todo
- Add Engineers assistant
- Integration with Google Assistant

## Contributing

You can clone with Android Studio and you're good to go, but to actually
build the app you need a frontier api key that you can get from [here](https://user.frontierstore.net/), 
then you need to go in developer zone and create a client. Once you have
the api key create a gradle.properties file in root folder with a line
`FrontierClientId = "YOUR_API_KEY"`. 

Also you'd need to open a firebase project because I'm using crashlytics.
If you don't want to, you can always remove the dependencies lines in 
`app\build.gradle`, without committing the changes, or execute a gradle build without the Crashlytics tasks `gradlew build -x processDebugGoogleServices -x fabricGenerateResourcesDebug -x processReleaseGoogleServices -x fabricGenerateResourcesRelease`.

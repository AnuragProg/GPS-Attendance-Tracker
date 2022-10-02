# GPS-Attendance-Tracker

## Problem Statement:
It is always painful to keep track of your attendance and asking teacher again and again isn't an option.
Depending heavily on college portal to give exact attendance calculations is not so productive.
Keeping a diary to keep daily attendance track is also very painful.
There are app's that allow keeping track of attendance but are same as doing regular entry except for the fact that you don't have to write with hand.
Is there a way to make it more simple and automatic so that the user doesn't have to keep track at all and some other entity would take care of it?
And if the user wants to see the attendance stats, this supposed worker will present him/her with detailed data.

## Solution:
This is where the GPS-Attendance-Tracker comes in.
GAT(in short) is an Android App that does all the work for you and makes everything very easy and effortless for you.
GAT uses User's GPS to retrieve user's current location and based on the distance between the institution and user's current location,
it decides whether the user is in class or not and marks the attendance accordingly. The only thing user has to do is to turn on internet and GPS and that's it.

### Features:
- Detailed Cards for Subjects and Logs
- Search Bar for filtering content
- GPS to automatically register user's attendance
- Timetable Screen to register subjects for periodic alarms
- Expanded Notification Features to mark attendance through notification only
- Feature to export subject and logs data to excel document

### About the app:
- Uses GPS service for retrieving user location
- Uses WorkManager for doing long-running tasks (calculating distance, marking attendance) when the alarm goes on
- Uses Dagger-Hilt for Dependency Injection
- Kotlin Flows handle the flow of data between Database and UI
- Uses Clean Architecture for code structure
- Uses Apache POI for writing data to excel
- Uses Open Source Maps for map tiles

# GPS-Attendance-Tracker

## Problem Statement:
It is always painful to keep track of your attendance and asking teacher again and again isn't an option.
Depending heavily on college portal to give exact attendance calculations is not so productive.
Keeping a diary to keep daily attendance track is also very painful.
There are app's that allow keeping track of attendance but are same as doing regular entry except for the fact that you don't have to carry a physical notebook for records.
Is there a way to make it more simple and automatic so that the user doesn't have to keep track at all and some other entity would take care of it?
And if the user wants to see the attendance stats, this supposed worker will present him/her with detailed data.

## Solution:
This is where the GPS-Attendance-Tracker comes in.
GAT(in short) is an Android App that does all the work for you and makes everything very easy and effortless for you.
GAT uses User's GPS to retrieve user's current location and based on the distance between the institution and user's current location,
it decides whether the user is in class or not and marks the attendance accordingly. The only thing user has to do is to turn on internet and GPS and that's it.

## Features:
1. Android app that marks your attendance automatically ( provided that you provide Coordinates(lat, lng) and range (acceptable distance from the class in which you will be marked present) of your class ). 
2. Users can simply add their subject and then add that subject along with its timing in the timetable.
3. When the time of the class arrives, app retrieves users current location and calculates the distance between current location and class location, if user falls under the provided range, user is marked present otherwise absent.
4. In case, app is unable to retrieve user's location or the coordinates of the class is not provided, then app allows user to mark attendance from notification only. User doesn't have to open the app at all.
5. In case of wrong attendance marked by the app (say your class happened somewhere else than usual place and app marked you absent), you can change the attendance status from the notification only. User doesn't have to open the app at all in this case as well.
6. Users can also take out the data of attendance in Excel file.

### Tech Used: Jetpack Compose(UI), Dagger-Hilt(Dependency Injection), Kotlin-Flows(Asynchronous Programming), BroadcastReceiver, AlarmManager, LocationManager, WorkManager, ApachePOI(Writing to Excel sheets)
### Architecture Used: Clean Architecture

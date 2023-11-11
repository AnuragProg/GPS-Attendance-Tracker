# GPS Attendance Tracker

## Problem Statement

Tracking attendance manually can be cumbersome and relying on college portals for accurate attendance calculations isn't always efficient. Traditional methods like maintaining a diary or using attendance-tracking apps still require manual input. Is there a way to automate attendance tracking seamlessly, eliminating the need for manual entries? The GPS Attendance Tracker (GAT) aims to provide a solution by automating attendance based on the user's location.

## Solution

GAT is an Android app that simplifies attendance tracking by using the user's GPS location. It determines the user's presence in class by comparing their current location with the specified coordinates and acceptable distance range from the class. All the user needs to do is enable internet and GPS on their device.

## Features

- Automatically marks attendance based on user's location (requires input of class coordinates and acceptable distance).
- Allows users to add subjects and schedule them in the timetable.
- Retrieves the user's location at the scheduled class time and marks attendance accordingly.
- Provides the option to manually mark attendance from notifications if location retrieval fails or coordinates are not provided.
- Allows users to correct attendance status directly from notifications.
- Enables users to export attendance data to an Excel file.

## Screenshots

<img src="https://github.com/AnuragProg/GPS-Attendance-Tracker/assets/95378716/bb51e9fe-6965-434e-b0c6-8fcc7a46a080" alt="Screenshot 1" width="200" height="450"/>
<img src="https://github.com/AnuragProg/GPS-Attendance-Tracker/assets/95378716/2ecc5670-c24e-4fc2-86d8-94e96e23f7a6" alt="Screenshot 2" width="200" height="450"/>
<img src="https://github.com/AnuragProg/GPS-Attendance-Tracker/assets/95378716/01bdf03f-670c-42f9-82cf-b2afbd1d2fb7" alt="Screenshot 3" width="200" height="450"/>
<img src="https://github.com/AnuragProg/GPS-Attendance-Tracker/assets/95378716/2e3d0a2a-864f-4103-9666-f10ae20b65e4" alt="Screenshot 4" width="200" height="450"/>
<img src="https://github.com/AnuragProg/GPS-Attendance-Tracker/assets/95378716/c50b2a9f-40ae-4e1d-bb11-bbf6623984d7" alt="Screenshot 5" width="200" height="450"/>



## Tech Stack

- Jetpack Compose (UI)
- Dagger-Hilt (Dependency Injection)
- Kotlin Flows (Asynchronous Programming)
- BroadcastReceiver, AlarmManager, LocationManager, WorkManager
- ApachePOI (Writing to Excel sheets)

## Architecture

Clean Architecture

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Configure class coordinates and acceptable distance in the app.
4. Build and run the app on your Android device.

## Contributions

Contributions are welcome! Feel free to open issues or submit pull requests.

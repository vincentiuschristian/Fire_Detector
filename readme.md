# Pantauin

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blue.svg)

**Pantauin** is a real-time **fire detection and monitoring Android application** that uses **IoT sensors** to detect temperature, humidity, gas, and fire conditions. Sensor data is transmitted via **MQTT** and displayed in the app. Users can view receive notifications when dangerous conditions are detected.

---

## Features

* **Real-time Monitoring**: Receives and displays live data from temperature, humidity, gas, and fire conditions.
* **Instant Alerts**: Send push notifications when a potential hazard is detected.
* **Map Visualization**: Shows the location of connected sensor on Maps for quick identification.
* **Data History**: Allows users to view the historical data logs recorded from sensor for analysis.
* **Emergency Services**: Provides quick-access buttons to directly call the police or ambulance.


---

## Core Libraries Used

This project is built with a set of modern and robust libraries to ensure efficient and reliable performance. The key libraries include:

* **Dagger Hilt**: For dependency injection to manage the application's dependency graph and simplify component lifecycles.
* **Retrofit**: A type-safe HTTP client for Android to communicate with the REST API for user authentication and data fetching.
* **Paho MQTT Client**: To subscribe to MQTT topics and handle real-time communication with IoT sensors.
* **Android Jetpack**: A suite of libraries including:
    * **Navigation Component**: To manage the in-app navigation flow between fragments.
    * **ViewModel**: To store and manage UI-related data in a lifecycle-conscious way.
    * **LiveData**: To build observable data objects that notify views of database changes.
* **Coroutines & Flow**: For managing background threads and handling asynchronous operations smoothly.
* **DataStore Preferences**: To persist simple key-value data, such as user session tokens, asynchronously.
* **Google Maps SDK**: To display maps and custom markers for sensor locations.

---

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* Android Studio (latest stable version recommended)
* Git

### Installation Steps

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/vincentiuschristian/Fire_Detector.git](https://github.com/vincentiuschristian/Fire_Detector.git)
    ```

2.  **Open the Project**
    Open the cloned project in Android Studio. Allow Gradle to sync and download all the required dependencies.

3.  **Set Up Google Maps API Key (Required)**
    This project uses the Google Maps SDK, which requires an API key to function.

    * Create a new file in the **root directory** of the project (the same level as `build.gradle` and `settings.gradle`).
    * Name the file `local.properties`.
    * Add your Google Maps API key to this file in the following format:
        ```properties
        MAPS_API_KEY=YOUR_API_KEY_HERE
        ```

4.  **Build and Run**
    You can now build and run the application on an Android emulator or a physical device.

---

## Screenshots

![Screenshot Aplikasi](https://github.com/user-attachments/assets/44b67d97-b57f-40db-8d4e-f32ff0d132e4)

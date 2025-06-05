# SmartSensor Network – Fall Detection with Smartphone Gyroscope

This is a student project developed to explore the feasibility of using the built-in motion sensors (specifically the gyroscope) of Android smartphones to detect human falls in real-time. The overarching goal is to provide a simple, portable, and cost-effective solution for identifying potentially dangerous falls and triggering emergency notifications when necessary.

## 🧠 Project Summary

The SmartSensor Network project leverages the gyroscope sensor inside an Android smartphone to detect fall events. By analyzing motion patterns and sensor data, the system aims to distinguish between normal activities (e.g., walking, sitting down) and potentially hazardous incidents such as falling. 

This concept could be useful for elderly people, workers in hazardous environments, or anyone at risk of sudden immobility. In its final implementation, the system could be extended to trigger emergency alerts or notifications.

## 📱 Features

- Real-time monitoring of gyroscope sensor data
- Simple algorithm to detect fall-like motion patterns
- Logging and analysis of sensor data for offline evaluation
- Android-native implementation (Java)


## 🚀 How It Works

The application listens to gyroscope data continuously while running in the background. A custom algorithm processes changes in angular velocity to identify rapid and irregular movements that may correspond to a fall.

### Example Detection Flow:

1. Device detects rapid angular motion (spike in gyroscope values)
2. System compares motion profile to fall thresholds
3. If thresholds are exceeded → potential fall detected
4. (Optional/Future) Trigger emergency protocol (e.g., SMS alert)

## About the Project
This was a university student project focused on exploring sensor-based health safety solutions using everyday mobile devices. The project was developed primarily for educational purposes.

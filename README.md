```markdown
# DelhiTravelApp (DTA)

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

## Introduction
DelhiTravelApp (DTA) is your ultimate companion for navigating the Delhi Metro network. Designed with usability and accessibility in mind, the app offers real-time metro updates, location tracking, and a unique user interface tailored to enhance the travel experience. Whether you’re a daily commuter or a first-time traveler, DTA is here to make your metro journey seamless and efficient.

---

## Table of Contents
- [Problem Statement](#problem-statement)
- [Functionalities](#functionalities)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [Upcoming Features](#upcoming-features)
- [Contributing](#contributing)
- [License](#license)

---

## Problem Statement
Navigating Delhi’s extensive metro network can be challenging due to:
- Lack of accurate, real-time information on metro arrivals and delays.
- Difficulty in finding optimal routes and interchanges.
- Accessibility challenges for visually impaired users.

DelhiTravelApp addresses these issues with a user-friendly design, real-time updates, and accessibility features.

---

## Functionalities
DelhiTravelApp provides the following core functionalities:
- **Multiple Activities/Fragments**: The app includes several activities such as:
  - **HomeActivity**: Displays metro line cards and navigational options.
  - **RoutesActivity**: Allows users to search for routes and interchanges.
  - **HistoryActivity**: Tracks past searches/routes.
  - **OptionActivity**: Lets users configure app settings.
- **Database Connectivity**: Uses a local database to store station data and route information through the `DatabaseModule` and `StationRepository`.
- **Caching of Data**: Station data is cached locally for offline use.
- **Error Handling**: Displays error messages for scenarios like missing permissions, TTS initialization failures, etc.

---

## Features

### Accessibility Features
- **Text-to-Speech (TTS)**: Voice guidance for visually impaired users.
- **Haptic Feedback**: Tactile feedback for better interaction.
- **Screen Reader Support**: Content descriptions for screen readers.
- **Disabled Person Mode**: Special mode with enhanced accessibility options.

### Native API Usage
- **Location API**: Tracks user movement between stations and provides real-time notifications.

### Sensing
- **Location Sensing**: Detects the user's position within the metro network.
- **Simulated Movement**: Simulates movement between stations with notifications.

### Unique User Interface
- **Expandable Metro Line Cards**: Visual route indicators with collapsible sections.
- **Animated Clock**: Real-time clock with animation.
- **Interactive Filters**: Chips for quick station selection.
- **Color-Coded Indicators**: Aligns with official Delhi Metro line colors for easy recognition.

### Others
- Real-time metro arrival notifications.
- Multi-language support.
---

## Installation
Follow these steps to set up the app on your local device:
1. Clone the repository:
   ```bash
   git clone https://github.com/Prince22378/DelhiTravelApp.git
   ```
2. Open the project in Android Studio.
3. Build and run the application on your emulator or Android device.

---

## Usage
- **HomeActivity**: View metro line cards and navigate to other sections.
- **RoutesActivity**: Find optimal routes and interchanges.
- **HistoryActivity**: Check your past searches or routes.
- **OptionActivity**: Configure app settings, including accessibility options.

---

## SS

---

## Upcoming Features
We’re constantly working to improve the DelhiTravelApp. Here are some features planned for future updates:
- Integration with the Delhi Metro card recharge system.
- - Locker availablility & notification setting.
- Line Interchange Naviagation.
- ....
---

# Currency Gate Android App

This folder contains a Kotlin + Jetpack Compose Android app for real-time currency conversion.

## Data source

The app fetches current exchange rates from `https://api.frankfurter.app/latest`.

> Note: Google does not provide a free official public currency conversion API for Android clients. This implementation uses a reliable public exchange endpoint to provide live rates.

## Features

- Live amount conversion (e.g., USD -> BDT)
- Displays calculated result, normalized rate, and last update date
- Simple Compose UI

## Run

1. Open `android-app` in Android Studio (Hedgehog or newer).
2. Let Gradle sync.
3. Run the `app` module on emulator/device.

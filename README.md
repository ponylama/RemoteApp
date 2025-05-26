# 📷 Remote-Controlled Android Camera over HTTP

This project lets you remotely control an Android phone's **camera and data access** over HTTP. It includes:

- ✅ An Android app that runs a **headless Ktor HTTP server** to:
    - Open the camera
    - Take a photo
    - Return device info
- 🐍 A Python client to trigger these actions remotely

Use it for automation, device diagnostics, or remote image capture on the same local network.

---

## 🚀 Features

### ✅ Android App (Kotlin + CameraX + Ktor)
- Opens camera programmatically
- Takes photos and saves them with timestamped names
- Serves device info (model, version, ID, etc.)
- Can get device ip from UI.
- Built with **CameraX**, **Ktor**.

### 🐍 Python Client
- Sends HTTP requests to the Android app (get android ip from UI in log)
- Easy to run from PC or server
- Shows real-time response from the device
- Should be at the same wifi network.

---

## 📡 HTTP API (from Android App)

| Method | Endpoint       | Description                 |
|--------|----------------|-----------------------------|
| GET    | `/`            | Health check (server running) |
| GET    | `/getprop`     | Get device property info    |
| POST   | `/opencamera`  | Open the device's camera    |
| POST   | `/takephoto`   | Take a photo and save it    |

---

## 🧑‍💻 How to Run

### 1. Android App Setup
- Import the project 'androidRemoteApp' into **Android Studio**
- The app automatically starts the HTTP server on **port 8080**
- Make sure to accept the **Camera permission** when prompted

### 1. python App Setup
- open the project 'pythonRemoteApp'
- pip install -r requirements.txt
- edit ip on python script
- run

#### ✅ Steps
1. Connect your Android device to the same **Wi-Fi network** as your computer
2. Build and run the app on the device
3. get android ip from logs after click 'get ip'
4. edit ip on python script (better to use pycharm)
5. pip install -r requirements.txt





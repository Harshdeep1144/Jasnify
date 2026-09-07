<p align="center">
  <img src="https://res.cloudinary.com/jasnify/image/upload/v1788795096/B2C_App_Logo_1200_630_qhf4au.png" alt="Jasnify App Logo" width="80%">
</p>

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Room](https://img.shields.io/badge/Database-Room-4285F4?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)

> **An AI-Powered, Real-Time Event Management & Collaboration Platform for Android.**  
> Jasnify simplifies complex multi-day event planning by offering intelligent real-time collaboration, role-based access controls, interactive event tools, and a multimodal conversational AI assistant.

---

## 🚀 What is Jasnify?

Planning events—whether weddings, corporate summits, or multi-day celebrations—involves managing moving parts across budgets, catering, guests, venues, and media. **Jasnify** centralizes this entire lifecycle into a unified mobile workspace. 

Collaborators can join dedicated event workspaces with tailored room permissions, converse hands-free with an AI planning assistant, track expenses, discover vendors, manage invitation cards, and share high-res event moments in real time.

---

## ✨ Platform Highlights

### 📅 **Multi-Day Event Workspaces & Role Controls**
* **Dynamic Event Rooms**: Specialized workspaces for *Budget, Catering, Checklist, Vendors, Venue, Guest, Cards, and Moments*.
* **Granular RBAC**: Role-Based Access Control (`OWNER`, `EDITOR`, `VIEWER`) dynamically adapts UI options and permissions per room.
* **Pre-Registration Invite Promotion**: Automatic mapping of email invitations to new user accounts upon registration.

### 🤖 **Multimodal AI Assistant & Generative UI**
* **Generative Native Components**: Rather than returning plain text responses, the AI engine dynamically generates interactive Jetpack Compose cards, venue/vendor carousels, and live budget status trackers directly in the chat thread.
* **Hands-Free Conversational Voice Mode**: Continuous speech interaction loop driven by speech recognition and voice synthesis.
* **3D Mathematical Canvas Orb (`VoiceOrb`)**: Real-time rendering of 1,800 Fibonacci points on Compose `Canvas` that dynamically morphs and pulses in sync with audio RMS volume.

### 💰 **Comprehensive Planning Modules**
* **Budget & Expense Tracker**: Live balance cards, category breakdowns, and editor attribution.
* **Catering Planner**: Cuisine filtering, dietary tags (`Veg`, `NonVeg`, `Jain`, `Eggetarian`), and dish discovery.
* **Guest List**: Contact integration, RSVP tracking, and category tags.
* **Venues & Vendors Catalog**: Interactive discovery, offer listings, and reviews with photo attachments.
* **Shared Event Moments**: High-resolution cloud media repository with nested album hierarchies.
* **In-Room Real-Time Group Chat**: Room messaging with read states, message editing windows, and FCM notifications.

---

## 📐 Architecture & Engineering Highlights

```
                  ┌─────────────────────────────────────────┐
                  │            Presentation Layer           │
                  │   Jetpack Compose (M3) + StateFlow      │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │               Domain Layer              │
                  │     Use Cases / Immutable Models / UDF  │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │                Data Layer               │
                  │  Room SQLite (Local) ── Firestore (Cloud)│
                  └─────────────────────────────────────────┘
```

### **Hybrid Offline-First Sync**
* **Zero-Latency Local Reads**: Sub-millisecond local reads powered by **Room SQLite** for instant application reactivity.
* **Real-time Ingestion**: Reactive `Flow` streams connected to **Firebase Firestore** snapshot listeners keep state synchronized across all collaborators.
* **Atomic Pending Resolution**: Cloud queries resolve pre-existing email invitations into active event memberships upon sign-up.

### **Custom Design System & Motion Graphics**
* **Continuous Squircle Geometry**: Apple-style continuous curvature for cards and bottom sheets.
* **Custom Elevation Shadows**: Multi-layered ambient and spot elevation rendering computed directly on Compose Canvas.
* **Spring-Animated Backdrop Motion**: Interactive background scaling and corner morphing during bottom sheet transitions.
* **Target-Anchored Feature Onboarding**: Dynamic cutout overlay calculations anchored to layout coordinates.

---

## 🛠️ Tech Stack Overview

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3), Navigation Compose |
| **Architecture** | Clean Architecture, MVVM / UDF, Single Activity, Hilt DI |
| **Local Persistence** | Room Database (SQLite), Encrypted Preferences |
| **Cloud Backend** | Firebase Auth, Cloud Firestore, Firebase Cloud Messaging (FCM) |
| **Security & Integrity** | Firebase App Check (Google Play Integrity API) |
| **Media & Storage** | Cloudinary API, Coil (Video & SVG Decoders) |
| **Animations** | Lottie Compose, Motion Layout Spring Physics |

---

## 🔒 Security & App Integrity

Jasnify leverages **Firebase App Check with Play Integrity API** in production to verify that cloud requests originate exclusively from genuine, untampered Jasnify app binaries downloaded from the Google Play Store.

---

## 📜 License

```
Copyright 2026 Harsh Deep

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

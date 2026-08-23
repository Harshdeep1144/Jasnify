# Notification System Implementation with FCM and Firestore

I have implemented a dynamic notification system that allows you to change notification content and UI styles directly from Firestore without requiring an app update.

## Changes Made

### 1. Dependencies Updated
- Added `firebase-messaging` to `libs.versions.toml` and `app/build.gradle.kts`.

### 2. New Notification Components
- **[NotificationConfig.kt](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/java/com/harshdeep/jasnify/notifications/model/NotificationConfig.kt)**: Data model for Firestore notification templates. Supports:
    - Title and Body
    - Image URL (for Big Picture style)
    - Deep Links
    - UI Types (`standard`, `bigText`, `bigPicture`)
- **[NotificationHelper.kt](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/java/com/harshdeep/jasnify/notifications/NotificationHelper.kt)**: Handles building and displaying notifications with different styles.
- **[FCMService.kt](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/java/com/harshdeep/jasnify/notifications/FCMService.kt)**: Listens for FCM messages. If a `notification_id` is provided in the data payload, it fetches the full config from Firestore.

### 3. App Configuration
- **[AndroidManifest.xml](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/AndroidManifest.xml)**: Registered the `FCMService` and added `POST_NOTIFICATIONS` permission.
- **[MainActivity.kt](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/java/com/harshdeep/jasnify/MainActivity.kt)**: Added permission request logic for Android 13+.

## How to Manage Notifications

### Step 1: Create a Template in Firestore
1. Go to your Firebase Console -> Firestore Database.
2. Create a collection named `notifications`.
3. Create a document with a unique ID (e.g., `welcome_message`).
4. Add the following fields:
   - `title`: "Welcome to Jasnify!"
   - `body`: "Thanks for downloading our app. Explore our new features."
   - `uiType`: "bigText" (or `standard`, `bigPicture`)
   - `imageUrl`: "https://example.com/image.png" (Optional)
   - `deepLink`: "https://jasnify.com/home" (Optional)

### Step 2: Send a Trigger Message
To send a notification to users, send an FCM **Data Message** (not a standard notification message) with this payload:
```json
{
  "to": "/topics/all",
  "data": {
    "notification_id": "welcome_message"
  }
}
```
*Note: You can also include `title` and `body` directly in the FCM data payload as a fallback.*

### Step 3: Update Without App Update
If you want to change the "Welcome" message content or add an image later:
1. Edit the `welcome_message` document in Firestore.
2. Send the FCM trigger again.
3. Users will see the updated content immediately!

## Important Notes
- **Free Tier**: This implementation uses FCM and Firestore, both of which are free on the Firebase Spark plan.
- **Permission**: The app will ask for notification permission on the first launch (for Android 13+).
- **Background Support**: Notifications will work even when the app is closed.



## Step 1: Install Android Studio
First, make sure you have Android Studio installed on your computer. It’s the main tool we’ll use to run the app. You can download it from the official Android Studio website.

## Step 2: Extract the Zip File
I’ve shared a zip file with the project. Extract the contents of the zip file to a directory on your computer.

## Step 3: Open the Project in Android Studio
Open Android Studio. On the welcome screen, click on "Open an existing Android Studio project." Navigate to the directory where you extracted the zip file and select the project folder.

## Step 4: Download the google-services.json File
The app is connected to Firebase, so you’ll need the google-services.json file for it to work properly. Here’s how to get it:

Go to the Firebase Console.
Open the Firebase project that’s linked with the student_portal app.
In the project settings, download the google-services.json file.
Place the google-services.json file in the app directory of the project in Android Studio.

## Step 5: Sync the Project with Gradle
Once you’ve added the google-services.json file, go back to Android Studio. You’ll see a notification at the top asking you to sync the project with Gradle files. Click on "Sync Now."

## Step 6: Build and Run the App
After the sync is complete, connect your Android device to your computer or set up an Android Virtual Device (AVD) in Android Studio. Then, click on the green "Run" button (the one that looks like a play button) in the toolbar.

## Step 7: Log In
Once the app is running on your device or emulator, you can log in with the credentials I’ve provided. The app should connect to Firebase and retrieve the necessary data.


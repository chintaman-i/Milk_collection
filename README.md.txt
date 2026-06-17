# Milk Collection Management System

# Project Setup Guide

Follow the below steps to run the project successfully.

---

# Step 1: Install Required Software

Install the following software:

1. Android Studio
Recommended version:

Android Studio Meerkat or newer

Download:

https://developer.android.com/studio

---

2. JDK

Required:

JDK 17+

Verify installation:

java --version

Expected:

17+

---

3. Android SDK

Open:

Android Studio

↓

SDK Manager

Install:

Android SDK Platform

Android SDK Build Tools

Android Emulator (optional)

---

# Step 2: Extract Project

Extract project ZIP:

MilkCollection.zip

Open folder:

MilkCollection

Project structure should look like:

MilkCollection/

app/

gradle/

gradlew

gradlew.bat

settings.gradle.kts

build.gradle.kts

README.md

---

# Step 3: Open Project

Open Android Studio

↓

Open Existing Project

↓

Select:

MilkCollection

Wait for:

Gradle Sync

DO NOT run project before sync completes.

---

# Step 4: Create Firebase Project

Open:

https://console.firebase.google.com

Create new project.

Example:

MilkCollection

Continue setup.

---

# Step 5: Add Android App To Firebase

Inside Firebase:

Add App

↓

Android

Package name:

com.example.milkcollection

(App package must match project package)

App nickname:

MilkCollection

Register app.

---

# Step 6: Download Firebase Config

Download:

google-services.json

Move file to:

MilkCollection/

app/

google-services.json

Final structure:

app/

src/

google-services.json

build.gradle.kts

---

# Step 7: Enable Authentication

Open:

Firebase Console

↓

Authentication

↓

Sign In Method

Enable:

1. Email / Password

Turn ON

---

2. Google Sign In

Turn ON

Save.

---

# Step 8: Configure Firestore Database

Open:

Firestore Database

↓

Create Database

Mode:

Production OR Test

Region:

Nearest region

Example:

asia-south1

Create database.

---

# Step 9: Create Required Collections

Create collection:

users

Fields:

uid

firstName

lastName

email

mobile

role

accountStatus

farmerId

---

Create:

milk_entries

Fields:

uid

quantity

fat

snf

animalType

rate

totalAmount

date

session

createdAt

---

Create:

payments

Fields:

uid

amount

paymentMethod

note

status

createdAt

---

Create:

admins

Fields:

email

role

---

# Step 10: Create Admin User

Open:

Authentication

Create admin account.

Example:

admin@gmail.com

Login once.

Open Firestore:

users

Update:

role = admin

accountStatus = approved

Example:

{

role: "admin",

accountStatus: "approved"

}

---

# Step 11: Create Firestore Indexes

IMPORTANT

Project histories use sorting.

Without indexes app will fail.

Create index:

Collection:

milk_entries

Fields:

uid ASC

createdAt DESC

Scope:

Collection

Save.

---

Create second index:

Collection:

payments

Fields:

uid ASC

createdAt DESC

Scope:

Collection

Save.

Wait until status:

Enabled

---

# Step 12: Sync Project

Open Android Studio.

Click:

Sync Project With Gradle Files

Wait until:

BUILD SUCCESSFUL

---

# Step 13: Run Application

Connect Android phone.

Enable:

Developer Options

↓

USB Debugging

Run:

Run App

OR

Shift + F10

Application should launch.

---

# Step 14: Demo Workflow

Register farmer account.

Status:

pending

Open admin.

Approve farmer.

Farmer logs in.

Admin creates:

Milk Entry

Farmer checks:

Milk History

Admin creates:

Payment Entry

Farmer confirms:

Received

Admin checks:

Farmer Management

Global Histories

---

# Generate APK

Open:

Build

↓

Build APK(s)

APK location:

app/build/outputs/apk/debug/

Generated file:

app-debug.apk

---

# Troubleshooting

Problem:

Build failed

Solution:

Delete:

build/

app/build/

Sync again.

---

Problem:

Firebase error

Solution:

Check:

google-services.json exists

Path:

app/google-services.json

---

Problem:

History sorting fails

Solution:

Check Firestore indexes.

Create:

uid + createdAt index.

---

Problem:

Login denied

Check:

accountStatus

Must be:

approved

---

Project Ready
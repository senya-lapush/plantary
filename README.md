# Plantary - Home Gardening App [Under Development]
App for setting notifications to remind users about watering their plants. Notifications can be set by either specifying time or connecting Arduino system with humidity sensors.

## Table of Contents
* [Technologies](#technologies)
* [Features](#features)
* [Firestore Database](#firestore-database)
* [Arduino System](#arduino-system)
* [Screenshots](#screenshots)

## Technologies
Project is created with:
* Android Studio (Java)
* Firebase (Cloud Firestore + Realtime Database + Storage)
* Arduino

## Features
* Registration and Authentication
* Plant Library (includes images, descriptions and plant care tips)
* Adding, Editing and Deleting Info on user's plant
* Possibility to connect Arduino system to get humidity level of plant soil
* Recommendations on what plant user should obtain by completing survey
* Setting Notifications by time or humidity level of plant soil

## Firestore Database

<img src="https://user-images.githubusercontent.com/99476262/204823489-ed38b971-0c8a-4a63-b7f3-27b0bdff656f.png" alt="Firestore_Database" width="600"/>

## Arduino System

<img src="https://user-images.githubusercontent.com/99476262/204823757-ed2bd580-9e91-4928-9bc6-00fe5079dc29.png" alt="Arduino_Nano" width="500"/>
<img src="https://user-images.githubusercontent.com/99476262/204823778-06584c24-c5f6-43c2-8c80-18e1d28ab3a6.png" alt="Arduino_Uno" width="500"/>

## Screenshots
* Registration and Authentication

<img src="https://user-images.githubusercontent.com/99476262/204817042-3507ad0b-4a93-4cc6-93c0-45461208fda3.jpg" alt="Registration" width="200"/> <img src="https://user-images.githubusercontent.com/99476262/204817126-e3b4d0ac-bbe9-4fe5-824c-170c2ec8146a.jpg" alt="Authentication" width="200"/> 

* Main Page

<img src="https://user-images.githubusercontent.com/99476262/204817195-5a65091e-2137-45bd-a86b-411be55f3b0a.jpg" alt="Main" width="200"/>

* Plant Library

<img src="https://user-images.githubusercontent.com/99476262/204817358-4fd6f9e5-d8d0-4fb9-a91a-b581a450ee39.jpg" alt="Plant_Library" width="200"/> <img src="https://user-images.githubusercontent.com/99476262/204817974-975ae03d-e61f-4521-8037-1b43077f3ef8.jpg" alt="Plant_Description" width="200"/> <img src="https://user-images.githubusercontent.com/99476262/204817983-4ccba44b-75a6-44b2-a915-7d2a9c57b4d7.jpg" alt="Plant_Description2" width="200"/>

* Planter Bot (Gives recommendations after completing a survey)

<img src="https://user-images.githubusercontent.com/99476262/204818500-a3692a43-f090-42ae-aa84-28e7af554d02.jpg" alt="Planter_Bot" width="200"/> <img src="https://user-images.githubusercontent.com/99476262/204818511-5f5685d1-edb4-4271-bb16-989c294af8ed.jpg" alt="Planter_Bot" width="200"/>

* Adding Plant

<img src="https://user-images.githubusercontent.com/99476262/204818873-02337574-c535-413c-8622-64228aff7d27.png" alt="Add_Plant" width="200"/> 
Setting notification by connecting arduino system (using MAC Address of ESP8266)
<img src="https://user-images.githubusercontent.com/99476262/204818951-9d3a91e0-3c29-4300-81be-d42d8ef2af1a.png" alt="Notification_Sensor" width="200"/> 
Setting notification by time
<img src="https://user-images.githubusercontent.com/99476262/204819065-5ec1f7b3-016a-4809-a388-0cadd7ae7ee0.png" alt="Notification_Time" width="200"/>

* Types of Notifications
Notification by time
<img src="https://user-images.githubusercontent.com/99476262/204820162-e7b1234a-eb48-4323-95a0-f9e52c33f538.png" alt="Add_Plant" width="400"/> 
Notification by humidity level 30-50%. User gets it every 6 hours if the humidity level is the same.
<img src="https://user-images.githubusercontent.com/99476262/204820217-eccdd678-5906-42a1-84e0-603204ec4f87.png" alt="Add_Plant" width="400"/> 
Notification by humidity level below 30%. User gets it every 2 hours if the humidity level is the same.
<img src="https://user-images.githubusercontent.com/99476262/204820284-1f04410d-4dcd-4c8a-854c-3a66d5141b8f.png" alt="Add_Plant" width="400"/> 

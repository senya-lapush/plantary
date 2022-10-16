package com.example.plantary.plant_item_main;

import android.net.Uri;

public class PlantItem {

    private final long plantInfo; //if sensorUsed equals to humidity %, else equals to time when to notify to water
    private final String plantName;
    private final String docID;
    private final String imagePath;
    private final boolean sensorUsed;
    private Uri uri;

    public PlantItem(long plantInfo, String plantName, String docID, String userID, boolean sensorUsed) {
        this.plantInfo = plantInfo;
        this.plantName = plantName;
        this.docID = docID;
        this.imagePath = "user_images/" + userID + "/" + docID + ".jpg";
        this.sensorUsed = sensorUsed;
    }

    public PlantItem(String plantName, String docID, String userID) {
        this.plantInfo = -1;
        this.plantName = plantName;
        this.docID = docID;
        this.imagePath = "user_images/" + userID + "/" + docID + ".jpg";
        this.sensorUsed = false;
    }

    public long getPlantInfo() {
        return plantInfo;
    }

    public String getPlantName() {
        return plantName;
    }

    public String getDocID() {
        return docID;
    }

    public String getImagePath() { return imagePath; }

    public boolean getSensorUsed() { return sensorUsed; }

    public Uri getPlantUri() {
        return uri;
    }

    public void setPlantUri(Uri uri) {
        this.uri = uri;
    }
}

package com.example.plantary.plant_library;

import android.net.Uri;

public class PlantLibraryItem {

    //private String plantType;
    private final String plantName;
    private final String description;
    private final String docID;
    private final String imagePath;
    private Uri uri;

    public PlantLibraryItem(String plantName, String docID, String description) {
        this.plantName = plantName;
        this.docID = docID;
        this.imagePath = "library_images/" + this.docID + ".jpg";
        this.description = description + "...";
    }

    public String getPlantName() {
        return plantName;
    }

    public String getDocID() {
        return docID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getPlantDescription() {
        return description;
    }

    public Uri getPlantUri() {
        return uri;
    }

    public void setPlantUri(Uri uri) {
        this.uri = uri;
    }
}

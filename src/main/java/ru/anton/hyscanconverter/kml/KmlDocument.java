package ru.anton.hyscanconverter.kml;


import java.util.ArrayList;
import java.util.List;

public class KmlDocument {

    private String name;
    private List<PlaceMark> placeMarks;

    public KmlDocument(){
        placeMarks = new ArrayList<>();
    }

    public void addPlaceMark(PlaceMark placeMark){
        placeMarks.add(placeMark);
    }

    public List<PlaceMark> getPlaceMarks() {
        return placeMarks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

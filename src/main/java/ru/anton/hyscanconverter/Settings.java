package ru.anton.hyscanconverter;

import ru.anton.hyscanconverter.csv.CsvReader;

import java.nio.file.Path;

public class Settings {

    private Path destinationFolder;
    private Path csvPath;
    private Path picturesPath;
    private Path iconPath;
    private String imgColumn;
    private String coordsColumn;
    private String markNameColumn;
    private String fileName;
    private String categoryName;
    private CsvReader csvReader;

    public Path getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(Path destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public Path getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(Path csvPath) {
        this.csvPath = csvPath;
    }

    public Path getPicturesPath() {
        return picturesPath;
    }

    public void setPicturesPath(Path picturesPath) {
        this.picturesPath = picturesPath;
    }

    public Path getIconPath() {
        return iconPath;
    }

    public void setIconPath(Path iconPath) {
        this.iconPath = iconPath;
    }

    public String getImgColumn() {
        return imgColumn;
    }

    public void setImgColumn(String imgColumn) {
        this.imgColumn = imgColumn;
    }

    public String getCoordsColumn() {
        return coordsColumn;
    }

    public void setCoordsColumn(String coordsColumn) {
        this.coordsColumn = coordsColumn;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CsvReader getCsvReader() {
        return csvReader;
    }

    public void setCsvReader(CsvReader csvReader) {
        this.csvReader = csvReader;
    }

    public String getMarkNameColumn() {
        return markNameColumn;
    }

    public void setMarkNameColumn(String markNameColumn) {
        this.markNameColumn = markNameColumn;
    }
}

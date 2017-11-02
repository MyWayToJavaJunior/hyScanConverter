package ru.anton.hyscanconverter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import ru.anton.hyscanconverter.csv.CsvReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private CsvReader csvReader;

    @FXML
    private TextField csvFileField;

    @FXML
    private AnchorPane columnsPane;

    @FXML
    private ChoiceBox<String> imageColumn;

    @FXML
    private ChoiceBox<String> coordsColumn;

    @FXML
    private ChoiceBox<String> nameColumn;

    @FXML
    private TextField imagesFolderField;

    @FXML
    private TextField iconFileField;

    @FXML
    private TextField categoryNameField;

    @FXML
    void convert(ActionEvent event) {

        Settings settings = new Settings();
        settings.setCategoryName(categoryNameField.getText());
        settings.setCoordsColumn(coordsColumn.getValue());
        settings.setCsvPath(Paths.get(csvFileField.getText()));
        settings.setIconPath(Paths.get(iconFileField.getText()));
        settings.setPicturesPath(Paths.get(imagesFolderField.getText()));
        settings.setImgColumn(imageColumn.getValue());
        settings.setCsvReader(csvReader);
        settings.setMarkNameColumn(nameColumn.getValue());

        CsvToKmlConverter converter = new CsvToKmlConverter();
        try {
            String result = converter.convert(csvReader, settings);

            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("kml","*.kml"));
            File file = chooser.showSaveDialog(categoryNameField.getScene().getWindow());

            if (file!=null){
                Files.write(file.toPath(), result.getBytes());
            }

        } catch (IOException | TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void selectCsvFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv", "*.csv"));
        fileChooser.setTitle("Select csv file");
        File file = fileChooser.showOpenDialog(categoryNameField.getScene().getWindow());

        if (file!=null){
            csvFileField.setText(file.getAbsolutePath());
        }
    }



    @FXML
    void selectIconFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select icon file");
        File file = fileChooser.showOpenDialog(categoryNameField.getScene().getWindow());

        if (file!=null){
            iconFileField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void selectImagesFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select image directory");
        File dir = directoryChooser.showDialog(categoryNameField.getScene().getWindow());
        if (dir!=null){
            imagesFolderField.setText(dir.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        csvReader = new CsvReader();


        csvFileField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                csvReader.loadCsv(newValue);
            } catch (IOException e) {
                e.printStackTrace();
            }

            fillChoiceBox();
            columnsPane.setDisable(false);
        });
    }



    private void fillChoiceBox(){
        List<String> headers = csvReader.getHeader();
        imageColumn.getItems().addAll(headers);
        imageColumn.setValue(headers.get(0));
        coordsColumn.getItems().addAll(headers);
        coordsColumn.setValue(headers.get(0));
        nameColumn.getItems().addAll(headers);
        nameColumn.setValue(headers.get(0));
    }


}

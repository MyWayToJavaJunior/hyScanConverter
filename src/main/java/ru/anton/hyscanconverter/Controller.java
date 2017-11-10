package ru.anton.hyscanconverter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.anton.hyscanconverter.csv.CsvReader;
import ru.anton.hyscanconverter.exceptions.ParseCoordsException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private final String csvPropertyName = "HyScanConverter.csvPath";
    private final String imgPropertyName = "HyScanConverter.imgPath";
    private final String savePropertyName = "HyScanConverter.savePath";
    private final String defaultImgColumn = "Нет";
    private CsvReader csvReader;
    private PropertiesManager propertiesManager;


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
    private AnchorPane convertPane;

    @FXML
    private AnchorPane pathsPane;

    @FXML
    private Label imageFolderLabel;

    @FXML
    private Button imageFolderBtn;

    @FXML
    private Label resultLabel;

    @FXML
    void showHelp(){
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane, 1100, 800);

        WebView webView = new WebView();
        webView.setPrefWidth(1100);
        webView.setPrefHeight(800);
        WebEngine webEngine = webView.getEngine();

        URL url = this.getClass().getResource("/help/help.html");

        webEngine.load(url.toString());
        pane.getChildren().add(webView);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void convert(ActionEvent event) {

        Settings settings = new Settings();
        settings.setCategoryName(categoryNameField.getText());
        settings.setCoordsColumn(coordsColumn.getValue());
        settings.setCsvPath(Paths.get(csvFileField.getText()));
        settings.setIconPath(Paths.get(iconFileField.getText()));
        if (imagesFolderField.getText().equals("Нет")){
            settings.setPicturesPath(null);
            settings.setImgColumn(null);
        } else {
            settings.setPicturesPath(Paths.get(imagesFolderField.getText()));
            settings.setImgColumn(imageColumn.getValue());
        }

        settings.setCsvReader(csvReader);
        settings.setMarkNameColumn(nameColumn.getValue());

        CsvToKmlConverter converter = new CsvToKmlConverter();


        try {
            String result = null;
            try {
                result = converter.convert(csvReader, settings);
            } catch (ParseCoordsException e) {
                e.printStackTrace();
                resultLabel.setText(e.getMessage());
                return;
            }

            FileChooser chooser = new FileChooser();
            String oldPath = propertiesManager.getProperty(savePropertyName);
            if (oldPath!=null){
                chooser.setInitialDirectory(new File(oldPath));
                System.out.println(oldPath);
            }

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("kml","*.kml"));
            File file = chooser.showSaveDialog(categoryNameField.getScene().getWindow());

            if (file!=null){
                Files.write(file.toPath(), result.getBytes());
                resultLabel.setText("Готово!");
                propertiesManager.setProperty(savePropertyName, file.getParent());
                propertiesManager.save();
            }

        } catch (IOException | TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
            resultLabel.setText("Что-то пошло не так!");
        }



    }

    @FXML
    void selectCsvFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv", "*.csv"));
        fileChooser.setTitle("Select csv file");

        String oldPath = propertiesManager.getProperty(csvPropertyName);
        if (oldPath!=null){
            System.out.println(oldPath);
            fileChooser.setInitialDirectory(new File(oldPath));
        }
        File file = fileChooser.showOpenDialog(categoryNameField.getScene().getWindow());

        if (file!=null){
            csvFileField.setText(file.getAbsolutePath());
            propertiesManager.setProperty(csvPropertyName, file.toPath().getParent().toString());
            propertiesManager.save();
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

        String oldPath = propertiesManager.getProperty(imgPropertyName);
        if (oldPath!=null){
            System.out.println(oldPath);
            directoryChooser.setInitialDirectory(new File(oldPath));
        }
        File dir = directoryChooser.showDialog(categoryNameField.getScene().getWindow());

        if (dir!=null){
            imagesFolderField.setText(dir.getAbsolutePath());
            propertiesManager.setProperty(imgPropertyName, dir.getParent());
            propertiesManager.save();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        csvReader = new CsvReader();
        try {
            propertiesManager = PropertiesManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        csvFileField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                csvReader.loadCsv(newValue);
            } catch (IOException e) {
                e.printStackTrace();
            }

            fillChoiceBox();
            columnsPane.setDisable(false);
            pathsPane.setDisable(false);
            convertPane.setDisable(false);
        });

        imageColumn.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals(defaultImgColumn)){
                    imageFolderBtn.setDisable(true);
                    imageFolderLabel.setDisable(true);
                    imagesFolderField.setDisable(true);
                } else {
                    imageFolderBtn.setDisable(false);
                    imageFolderLabel.setDisable(false);
                    imagesFolderField.setDisable(false);
                }
            }
        });
    }



    private void fillChoiceBox(){
        List<String> headers = csvReader.getHeader();
        imageColumn.getItems().clear();
        coordsColumn.getItems().clear();
        nameColumn.getItems().clear();
        imageColumn.getItems().add(defaultImgColumn);
        imageColumn.getItems().addAll(headers);
        imageColumn.setValue(defaultImgColumn);
        coordsColumn.getItems().addAll(headers);
        coordsColumn.setValue(headers.get(0));
        nameColumn.getItems().addAll(headers);
        nameColumn.setValue(headers.get(0));
    }


}

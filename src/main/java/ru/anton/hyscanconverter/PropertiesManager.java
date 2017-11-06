package ru.anton.hyscanconverter;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesManager {

    private Path pathToPropFile;
    private Properties properties;
    private final String folderName = "HyScanMarksConverter";
    private final String fileName = "hyscanconverter.properties";

    private static PropertiesManager instance;

    private PropertiesManager() throws IOException {
        pathToPropFile = Paths.get(System.getProperty("user.home"));
        pathToPropFile = pathToPropFile.resolve(folderName);
        if (!Files.exists(pathToPropFile)){
            Files.createDirectory(pathToPropFile);
        }
        pathToPropFile = pathToPropFile.resolve(fileName);
        if (!Files.exists(pathToPropFile)){
            Files.createFile(pathToPropFile);
        }
        properties = new Properties();
        properties.load(new FileReader(pathToPropFile.toFile()));

    }

    public static PropertiesManager getInstance() throws IOException {
        if (instance == null){
            instance = new PropertiesManager();
        }
        return instance;
    }

    public void setProperty(String key, String value){
        properties.setProperty(key, value);
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    public void save(){
        try (FileOutputStream fos = new FileOutputStream(pathToPropFile.toFile());){
            properties.store(fos, "new props");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

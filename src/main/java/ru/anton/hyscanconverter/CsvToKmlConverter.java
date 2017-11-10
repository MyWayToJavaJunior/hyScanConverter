package ru.anton.hyscanconverter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ru.anton.hyscanconverter.csv.CsvReader;
import ru.anton.hyscanconverter.exceptions.ParseCoordsException;
import ru.anton.hyscanconverter.kml.KmlDocument;
import ru.anton.hyscanconverter.kml.PlaceMark;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvToKmlConverter {

    private Settings settings;
    private CsvReader csvReader;

    public String convert(CsvReader reader, Settings settings) throws IOException, TransformerException, ParserConfigurationException, ParseCoordsException {
        this.csvReader = reader;
        this.settings = settings;

        KmlDocument document = convertCsvToKmlDokument();
        return convertKmlDocumentToXml(document);
    }


    public String convertKmlDocumentToXml(KmlDocument document) throws ParserConfigurationException, TransformerException, ParseCoordsException {

        DocumentBuilderFactory dbFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // kml element
        Element kml = doc.createElement("kml");
        /*Attr xmlns = doc.createAttribute("xmlns");
        xmlns.setValue("http://earth.google.com/kml/2.2");*/
        kml.setAttribute("xmlns", "http://earth.google.com/kml/2.2");
        doc.appendChild(kml);
        //Document element
        Element docElem = doc.createElement("Document");
        kml.appendChild(docElem);
        //Folder element
        Element folderElem = doc.createElement("Folder");
        docElem.appendChild(folderElem);
        //
        Element folderNameElem = doc.createElement("name");
        folderNameElem.appendChild(doc.createTextNode(document.getName()));
        folderElem.appendChild(folderNameElem);
        //
        Element openElem = doc.createElement("open");
        openElem.appendChild(doc.createTextNode("1"));
        folderElem.appendChild(openElem);
        //
        Element styleElem = doc.createElement("Style");
        folderElem.appendChild(styleElem);
        //
        Element listStyleElem = doc.createElement("ListStyle");
        styleElem.appendChild(listStyleElem);
        //
        Element listItemTypeElem = doc.createElement("listItemType");
        listItemTypeElem.appendChild(doc.createTextNode("check"));
        listStyleElem.appendChild(listItemTypeElem);
        //
        Element bgColorElement = doc.createElement("bgColor");
        listStyleElem.appendChild(bgColorElement);
        bgColorElement.appendChild(doc.createTextNode("00ffffff"));
        //
        for (PlaceMark placeMark :
                document.getPlaceMarks()) {
            folderElem.appendChild(createPlaceMarkElement(placeMark, doc));
        }


        /*TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult consoleResult = new StreamResult(System.out);
        transformer.transform(source, consoleResult);*/

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        /*DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(settings.getDestinationFolder().resolve(settings.getFileName()).toFile());
        transformer.transform(source, result);*/

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        return writer.toString();
    }

    private Element createPlaceMarkElement(PlaceMark placeMark, Document doc) throws ParseCoordsException {
        Element mark = doc.createElement("Placemark");
        //
        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode(placeMark.getName()));
        mark.appendChild(nameElement);
        //
        Element descriptionElement = doc.createElement("description");
        descriptionElement.appendChild(doc.createTextNode(placeMark.getDescription()));
        mark.appendChild(descriptionElement);
        //
        Element styleElement = doc.createElement("Style");
        mark.appendChild(styleElement);
        //
        Element labelStyleElement = doc.createElement("LabelStyle");
        styleElement.appendChild(labelStyleElement);
        //
        Element colorElement = doc.createElement("color");
        colorElement.appendChild(doc.createTextNode("A600FFFF"));
        labelStyleElement.appendChild(colorElement);
        //
        Element scaleElement = doc.createElement("scale");
        scaleElement.appendChild(doc.createTextNode("0.785714285714286"));
        labelStyleElement.appendChild(scaleElement);
        //
        Element iconStyleElement = doc.createElement("IconStyle");
        styleElement.appendChild(iconStyleElement);
        //
        Element iconScaleElement = doc.createElement("scale");
        iconScaleElement.appendChild(doc.createTextNode("0.5"));
        iconStyleElement.appendChild(scaleElement);
        //
        Element iconElement =  doc.createElement("Icon");
        iconStyleElement.appendChild(iconElement);
        //
        Element hrefElement = doc.createElement("href");
        hrefElement.appendChild(doc.createTextNode(placeMark.getIconPath()));
        iconElement.appendChild(hrefElement);
        //
        Element hotSpotElement = doc.createElement("hotspot");
        hotSpotElement.setAttribute("x", "0,5");
        hotSpotElement.setAttribute("y", "0");
        hotSpotElement.setAttribute("xunits", "fraction");
        hotSpotElement.setAttribute("yunits", "fraction");
        iconStyleElement.appendChild(hotSpotElement);
        //
        Element pointElement = doc.createElement("Point");
        mark.appendChild(pointElement);
        //
        Element extrudeElement = doc.createElement("extrude");
        extrudeElement.appendChild(doc.createTextNode("1"));
        pointElement.appendChild(extrudeElement);
        //
        Element coordsElement = doc.createElement("coordinates");
        coordsElement.appendChild(doc.createTextNode(prepareCoords(placeMark.getCoordinates())));
        pointElement.appendChild(coordsElement);
        return mark;
    }

    private KmlDocument convertCsvToKmlDokument() throws IOException {
        KmlDocument kmlDocument = new KmlDocument();
        kmlDocument.setName(settings.getCategoryName());

        final int imageColumn = settings.getImgColumn().equals("Нет") ? 0 : csvReader.getHeader().indexOf(settings.getImgColumn());
        final int coordsColumn = csvReader.getHeader().indexOf(settings.getCoordsColumn());
        final int markNameColumn = csvReader.getHeader().indexOf(settings.getMarkNameColumn());
        final int galsColumn = csvReader.getHeader().indexOf("Галс");
        final int headerSize = csvReader.getHeader().size();

        System.out.println(settings);

        csvReader.getRecords().forEach(record->{
            PlaceMark placeMark = new PlaceMark();
            if ((coordsColumn <= record.length-1)){
                placeMark.setCoordinates(record[coordsColumn]);

                if (!settings.getImgColumn().equals(new String("Нет".getBytes(Charset.forName("UTF-8")))) && (imageColumn <= record.length-1)){
                    String description = "<img src='"+settings.getPicturesPath().toString()+"\\"+record[imageColumn]+"'/>";
                    placeMark.setDescription(description);
                    System.out.println("set");
                } else placeMark.setDescription("No images");


                placeMark.setIconPath(settings.getIconPath().toString());
                placeMark.setName(record[markNameColumn]+"_галс"+Integer.parseInt(record[galsColumn]));
                kmlDocument.addPlaceMark(placeMark);
            }

        });
        return kmlDocument;
    }

    private String prepareCoords(String coordinates) throws ParseCoordsException {

        coordinates = coordinates.replace(",", "").replace("  ", " ");

        String[] coords = coordinates.split(" ");
        String newCoords = "";
        try {
            if (coords[0].equals("")) {
                newCoords = coords[2].replace(" ","")+","+coords[1].replace(" ","");
                newCoords = newCoords.replaceAll("°E","").replaceAll("°N", "");
            } else newCoords = coords[1]+","+coords[0];

            newCoords = newCoords+",0,0";
        } catch (ArrayIndexOutOfBoundsException e){
           throw new ParseCoordsException("Не могу обработать координаты");
        }


        return newCoords;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public static interface EventHandler{
        void handle(String msg);
    }
}

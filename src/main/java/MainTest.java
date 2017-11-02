import ru.anton.hyscanconverter.CsvToKmlConverter;
import ru.anton.hyscanconverter.kml.KmlDocument;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class MainTest {
    public static void main(String[] args) throws IOException, JAXBException {
        //Path path = Paths.get("C:\\Users\\User\\Desktop\\SONAR\\20171028_01\\метки\\18.csv");
        /*CsvReader reader = new CsvReader();
        reader.loadCsv("C:\\Users\\User\\Desktop\\SONAR\\20171028_01\\метки\\18.csv");
        System.out.println(reader.getRecordsValues("Комментарий"));*/

        CsvToKmlConverter csvToKmlConverter = new CsvToKmlConverter();

        try {
            csvToKmlConverter.convertKmlDocumentToXml(new KmlDocument());
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
}

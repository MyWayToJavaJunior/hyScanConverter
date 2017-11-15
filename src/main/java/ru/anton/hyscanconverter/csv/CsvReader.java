package ru.anton.hyscanconverter.csv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CsvReader {
    private static final String symbol = "\t";

    private Path pathToFile;
    private List<String[]>  records;
    private List<String> header;

    public void loadCsv(String path) throws IOException {
        pathToFile = Paths.get(path);
        loadCsv(pathToFile);
    }

    public void loadCsv(Path path) throws IOException {
        List<String> rawRecords = Files.readAllLines(path, Charset.forName("cp1251"));
        parseHeader(rawRecords.get(0));
        rawRecords.remove(0);
        parseRecords(rawRecords);
    }

    public List<String> getHeader(){
        return header;
    }

    public List<String[]> getRecords(){
        return records;
    }

    public String getRecordValue(int recordNumber, String headerName){
        String[] record = records.get(recordNumber);
        int index = headerName.indexOf(headerName);
        return record[index];
    }

    public List<String> getRecordsValues(String headerName){
        List<String> values = new ArrayList<>();
        int index = header.indexOf(headerName);
        records.forEach(r->{
            values.add(r[index]);
        });
        return values;
    }

    private void parseHeader(String string){
        header = Arrays.stream(string.split(symbol)).collect(Collectors.toList());
    }

    private void parseRecords(List<String> recordsList){
        records = new ArrayList<>();
        recordsList.forEach(r->{
            String[] values = r.split(symbol);
            records.add(values);
        });
    }

}

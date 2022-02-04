package com.damgem.DataImporter.Connector;

import com.damgem.DataImporter.DataImporterError;
import com.damgem.DataImporter.Field.NamedValue;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelConnector implements DataConnector {

    private final String target, subTarget;

    private String cellToString(Cell cell) {
        return switch (cell.getCellType()) {
            case _NONE, BLANK -> "";
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue();
            case FORMULA -> "<formula>";
            case BOOLEAN -> cell.getBooleanCellValue() ? "<ja>" : "<nein>";
            case ERROR -> "<error>";
        };
    }

    private List<String> rowToList(Row row) {
        if(row == null) return new ArrayList<>();
        List<String> cellStrings = new ArrayList<>(row.getLastCellNum());
        for (Cell cell : row) {
           cellStrings.add(this.cellToString(cell));
        }
        return cellStrings;
    }

    private void addRowToSheet(Sheet sheet, List<String> data) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int ci = 0; ci < data.size(); ci++) {
            row.createCell(ci, CellType.STRING).setCellValue(data.get(ci));
        }
    }

    private Workbook openWorkbook() throws DataImporterError {
        try {
            // new XSSFWorkbook(new FileInputStream(this.target));
            return new HSSFWorkbook(new FileInputStream(this.target));
        } catch(FileNotFoundException ignored) {
            // new XSSFWorkbook();
            return new HSSFWorkbook();
        } catch (Exception error) {
            throw new DataImporterError("Target Fehler", "Kann die Datei \"" + target +
                    "\" weder korrekt lesen noch erstellen.\n\n" + error.getMessage());
        }
    }

    private Sheet openSheet(Workbook workbook) {
        if(this.subTarget != null && !this.subTarget.isEmpty()) {
            Sheet sheet = workbook.getSheet(subTarget);
            return (sheet == null) ? workbook.createSheet(subTarget) : sheet;
        }
        else {
            if(workbook.getNumberOfSheets() == 0) workbook.createSheet();
            return workbook.getSheetAt(0);
        }
    }

    public ExcelConnector(String target, String subTarget) throws DataImporterError {
        if(target == null) {
            throw new DataImporterError("Unvollständige Konfiguration", "target ist null.");
        }
        this.target = target;
        this.subTarget = subTarget;
    }

    @Override
    public void write(String target, String subTarget, List<NamedValue> data) throws DataImporterError {

        Workbook workbook = this.openWorkbook();
        Sheet sheet = this.openSheet(workbook);

        // compile field name list
        List<String> dataKeys = data.stream().map(kv -> kv.name).collect(Collectors.toList());

        // read field names
        int rows = sheet.getLastRowNum();
        if(rows == -1) {
            this.addRowToSheet(sheet, dataKeys);
        } else if(!dataKeys.equals(rowToList(sheet.getRow(0)))) {
            throw new DataImporterError("Namensfehler", "Spaltennamen der Excel-Tabelle " +
                    "stimmen nicht mit den in der Konfiguration spezifizierten Feldnamen überein.");
        }

        // insert data
        List<String> dataFieldValues = data.stream().map(kv -> kv.value).collect(Collectors.toList());
        this.addRowToSheet(sheet, dataFieldValues);

        // write to file
        try (OutputStream fileOut = new FileOutputStream(target)) {
            workbook.write(fileOut);
        } catch (IOException ioe) {
            throw new DataImporterError("Schreibfehler", "Die Datei \"" +
                    Paths.get(target).toAbsolutePath() + "\" konnte nicht zum Schreiben geöffnet werden.");
        }
    }
}

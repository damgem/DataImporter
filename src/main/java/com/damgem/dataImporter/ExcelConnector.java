package com.damgem.dataImporter;

import javafx.scene.chart.PieChart;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelConnector implements DataConnector {

    private String readCell(Cell cell) {
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK: return "";
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case STRING: return cell.getStringCellValue();
            case FORMULA: return "<formula>";
            case BOOLEAN: return cell.getBooleanCellValue() ? "<ja>" : "<nein>";
            case ERROR: return "<error>";
            default: throw new RuntimeException("Unknown cell type!");
        }
    }

    private List<String> readRow(Row row) {
        if(row == null) return new ArrayList<>();
        List<String> cellStrings = new ArrayList<>(row.getLastCellNum());
        for (Cell cell : row) {
           cellStrings.add(this.readCell(cell));
        }
        return cellStrings;
    }

    @Override
    public List<String> readLastRow(String path) {
        // read workbook
        Workbook wb;
        try {
            InputStream fs = new FileInputStream(path);
            // if(path.endsWith(".xlsx")) wb = new XSSFWorkbook(fs);
            if(path.endsWith(".xls")) wb = new HSSFWorkbook(fs);
            else throw new RuntimeException("Invalid Extension!");
        } catch(FileNotFoundException ignored) {
            return new ArrayList<>();
        } catch (Exception err) {
            throw new RuntimeException(err);
        }

        // read sheet
        if(wb.getNumberOfSheets() == 0) return new ArrayList<>();
        Sheet sheet = wb.getSheetAt(0);

        // read row
        int rows = sheet.getLastRowNum();
        if(rows == -1) return new ArrayList<>();
        return this.readRow(sheet.getRow(0));
    }

    private void appendRow(Sheet sheet, List<String> data) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int ci = 0; ci < data.size(); ci++) {
            row.createCell(ci, CellType.STRING).setCellValue(data.get(ci));
        }
    }

    @Override
    public void write(String target, String subTarget, List<Field> data) throws DataConnectorError {
        // Try read an existing workbook otherwise create one
        Workbook wb;
        try {
            // wb = new XSSFWorkbook(new FileInputStream(path));
            wb = new HSSFWorkbook(new FileInputStream(target));
        } catch(FileNotFoundException ignored) {
            // wb = new XSSFWorkbook();
            wb = new HSSFWorkbook();
        } catch (Exception err) {
            throw new DataConnectorError("Target Fehler", "Kann die Datei \"" + target +
                    "\" weder korrekt lesen noch erstellen.\n\n" + err.getMessage());
        }

        // get sheet or create one
        Sheet sheet;
        if(subTarget != null) {
            sheet = wb.getSheet(subTarget);
            if(sheet == null) sheet = wb.createSheet(subTarget);
        }
        else {
            if(wb.getNumberOfSheets() == 0) wb.createSheet();
            sheet = wb.getSheetAt(0);
        }

        // compile field name list
        List<String> dataFieldNames = data.stream().map(kv -> kv.name).collect(Collectors.toList());

        // read field names
        int rows = sheet.getLastRowNum();
        if(rows == -1) {
            this.appendRow(sheet, dataFieldNames);
        } else {
            List<String> excelFieldNames = readRow(sheet.getRow(0));
            if(!dataFieldNames.equals(excelFieldNames)) {
                throw new DataConnectorError("Mapping mismatch", "Spaltennamen der Exceltabelel" +
                        "stimmen nicht mit den im Mapping spezifierten Feldnamen \u00FCberein.\n\nExcel: " +
                        excelFieldNames + "\n\nMapping: " + dataFieldNames);
            }
        }

        // insert data
        List<String> dataFieldValues = data.stream().map(kv -> kv.value.getValue()).collect(Collectors.toList());
        this.appendRow(sheet, dataFieldValues);

        // write to file
        try (OutputStream fileOut = new FileOutputStream(target)) {
            wb.write(fileOut);
        } catch (IOException ioe) {
            throw new DataConnectorError("Schreibfehler", "Die Datei \"" + target +
                    "\" konnte nicht zum schreiben ge\u00F6ffnet werden.");
        }
    }
}

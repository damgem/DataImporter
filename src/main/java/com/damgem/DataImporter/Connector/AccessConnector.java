package com.damgem.DataImporter.Connector;

import com.damgem.DataImporter.DataImporterError;
import com.damgem.DataImporter.Field.NamedValue;
import com.healthmarketscience.jackcess.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessConnector implements DataConnector {

    private final String target, subTarget;

    public AccessConnector(String target, String subTarget) throws DataImporterError {
        if(target == null) {
            throw new DataImporterError("Unvollständige Konfiguration", "target ist null.");
        }
        if(subTarget == null) {
            throw new DataImporterError("Unvollständige Konfiguration", "subTarget ist null.");
        }
        this.target = target;
        this.subTarget = subTarget;
    }

    private Database openDatabase() throws DataImporterError {
        File file = new File(target);
        if(!file.exists()) {
            throw new DataImporterError("Fehler beim Öffnen der Datenbank", "Datei "
                    + file.getAbsolutePath() + " existiert nicht.");
        }
        try {
            return DatabaseBuilder.open(file);
        } catch (Exception error) {
            throw new DataImporterError("Fehler beim Öffnen der Datenbank", "Datei "
                    + file.getAbsolutePath() + " konnte nicht korrekt gelesen werden. ");
        }
    }

    private Table openTable() throws DataImporterError {
        Database database = this.openDatabase();
        try {
            return database.getTable(subTarget);
        } catch (Exception error) {
            throw new DataImporterError("Fehler beim Öffnen der Tabelle ", "Tabelle mit dem Namen " +
                    subTarget + " konnte nicht gefunden oder nicht korrekt gelesen werden:\n" + error.getMessage());
        }
    }

    @Override
    public void write(String target, String subTarget, List<NamedValue> data) throws DataImporterError {

        Table table = this.openTable();

        List<String> accessColumnNames = table.getColumns().stream().map(Column::getName).collect(Collectors.toList());
        List<String> dataColumnNames = data.stream().map(f -> f.name).collect(Collectors.toList());

        // assure that accessColumnNames is equal to dataColumnNames
        if(accessColumnNames.size() != dataColumnNames.size())
        {
            throw new DataImporterError("Namensfehler", "Die Anzahl der Spalten in der " +
                    "Datenbank-Tabelle (" + accessColumnNames.size() + ") ist nicht gleich der Anzahl der " +
                    "konfigurierten Anzahl von Feldnamen (" + dataColumnNames.size() + ").");
        }
        for(int i = 0; i < accessColumnNames.size(); i++)
        {
            if(! accessColumnNames.get(i).equals(dataColumnNames.get(i)))
            {
                throw new DataImporterError("Namensfehler", "Der " + i+1 + ". Spaltenname (" +
                        accessColumnNames.get(i) + ") der Datenbank-Tabelle stimmt nicht mit dem " + i+1 +
                        ". konfiguriertem Feldnamen (" + dataColumnNames.get(i) + ") überein.");
            }
        }

        Map<String, Object> map = new HashMap<>();

        // make sure there are no empty numerical values
        data.forEach(f -> {
            if(table.getColumn(f.name).getType() == DataType.LONG && f.value.isEmpty()) {
                map.put(f.name, 0);
            } else {
                map.put(f.name, f.value);
            }
        });

        try { table.addRowFromMap(map); }
        catch (Exception error) {
            throw new DataImporterError("Schreibfehler", error.getMessage());
        }
    }
}

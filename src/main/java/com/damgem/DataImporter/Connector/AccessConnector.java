package com.damgem.DataImporter.Connector;

import com.damgem.DataImporter.TitledError;
import com.damgem.DataImporter.UIStringField;
import com.healthmarketscience.jackcess.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessConnector implements DataConnector {

    private String target, subTarget;

    public AccessConnector(String target, String subTarget) throws TitledError {
        if(target == null) {
            throw new TitledError("Unvollständige Konfiguration", "target ist null.");
        }
        if(subTarget == null) {
            throw new TitledError("Unvollständige Konfiguration", "subTarget ist null.");
        }
        this.target = target;
        this.subTarget = subTarget;
    }

    private Database openDatabase() throws TitledError {
        File file = new File(target);
        if(!file.exists()) {
            throw new TitledError("Fehler beim Öffnen der Datenbank", "Datei "
                    + file.getAbsolutePath() + " existiert nicht.");
        }
        try {
            return DatabaseBuilder.open(file);
        } catch (Exception error) {
            throw new TitledError("Fehler beim Öffnen der Datenbank", "Datei "
                    + file.getAbsolutePath() + " konnte nicht korrekt gelesen werden. ");
        }
    }

    private Table openTable() throws TitledError {
        Database database = this.openDatabase();
        try {
            return database.getTable(subTarget);
        } catch (Exception error) {
            throw new TitledError("Fehler beim Öffnen der Tabelle ", "Tabelle mit dem Namen " +
                    subTarget + " konnte nicht gefunden oder nicht korrekt gelesen werden:\n" + error.getMessage());
        }
    }

    @Override
    public void write(String target, String subTarget, List<UIStringField> data) throws TitledError {

        Table table = this.openTable();

        List<String> accessColumnNames = table.getColumns().stream().map(Column::getName).collect(Collectors.toList());
        List<String> dataColumnNames = data.stream().map(f -> f.name).collect(Collectors.toList());

        if(!accessColumnNames.equals(dataColumnNames)) {
            throw new TitledError("Namensfehler", "Spaltennamen der Datenbank-Tabelle " +
                    "stimmen nicht mit den in der Konfiguration spezifierten Feldnamen überein.");
        }

        Map<String, Object> map = new HashMap<>();

        // make sure there are no empty numerical values
        data.forEach(f -> {
            if(table.getColumn(f.name).getType() == DataType.LONG && f.value.isEmpty().get()) {
                map.put(f.name, 0);
            } else {
                map.put(f.name, f.value.getValue());
            }
        });

        try { table.addRowFromMap(map); }
        catch (Exception error) {
            throw new TitledError("Schreibfehler", error.getMessage());
        }
    }
}

package com.damgem.dataImporter;

import com.healthmarketscience.jackcess.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessConnector implements DataConnector {

    @Override
    public List<String> readLastRow(String path) throws DataConnectorError {
        /*Database db;
        try {
            File file = new File(path);
            if(!file.exists()) {
                throw new DataConnectorError("Datei nicht gefunden", "Datei \"" + path +
                        "\" existiert nicht. (absoluter Pfad: \"" + file.getAbsolutePath() + "\")");
            }
            db = DatabaseBuilder.open(file);
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
        Table table;
        try {
            System.out.println("table names: " + db.getTableNames());
            table = db.getTable(this.tableName);
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
        List<String> columnNames = table.getColumns().stream().map(Column::getName).collect(Collectors.toList());
        System.out.println(columnNames);
        table.stream().forEach(System.out::println);
        */
        return null;
        // return table.stream().reduce((first, second) -> second).stream().flatMap(r -> r.values().stream().map(Object::toString)).collect(Collectors.toList());
    }

    @Override
    public void write(String target, String subTarget, List<Field> data) throws DataConnectorError {
        Database db;
        if(target == null) {
            throw new DataConnectorError("Keine Access Datenbank angegeben", "\"target\" Parameter ist null.");
        }
        File file = new File(target);
        if(!file.exists()) {
            throw new DataConnectorError("Datenbank nicht gefunden", "Datei \"" + target +
                    "\" existiert nicht. (absoluter Pfad: \"" + file.getAbsolutePath() + "\")");
        }
        try {
            db = DatabaseBuilder.open(file);
        } catch (Exception error) {
            throw new DataConnectorError("Fehler beim \u00D6ffnen der Datenbank", "Datei " + target +
                    "\" konnte nicht korrekt gelesen werden. (absoluter Pfad: \"" + file.getAbsolutePath() + "\")\n\n" +
                    error.getMessage());
        }
        Table table;
        try {
            System.out.println("table names: " + db.getTableNames());
            table = db.getTable(subTarget);
        } catch (Exception error) {
            throw new DataConnectorError("Fehler beim \u00D6ffnen der Datenbanktabelle", "Tabelle \"" +
                    subTarget + "\" in der Datenbank \"" + file.getAbsolutePath() + "\" konnte nicht gefunden oder" +
                    "nicht korrekt gelesen werden.\n\n" + error.getMessage());
        }

        List<String> accessColumnNames = table.getColumns().stream().map(Column::getName).collect(Collectors.toList());
        List<String> dataColumnNames = data.stream().map(f -> f.name).collect(Collectors.toList());
        if(!accessColumnNames.equals(dataColumnNames)) {
            throw new DataConnectorError("Mapping mismatch", "Spaltennamen der Accesstabelle " +
                    "stimmen nicht mit den im Mapping spezifierten Feldnamen \u00FCberein.\n\nAccess: " +
                    accessColumnNames + "\n\nMapping: " + dataColumnNames);
        }

        Map<String, Object> map = new HashMap<>();
        data.forEach(f -> {
            if(table.getColumn(f.name).getType() == DataType.LONG && f.value.isEmpty().get()) {
                map.put(f.name, 0);
            } else {
                map.put(f.name, f.value.getValue());
            }
        });

        table.getColumns().forEach(c -> System.out.println(c.getName() + " : " + c.getType().name()));

        try {
            table.addRowFromMap(map);
        } catch (Exception error) {
            throw new DataConnectorError("Fehler beim Einf\u00FCgen einer neuen Zeile", error.getMessage());
        }
    }
}

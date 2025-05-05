package lld.rdbms;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Database {
    private String name;
    private Map<String, Table> tableMap;

    public Database(String name) {
        this.name = name;
        this.tableMap = new HashMap<>();
    }

    public void createTable(String tableName, List<Column> columns) {
        if (tableMap.containsKey(tableName)) {
            System.out.println("Table Already Exists! : " + tableName);
            return;
        }
        Table table = new Table(tableName, columns);
        tableMap.put(tableName, table);
    }

    public void dropTable(String tableName) {
        if (!tableMap.containsKey(tableName)) {
            System.out.println("Table doesn't Exists : " + tableName);
            return;
        }
        tableMap.remove(tableName);
        System.out.println("Table + " + tableName + " dropped!");
    }

    public void truncate(String tableName) {
        Table table = tableMap.get(tableName);
        table.truncateRows();
    }

    public void insertTableRows(String tableName, Map<Column, Object> columnValues) {
        if (!checkIfTableExists(tableName)) {
            return;
        }
        Table table = tableMap.get(tableName);
        table.insertRow(columnValues);
    }

    public void printTableAllRows(String tableName) {
        if(!checkIfTableExists(tableName)) return;
        Table table = tableMap.get(tableName);
        table.printRows();
    }

    public void filterTableRecordsByColumnValue(String tableName, Column column, Object value) {
        if(!checkIfTableExists(tableName)) return;
        Table table = tableMap.get(tableName);
        List<Row> rows = table.getRecordsByColumnValue(column, value);
        System.out.println("Print matching rows for the Table : " + tableName);
        table.printRecords(rows);
    }

    private Boolean checkIfTableExists(String tableName) {
        if(!tableMap.containsKey(tableName)) {
            System.out.println("TableName: "+tableName+" does not exists");
            return false;
        }
        return true;
    }
}

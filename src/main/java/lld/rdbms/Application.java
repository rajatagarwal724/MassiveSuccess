package lld.rdbms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {

    private static final String tableName = "Employee";

    public static void main(String[] args) {
        Column name = new Column("name", ColumnType.STRING);
        Column age = new Column("age", ColumnType.INT);
        Column salary = new Column("salary", ColumnType.INT);

        Database db = new Database("MyDB");

        List<Column> columns = new ArrayList<>();
        columns.add(name);
        columns.add(age);
        columns.add(salary);

        db.createTable(tableName, columns);

        Map<Column, Object> columnValues = new HashMap<>();
        columnValues.put(name, "John");
        columnValues.put(age, 25);
        columnValues.put(salary, 10000);

        db.insertTableRows(tableName, columnValues);

        db.printTableAllRows(tableName);


//        columnValues.clear();
//        columnValues.put(name, "Kim");
//        columnValues.put(age, 28);
//        columnValues.put(salary, 12000);
//        db.insertTableRows(tableName,columnValues);
//        db.printTableAllRows(tableName);
//        db.filterTableRecordsByColumnValue(tableName, age, 28);
//
//        db.filterTableRecordsByColumnValue(tableName, name, "John");
//        db.truncate(tableName);
//        db.dropTable(tableName);
//        db.printTableAllRows(tableName);
    }
}

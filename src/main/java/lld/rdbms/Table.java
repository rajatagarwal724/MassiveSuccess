package lld.rdbms;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class Table {
    private AtomicInteger autoIncrementId;
    private String name;
    // Column Name to Type Mappings
    private Map<String, Column> columnMap;
    private List<Row> rows = new ArrayList<>();

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columnMap = columns
                .stream()
                .collect(
                        Collectors.toMap(
                                Column::getName,
                                Function.identity(),
                                (x, y) -> y,
                                LinkedHashMap::new
                        )
                );
        this.autoIncrementId = new AtomicInteger();
    }

    protected void truncateRows() {
        getRows().clear();
    }

    protected void insertRow(Map<Column, Object> columnValues) {
        Optional<Map.Entry<Column, Object>> unRecognizedCol = columnValues
                .entrySet()
                .stream()
                .filter(entry -> !getColumnMap().containsKey(entry.getKey().getName()))
                .findFirst();

        if (unRecognizedCol.isPresent()) {
            System.out.println("Column not Defined : " + unRecognizedCol.get().getKey().getName());
            return;
        }
        Integer rowId = getAutoIncrementId();
        Row row = new Row(rowId, columnValues);
        this.rows.add(row);
    }

    protected List<Row> getRecordsByColumnValue(Column column, Object vale) {
        return this.rows
                .stream()
                .filter(row -> row.getColumnData().containsKey(column) && row.getColumnData().get(column).equals(vale))
                .collect(Collectors.toList());
    }

    protected void printRows() {
        System.out.println("Print all rows of Table: " + name);
        printRecords(this.rows);
    }

    protected void printRecords(List<Row> rows) {
        System.out.print("\t");
        for (Map.Entry<String, Column> entry: columnMap.entrySet()) {
            System.out.print(entry.getKey());
            System.out.print("\t");
        }

        for (Row row: rows) {
            System.out.print("\n" + row.getRowId() +"." + "\t");
            for (Map.Entry<String, Column> entry: columnMap.entrySet()) {
                System.out.print(row.getColumnData().get(entry.getValue()));
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    public int getAutoIncrementId() {
        return this.autoIncrementId.incrementAndGet();
    }
}

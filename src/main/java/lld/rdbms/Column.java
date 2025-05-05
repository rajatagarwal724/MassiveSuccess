package lld.rdbms;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Column {
    private String name;
    private ColumnType type;
}

package lld.rdbms;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
public class Row {
    private Integer rowId;
    private Map<Column, Object> columnData;
}

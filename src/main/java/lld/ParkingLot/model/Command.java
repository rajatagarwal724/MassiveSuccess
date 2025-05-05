package lld.ParkingLot.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class Command {
    private String name;
    private List<String> params;

    public Command(final String input) {
        String[] args = input.split(StringUtils.EMPTY);
        this.name = args[0];
        this.params = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            params.add(args[1]);
        }
    }
}

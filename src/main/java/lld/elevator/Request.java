package lld.elevator;

import lombok.Getter;

@Getter
public class Request {
    private int floor;

    public Request(int floor) {
        this.floor = floor;
    }
}

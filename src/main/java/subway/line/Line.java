package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import subway.station.Station;
import subway.station.StationDao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Line {
    private static final int FIRST_INDEX = 0;
    public static final int MIN_SECTION_SIZE = 1;

    private Long id;
    private String name;
    private String color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}

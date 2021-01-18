package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Line;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class LineChangeRequest {
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,255}$")
    @NotEmpty
    private final String name;
    @Size(min = 1, max = 20)
    @NotEmpty
    private final String color;

    public LineChangeRequest(@JsonProperty("name") String name,
                             @JsonProperty("color") String color) {
        this.name = name;
        this.color = color;
    }

    public Line getDomain() {
        return new Line(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}

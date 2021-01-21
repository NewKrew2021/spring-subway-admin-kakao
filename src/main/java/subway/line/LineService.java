package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionDao;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
public class LineService {
    private StationDao stationDao;
    private LineDao lineDao;
    private SectionDao sectionDao;

    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao){
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse createLine(LineRequest lineRequest){
        Line line = new Line(lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getExtraFare());

        Line newline = lineDao.save(line, lineRequest);
        sectionDao.save(new Section(newline.getId(), lineRequest.getUpStationId(), 0));
        sectionDao.save(new Section(newline.getId(), lineRequest.getDownStationId(), lineRequest.getDistance()));

        return makeLineResponse(newline);
    }

    public List<LineResponse> showLines(){
        return lineDao.findAll().stream()
                .map((Line line) -> makeLineResponse(line))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id){
        Line line = lineDao.findById(id);
        return makeLineResponse(line);
    }

    private LineResponse makeLineResponse(Line line){
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                getStationInfo(line.getId()),
                line.getExtraFare());
    }

    private List<StationResponse> getStationInfo(Long id) {
        List<Station> stations = new ArrayList<>();

        for (Section section : sectionDao.findByLineId(id)) {
            stations.addAll(stationDao.findByUpDownId(section.getStationId()));
        }

        return stations.stream()
                .distinct()
                .filter(distinctByKey(station -> station.getName()))
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public void updateLine(Long id, LineRequest lineRequest){
        lineDao.update(id, lineRequest);
    }

    public void deleteById(Long id){
        lineDao.deleteById(id);
    }
}


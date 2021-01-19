package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionDao;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;
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
        sectionDao.save(new Section(newline.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()));


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
        List<Long> stations = new ArrayList<>();

        sectionDao.findByLineId(id).stream()
                .forEach(section -> {
                    stations.add(stationDao.findById(section.getUpStationId()).getId());
                    stations.add(stationDao.findById(section.getDownStationId()).getId());
                });

        return stations.stream()
                .map(stationId -> new StationResponse(stationId, stationDao.findById(stationId).getName()))
                .collect(Collectors.toList());
    }

    public void updateLine(Long id, LineRequest lineRequest){
        lineDao.update(id, lineRequest);
    }

    public void deleteById(Long id){
        lineDao.deleteById(id);
        sectionDao.deleteByLineId(id);
    }
}

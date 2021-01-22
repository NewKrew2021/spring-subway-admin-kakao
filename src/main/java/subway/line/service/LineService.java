package subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.line.domain.Line;
import subway.line.dao.LineDao;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.section.domain.Section;
import subway.section.dao.SectionDao;
import subway.station.dao.StationDao;
import subway.station.domain.Stations;
import subway.station.dto.StationResponse;

import java.util.*;
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

    @Transactional
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
        List<Long> stationIdGroup = sectionDao.findByLineId(id).stream()
                .map(Section::getStationId)
                .collect(Collectors.toList());

        Stations stations = new Stations(stationDao.findByUpDownId(stationIdGroup));

        return stations.getStationResponse();
    }

    public void updateLine(Long id, LineRequest lineRequest){
        lineDao.update(id, lineRequest);
    }

    public void deleteById(Long id){
        lineDao.deleteById(id);
    }
}


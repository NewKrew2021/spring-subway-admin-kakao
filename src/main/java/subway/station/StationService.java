package subway.station;

import org.springframework.stereotype.Service;
import subway.station.domain.Station;
import subway.station.vo.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResultValue create(StationCreateValue createValue) {
        Station insertSection = stationDao.insert(new Station(createValue.getName()));

        Station newStation = Optional.ofNullable(insertSection)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Station with name %s already exists", createValue.getName())));

        return newStation.toResultValue();
    }

    public StationResultValues findAll() {
        return new StationResultValues(stationDao.findAll()
                .stream()
                .map(station -> new StationResultValue(station.getID(), station.getName()))
                .collect(Collectors.toList()));
    }

    public StationResultValue findByID(StationReadValue findValue) {
        Station foundSection = stationDao.findByID(new Station(findValue.getID()));

        Station station = Optional.ofNullable(foundSection)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Could not find station with id: %d", findValue.getID())));

        return station.toResultValue();
    }

    public void deleteByID(StationDeleteValue deleteValue) {
        Station station = stationDao.deleteByID(new Station(deleteValue.getID()));

        if (wasNotDeleted(station)) {
            throw new NoSuchElementException(String.format("Could not delete station %d", deleteValue.getID()));
        }
    }

    private boolean wasNotDeleted(Station station) {
        return station != null;
    }
}

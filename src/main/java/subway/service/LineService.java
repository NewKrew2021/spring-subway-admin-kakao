package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dto.Line;

import java.util.List;

@Service
public class LineService {

    @Autowired
    LineDao lineDao;


    public boolean insertLine(Line line) {
        if (lineDao.isContainSameName(line.getName())) {
            return false;
        }
        return lineDao.save(line) != 0;
    }

    public Line findLineByName(String name) {
        return lineDao.findLineByName(name);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void modifyLine(Line line) {
        lineDao.updateLine(line);
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }

}

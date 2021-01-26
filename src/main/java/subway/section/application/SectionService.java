package subway.section.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import subway.section.dao.SectionDao;
import subway.section.domain.Section;
import subway.section.domain.Sections;
import subway.section.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public ResponseEntity<Void> insertSection(SectionRequest sectionRequest, long lineId) {
        Sections sections = new Sections(sectionDao.getSections(lineId));
        Section newSection = sections.checkAddSection(sectionRequest, lineId);

        if (newSection != null) {
            sectionDao.save(newSection);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public ResponseEntity<Void> deleteSection(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.getSections(lineId));
        if (sections.isLeastSizeSections() || !sections.hasSection(stationId)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        sectionDao.delete(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}

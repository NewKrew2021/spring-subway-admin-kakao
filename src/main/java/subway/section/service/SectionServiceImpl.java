package subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import subway.common.exception.NotDeletableEntityException;
import subway.common.exception.NotExistEntityException;
import subway.common.exception.NotUpdatableEntityException;
import subway.section.dao.SectionDao;
import subway.section.vo.Section;
import subway.section.vo.Sections;

@Service
@Transactional
public class SectionServiceImpl implements SectionService {
    private final SectionDao sectionDao;

    public SectionServiceImpl(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Override
    public Section create(Section section) {
        return sectionDao.insert(section);
    }

    @Override
    public void connect(Section section) {
        Sections sections = sectionDao.findSectionsByLineId(section.getLineId());

        // 종점에 연결
        if (sections.isExtendable(section)) {
            sectionDao.insert(section);
            return;
        }

        // 중간에 연결
        // 1. 축소 가능한 구간을 탐색
        // 2. 구간 축소 (수정)
        // 3. 구간 연결 (삽입)
        Section collapsibleSection = sections.findCollapsibleSection(section)
                .orElseThrow(() -> new IllegalStateException("연결할 수 없는 지하철 구간입니다."));

        sectionDao.update(collapsibleSection.collapse(section));
        sectionDao.insert(section);
    }

    @Override
    @Transactional(readOnly = true)
    public Section findSectionById(Long id) {
        return sectionDao.findSectionById(id)
                .orElseThrow(() -> new NotExistEntityException("존재하지 않는 지하철 구간입니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Sections findSectionsByLineId(Long lineId) {
        return sectionDao.findSectionsByLineId(lineId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void update(Section section) {
        if (isNotExist(section.getId())) {
            throw new NotExistEntityException("존재하지 않는 지하철 구간 입니다.");
        }

        if (isNotUpdated(sectionDao.update(section))) {
            throw new NotUpdatableEntityException("지하철 구간을 수정할 수 없습니다.");
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void delete(Long id) {
        if (isNotExist(id)) {
            throw new NotExistEntityException("존재하지 않는 지하철 구간 입니다.");
        }

        if (isNotUpdated(sectionDao.delete(id))) {
            throw new NotDeletableEntityException("지하철 구간을 삭제할 수 없습니다.");
        }
    }

    @Override
    public void deleteByLineId(Long lineId) {
        // 지하철 노선을 삭제할 때는 삭제 가능 여부를 검사하지 않는다 (강제 삭제)
        sectionDao.findSectionsByLineId(lineId)
                .stream()
                .forEach(section -> delete(section.getId()));
    }

    @Override
    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        // 지하철 역을 삭제할 때는 삭제 가능 여부를 검사한다
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        if (sections.isNotDeletable()) {
            throw new NotDeletableEntityException("삭제할 수 없는 지하철 구간입니다.");
        }

        sections = sections.filterByStationId(stationId);
        assert sections.size() == 1 || sections.size() == 2;

        sections.stream()
                .forEach(section -> delete(section.getId()));

        if (sections.isMultiple()) {
            sectionDao.insert(sections.merge());
        }
    }

    private boolean isNotExist(Long id) {
        return !sectionDao.findSectionById(id).isPresent();
    }

    private boolean isNotUpdated(int update) {
        return update == 0;
    }
}

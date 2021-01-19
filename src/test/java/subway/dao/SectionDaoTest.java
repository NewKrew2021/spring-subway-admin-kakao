package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.domain.SectionGroup;
import subway.exception.NoContentException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SectionDaoTest {

    private final SectionDao sectionDao;

    @Autowired
    public SectionDaoTest(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Test
    @DisplayName("구간이 제대로 저장되는지 확인한다.")
    void save() {
        Section savedSection = sectionDao.save(new Section(1L, 1L, 2L, 10));
        Section foundSection = sectionDao.findOne(savedSection.getId());

        assertThat(savedSection).isEqualTo(foundSection);
    }

    @Test
    @DisplayName("SectionGroup이 모두 저장되는지 확인한다.")
    void saveAll() {
        SectionGroup savedSections = new SectionGroup(Arrays.asList(
                new Section(1L, 1L, 2L, 10),
                new Section(1L, 2L, 3L, 10)));

        sectionDao.saveAll(savedSections);

        assertThat(sectionDao.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("목록을 조회했을 때 개수가 정확한지 확인한다.")
    void findAllByLineId() {
        sectionDao.save(new Section(1L, 1L, 2L, 10));
        sectionDao.save(new Section(1L, 2L, 3L, 10));
        sectionDao.save(new Section(2L, 4L, 5L, 10));

        assertThat(sectionDao.findAllByLineId(1L).size()).isEqualTo(2);
        assertThat(sectionDao.findAllByLineId(2L).size()).isEqualTo(1);
    }

    @Test
    @DisplayName("구간이 제대로 수정되는지 확인한다.")
    void update() {
        Section savedSection = sectionDao.save(new Section(1L, 1L, 2L, 10));
        Section updatedSection = new Section(savedSection.getId(), 1L, 1L, 3L, 5);

        sectionDao.update(updatedSection);
        Section foundSection = sectionDao.findOne(savedSection.getId());

        assertThat(updatedSection).isEqualTo(foundSection);
    }

    @Test
    @DisplayName("삭제했을 때 더 이상 조회되지 않는 것을 확인한다.")
    void deleteById() {
        assertThatThrownBy(() -> {
            Section section = sectionDao.save(new Section(1L, 1L, 2L, 10));
            sectionDao.deleteById(section.getId());
            sectionDao.findOne(section.getId());
        }).isInstanceOf(NoContentException.class);
    }
}
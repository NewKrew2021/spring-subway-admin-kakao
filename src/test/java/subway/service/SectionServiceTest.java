package subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import subway.dao.LineDao;
import subway.domain.Line;

public class SectionServiceTest {

    @Autowired
    SectionService sectionService;
    @Autowired
    LineDao lineDao;

    @BeforeEach
    void Setup(){

    }

    @Test
    public void testInsertSection(){


    }



}

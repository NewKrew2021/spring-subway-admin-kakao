package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import subway.line.LineRequest;

@Repository
public class SectionDao {

    final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(SectionRequest sectionRequest, long lineId){

    }
    /*
    create table if not exists SECTION
(
    id bigint auto_increment not null,
    line_id bigint not null,
    up_distance int,
    station_id bigint not null,
    down_distance int,
    next_id bigint,
    primary key(id)
    );

    * */
    public void save(Section section){
        String SQL = "INSERT INTO section (lind_id ,up_distance, station_id, down_distance, next_id ) VALUES (?, ?, ?, ?, ?)";
    }

    public LineRequest getSections(long lineId) {
        return null;
    }
}

package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;

@Repository
public class SectionDao {
    LinkedList<Section> sections;

    private JdbcTemplate jdbcTemplate;

//    public Section save(Section section) {
//        String sql = "insert into section (name, color) values (?, ?)";
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbcTemplate.update(connection -> {
//            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
//            ps.setString(1, line.getName());
//            ps.setString(2, line.getColor());
//            return ps;
//        }, keyHolder);
//        Line persistLine = new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
//
//        return persistLine;
//    }

}

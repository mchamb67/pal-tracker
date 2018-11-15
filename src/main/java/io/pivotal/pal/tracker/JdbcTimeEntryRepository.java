package io.pivotal.pal.tracker;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private ArrayList<TimeEntry> timeEntries=new ArrayList<TimeEntry>();
    private JdbcTemplate template;
//    private final ResultSetExtractor<TimeEntry> extractor =
//            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
    private String updateSQL = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)";
    public JdbcTimeEntryRepository( DataSource dataSource){
     this.template= new JdbcTemplate(dataSource);

    }
//    KeyHolder keyHolder= new KeyHolder() {
//        @Override
//        public Number getKey() throws InvalidDataAccessApiUsageException {
//
//            return id;
//        }
//
//        @Override
//        public Map<String, Object> getKeys() throws InvalidDataAccessApiUsageException {
//            return null;
//        }
//
//        @Override
//        public List<Map<String, Object>> getKeyList() {
//            return null;
//        }
//    };

    public TimeEntry create(TimeEntry timeEntry) {
        //insert into database
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        // define SQL types of the arguments
        int[] types = {Types.BIGINT, Types.BIGINT, Types.DATE, Types.INTEGER};

        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(updateSQL, RETURN_GENERATED_KEYS);
            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

                    return statement;
        }, generatedKeyHolder);


       // int rows = template.update(updateSQL, types,generatedKeyHolder);
        //int id = (int) keyHolder.getKey();
        return find(generatedKeyHolder.getKey().longValue());
    }

    public TimeEntry find(long timeEntryId) {
        //Look at casting
        try {
           // KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            Integer thisHours = template.queryForObject("select hours from time_entries where id=?",new Object[]{timeEntryId}, Integer.class);
            BigInteger ProjectID = template.queryForObject("select project_id from time_entries where id=?",new Object[]{timeEntryId}, BigInteger.class);

            BigInteger UserID = template.queryForObject("select user_id from time_entries where id=?",new Object[]{timeEntryId}, BigInteger.class);
            Date thisDate = template.queryForObject("select date from time_entries where id=?",new Object[]{timeEntryId}, Date.class);

           // System.out.println("--__--------------------------------",insertedTimeEntry);
            //if(ProjectID==null|| UserID==null||thisDate==null ||thisHours==null){
            //    return null;
            //}
            TimeEntry insertedTimeEntry = new TimeEntry(timeEntryId,ProjectID.longValue(),UserID.longValue(),thisDate.toLocalDate(), thisHours);
            return insertedTimeEntry;
        }
        catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    public List<TimeEntry> list() {
        List<TimeEntry> timeEntries=new ArrayList<TimeEntry>();

        timeEntries=template.query("select * from time_entries",new ResultSetExtractor<List<TimeEntry>>() {
                    @Override
                    public List<TimeEntry> extractData(ResultSet rs) throws SQLException,
                            DataAccessException {

                        List<TimeEntry> list = new ArrayList<TimeEntry>();
                        while (rs.next()) {
                            TimeEntry timeEntry = new TimeEntry();
                            timeEntry.setId(rs.getInt("id"));
                            timeEntry.setProjectId(rs.getInt("project_id"));
                            timeEntry.setUserId(rs.getInt("user_id"));
                            timeEntry.setDate(rs.getDate("date").toLocalDate());
                            timeEntry.setHours(rs.getInt("hours"));
                            list.add(timeEntry);
                        }
                        return list;
                    }
                });

        return timeEntries;
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        try{

            template.update(connection -> {
                PreparedStatement statement = connection.prepareStatement("Update time_entries SET project_id=?, user_id=? , date=?, hours=? where id=? ");
                statement.setLong(1, timeEntry.getProjectId());
                statement.setLong(2, timeEntry.getUserId());
                statement.setDate(3,java.sql.Date.valueOf(timeEntry.getDate()));
                statement.setInt(4, timeEntry.getHours());
                statement.setLong(5, id);

                return statement;
            });

            return find(id);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public TimeEntry delete(long id) {


        TimeEntry deletedTimeEntry= find(id);
        // define SQL types of the arguments

        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("Delete from time_entries where id=?");
            statement.setLong(1, id);

            return statement;
        });


        // int rows = template.update(updateSQL, types,generatedKeyHolder);
        //int id = (int) keyHolder.getKey();
        return deletedTimeEntry;
    }
}

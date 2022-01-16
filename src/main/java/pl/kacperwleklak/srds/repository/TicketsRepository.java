package pl.kacperwleklak.srds.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import pl.kacperwleklak.srds.model.dao.TicketDAO;
import pl.kacperwleklak.srds.model.dao.TicketKey;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketsRepository extends CassandraRepository<TicketDAO, TicketKey> {

    @Query(value = "SELECT DISTINCT date, theater FROM ticket")
    List<TicketDAO> findDistinctShows();

    @Query(value = "SELECT * FROM ticket WHERE date=?0 AND theater=?1 LIMIT 1")
    TicketDAO findDistinctByTicketKeyDateAndTicketKeyTheater(LocalDateTime localDateTime, int theater);

    List<TicketDAO> findByTicketKeyDateAndTicketKeyTheater(LocalDateTime localDateTime, int theater);
}

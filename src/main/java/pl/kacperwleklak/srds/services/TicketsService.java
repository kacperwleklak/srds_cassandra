package pl.kacperwleklak.srds.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kacperwleklak.srds.controller.requests.NewShowRequest;
import pl.kacperwleklak.srds.model.Discount;
import pl.kacperwleklak.srds.model.Show;
import pl.kacperwleklak.srds.model.Ticket;
import pl.kacperwleklak.srds.model.dao.TicketDAO;
import pl.kacperwleklak.srds.model.dao.TicketKey;
import pl.kacperwleklak.srds.repository.TicketsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketsService {

    private final Integer ROWS;
    private final Integer ROW_SEATS;

    private final TicketsRepository ticketsRepository;

    @Autowired
    public TicketsService(@Value("${cinema.seats.rows.count}") Integer rows,
                          @Value("${cinema.seats.rows.seats}") Integer rowSeats,
                          TicketsRepository ticketsRepository) {
        this.ROWS = rows;
        this.ROW_SEATS = rowSeats;
        this.ticketsRepository = ticketsRepository;
    }

    public void generateTicketsForShow(NewShowRequest newShowRequest) {
        List<Ticket> tickets = new ArrayList<>();
        for (int row = 1; row <= ROWS; row++) {
            for (int seat = 1; seat <= ROW_SEATS; seat++) {
                Ticket ticket = Ticket.builder()
                        .date(newShowRequest.getDate())
                        .theater(newShowRequest.getTheater())
                        .row(row)
                        .number(seat)
                        .basePrice(newShowRequest.getBasePrice())
                        .movieTitle(newShowRequest.getMovieTitle())
                        .discount(Discount.NORMAL)
                        .taken(false).build();
                tickets.add(ticket);
            }
        }
        List<TicketDAO> ticketDAOS = tickets.stream()
                .map(Ticket::toTicketDAO)
                .collect(Collectors.toList());
        ticketsRepository.saveAll(ticketDAOS);
    }

    public List<Show> getAllShows() {
        List<TicketDAO> distinctShows = ticketsRepository.findDistinctShows();
        return distinctShows.stream()
                .map(ticketDAO -> {
                    TicketKey ticketKey = ticketDAO.getTicketKey();
                    return ticketsRepository.findDistinctByTicketKeyDateAndTicketKeyTheater(ticketKey.getDate(), ticketKey.getTheater());
                }).map(ticketDAO -> {
                    Show show = new Show();
                    show.setDate(ticketDAO.getTicketKey().getDate());
                    show.setMovieTitle(ticketDAO.getMovieTitle());
                    show.setPrice(ticketDAO.getBasePrice());
                    show.setTheater(ticketDAO.getTicketKey().getTheater());
                    return show;
                }).collect(Collectors.toList());
    }

    public List<Ticket> getAllTicketsForShow(LocalDateTime dateTime, int theater) {
        return ticketsRepository.findByTicketKeyDateAndTicketKeyTheater(dateTime, theater)
                .stream().map(TicketDAO::toTicket).collect(Collectors.toList());
    }
}

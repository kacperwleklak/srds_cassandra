package pl.kacperwleklak.srds.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kacperwleklak.srds.controller.requests.TicketRequest;
import pl.kacperwleklak.srds.controller.requests.NewShowRequest;
import pl.kacperwleklak.srds.exception.FreeTicketException;
import pl.kacperwleklak.srds.exception.TakenTicketException;
import pl.kacperwleklak.srds.exception.TicketNotFoundException;
import pl.kacperwleklak.srds.model.Discount;
import pl.kacperwleklak.srds.model.Show;
import pl.kacperwleklak.srds.model.Ticket;
import pl.kacperwleklak.srds.model.dao.TicketDAO;
import pl.kacperwleklak.srds.model.dao.TicketKey;
import pl.kacperwleklak.srds.repository.TicketsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public Ticket buyTicket(TicketRequest ticketRequest) throws TicketNotFoundException, TakenTicketException {
        TicketKey ticketKey = ticketRequest.toTicketKey();
        Optional<TicketDAO> ticketToBuy = ticketsRepository.findById(ticketKey);
        if (ticketToBuy.isEmpty()) {
            throw new TicketNotFoundException(ticketKey);
        }
        TicketDAO ticketDAOToBuy = ticketToBuy.get();
        if (ticketDAOToBuy.isTaken()) {
            throw new TakenTicketException(ticketKey);
        }
        ticketDAOToBuy.setDiscount(ticketRequest.getDiscount());
        ticketDAOToBuy.setTaken(true);
        TicketDAO ticketSaved = ticketsRepository.save(ticketDAOToBuy);
        return ticketSaved.toTicket();
    }

    public List<Ticket> buyTickets(List<TicketRequest> ticketRequests)
            throws TicketNotFoundException, TakenTicketException {
        List<TicketKey> ticketKeys = ticketRequests.stream()
                .map(TicketRequest::toTicketKey)
                .collect(Collectors.toList());
        List<TicketDAO> ticketDAOSbyId = ticketKeys.stream().map(ticketsRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertNoDifference(ticketKeys, ticketDAOSbyId);
        assertAnyTaken(ticketDAOSbyId);
        List<TicketDAO> ticketsTaken = ticketDAOSbyId.stream()
                .peek(ticketDAO -> {
                    ticketDAO.setTaken(true);
                    ticketDAO.setDiscount(ticketRequests.get(0).getDiscount());
                }).collect(Collectors.toList());
        return ticketsRepository.saveAll(ticketsTaken).stream().map(TicketDAO::toTicket).collect(Collectors.toList());
    }

    public List<Ticket> returnTickets(List<TicketRequest> ticketRequests) throws TicketNotFoundException, FreeTicketException {
        List<TicketKey> ticketKeys = ticketRequests.stream()
                .map(TicketRequest::toTicketKey)
                .collect(Collectors.toList());
        List<TicketDAO> ticketDAOSbyId = ticketKeys.stream().map(ticketsRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertNoDifference(ticketKeys, ticketDAOSbyId);
        assertEveryTaken(ticketDAOSbyId);
        List<TicketDAO> ticketsTaken = ticketDAOSbyId.stream()
                .peek(ticketDAO -> {
                    ticketDAO.setTaken(false);
                    ticketDAO.setDiscount(Discount.NORMAL.name());
                }).collect(Collectors.toList());
        return ticketsRepository.saveAll(ticketsTaken).stream().map(TicketDAO::toTicket).collect(Collectors.toList());

    }

    private void assertNoDifference(List<TicketKey> ticketRequests, List<TicketDAO> ticketsFound)
            throws TicketNotFoundException {
        List<TicketKey> differences = findDifferences(ticketRequests, ticketsFound);
        if (!differences.isEmpty()) {
            String ticketsNotFound = differences.stream()
                    .map(TicketKey::toString)
                    .collect(Collectors.joining(", "));
            throw new TicketNotFoundException("Tickets not found: " + ticketsNotFound);
        }
    }

    private void assertAnyTaken(List<TicketDAO> ticketDAOS) throws TakenTicketException {
        List<TicketKey> ticketsTaken = ticketDAOS.stream()
                .filter(TicketDAO::isTaken)
                .map(TicketDAO::getTicketKey)
                .collect(Collectors.toList());
        if (!ticketsTaken.isEmpty()) {
            String ticketsTakenString = ticketsTaken.stream()
                    .map(TicketKey::toString)
                    .collect(Collectors.joining(", "));
            throw new TakenTicketException("Tickets already taken: " + ticketsTakenString);
        }
    }

    private void assertEveryTaken(List<TicketDAO> ticketDAOS) throws FreeTicketException {
        List<TicketKey> ticketsNotTaken = ticketDAOS.stream()
                .filter(ticketDAO -> !ticketDAO.isTaken())
                .map(TicketDAO::getTicketKey)
                .collect(Collectors.toList());
        if (!ticketsNotTaken.isEmpty()) {
            String ticketsNotTakenString = ticketsNotTaken.stream()
                    .map(TicketKey::toString)
                    .collect(Collectors.joining(", "));
            throw new FreeTicketException("Tickets not taken found: " + ticketsNotTakenString);
        }
    }

    private List<TicketKey> findDifferences(List<TicketKey> ticketRequests, List<TicketDAO> ticketsFound) {
        List<TicketKey> foundKeys = ticketsFound.stream()
                .map(TicketDAO::getTicketKey)
                .collect(Collectors.toList());
        List<TicketKey> difference = new ArrayList<>(ticketRequests);
        difference.removeAll(foundKeys);
        return difference;
    }
}

package pl.kacperwleklak.srds.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kacperwleklak.srds.controller.requests.TicketRequest;
import pl.kacperwleklak.srds.exception.FreeTicketException;
import pl.kacperwleklak.srds.exception.TakenTicketException;
import pl.kacperwleklak.srds.exception.TicketNotFoundException;
import pl.kacperwleklak.srds.model.Ticket;
import pl.kacperwleklak.srds.services.TicketsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketsRestController {

    private final TicketsService ticketsService;

    @Autowired
    public TicketsRestController(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }

    @GetMapping
    public List<Ticket> getAllTicketsForShow(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                                             @RequestParam int theater) {
        return ticketsService.getAllTicketsForShow(date, theater);
    }

    @PutMapping
    public ResponseEntity<?> buyTicket(@RequestBody TicketRequest ticketRequest) {
        try {
            return ResponseEntity.ok(ticketsService.buyTicket(ticketRequest));
        } catch (TicketNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (TakenTicketException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/bulk")
    public ResponseEntity<?> buyTickets(@RequestBody List<TicketRequest> ticketRequest) {
        try {
            return ResponseEntity.ok(ticketsService.buyTickets(ticketRequest));
        } catch (TicketNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (TakenTicketException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/bulk/return")
    public ResponseEntity<?> returnTickets(@RequestBody List<TicketRequest> ticketRequest) {
        try {
            return ResponseEntity.ok(ticketsService.returnTickets(ticketRequest));
        } catch (TicketNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (FreeTicketException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}

package pl.kacperwleklak.srds.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}

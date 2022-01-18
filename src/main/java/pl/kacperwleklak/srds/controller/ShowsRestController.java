package pl.kacperwleklak.srds.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kacperwleklak.srds.controller.requests.NewShowRequest;
import pl.kacperwleklak.srds.model.Show;
import pl.kacperwleklak.srds.services.TicketsService;

import java.util.List;

@RestController
@RequestMapping("api/shows")
public class ShowsRestController {

    private final TicketsService ticketsService;

    @Autowired
    public ShowsRestController(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }


    @PostMapping
    public void createNewShow(@RequestBody NewShowRequest newShowRequest) {
        ticketsService.generateTicketsForShow(newShowRequest);
    }

    @GetMapping
    public ResponseEntity<List<Show>> getShows() {
        return ResponseEntity.ok(ticketsService.getAllShows());
    }
}

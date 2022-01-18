package pl.kacperwleklak.srds.controller.requests;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import pl.kacperwleklak.srds.model.dao.TicketKey;

import java.time.LocalDateTime;

@Data
public class TicketRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;
    private int theater;

    private int row;

    private int number;

    private String discount;

    public TicketKey toTicketKey() {
        return new TicketKey(date, theater, row, number);
    }
}

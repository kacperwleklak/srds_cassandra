package pl.kacperwleklak.srds.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.kacperwleklak.srds.model.dao.TicketDAO;
import pl.kacperwleklak.srds.model.dao.TicketKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    private LocalDateTime date;
    private int theater;
    private int row;
    private int number;
    private boolean taken;
    private BigDecimal basePrice;
    private Discount discount;
    private String movieTitle;

    public TicketDAO toTicketDAO() {
        TicketKey ticketKey = new TicketKey(date, theater, row, number);
        return new TicketDAO(ticketKey, taken, basePrice, discount.name(), movieTitle);
    }
}

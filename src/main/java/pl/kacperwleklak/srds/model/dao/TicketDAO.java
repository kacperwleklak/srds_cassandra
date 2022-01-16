package pl.kacperwleklak.srds.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import pl.kacperwleklak.srds.model.Discount;
import pl.kacperwleklak.srds.model.Ticket;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "ticket")
public class TicketDAO {

    @PrimaryKey
    private TicketKey ticketKey;

    private boolean taken;
    private BigDecimal basePrice;
    private String discount;
    private String movieTitle;

    public Ticket toTicket() {
        return Ticket.builder()
                .date(ticketKey.getDate())
                .theater(ticketKey.getTheater())
                .row(ticketKey.getRow())
                .number(ticketKey.getNumber())
                .taken(taken)
                .basePrice(basePrice)
                .discount(Discount.valueOf(discount))
                .movieTitle(movieTitle).build();
    }
}

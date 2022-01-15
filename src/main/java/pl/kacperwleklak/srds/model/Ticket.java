package pl.kacperwleklak.srds.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(value="seat")
public class Ticket {

    @PrimaryKey
    private Date showDate;
    @PrimaryKey
    private int theater;
    @PrimaryKey
    private int row;
    @PrimaryKey
    private int number;

    private boolean taken;
    private BigDecimal basePrice;
    private String discount;
    private String movieTitle;
}

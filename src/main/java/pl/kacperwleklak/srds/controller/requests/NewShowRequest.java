package pl.kacperwleklak.srds.controller.requests;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NewShowRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    private int theater;

    private BigDecimal basePrice;

    private String movieTitle;
}

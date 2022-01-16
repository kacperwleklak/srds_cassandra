package pl.kacperwleklak.srds.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Show {
    private LocalDateTime date;
    private int theater;
    private BigDecimal price;
    private String movieTitle;
}

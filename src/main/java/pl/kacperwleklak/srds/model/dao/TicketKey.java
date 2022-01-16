package pl.kacperwleklak.srds.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyClass
public class TicketKey implements Serializable {

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private LocalDateTime date;
    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private int theater;
    @PrimaryKeyColumn(ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private int row;
    @PrimaryKeyColumn(ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private int number;
}

package de.challenge.stats.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transaction {

    @NotNull
    private BigDecimal amount;

    @NotNull
    @JsonFormat(shape = NUMBER)
    private Instant timestamp;

}

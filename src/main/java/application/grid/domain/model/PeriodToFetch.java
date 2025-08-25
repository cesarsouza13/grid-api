package application.grid.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PeriodToFetch {
    LocalDate starteDate;
    LocalDate endDate;
}

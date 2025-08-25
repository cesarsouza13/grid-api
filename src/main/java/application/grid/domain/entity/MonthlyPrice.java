package application.grid.domain.entity;

import application.grid.domain.Enum.Region;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "monthly_price")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class MonthlyPrice {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private LocalDate date; // corresponde ao mês

        @Enumerated(EnumType.STRING)
        private Region region;

        private BigDecimal price;

    public MonthlyPrice(LocalDate date, Region region, BigDecimal price) {
        this.date = date;
        this.region = region;
        this.price = price;
    }
}

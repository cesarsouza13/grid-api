package application.grid.repository;

import application.grid.domain.entity.MonthlyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyPriceRepository extends JpaRepository<MonthlyPrice, Long> {

    Optional<MonthlyPrice> findTopByOrderByDateDesc();

    List<MonthlyPrice> findByDateAfterOrderByDateDesc(LocalDate startDate);

    boolean existsByDate(LocalDate date);
}

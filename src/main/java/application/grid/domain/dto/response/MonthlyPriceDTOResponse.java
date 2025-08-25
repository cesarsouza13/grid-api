package application.grid.domain.dto.response;

import application.grid.domain.Enum.Region;
import application.grid.domain.entity.MonthlyPrice;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyPriceDTOResponse {

    private LocalDate date;
    private Region region;
    private BigDecimal price;

   public static MonthlyPriceDTOResponse fromEntity(MonthlyPrice entity){
       if(entity == null){
           return null;
       }
       MonthlyPriceDTOResponse monthlyPriceDTOResponse = new MonthlyPriceDTOResponse();

       monthlyPriceDTOResponse.setPrice(entity.getPrice());
       monthlyPriceDTOResponse.setDate(entity.getDate());
       monthlyPriceDTOResponse.setRegion(entity.getRegion());

       return monthlyPriceDTOResponse;
   }
}

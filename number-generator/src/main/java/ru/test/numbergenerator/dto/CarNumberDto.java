package ru.test.numbergenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.test.numbergenerator.entities.CarNumber;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CarNumberDto {

    private Integer intNumber;

    private String number;

    public static CarNumberDto toDto(CarNumber carNumber) {
        return CarNumberDto.builder()
                .intNumber(carNumber.getIntNumber())
                .number(carNumber.getNumber())
                .build();
    }
}

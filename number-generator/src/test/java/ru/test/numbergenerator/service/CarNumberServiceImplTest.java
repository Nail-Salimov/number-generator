package ru.test.numbergenerator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import ru.test.numbergenerator.dto.CarNumberDto;
import ru.test.numbergenerator.entities.CarNumber;
import ru.test.numbergenerator.exception.NumberRunOutException;
import ru.test.numbergenerator.repositories.CarNumberJpaRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
public class CarNumberServiceImplTest {

    @Mock
    private CarNumberJpaRepository repository;

    @InjectMocks
    private CarNumberServiceImpl service;

    @Test
    public void getNext_whenTableEmpty_thenReturnZeroNumber() throws NumberRunOutException {
        CarNumberDto expected = CarNumberDto.builder()
                .number("А000АА 116 RUS")
                .intNumber(0)
                .build();
        given(repository.findNextIntNumber()).willReturn(Optional.empty());
        when(repository.save(Mockito.any(CarNumber.class))).thenAnswer((i -> i.getArguments()[0]));
        CarNumberDto actual = service.getNextNumber();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void getNext_whenTableHaveSecondNumberAndDontHaveFirst_thenReturnZeroNumber() throws NumberRunOutException {
        CarNumberDto expected = CarNumberDto.builder()
                .number("А000АА 116 RUS")
                .intNumber(0)
                .build();
        given(repository.findNextIntNumber()).willReturn(Optional.of(2));
        given(repository.findPrevIntNumber()).willReturn(Optional.of(1));
        when(repository.save(Mockito.any(CarNumber.class))).thenAnswer((i -> i.getArguments()[0]));
        CarNumberDto actual = service.getNextNumber();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void getNext_whenAllNumberRunOut_thenThrowNumberRunOutException() {
        int maxNumber = 1727999;
        given(repository.findNextIntNumber()).willReturn(Optional.of(maxNumber + 1));
        NumberRunOutException exception = assertThrows(NumberRunOutException.class, () -> {
            service.getNextNumber();
        });
        assertEquals("Car number have run out", exception.getMessage());
    }

    @Test
    public void getRandom_whenAllNumberRunOut_thenThrowNumberRunOutException(){
        int maxNumber = 1727999;
        LinkedList<Integer> allNextList = new LinkedList<>();
        allNextList.add(maxNumber+1);
        given(repository.findAllNextIntNumber()).willReturn(allNextList);
        given(repository.findAllPrevIntNumber()).willReturn(Collections.emptyList());
        NumberRunOutException exception = assertThrows(NumberRunOutException.class, () -> {
            service.getRandomNumber();
        });
        assertEquals("Car number have run out", exception.getMessage());
    }

    @Test
    public void getRandom_whenTableEmpty_thenReturnSomeCarNumberDto() throws NumberRunOutException {
        given(repository.findAllNextIntNumber()).willReturn(Collections.emptyList());
        given(repository.findAllPrevIntNumber()).willReturn(Collections.emptyList());
        when(repository.save(Mockito.any(CarNumber.class))).thenAnswer((i -> i.getArguments()[0]));
        CarNumberDto actual = service.getRandomNumber();
        assertThat(actual).hasNoNullFieldsOrProperties();
    }

    @Test
    public void getRandom_whenFirstNumberIsAccess_thenReturnSomeCarNumberDto() throws NumberRunOutException {
        LinkedList<Integer> allNextList = new LinkedList<>();
        allNextList.add(2);
        allNextList.add(5);
        allNextList.add(10);
        LinkedList<Integer> allPrevList = new LinkedList<>();
        allPrevList.add(1);
        allPrevList.add(4);
        allPrevList.add(9);
        given(repository.findAllNextIntNumber()).willReturn(allNextList);
        given(repository.findAllPrevIntNumber()).willReturn(allPrevList);
        when(repository.save(Mockito.any(CarNumber.class))).thenAnswer((i -> i.getArguments()[0]));
        CarNumberDto actual = service.getRandomNumber();
        assertThat(actual).hasNoNullFieldsOrProperties();
    }

}

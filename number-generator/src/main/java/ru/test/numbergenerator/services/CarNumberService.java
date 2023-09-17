package ru.test.numbergenerator.services;

import ru.test.numbergenerator.dto.CarNumberDto;
import ru.test.numbergenerator.exceptions.NumberRunOutException;

public interface CarNumberService {

    CarNumberDto getNextNumber() throws NumberRunOutException;

    CarNumberDto getRandomNumber() throws NumberRunOutException;
}

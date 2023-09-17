package ru.test.numbergenerator.service;

import ru.test.numbergenerator.dto.CarNumberDto;
import ru.test.numbergenerator.exception.NumberRunOutException;

public interface CarNumberService {

    CarNumberDto getNextNumber() throws NumberRunOutException;

    CarNumberDto getRandomNumber() throws NumberRunOutException;
}

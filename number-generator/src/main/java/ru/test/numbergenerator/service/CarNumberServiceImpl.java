package ru.test.numbergenerator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.test.numbergenerator.dto.CarNumberDto;
import ru.test.numbergenerator.entities.CarNumber;
import ru.test.numbergenerator.exception.NumberRunOutException;
import ru.test.numbergenerator.repositories.CarNumberJpaRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CarNumberServiceImpl implements CarNumberService {

    private final CarNumberJpaRepository carNumberRepository;
    private final Random random;
    private final int MAX_INT_NUMBER = 1727999;
    private final int MIN_INT_NUMBER = 0;

    /**
     * Only Russian letters
     */
    private final char[] validLetters = {'А', 'В', 'Е', 'К', 'М', 'Н', 'О', 'Р', 'С', 'Т', 'У', 'Х'};

    @Autowired
    public CarNumberServiceImpl(CarNumberJpaRepository carNumberRepository) {
        this.carNumberRepository = carNumberRepository;
        random = new Random();
    }

    @Override
    public CarNumberDto getNextNumber() throws NumberRunOutException {
        Optional<Integer> optionalNext = carNumberRepository.findNextIntNumber();
        int nextIntNumber = optionalNext.orElse(0);
        if (nextIntNumber != 0) {
            Optional<Integer> optionalPrev = carNumberRepository.findPrevIntNumber();
            if (nextIntNumber > MAX_INT_NUMBER && !optionalPrev.isPresent()) {
                throw new NumberRunOutException("Car number have run out");
            }
            if (optionalPrev.isPresent()) {
                if (optionalPrev.get() < nextIntNumber) {
                    nextIntNumber = 0;
                }
            }
        }
        String numberStr = intToNumber(nextIntNumber);
        CarNumber number = new CarNumber();
        number.setIntNumber(nextIntNumber);
        number.setNumber(numberStr);
        number = carNumberRepository.save(number);
        return CarNumberDto.toDto(number);
    }

    @Override
    public CarNumberDto getRandomNumber() throws NumberRunOutException {
        List<Integer> nextList = carNumberRepository.findAllNextIntNumber();
        List<Integer> prevList = carNumberRepository.findAllPrevIntNumber();
        List<Pair<Integer, Integer>> pairList = new LinkedList<>();
        List<Integer> counts = new LinkedList<>();

        if (prevList.isEmpty() && nextList.isEmpty()) {
            pairList.add(Pair.of(MIN_INT_NUMBER, MAX_INT_NUMBER + 1));
            counts.add(MAX_INT_NUMBER + 1);
        } else if (prevList.isEmpty() && nextList.size() == 1) {
            pairList.add(Pair.of(nextList.get(0), MAX_INT_NUMBER + 1));
            counts.add(MAX_INT_NUMBER - nextList.get(0) + 1);
            nextList.remove(0);
        } else if (!prevList.isEmpty() && !nextList.isEmpty()) {
            if (prevList.get(0) < nextList.get(0)) {
                pairList.add(Pair.of(0, prevList.get(0)));
                counts.add(prevList.get(0));
                prevList.remove(0);
            }
            int length = prevList.size();
            for (int i = 0; i < length; i++) {
                pairList.add(Pair.of(nextList.get(0), prevList.get(0)));
                counts.add(prevList.get(0) - nextList.get(0));
                prevList.remove(0);
                nextList.remove(0);
            }
            if (nextList.get(0) < MAX_INT_NUMBER) {
                pairList.add(Pair.of(nextList.get(0), MAX_INT_NUMBER + 1));
                counts.add(MAX_INT_NUMBER + 1 - nextList.get(0));
                nextList.remove(0);
            }
        } else {
            throw new IllegalStateException("unprocessed state");
        }
        int allCount = counts.stream().reduce(0, Integer::sum);
        if (allCount == 0) {
            throw new NumberRunOutException("Car number have run out");
        }
        Integer intNumber = findIntNumber(allCount, pairList);
        CarNumber carNumber = new CarNumber();
        carNumber.setNumber(intToNumber(intNumber));
        carNumber.setIntNumber(intNumber);
        return CarNumberDto.toDto(carNumberRepository.save(carNumber));
    }

    private Integer findIntNumber(int allCount, List<Pair<Integer, Integer>> pairList) {
        int result = random.nextInt(allCount);
        Integer intNumber = null;
        for (Pair<Integer, Integer> pair : pairList) {
            if (pair.getSecond() - pair.getFirst() > result) {
                intNumber = pair.getFirst() + result;
                break;
            } else {
                result = result - (pair.getSecond() - pair.getFirst());
            }
        }
        if (intNumber == null) {
            throw new IllegalStateException("processing error");
        }
        return intNumber;
    }

    /**
     * Convert int number to car number
     * @param value (int format of car number)
     * @return car number in String format
     */
    private String intToNumber(int value) {
        if (value < MIN_INT_NUMBER || value > MAX_INT_NUMBER) {
            throw new IllegalArgumentException(value + " can't be convert to car number");
        }
        StringBuilder numbers = new StringBuilder(String.valueOf(value % 1000));
        if (value % 1000 < 100) {
            for (int i = 0; i < 4 - numbers.length(); i++) {
                numbers.insert(0, "0");
            }
        }
        String letters = decimalToLetters(value / 1000);
        return letters.charAt(0) + numbers.toString() + letters.substring(1, 3) + " 116 RUS";
    }

    private String decimalToLetters(int intLetters) {
        if (intLetters < 0 || intLetters > 1727) {
            throw new IllegalArgumentException(intLetters + " can't be convert to letters");
        }
        char thirdLetter = validLetters[intLetters % 12];
        int i = intLetters / 12;
        char secondLetter = validLetters[i % 12];
        char firstLetter = validLetters[i / 12];
        return String.valueOf(firstLetter) + secondLetter + thirdLetter;
    }

    /**
     * Convert car number to int number
     * @param carNumber (car number)
     * @return car number in int format
     */
    private int numberToInt(String carNumber) {
        String[] split = carNumber.split(" ");
        if (split.length != 3 || !split[1].equals("116") || !split[2].equals("RUS")) {
            throw new IllegalArgumentException(carNumber + " is not valid number");
        }
        String value = split[0];
        if (value.length() != 6) {
            throw new IllegalArgumentException("unique part length is not 6");
        }
        char firstLetter = value.charAt(0);
        char secondLetter = value.charAt(4);
        char thirdLetter = value.charAt(5);
        String numbers = carNumber.substring(1, 4);
        if (!isValidLetter(firstLetter) || !isValidLetter(secondLetter) || !isValidLetter(thirdLetter) || !isNumeric(numbers)) {
            throw new IllegalArgumentException(carNumber + " is not valid number");
        }
        return convertToDecimal(firstLetter, secondLetter, thirdLetter) * 1000 + Integer.parseInt(numbers);
    }

    private int convertToDecimal(char first, char second, char third) {
        return letterToDecimal(first) * 144 + letterToDecimal(second) * 12 + letterToDecimal(third);
    }

    private int letterToDecimal(char letter) {
        for (int i = 0; i < validLetters.length; i++) {
            if (validLetters[i] == letter) {
                return i;
            }
        }
        throw new IllegalArgumentException(letter + " is not valid letter");
    }

    private boolean isValidLetter(char letter) {
        if (!Character.isLetter(letter)) {
            return false;
        }
        for (char validLetter : validLetters) {
            if (validLetter == letter) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}

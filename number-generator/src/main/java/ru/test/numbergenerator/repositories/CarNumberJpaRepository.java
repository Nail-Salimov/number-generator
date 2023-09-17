package ru.test.numbergenerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.test.numbergenerator.entities.CarNumber;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarNumberJpaRepository extends JpaRepository<CarNumber, Long> {

    @Query(value = "SELECT (int_number + 1) as number FROM car_number WHERE (SELECT 1 FROM car_number as n where n.int_number = (car_number.int_number + 1)) IS NULL ORDER BY car_number.int_number LIMIT 1", nativeQuery = true)
    Optional<Integer> findNextIntNumber();

    @Query(value = "SELECT (int_number) as number FROM car_number WHERE (SELECT 1 FROM car_number as n where n.int_number = (car_number.int_number - 1)) IS NULL AND int_number > 0 ORDER BY car_number.int_number LIMIT 1", nativeQuery = true)
    Optional<Integer> findPrevIntNumber();

    @Query(value = "SELECT (int_number + 1) as number FROM car_number WHERE (SELECT 1 FROM car_number as n where n.int_number = (car_number.int_number + 1)) IS NULL ORDER BY car_number.int_number", nativeQuery = true)
    List<Integer> findAllNextIntNumber();

    @Query(value = "SELECT (int_number) as number FROM car_number WHERE (SELECT 1 FROM car_number as n where n.int_number = (car_number.int_number - 1)) IS NULL AND int_number > 0 ORDER BY car_number.int_number", nativeQuery = true)
    List<Integer> findAllPrevIntNumber();
}

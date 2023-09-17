package ru.test.numbergenerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.numbergenerator.dto.CarNumberDto;
import ru.test.numbergenerator.exception.NumberRunOutException;
import ru.test.numbergenerator.service.CarNumberService;

@RestController
@RequestMapping("/number")
public class CarNumberRestController {

    @Autowired
    private CarNumberService carNumberService;

    @GetMapping(value = "/next")
    public ResponseEntity<?> getNext(){
        try {
            CarNumberDto dto = carNumberService.getNextNumber();
            return ResponseEntity.ok(dto.getNumber());
        } catch (NumberRunOutException e) {
            return ResponseEntity.status(507).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandom(){
        try {
            CarNumberDto dto = carNumberService.getRandomNumber();
            return ResponseEntity.ok(dto.getNumber());
        } catch (NumberRunOutException e) {
            return ResponseEntity.status(507).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}

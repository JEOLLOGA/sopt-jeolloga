package sopt.jeolloga.domain.templestay.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.jeolloga.domain.templestay.api.dto.FilterRes;
import sopt.jeolloga.domain.templestay.api.service.CoordinatesService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class CoordinateController {

    private final CoordinatesService coordinatesService;

    public CoordinateController(CoordinatesService coordinatesService){
        this.coordinatesService = coordinatesService;
    }

    @PostMapping("public/updateCoordinate")
    public void updateCoordinate() {
        coordinatesService.updateAllAdressToCoordinate();
    }
}

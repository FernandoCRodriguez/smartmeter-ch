package com.energia.consumption.lectura;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lecturas")
public class LecturaController {

    private final LecturaRepository repository;

    public LecturaController(LecturaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<LecturaResponse> ultimas() {
        return repository.findTop20ByOrderByMomentoDesc().stream()
                .map(LecturaResponse::desde)
                .toList();
    }
}
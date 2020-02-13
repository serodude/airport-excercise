package com.capgemini.airport.api;

import com.capgemini.airport.model.Airport;
import com.capgemini.airport.persistence.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/airports")
public class AirportController {
    @Autowired
    AirportRepository airportRepository;

    @GetMapping
    public Iterable<Airport> getAirports() {
        return airportRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airport> getAirport(@PathVariable long id) {
        Optional<Airport> airport = airportRepository.findById(id);
        if (airport.isPresent()) {
            return ResponseEntity.ok(airport.get());
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Airport> createAirport(@RequestBody Airport airport) {
        return ResponseEntity.ok(airportRepository.save(airport));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Airport> updateAirport(@RequestBody Airport airport) {
        return ResponseEntity.ok(airportRepository.save(airport));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAirport(@PathVariable long id) {
        airportRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

package com.capgemini.airport.api;

import com.capgemini.airport.model.Airport;
import com.capgemini.airport.model.Message;
import com.capgemini.airport.model.Plane;
import com.capgemini.airport.persistence.AirportRepository;
import com.capgemini.airport.persistence.PlaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/planes")
public class PlaneController {
    @Autowired
    PlaneRepository planeRepository;

    @Autowired
    AirportRepository airportRepository;

    @GetMapping
    public Iterable<Plane> getPlanes() {
        return planeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plane> getPlane(@PathVariable long id) {
        Optional<Plane> plane = planeRepository.findById(id);
        if (plane.isPresent()) {
            return ResponseEntity.ok(plane.get());
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Plane> createPlane(@RequestBody Plane plane) {
        return ResponseEntity.ok(planeRepository.save(plane));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plane> updatePlane(@RequestBody Plane plane) {
        return ResponseEntity.ok(planeRepository.save(plane));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePlane(@PathVariable long id) {
        planeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/flyto/{id}/{airportId}")
    public ResponseEntity<?> flyPlane(@PathVariable long id, @PathVariable long airportId) {
        System.out.println("fly plane");
        Optional<Plane> result = planeRepository.findById(id);
        Optional<Airport> airportResult = airportRepository.findById(airportId);
        if (result.isPresent() && airportResult.isPresent()) {
            Plane plane = result.get();
            if (plane.getAirport().getId() == airportId) {
                return ResponseEntity.badRequest().body(new Message("Plane is already at airport " + plane.getAirport().getName()));
            } else if (plane.getFuel() < 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Plane has not enough fuel left."));
            }
            else {
                plane.setFuel(plane.getFuel() - 2);
                plane.setAirport(airportResult.get());
                return ResponseEntity.ok(planeRepository.save(plane));
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Either the given plane or airport does not exist."));
        }
    }

    @GetMapping("/refuel/{id}")
    public ResponseEntity<?> refuelPlane(@PathVariable long id) {
        System.out.println("refuel plane");
        Optional<Plane> result = planeRepository.findById(id);
        if (result.isPresent()) {
            Plane plane = result.get();
            if (plane.getFuel() >= 5) {
                return ResponseEntity.badRequest().body(new Message("Plane is already full of fuel "));
            } else {
                plane.setFuel(5);
                return ResponseEntity.ok(planeRepository.save(plane));
            }
        } else {
            return ResponseEntity.badRequest().body(new Message("ERROR 404 plane not found"));
        }
    }
}

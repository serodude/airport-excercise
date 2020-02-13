package com.capgemini.airport.api;

import com.capgemini.airport.model.Plane;
import com.capgemini.airport.persistence.PlaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/planes")
public class PlaneController {
    @Autowired
    PlaneRepository planeRepository;

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
}

package com.capgemini.airport.api;

import com.capgemini.airport.model.Airport;
import com.capgemini.airport.model.Plane;
import com.capgemini.airport.persistence.PlaneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PlaneControllerTest {

    @InjectMocks
    private PlaneController planeController;

    @Mock
    private PlaneRepository planeRepository;

    private MockMvc mockMvc;

    private List<Plane> planes;
    private List<Airport> airports;

    @BeforeEach
    public void Setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(planeController).build();
        airports = new ArrayList<>();
        airports.add(createDummyAirport(1L, "Amsterdam"));
        airports.add(createDummyAirport(2L, "London"));

        planes = new ArrayList<>();
        planes.add(createDummyPlane(1L, "Biplane", 5, airports.get(0)));
        planes.add(createDummyPlane(2L, "Kamikaze", 5, airports.get(0)));
        planes.add(createDummyPlane(3L, "vliegtuig", 5, airports.get(1)));
    }

    private Airport createDummyAirport(Long id, String name) {
        Airport airport = new Airport();
        airport.setId(id);
        airport.setName(name);
        return airport;
    }

    private Plane createDummyPlane(Long id, String name, int fuel, Airport airport) {
        Plane plane = new Plane();
        plane.setId(id);
        plane.setName(name);
        plane.setFuel(fuel);
        plane.setAirport(airport);
        return plane;
    }

    @Test
    void getPlanes() throws Exception {
        when(planeRepository.findAll()).thenReturn(planes);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/planes"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].id").value(planes.get(0).getId()))
                .andExpect(jsonPath("$.[0].name").value(planes.get(0).getName()))
                .andExpect(jsonPath("$.[0].airport.id").value(planes.get(0).getAirport().getId()))
                .andExpect(jsonPath("$.[1].id").value(planes.get(1).getId()))
                .andExpect(jsonPath("$.[1].name").value(planes.get(1).getName()))
                .andExpect(jsonPath("$.[1].airport.id").value(planes.get(1).getAirport().getId()))
                .andExpect(jsonPath("$.[2].id").value(planes.get(2).getId()))
                .andExpect(jsonPath("$.[2].name").value(planes.get(2).getName()))
                .andExpect(jsonPath("$.[2].airport.id").value(planes.get(2).getAirport().getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getPlane() throws Exception {
        when(planeRepository.findById(1L)).thenReturn(Optional.of(planes.get(1)));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/planes/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id").value(planes.get(1).getId()))
                .andExpect(jsonPath("$.name").value(planes.get(1).getName()))
                .andExpect(jsonPath("$.fuel").value(planes.get(1).getFuel()))
                .andExpect(jsonPath("$.airport.id").value(planes.get(1).getAirport().getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void createPlane() throws Exception {
        Plane newPlane = createDummyPlane(4L, "Pete plane", 5, airports.get(1));
        when(planeRepository.save(any(Plane.class))).thenReturn(newPlane);

        ObjectMapper mapper = new ObjectMapper();
        String jsonObj = mapper.writeValueAsString(newPlane);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/planes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(newPlane.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newPlane.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fuel").value(newPlane.getFuel()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updatePlane() throws Exception {
        Plane updatePlane = createDummyPlane(3L, "Vliegtuig", 3, airports.get(1));
        when(planeRepository.save(any(Plane.class))).thenReturn(updatePlane);

        ObjectMapper mapper = new ObjectMapper();
        String jsonObj = mapper.writeValueAsString(updatePlane);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/planes/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updatePlane.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updatePlane.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fuel").value(updatePlane.getFuel()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deletePlane() throws Exception {
//        doNothing().when(planeRepository);
//        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/plane/1"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
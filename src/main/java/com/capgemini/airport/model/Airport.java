package com.capgemini.airport.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "airport", cascade=CascadeType.ALL)
    private List<Plane> planes;

    public Airport() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlanes(List<Plane> planes) {
        this.planes = planes;
    }
}

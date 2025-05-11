package com.assignment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class Cart implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    int quantity;

    @ManyToOne
    @JoinColumn(name = "Username")
    User user;

    @ManyToOne
    @JoinColumn(name = "Productid")
    Product product;
}

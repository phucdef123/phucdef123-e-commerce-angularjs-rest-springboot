package com.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@ToString(exclude = "userProfile")
public class User implements Serializable {
    @Id
    private String username;
    private String password;
    private Boolean enabled;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Authority> authorities;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Cart> carts;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}

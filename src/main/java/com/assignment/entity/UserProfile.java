package com.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Entity
@Table(name = "userprofile")
@ToString(exclude = "user")
public class UserProfile implements Serializable {
    @Id
    String username;
    String fullname;
    @Column(unique = true)
    String email;
    String photo;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "Username")
    private User user;
}

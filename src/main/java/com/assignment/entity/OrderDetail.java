package com.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data 
@Entity
@Table(name = "orderdetails")
public class OrderDetail implements Serializable{
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	Double price; 
	Integer quantity;

	@ManyToOne
	@JoinColumn(name = "Productid")
	Product product;

//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "Orderid")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	Order order;

}

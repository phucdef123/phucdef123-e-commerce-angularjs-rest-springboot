package com.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "Orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String address;
    @Temporal(TemporalType.DATE)
    @Column(name = "Createdate")
    Date createDate = new Date();

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

    @Transient
    public double getTotal() {
        if (orderDetails == null) return 0;
        return orderDetails.stream()
                .mapToDouble(od -> od.getPrice() * od.getQuantity())
                .sum();
    }

    @ManyToOne
    @JoinColumn(name = "username")
    User user;


    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;

	public enum OrderStatus {
		PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
	}
}

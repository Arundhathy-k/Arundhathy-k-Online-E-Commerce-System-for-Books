package com.kovan.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private LocalDate paymentDate;

    private String paymentMethod;

    private String  paymentStatus;

    private BigDecimal amount;

    private String paymentReferenceNumber;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    @JsonBackReference
    private Order order;
}

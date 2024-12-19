package com.kovan.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private LocalDate paymentDate;

    private String paymentMethod; // Credit card, PayPal

    private String  paymentStatus; // Completed, Pending, Failed

    private Double amount;

    private String paymentReferenceNumber;

    private String transactionType; // Purchase,Return,Restock

    private int transactionQuantity; // the quantity of books involved in the transaction

    private String transactionNotes; //any additional information, such as reason for return or restocking

}

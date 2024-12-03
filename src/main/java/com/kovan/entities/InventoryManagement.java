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
@Table(name = "inventory_management")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryTransactionId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bookId")
    private Book book;

    private String transactionType; // Purchase, Return, Restock

    private int quantity;
    private LocalDate transactionDate;
    private String notes;

}

package com.kovan.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "shopping_cart")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String createdDate;
    private String lastUpdatedDate;

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate.toString();
    }
    public Instant getCreatedDate() {
        return Instant.parse(createdDate);
    }
    public void setLastUpdatedDate(Instant lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate.toString();
    }
    public Instant getLastUpdatedDate() {
        return Instant.parse(lastUpdatedDate);
    }

}

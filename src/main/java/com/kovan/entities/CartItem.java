package com.kovan.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cartId")
    private ShoppingCart cart;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bookId")
    private Book book;

    private int quantity;

}

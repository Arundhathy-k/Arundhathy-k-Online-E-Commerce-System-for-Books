package com.kovan.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "review")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bookId")
    private Book book;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private User user;

    private int rating; // 1 to 5
    private String comment;
    private LocalDate reviewDate;

}

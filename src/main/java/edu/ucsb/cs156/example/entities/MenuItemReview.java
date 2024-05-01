package edu.ucsb.cs156.example.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "menuitemreviews")
public class MenuItemReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // unique id for each review

    private long itemId; // unique id for each menu item
    private String reviewerEmail; // email of the reviewer
    private int stars; // number of stars given to the menu item (0 to 5)
    private LocalDateTime dateReviewed; // date the review was written
    private String comment; // comment written by the reviewer
}

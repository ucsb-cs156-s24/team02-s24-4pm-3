package edu.ucsb.cs156.example.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "menuitemreview")
public class MenuItemReview {
    @Id
    private String code; // the ucsb dining hall code, unique to each dining hall
    private long id; // unique id for each review
    private long itemId; // unique id for each menu item
    private String reviewerEmail; // email of the reviewer
    private short stars; // number of stars given to the menu item (0 to 5)
    private String dateReviewed; // date the review was written
    private String comment; // comment written by the reviewer
}

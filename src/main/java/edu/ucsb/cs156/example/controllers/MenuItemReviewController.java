package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Tag(name = "MenuItemReview")
@RequestMapping("/api/menuitemreview")
@RestController
@Slf4j
public class MenuItemReviewController extends ApiController{
    @Autowired
    MenuItemReviewRepository menuItemReviewRepository;

    @Operation(summary= "Lists all the reviews in the database")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<MenuItemReview> allReviews() {
        return menuItemReviewRepository.findAll();
    }

    @Operation(summary= "Create a new review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public MenuItemReview postMenuItemReview(
            @Parameter(name="itemId") @RequestParam long itemId,
            @Parameter(name="reviewerEmail") @RequestParam String reviewerEmail,
            @Parameter(name="stars") @RequestParam int stars,
            @Parameter(name="dateReviewed", description="date in in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam("dateReviewed") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateReviewed,
            @Parameter(name="comment") @RequestParam String comment)
            throws JsonProcessingException {
                // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                // See: https://www.baeldung.com/spring-date-parameters

                log.info("dateReviewed={}", dateReviewed);

                MenuItemReview menuItemReview = new MenuItemReview();
                menuItemReview.setItemId(itemId);
                menuItemReview.setReviewerEmail(reviewerEmail);
                menuItemReview.setStars(stars);
                menuItemReview.setDateReviewed(dateReviewed);
                menuItemReview.setComment(comment);

                return menuItemReviewRepository.save(menuItemReview);
            }

    @Operation(summary= "Get a single review")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public MenuItemReview getById(@Parameter(name="id") @RequestParam Long id) {
        return menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));
    }

    @Operation(summary= "Delete a MenuItemReview")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteMenuItemReview(@Parameter(name="id") @RequestParam Long id) {
        MenuItemReview review = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));
        menuItemReviewRepository.delete(review);
        return genericMessage("MenuItemReview with id %s deleted".formatted(id));
    }

    @Operation(summary= "Update a single review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public MenuItemReview updateMenuItemReview(
            @Parameter(name="id", description="The id of the review you want to update") @RequestParam Long id,
            @RequestBody @Valid MenuItemReview incoming) {

        MenuItemReview review = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

        review.setItemId(incoming.getItemId());
        review.setReviewerEmail(incoming.getReviewerEmail());
        review.setStars(incoming.getStars());
        review.setDateReviewed(incoming.getDateReviewed());
        review.setComment(incoming.getComment());

        menuItemReviewRepository.save(review);

        return review;
    }
}

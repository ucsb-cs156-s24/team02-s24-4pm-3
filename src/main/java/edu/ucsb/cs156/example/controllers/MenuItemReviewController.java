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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

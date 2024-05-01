package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = MenuItemReviewController.class)
@Import(TestConfig.class)
public class MenuItemReviewControllerTests extends ControllerTestCase{
    @MockBean
    MenuItemReviewRepository menuItemReviewRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/menuitemreview/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/menuitemreview/all"))
                .andExpect(status().is(403)); // logged-out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/menuitemreview/all"))
                .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_menuitemreview() throws Exception {

        // arrange
        LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
        MenuItemReview review1 = MenuItemReview.builder()
                .itemId((long)1)
                .reviewerEmail("ucsbstudent1@ucsb.edu")
                .stars((short) 5)
                .comment("I hecking loved it!")
                .dateReviewed(ldt1)
                .build();

        LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");
        MenuItemReview review2 = MenuItemReview.builder()
                .itemId((long)5)
                .reviewerEmail("ucsbstudent2@ucsb.edu")
                .stars((short) 1)
                .comment("this shit sucks, boo!")
                .dateReviewed(ldt2)
                .build();

        ArrayList<MenuItemReview> expectedReviews = new ArrayList<>();
        expectedReviews.addAll(Arrays.asList(review1, review2));

        when(menuItemReviewRepository.findAll()).thenReturn(expectedReviews);

        // act
        MvcResult response = mockMvc.perform(get("/api/menuitemreview/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(menuItemReviewRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedReviews);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // Tests for POST /api/ucsbdates/post...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/menuitemreview/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/menuitemreview/post"))
                .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_menuitemreview() throws Exception {
        // arrange

        LocalDateTime ldt1 = LocalDateTime.parse("2024-01-03T00:00:00");

        MenuItemReview review1 = MenuItemReview.builder()
                .itemId((long)1)
                .reviewerEmail("ucsbstudent1@ucsb.edu")
                .stars((short) 5)
                .comment("I hecking loved it!")
                .dateReviewed(ldt1)
                .build();

        when(menuItemReviewRepository.save(eq(review1))).thenReturn(review1);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/menuitemreview/post?itemId=1&reviewerEmail=ucsbstudent1@ucsb.edu&stars=5&comment=I hecking loved it!&localDateTime=2024-01-03T00:00:00")
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(menuItemReviewRepository, times(1)).save(review1);
        String expectedJson = mapper.writeValueAsString(review1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}

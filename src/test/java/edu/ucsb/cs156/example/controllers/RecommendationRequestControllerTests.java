package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase{
    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/recommendationrequests/all
        
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendationrequests/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendationrequests/all"))
                            .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_recommendationrequests() throws Exception {

            // arrange
            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
            LocalDateTime ldt2 = LocalDateTime.parse("2022-02-03T00:00:00");

            RecommendationRequest rr1 = RecommendationRequest.builder()
                            .requesterEmail("a@gmail.com")
                            .professorEmail("p@gmail.com")
                            .explanation("I need letter")
                            .dateRequested(ldt1)
                            .dateNeeded(ldt2)
                            .done(false)
                            .build();
            
            LocalDateTime ldt3 = LocalDateTime.parse("2022-01-03T00:00:00");
            LocalDateTime ldt4 = LocalDateTime.parse("2022-02-03T00:00:00");

            RecommendationRequest rr2 = RecommendationRequest.builder()
                            .requesterEmail("b@gmail.com")
                            .professorEmail("p@gmail.com")
                            .explanation("I need letter, pls")
                            .dateRequested(ldt3)
                            .dateNeeded(ldt4)
                            .done(false)
                            .build();

            ArrayList<RecommendationRequest> expectedRequests = new ArrayList<>();
            expectedRequests.addAll(Arrays.asList(rr1, rr2));

            when(recommendationRequestRepository.findAll()).thenReturn(expectedRequests);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequests/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedRequests);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // Tests for POST /api/ucsbdates/post...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/recommendationrequests/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/recommendationrequests/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_request() throws Exception {
            // arrange

            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
            LocalDateTime ldt2 = LocalDateTime.parse("2022-02-03T00:00:00");

            RecommendationRequest rr1 = RecommendationRequest.builder()
                            .requesterEmail("a@gmail.com")
                            .professorEmail("p@gmail.com")
                            .explanation("I need letter")
                            .dateRequested(ldt1)
                            .dateNeeded(ldt2)
                            .done(false)
                            .build();

            when(recommendationRequestRepository.save(eq(rr1))).thenReturn(rr1);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/recommendationrequests/post?requesterEmail=a@gmail.com&professorEmail=p@gmail.com&explanation=I need letter&dateRequested=2022-01-03T00:00:00&dateNeeded=2022-02-03T00:00:00&done=false")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).save(rr1);
            String expectedJson = mapper.writeValueAsString(rr1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // Get tests

    @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/recommendationrequests?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-02-03T00:00:00");

                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("a@gmail.com")
                                .professorEmail("p@gmail.com")
                                .explanation("I need letter")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();
                when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.of(rr1));

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequests?id=1"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(recommendationRequestRepository, times(1)).findById(eq(1L));
                String expectedJson = mapper.writeValueAsString(rr1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(recommendationRequestRepository.findById(eq(123L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequests?id=123"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(eq(123L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RecommendationRequest with id 123 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_ucsbdate() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-02-03T00:00:00");

                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("a@gmail.com")
                                .professorEmail("p@gmail.com")
                                .explanation("I need letter")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();
                
                LocalDateTime ldt3 = LocalDateTime.parse("2022-03-03T00:00:00");
                LocalDateTime ldt4 = LocalDateTime.parse("2022-04-03T00:00:00");

                RecommendationRequest rr2 = RecommendationRequest.builder()
                                .requesterEmail("b@gmail.com")
                                .professorEmail("asdfp@gmail.com")
                                .explanation("I need letter, pls")
                                .dateRequested(ldt3)
                                .dateNeeded(ldt4)
                                .done(true)
                                .build();

                String requestBody = mapper.writeValueAsString(rr2);

                when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.of(rr1));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/recommendationrequests?id=1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(1L);
                verify(recommendationRequestRepository, times(1)).save(rr2); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_ucsbdate_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-02-03T00:00:00");

                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("a@gmail.com")
                                .professorEmail("p@gmail.com")
                                .explanation("I need letter")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                String requestBody = mapper.writeValueAsString(rr1);

                when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/recommendationrequests?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 67 not found", json.get("message"));

        }
        
        // DELETE TESTS
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_req() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime ldt2 = LocalDateTime.parse("2022-02-03T00:00:00");

                RecommendationRequest rr1 = RecommendationRequest.builder()
                                .requesterEmail("a@gmail.com")
                                .professorEmail("p@gmail.com")
                                .explanation("I need letter")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.of(rr1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequests?id=1")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(1L);
                verify(recommendationRequestRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 1 deleted", json.get("message"));
        }
        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_request_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(recommendationRequestRepository.findById(eq(123L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequests?id=123")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(123L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 123 not found", json.get("message"));
        }
}

package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = HelpRequestsController.class)
@Import(TestConfig.class)
public class HelpRequestsControllerTests extends ControllerTestCase {
    @MockBean
    HelpRequestRepository helpRequestRepository;

    @MockBean
    UserRepository userRepository;

    // Begin tests for GET /api/helprequests/all
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/helprequests/all"))
                .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/helprequests/all"))
                .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void logged_in_user_can_get_all_helprequests() throws Exception {
        // arrange


        var helpRequest1 = HelpRequest.builder()
                .requesterEmail("ewetzel@ucsb.edu")
                .requestTime(LocalDateTime.parse("2022-01-03T00:00:00"))
                .explanation("I can't find my glasses")
                .tableOrBreakoutRoom("table 3")
                .teamId("s24-4pm-3")
                .solved(false)
                .build();

        var helpRequest2 = HelpRequest.builder()
                .requesterEmail("pconrad@ucsb.edu")
                .requestTime(LocalDateTime.parse("2022-02-03T00:00:00"))
                .explanation("I am lost in Lisbon, Portugal")
                .tableOrBreakoutRoom("breakout room 4")
                .teamId("s24-4pm-4")
                .solved(false)
                .build();


        ArrayList<HelpRequest> expectedHelpRequests = new ArrayList<>(List.of(helpRequest1, helpRequest2));

        when(helpRequestRepository.findAll()).thenReturn(expectedHelpRequests);

        // act
        MvcResult response = mockMvc.perform(get("/api/helprequests/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(helpRequestRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedHelpRequests);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // End tests for GET /api/helprequests/all

    // Begin tests for POST /api/helprequests/post
    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/helprequests/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/helprequests/post"))
                .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void an_admin_user_can_post_a_new_helprequest() throws Exception {
        // arrange

        var helpRequest = HelpRequest.builder()
                .requesterEmail("ewetzel@ucsb.edu")
                .requestTime(LocalDateTime.parse("2022-01-03T00:00:00"))
                .explanation("I can't find my glasses")
                .tableOrBreakoutRoom("table 3")
                .teamId("s24-4pm-3")
                .solved(false)
                .build();

        when(helpRequestRepository.save(eq(helpRequest))).thenReturn(helpRequest);

        // act
        String requestUrl = "/api/helprequests/post?" +
                "requesterEmail=ewetzel@ucsb.edu" +
                "&teamId=s24-4pm-3" +
                "&requestTime=2022-01-03T00:00:00" +
                "&explanation=I can't find my glasses" +
                "&tableOrBreakoutRoom=table 3" +
                "&solved=false";
        MvcResult response = mockMvc.perform(
                        post(requestUrl)
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(helpRequestRepository, times(1)).save(helpRequest);
        String expectedJson = mapper.writeValueAsString(helpRequest);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // End tests for POST /api/helprequests/post

    // Begin tests for GET /api/helprequests?id=...
    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/helprequests?id=7"))
                .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

        // arrange
        var helpRequest = HelpRequest.builder()
                .requesterEmail("ewetzel@ucsb.edu")
                .requestTime(LocalDateTime.parse("2022-01-03T00:00:00"))
                .explanation("I can't find my glasses")
                .tableOrBreakoutRoom("table 3")
                .teamId("s24-4pm-3")
                .solved(false)
                .build();

        when(helpRequestRepository.findById(eq(7L))).thenReturn(Optional.of(helpRequest));

        // act
        MvcResult response = mockMvc.perform(get("/api/helprequests?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(helpRequestRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(helpRequest);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

        // arrange

        when(helpRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/helprequests?id=7"))
                .andExpect(status().isNotFound()).andReturn();

        // assert

        verify(helpRequestRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("HelpRequest with id 7 not found", json.get("message"));
    }

    // End tests for GET /api/helprequests?id=...

    // Begin tests for PUT /api/helprequests?id=...
    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void admin_can_edit_an_existing_helprequest() throws Exception {
        // arrange

        var helpRequestOrig = HelpRequest.builder()
                .requesterEmail("ewetzel@ucsb.edu")
                .requestTime(LocalDateTime.parse("2022-01-03T00:00:00"))
                .explanation("I can't find my glasses")
                .tableOrBreakoutRoom("table 3")
                .teamId("s24-4pm-3")
                .solved(false)
                .build();

        var helpRequestEdited = HelpRequest.builder()
                .requesterEmail("pconrad@ucsb.edu")
                .requestTime(LocalDateTime.parse("2023-01-03T00:00:00"))
                .explanation("I found my glasses")
                .tableOrBreakoutRoom("table 4")
                .teamId("s24-4pm-4")
                .solved(true)
                .build();

        String requestBody = mapper.writeValueAsString(helpRequestEdited);

        when(helpRequestRepository.findById(eq(67L))).thenReturn(Optional.of(helpRequestOrig));

        // act
        MvcResult response = mockMvc.perform(
                        put("/api/helprequests?id=67")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(requestBody)
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(helpRequestRepository, times(1)).findById(67L);
        verify(helpRequestRepository, times(1)).save(helpRequestEdited); // solved should be set to true
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }


    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void admin_cannot_edit_helprequest_that_does_not_exist() throws Exception {
        // arrange

        var helpRequestEdited = HelpRequest.builder()
                .requesterEmail("ewetzel@ucsb.edu")
                .requestTime(LocalDateTime.parse("2022-01-03T00:00:00"))
                .explanation("I can't find my glasses")
                .tableOrBreakoutRoom("table 3")
                .teamId("s24-4pm-3")
                .solved(true)
                .build();

        String requestBody = mapper.writeValueAsString(helpRequestEdited);

        when(helpRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                        put("/api/helprequests?id=67")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(requestBody)
                                .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(helpRequestRepository, times(1)).findById(67L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("HelpRequest with id 67 not found", json.get("message"));
    }

    // End tests for PUT /api/helprequests?id=...

    // Begin tests for DELETE /api/helprequests?id=...
    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void admin_can_delete_a_help_request() throws Exception {
        // arrange

        var helpRequest = HelpRequest.builder()
                .requesterEmail("ewetzel@ucsb.edu")
                .requestTime(LocalDateTime.parse("2022-01-03T00:00:00"))
                .explanation("I can't find my glasses")
                .tableOrBreakoutRoom("table 3")
                .teamId("s24-4pm-3")
                .solved(false)
                .build();

        when(helpRequestRepository.findById(eq(15L))).thenReturn(Optional.of(helpRequest));

        // act
        MvcResult response = mockMvc.perform(
                        delete("/api/helprequests?id=15")
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(helpRequestRepository, times(1)).findById(15L);
        verify(helpRequestRepository, times(1)).delete(any());

        Map<String, Object> json = responseToJson(response);
        assertEquals("HelpRequest with id 15 deleted", json.get("message"));
    }

    @WithMockUser(roles = {"ADMIN", "USER"})
    @Test
    public void admin_tries_to_delete_non_existent_help_request_and_gets_right_error_message()
            throws Exception {
        // arrange

        when(helpRequestRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                        delete("/api/helprequests?id=15")
                                .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(helpRequestRepository, times(1)).findById(15L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("HelpRequest with id 15 not found", json.get("message"));
    }


}

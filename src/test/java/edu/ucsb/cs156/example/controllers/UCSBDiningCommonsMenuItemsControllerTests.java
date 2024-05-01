package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemsController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBDiningCommonsMenuItemsRepository UCSBDiningCommonsMenuItemsRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/UCSBDiningCommonsMenuItem/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_UCSBDiningCommonsMenuItem() throws Exception {

                UCSBDiningCommonsMenuItems UCSBDiningCommonsMenuItem1 = UCSBDiningCommonsMenuItems.builder()
                                .diningCommonsCode("portola")
                                .name("Cream of Broccoli Soup (v)")
                                .station("Greens & Grains")
                                .build();

                UCSBDiningCommonsMenuItems UCSBDiningCommonsMenuItem2 = UCSBDiningCommonsMenuItems.builder()
                                .diningCommonsCode("ortega")
                                .name("Chicken Caesar Salad")
                                .station("Entrees")
                                .build();

                ArrayList<UCSBDiningCommonsMenuItems> expectedMenuItems = new ArrayList<>();
                expectedMenuItems.addAll(Arrays.asList(UCSBDiningCommonsMenuItem1, UCSBDiningCommonsMenuItem2));

                when(UCSBDiningCommonsMenuItemsRepository.findAll()).thenReturn(expectedMenuItems);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBDiningCommonsMenuItemsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedMenuItems);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/UCSBDiningCommonsMenuItem/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                                .andExpect(status().is(403)); // user must have ADMIN role to post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_UCSBDiningCommonsMenuItem() throws Exception {
                
                // arrange
                UCSBDiningCommonsMenuItems UCSBDiningCommonsMenuItems1 = UCSBDiningCommonsMenuItems.builder()
                                .diningCommonsCode("portola")
                                .name("CreamofBroccoliSoup(v)")
                                .station("Greens&Grains")
                                .build();

                when(UCSBDiningCommonsMenuItemsRepository.save(eq(UCSBDiningCommonsMenuItems1))).thenReturn(UCSBDiningCommonsMenuItems1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbdiningcommonsmenuitem/post")
                                        .param("diningCommonsCode", "portola")
                                        .param("name", "CreamofBroccoliSoup(v)")
                                        .param("station", "Greens&Grains")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBDiningCommonsMenuItemsRepository, times(1)).save(UCSBDiningCommonsMenuItems1);
                String expectedJson = mapper.writeValueAsString(UCSBDiningCommonsMenuItems1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for GET /api/UCSBDiningCommonsMenuItem?=id...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=24"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_id_when_id_exists() throws Exception {

                // arrange
                UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItem = UCSBDiningCommonsMenuItems.builder()
                                .diningCommonsCode("carrillo")
                                .name("ClamChowder")
                                .station("Soup")
                                .build();

                when(UCSBDiningCommonsMenuItemsRepository.findById(eq(24L))).thenReturn(Optional.of(ucsbDiningCommonsMenuItem));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=24"))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(UCSBDiningCommonsMenuItemsRepository, times(1)).findById(eq(24L));
                String expectedJson = mapper.writeValueAsString(ucsbDiningCommonsMenuItem);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_id_when_id_does_not_exist() throws Exception {

                // arrange
                when(UCSBDiningCommonsMenuItemsRepository.findById(eq(24L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=24"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(UCSBDiningCommonsMenuItemsRepository, times(1)).findById(eq(24L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBDiningCommonsMenuItems with id 24 not found", json.get("message"));
        }
}

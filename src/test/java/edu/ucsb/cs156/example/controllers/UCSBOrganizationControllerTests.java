package edu.ucsb.cs156.example.controllers;


import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import liquibase.pro.packaged.U;
import edu.ucsb.cs156.example.controllers.UCSBOrganizationController;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper; // Import ObjectMapper if not already imported

import edu.ucsb.cs156.example.entities.UCSBDiningCommons;

import edu.ucsb.cs156.example.ControllerTestCase;





@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase {
    @MockBean
    UCSBOrganizationRepository ucsbOrganizationRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc; // imports not coming in correctly

    @Autowired
    private ObjectMapper mapper; // imports not coming in correctly

     // Tests for POST /api/ucsborganizations...

     @Test
     public void logged_out_users_cannot_post() throws Exception {
             mockMvc.perform(post("/api/UCSBOrganization/post"))
                             .andExpect(status().is(403));
     }

     @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/UCSBOrganization/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_commons() throws Exception {
                // arrange

                UCSBOrganization KC = UCSBOrganization.builder()
                                .orgCode("KC")
                                .orgTranslationShort("KevinClubShort")
                                .orgTranslation("KevinClub")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationRepository.save(eq(KC))).thenReturn(KC);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/UCSBOrganization/post?orgCode=KC&orgTranslationShort=KevinClubShort&orgTranslation=KevinClub&inactive=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).save(KC);
                String expectedJson = mapper.writeValueAsString(KC);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }



        // Tests for GET /api/ucsborganization/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/UCSBOrganization/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/UCSBOrganization/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsb_organization() throws Exception {

                // arrange

                UCSBOrganization KC = UCSBOrganization.builder()
                                .orgCode("KC")
                                .orgTranslationShort("KevinClubShort")
                                .orgTranslation("KevinClub")
                                .inactive(true)
                                .build();

                UCSBOrganization FFF = UCSBOrganization.builder()
                                .orgCode("FFF")
                                .orgTranslationShort("French Fishing Fanatics Short")
                                .orgTranslation("French Fishing Fanatics")
                                .inactive(true)
                                .build();

                UCSBOrganization AAA = UCSBOrganization.builder()
                                .orgCode("AAA")
                                .orgTranslationShort("Triple A Short")
                                .orgTranslation("Triple A")
                                .inactive(true)
                                .build();

                ArrayList<UCSBOrganization> expectedOrganizations = new ArrayList<>();
                expectedOrganizations.addAll(Arrays.asList(KC, FFF, AAA));

                when(ucsbOrganizationRepository.findAll()).thenReturn(expectedOrganizations);

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBOrganization/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedOrganizations);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }


        // Tests for GET /api/UCSBOrganization

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/UCSBOrganization?orgCode=test"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                UCSBOrganization organization = UCSBOrganization.builder()
                                .orgCode("KEV")
                                .orgTranslationShort("Kevin short")
                                .orgTranslation("Kevin")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationRepository.findById(eq("KEV"))).thenReturn(Optional.of(organization));

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBOrganization?orgCode=KEV"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findById(eq("KEV"));
                String expectedJson = mapper.writeValueAsString(organization);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbOrganizationRepository.findById(eq("KEV"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/UCSBOrganization?orgCode=KEV"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findById(eq("KEV"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganization with id KEV not found", json.get("message"));
        }



        // Tests for PUT /api/UCSBOrganization

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_organization() throws Exception {
                // arrange

                UCSBOrganization randomOrig = UCSBOrganization.builder()
                                .orgCode("RANDOM")
                                .orgTranslationShort("random")
                                .orgTranslation("random")
                                .inactive(true)
                                .build();
                                
                UCSBOrganization randomEdited = UCSBOrganization.builder()
                                .orgCode("RNDM")
                                .orgTranslationShort("random new")
                                .orgTranslation("random new")
                                .inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(randomEdited);

                when(ucsbOrganizationRepository.findById(eq("random"))).thenReturn(Optional.of(randomOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBOrganization?orgCode=random")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("random");
                verify(ucsbOrganizationRepository, times(1)).save(randomEdited); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_org_that_does_not_exist() throws Exception {
                // arrange
                
                UCSBOrganization dog = UCSBOrganization.builder()
                                .orgCode("dog")
                                .orgTranslationShort("dog short")
                                .orgTranslation("dog")
                                .inactive(true)
                                .build();

                String requestBody = mapper.writeValueAsString(dog);

                when(ucsbOrganizationRepository.findById(eq("dog"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/UCSBOrganization?orgCode=dog")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("dog");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id dog not found", json.get("message"));

        }




        // Tests for DELETE /api/UCSBOrganization

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_org() throws Exception {
                // arrange

                UCSBOrganization KEV = UCSBOrganization.builder()
                                .orgCode("KEV")
                                .orgTranslationShort("Kevin short")
                                .orgTranslation("Kevin")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationRepository.findById(eq("KEV"))).thenReturn(Optional.of(KEV));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBOrganization?orgCode=KEV")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("KEV");
                verify(ucsbOrganizationRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id KEV deleted", json.get("message"));
        }


        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_organization_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbOrganizationRepository.findById(eq("non-existent-organization"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/UCSBOrganization?orgCode=non-existent-organization")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("non-existent-organization");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id non-existent-organization not found", json.get("message"));
        }





    

}

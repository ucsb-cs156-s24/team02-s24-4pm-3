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




@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests {
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




    

}

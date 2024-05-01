package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;


@Tag(name = "UCSBDiningCommonsMenuItem") 
@RequestMapping("/api/ucsbdiningcommonsmenuitem")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemsController extends ApiController {
    
    @Autowired
    UCSBDiningCommonsMenuItemsRepository ucsbDiningCommonsMenuItemsRepository;

    @Operation(summary = "Returns list of all menu Items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItems> allUCSBDiningCommonsMenuItems() {
        Iterable<UCSBDiningCommonsMenuItems> menuItems = ucsbDiningCommonsMenuItemsRepository.findAll();
        return menuItems;
    }

    @Operation(summary = "Creates a new menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItems postUCSBDiningCommonsMenuItem(
            @Parameter(name = "diningCommonsCode") @RequestParam String diningCommonsCode,
            @Parameter(name = "name") @RequestParam String name,
            @Parameter(name = "station") @RequestParam String station)
            throws JsonProcessingException {

        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems = new UCSBDiningCommonsMenuItems();
        ucsbDiningCommonsMenuItems.setDiningCommonsCode(diningCommonsCode);
        ucsbDiningCommonsMenuItems.setName(name);
        ucsbDiningCommonsMenuItems.setStation(station);

        UCSBDiningCommonsMenuItems newUcsbDiningCommonsMenuItem = ucsbDiningCommonsMenuItemsRepository.save(ucsbDiningCommonsMenuItems);

        return newUcsbDiningCommonsMenuItem;
    }

    @Operation(summary= "Gets a single menu item")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItems getById(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItems = ucsbDiningCommonsMenuItemsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

        return ucsbDiningCommonsMenuItems;
    }

    @Operation(summary= "Deletes a single menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBDiningCommonsMenuItem(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBDiningCommonsMenuItems ucsbDiningCommonsMenuItem = ucsbDiningCommonsMenuItemsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

        ucsbDiningCommonsMenuItemsRepository.delete(ucsbDiningCommonsMenuItem);
        return genericMessage("UCSBDiningCommonsMenuItem %s deleted".formatted(id));
    }
}

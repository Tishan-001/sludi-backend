package com.sludi.sludi.controller;

import com.sludi.sludi.domain.Identity;
import com.sludi.sludi.service.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identity")
public class IdentityController {

    @Autowired
    private IdentityService identityService;

    @PostMapping("/init")
    public ResponseEntity<String> initLedger() throws Exception {
        identityService.initLedger();
        return ResponseEntity.ok("Ledger initialized successfully");
    }

    @GetMapping("/identities")
    public ResponseEntity<String> getAllIdentities() throws Exception {
        return ResponseEntity.ok(identityService.getAllIdentities());
    }

    @PostMapping("/identities")
    public ResponseEntity<String> createIdentity(@RequestBody Identity request) throws Exception {
        identityService.createIdentity(request);
        return ResponseEntity.ok("Identity created successfully");
    }

    @GetMapping("/identities/{nic}")
    public ResponseEntity<String> readIdentity(@PathVariable String nic) throws Exception {
        return ResponseEntity.ok(identityService.readIdentity(nic));
    }

    @PutMapping("/identities/{nic}")
    public ResponseEntity<String> updateIdentity(@PathVariable String nic, @RequestBody Identity request)
            throws Exception {
        identityService.updateIdentity(nic, request);
        return ResponseEntity.ok("Identity updated successfully");
    }

    @DeleteMapping("/identities/{nic}")
    public ResponseEntity<String> deleteIdentity(@PathVariable String nic) throws Exception {
        identityService.deleteIdentity(nic);
        return ResponseEntity.ok("Identity deleted successfully");
    }

    @GetMapping("/identities/{nic}/exists")
    public ResponseEntity<Boolean> identityExists(@PathVariable String nic) throws Exception {
        return ResponseEntity.ok(identityService.identityExists(nic));
    }
}

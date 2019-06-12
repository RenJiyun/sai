package com.eggip.sai.web.rest;

import com.eggip.sai.domain.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StoreResource {

    private final Logger logger = LoggerFactory.getLogger(StoreResource.class);


    @GetMapping("/stores")
    public ResponseEntity<List<Store>> getStores() {
        return null;
    }


    @PostMapping("/stores")
    public ResponseEntity<Store> createStore() {
        return null;
    }

    @PutMapping("/stores")
    public ResponseEntity<Store> updateStore() {
        return null;
    }

}

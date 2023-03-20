package com.nassafy.api.controller;

import com.nassafy.api.service.AttrationService;
import com.nassafy.core.entity.Attraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AttrationController {
    @Autowired
    private AttrationService attrationService;

    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello");}

    @GetMapping("/stamps/nations")
    public ResponseEntity<List<String>> getAllNation(){
        List<String> nations = attrationService.getAllNation();
        return ResponseEntity.ok(nations);
    }

//    @PostMapping("/stamps/attrations/{nation}")
//    public ResponseEntity<>


    @GetMapping("/stamps/attrations/{nation}")
    public ResponseEntity<List<Attraction>> getAttrationsByNation(@PathVariable String nation){
        List<Attraction> attractionList = attrationService.getAttractionByNation(nation);
        return ResponseEntity.ok(attractionList);
    }
}

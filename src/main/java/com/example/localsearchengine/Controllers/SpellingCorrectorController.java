package com.example.localsearchengine.Controllers;

import com.example.localsearchengine.SpellingCorrector.SpellingCorrector;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "spelling")
public class SpellingCorrectorController {

    @Autowired
    SpellingCorrector spellingCorrector;


    @GetMapping(value = "suggest")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String word){
        if(word.length() < 3) return ResponseEntity.notFound().build();
        System.out.println("Suggesting for " + word + ".");
        List<String> returnedSuggestions = spellingCorrector.returnSuggestions(word);
        return returnedSuggestions != null && !returnedSuggestions.isEmpty() ? ResponseEntity.ok(returnedSuggestions) : ResponseEntity.notFound().build();
    }
}

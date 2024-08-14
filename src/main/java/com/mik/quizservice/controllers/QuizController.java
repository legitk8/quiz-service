package com.mik.quizservice.controllers;

import com.mik.quizservice.models.Quiz;
import com.mik.quizservice.models.QuizDto;
import com.mik.quizservice.models.Response;
import com.mik.quizservice.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return quizService.healthCheck();
    }

    // TODO: check if number of questions enough
    @PostMapping("/create")
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizDto quizDto) {
        return quizService.createQuiz(quizDto);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable int id) {
        return quizService.getQuiz(id);
    }

    @PostMapping("/submit/{id}")
    public ResponseEntity<Integer> submitQuiz(@PathVariable("id") int quizId, @RequestBody List<Response> responses) {
        return quizService.getScore(quizId, responses);
    }
}

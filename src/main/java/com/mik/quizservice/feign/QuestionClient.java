package com.mik.quizservice.feign;

import com.mik.quizservice.models.Question;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "QUESTION-SERVICE")
public interface QuestionClient {
    @GetMapping("/api/question/generate")
    ResponseEntity<List<Integer>> generateQuestionIds(@RequestParam List<String> categories,
                                                             @RequestParam int count);

    @GetMapping("/api/question/questions")
    ResponseEntity<List<Question>> getQuestionsByIds(@RequestParam List<Integer> questionIds);

}

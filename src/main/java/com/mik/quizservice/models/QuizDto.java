package com.mik.quizservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {
    private String title;
    private List<String> categories;
    private int numberOfQuestions;
    private List<QuestionWrapper> questions;
}

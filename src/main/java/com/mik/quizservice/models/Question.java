package com.mik.quizservice.models;

import lombok.Data;

@Data
public class Question {
    private int id;
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;
    private String category;
}

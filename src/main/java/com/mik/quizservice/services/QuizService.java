package com.mik.quizservice.services;

import com.mik.quizservice.feign.QuestionClient;
import com.mik.quizservice.models.*;
import com.mik.quizservice.repositories.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    QuestionClient questionClient;

    @Autowired
    public QuizService(QuizRepository quizRepository, QuestionClient questionClient) {
        this.quizRepository = quizRepository;
        this.questionClient = questionClient;
    }

    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("health ok", HttpStatus.OK);
    }

    public ResponseEntity<Quiz> createQuiz(QuizDto quizDto) {
        String title = quizDto.getTitle();
        List<String> categories = quizDto.getCategories();
        int numOfQuestions = quizDto.getNumberOfQuestions();

        ResponseEntity<List<Integer>> response = questionClient.generateQuestionIds(categories, numOfQuestions);
        List<Integer> questionIds = response.getBody();

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCategories(categories);
        quiz.setQuestionIds(questionIds);

        quizRepository.save(quiz);

        return new ResponseEntity<>(quiz, response.getStatusCode());
    }

    // makes sure
    // question is not null or empty
    // quiz is not null
    public ResponseEntity<List<Question>> getQuestionsFromQuizId(int quizId) {
        Optional<Quiz> quiz = quizRepository.findById(quizId);

        if (quiz.isPresent()) {
            // questionId List from quiz-table
            List<Integer> questionIds = quiz.get().getQuestionIds();

            // fetch the questions from question-service
            ResponseEntity<List<Question>> response = questionClient.getQuestionsByIds(questionIds);
            List<Question> questions = response.getBody();

            // TODO: handle validation separately
            if(questions == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(questions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(questions, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<QuizDto> getQuiz(int quizId) {
        ResponseEntity<List<Question>> responseEntity = getQuestionsFromQuizId(quizId);
        List<Question> questions = responseEntity.getBody();
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        if(statusCode == HttpStatus.OK) {
            Quiz quiz = quizRepository.findById(quizId).orElse(null);

            QuizDto quizDto = getQuizDto(questions, quiz, quiz.getQuestionIds());
            return new ResponseEntity<>(quizDto, statusCode);
        }
        else {
            return new ResponseEntity<>(statusCode);
        }
    }

    // helper method to create quizDto
    private static QuizDto getQuizDto(List<Question> questions, Quiz quiz, List<Integer> questionIds) {
        List<QuestionWrapper> wrappedQuestions = new ArrayList<>();

        for(Question question : questions) {
            QuestionWrapper wrappedQuestion = new QuestionWrapper(question.getId(), question.getQuestionTitle(),
                    question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4());

            wrappedQuestions.add(wrappedQuestion);
        }

        return new QuizDto(quiz.getTitle(), quiz.getCategories(), questionIds.size(),
                wrappedQuestions);
    }

    // calculate quiz score
    public ResponseEntity<Integer> getScore(int quizId, List<Response> responses) {
        int score = 0;

        ResponseEntity<List<Question>> responseEntity = getQuestionsFromQuizId(quizId);
        List<Question> questions = responseEntity.getBody();
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        if(statusCode == HttpStatus.OK) {
            Map<Integer, Response> responseMap = new HashMap<>();
            for(Response response : responses) {
                responseMap.put(response.getQuestionId(), response);
            }

            for(Question question : questions) {
                Response response = responseMap.get(question.getId());
                if (response != null && question.getCorrectAnswer().equals(response.getAnswer())) {
                    score++;
                }

            }

            return new ResponseEntity<>(score, statusCode);
        }
        else {
            return new ResponseEntity<>(statusCode);
        }

    }
}
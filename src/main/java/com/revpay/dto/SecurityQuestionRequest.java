package com.revpay.dto;

public class SecurityQuestionRequest {

    private String question;
    private String answer;

    // ===== CONSTRUCTOR =====
    public SecurityQuestionRequest() {
    }

    // ===== GETTERS & SETTERS =====

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
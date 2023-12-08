package com.example.gcache.model;

public class Question {

    private String answer;
    private int index;
    private String question;

    public Question() {}

    public Question(String answer,
                    int index,
                    String question) {
        this.answer = answer;
        this.index = index;
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

}

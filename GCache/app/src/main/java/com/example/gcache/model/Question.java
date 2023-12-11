package com.example.gcache.model;

/**
 * This class represents the list of questions that is generated for the user
 * when creating a post. Also adds into the point system
 */
public class Question {

    private String answer;
    private int index;
    private String question;

    public Question() {}

    /**
     * Parameters of a question
     * @param answer user's answer to question
     * @param index val of index
     * @param question the question asked to user
     */
    public Question(String answer,
                    int index,
                    String question) {
        this.answer = answer;
        this.index = index;
        this.question = question;
    }

    /**
     * String that returns user answer
     * @return string answer
     */
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Gets index
     * @return val of index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index
     * @param index set val of index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * String that returns the question for user
     * @return string question
     */
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

}

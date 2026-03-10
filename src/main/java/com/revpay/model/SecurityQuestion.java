package com.revpay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "SECURITY_QUESTIONS")
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_seq")
    @SequenceGenerator(name = "sq_seq", sequenceName = "GEN_SQ_ID", allocationSize = 1)
    @Column(name = "SQ_ID")
    private Long sqId;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "USER_ID", unique = true)
    private User user;

    @Column(name = "QUESTION")
    private String question;

    @Column(name = "ANSWER_HASH")
    private String answerHash;

    public SecurityQuestion() {}

    public SecurityQuestion(Long sqId, User user, String question, String answerHash) {
        this.sqId = sqId;
        this.user = user;
        this.question = question;
        this.answerHash = answerHash;
    }

    public Long getSqId() { return sqId; }
    public void setSqId(Long sqId) { this.sqId = sqId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswerHash() { return answerHash; }
    public void setAnswerHash(String answerHash) { this.answerHash = answerHash; }
}
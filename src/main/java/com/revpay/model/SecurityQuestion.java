package com.revpay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SECURITY_QUESTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_seq")
    @SequenceGenerator(name = "sq_seq", sequenceName = "GEN_SQ_ID", allocationSize = 1)
    @Column(name = "SQ_ID")
    private Long sqId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY) // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @JoinColumn(name = "USER_ID", unique = true)
    private User user;

    @Column(name = "QUESTION")
    private String question;

    @Column(name = "ANSWER_HASH")
    private String answerHash;
}
package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class MemberEntity {

    @Id
    private Long seq;

    private String loginId;

    private String password;
}

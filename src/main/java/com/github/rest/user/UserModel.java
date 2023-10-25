package com.github.rest.user;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private int id;

    @Getter
    @Column(name = "username")
    private String username;

    @Getter
    @Column(name = "password")
    private String password;

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;

    }
}

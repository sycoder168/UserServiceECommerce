package com.scaler.userservicecapstone.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class User extends Base {
    private String name;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
}

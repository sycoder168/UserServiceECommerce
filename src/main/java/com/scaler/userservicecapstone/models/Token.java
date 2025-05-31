package com.scaler.userservicecapstone.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Token extends Base {
    private String tokenValue;
    private Date expiryDate;

    @ManyToOne
    private User user;
}

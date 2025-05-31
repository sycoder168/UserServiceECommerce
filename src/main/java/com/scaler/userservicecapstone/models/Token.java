package com.scaler.userservicecapstone.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Token extends Base {
    private String tokenValue;
    private String expiryDate;

    @ManyToOne
    private User user;
}

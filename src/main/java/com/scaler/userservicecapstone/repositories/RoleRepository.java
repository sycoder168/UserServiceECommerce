package com.scaler.userservicecapstone.repositories;

import com.scaler.userservicecapstone.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // You can add custom query methods here if needed
    // For example:
    // Role findByValue(int value);
}
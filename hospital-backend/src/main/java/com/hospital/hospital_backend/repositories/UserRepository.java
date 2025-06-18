package com.hospital.hospital_backend.repositories;

import com.hospital.hospital_backend.entities.ERole;
import com.hospital.hospital_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<User> findByFirebaseId(String firebaseId);
    Optional<User> findByPatMedAdminIdAndRole(Long patMedAdminId, ERole role);

}

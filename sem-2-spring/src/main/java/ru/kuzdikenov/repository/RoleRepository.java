package ru.kuzdikenov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kuzdikenov.model.Role;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}

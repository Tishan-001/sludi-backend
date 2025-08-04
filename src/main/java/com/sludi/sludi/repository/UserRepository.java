package com.sludi.sludi.repository;

import com.sludi.sludi.domain.Identity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Identity, String> {

}

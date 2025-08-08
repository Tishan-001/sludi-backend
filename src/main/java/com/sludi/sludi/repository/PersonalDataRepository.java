package com.sludi.sludi.repository;

import com.sludi.sludi.domain.PersonalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalDataRepository extends JpaRepository<PersonalData, Long> {

    Optional<PersonalData> findByNic(String nic);

    boolean existsByNic(String nic);

    void deleteByNic(String nic);
}
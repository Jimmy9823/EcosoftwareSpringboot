package com.EcoSoftware.Scrum6.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.CapacitacionEntity;

@Repository
public interface CapacitacionRepository extends JpaRepository<CapacitacionEntity, Long> {

    List<CapacitacionEntity> findByNombreContainingIgnoreCase(String nombre);
}

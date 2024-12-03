package com.kovan.repository;

import com.kovan.entities.InventoryManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryManagementRepository extends JpaRepository<InventoryManagement,Long> {
}

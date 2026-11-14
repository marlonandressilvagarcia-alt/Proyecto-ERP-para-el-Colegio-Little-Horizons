package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.SchoolResource;

import java.util.List;
import java.util.Optional;

public interface ResourceDAO {
    boolean create(SchoolResource resource);
    Optional<SchoolResource> findById(int id);
    Optional<SchoolResource> findByCode(String code);
    List<SchoolResource> findAll();
    List<SchoolResource> findByCategory(int categoryId);
    boolean updateStatus(int resourceId, String newStatus);
    List<ResourceCategory> findAllCategories();
}

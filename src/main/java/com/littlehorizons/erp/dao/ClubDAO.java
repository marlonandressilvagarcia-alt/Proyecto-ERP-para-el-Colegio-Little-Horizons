package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;

import java.util.List;
import java.util.Optional;

public interface ClubDAO {
    boolean create(Club club);
    Optional<Club> findById(int id);
    Optional<Club> findByName(String name);
    List<Club> findAll();
    int countActiveMembers(int clubId);
    boolean addMembership(ClubMembership membership);
    boolean withdrawMembership(int studentId, int clubId, String reason);
    List<ClubMembership> findMembershipsByStudent(int studentId);
}

package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Attendance;

import java.util.List;

public interface AttendanceDAO {
    boolean recordAttendance(Attendance attendance);
    List<Attendance> findByStudent(int studentId, int subjectId);
    List<Attendance> findUnexcusedAbsences(int studentId);
}

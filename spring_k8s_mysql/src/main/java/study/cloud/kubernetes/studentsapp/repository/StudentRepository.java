package study.cloud.kubernetes.studentsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.cloud.kubernetes.studentsapp.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}

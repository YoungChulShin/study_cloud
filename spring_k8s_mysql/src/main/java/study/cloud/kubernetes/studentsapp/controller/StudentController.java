package study.cloud.kubernetes.studentsapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import study.cloud.kubernetes.studentsapp.entity.Student;
import study.cloud.kubernetes.studentsapp.repository.StudentRepository;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StudentController {

    private final StudentRepository studentRepository;

    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    @PostMapping("/student")
    public String addStudent(@RequestBody Student student) {
        studentRepository.save(student);
        return "saved. " + student.getId();
    }

    @GetMapping("/student/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentRepository.findById(id).orElseGet(null);
    }

    @DeleteMapping("/student/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "deleted. " + id;
    }
}

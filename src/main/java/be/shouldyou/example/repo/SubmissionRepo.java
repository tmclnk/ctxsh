package be.shouldyou.example.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import be.shouldyou.example.entity.Submission;

public interface SubmissionRepo extends JpaRepository<Submission, Integer> {

}

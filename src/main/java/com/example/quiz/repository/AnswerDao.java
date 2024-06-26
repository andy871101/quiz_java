package com.example.quiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz.entity.Answer;

@Repository
public interface AnswerDao extends JpaRepository<Answer, Integer> {

	public List<Answer> findByQuizId(int quizId);

	public List<Answer> findByQuizIdOrderByQuId(int quizId);

	public boolean existsByQuizIdAndEmail(int quizId, String email);
	
	@Query(value = "SELECT * FROM quiz.answer WHERE quiz_id", nativeQuery = true)
	public List<Answer> answerSearch(int quizId);
}

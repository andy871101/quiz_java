package com.example.quiz.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz.entity.Answer;
import com.example.quiz.entity.Quiz;
import com.example.quiz.entity.QuizId;

@Repository
@Transactional
public interface QuizDao extends JpaRepository<Quiz, QuizId> {

	public boolean existsByQuizId(int quizId);

//	後台搜尋用
	public List<Quiz> findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String quizName,
			LocalDate startDate, LocalDate endDate);

//	前台搜尋用
	public List<Quiz> findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
			String quizName, LocalDate startDate, LocalDate endDate);

//	刪問卷
	public void deleteAllByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(List<Integer> quizIds,
			List<Integer> quizIds2, LocalDate now);

//	刪題目
	public void deleteByQuizIdAndQuIdInAndPublishedFalseOrQuizIdAndQuIdInAndStartDateAfter(int quizId1,
			List<Integer> quizIds1, int quizId2, List<Integer> quizIds2, LocalDate now);

	public List<Quiz> findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(int quizId1, int quizId2,
			LocalDate now);

	public boolean existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(int quizId1, int quizId2, LocalDate now);

	public void deleteByQuizId(int quizId);

	@Query(value = "select qu_id from  quiz.quiz where quiz_id = ?1 And necessary = true", nativeQuery = true)
	public List<Integer> findQuIdsByQuizIdAndNecessaryTrue(int quizId);

	public List<Quiz> findByQuizId(int quizId);

	@Query(value = "SELECT * FROM quiz.quiz WHERE qu_id=1 AND quiz_name LIKE CONCAT('%', ?1, '%') AND start_date >= ?2 AND end_date <= ?3 AND published = 1;", nativeQuery = true)
	public List<Quiz> showQuiz(String quizName, LocalDate startDate, LocalDate endDate, boolean isBack);
	
	@Query(value = "SELECT * FROM quiz.quiz WHERE qu_id=1 AND quiz_name LIKE CONCAT('%', ?1, '%') AND start_date >= ?2 AND end_date <= ?3", nativeQuery = true)
	public List<Quiz> backShowQuiz(String quizName, LocalDate startDate, LocalDate endDate);
	
	
	
}

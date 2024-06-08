package com.example.quiz.service.ifs;

import java.time.LocalDate;
import java.util.List;

import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.AnswerRes;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.DeleteQuizReq;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticsRes;

public interface QuizService {

	public BaseRes create(CreateOrUpdateReq req);

	public SearchRes search(String quizName, LocalDate startDate, LocalDate endDate, boolean isBackend);

	public BaseRes deleteQuiz(DeleteQuizReq req);

	public BaseRes deleteQuestions(int quizId, List<Integer> quIds);

	public BaseRes update(CreateOrUpdateReq req);

	public BaseRes answer(AnswerReq req);

	public StatisticsRes statistics(int quizId);

	public BaseRes objMapper(String str);
//	�e�x�j�M
	public SearchRes showQuiz(String quizName, LocalDate startDate, LocalDate endDate,boolean isBack);
//	��x�j�M	
	public SearchRes backShowQuiz(String quizName, LocalDate startDate, LocalDate endDate);
//	id�j�Mquiz
	public SearchRes replySearch (int quizId);
// 	��quizId���D��
	public AnswerRes answerSearch (int quizId);
	
}

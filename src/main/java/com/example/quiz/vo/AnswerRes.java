package com.example.quiz.vo;

import java.util.List;

import com.example.quiz.entity.Answer;

public class AnswerRes extends BaseRes{
	private List<Answer> answerList;

	public AnswerRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AnswerRes(int code, String message) {
		super(code, message);
	}

	public AnswerRes(int code, String message, List<Answer> quizList) {
		super(code, message);
		this.answerList = quizList;
	}

	public List<Answer> getQuizList() {
		return answerList;
	}

	public void setQuizList(List<Answer> quizList) {
		this.answerList = quizList;
	}
}

package com.example.quiz.entity;

import java.io.Serializable;

public class QuizId implements Serializable {

	private int quizId;

	private int quId;

	public QuizId() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QuizId(int quizId, int quId) {
		super();
		this.quizId = quizId;
		this.quId = quId;
	}

}

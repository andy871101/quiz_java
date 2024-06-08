package com.example.quiz.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@IdClass(value = QuizId.class)
@Table(name = "quiz")
public class Quiz {

	public Quiz() {
		super();
	}

	public Quiz(int quizId, int quId, String quizName, String quizDiscription, LocalDate startTime, LocalDate endTime,
			String question, String type, boolean necessary, String options, boolean published) {
		super();
		this.quizId = quizId;
		this.quId = quId;
		this.quizName = quizName;
		this.quizDiscription = quizDiscription;
		this.startDate = startTime;
		this.endDate = endTime;
		this.Question = question;
		this.type = type;
		this.necessary = necessary;
		this.options = options;
		this.published = published;
	}

	@Id
	@Column(name = "quiz_id")
	@JsonProperty("quiz_id")
	private int quizId;// 問卷編號

	@Id
	@Column(name = "qu_id")
	@JsonProperty("qu_id")
	private int quId;// 題目編號

	@Column(name = "quiz_name")
	@JsonProperty("quiz_name")
	private String quizName;// 問卷名稱

	@Column(name = "quiz_discription")
	@JsonProperty("quiz_discription")
	private String quizDiscription;// 問卷說明

	@Column(name = "start_date")
	@JsonProperty("start_date")
	private LocalDate startDate;// 問卷開始時間

	@Column(name = "end_date")
	@JsonProperty("end_date")
	private LocalDate endDate;// 問卷結束時間

	@Column(name = "question")
	@JsonProperty("question_name")
	private String Question;// 題目問題

	@Column(name = "type")
	@JsonProperty("question_type")
	private String type;// 題目單、多選、文字敘述題

	@Column(name = "necessary")
	@JsonProperty("is_necessary")
	private boolean necessary;// 題目必填勾選欄位

	@Column(name = "options")
	@JsonProperty("question_options")
	private String options;// 題目選項

	@Column(name = "published")
	@JsonProperty("is_published")
	private boolean published;// 是否發布

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getQuId() {
		return quId;
	}

	public void setQuId(int quId) {
		this.quId = quId;
	}

	public String getQuizName() {
		return quizName;
	}

	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}

	public String getQuizDiscription() {
		return quizDiscription;
	}

	public void setQuizDiscription(String quizDiscription) {
		this.quizDiscription = quizDiscription;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getQuestion() {
		return Question;
	}

	public void setQuestion(String question) {
		Question = question;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNecessary() {
		return necessary;
	}

	public void setNecessary(boolean necessary) {
		this.necessary = necessary;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

}

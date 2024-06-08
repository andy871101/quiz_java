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
	private int quizId;// �ݨ��s��

	@Id
	@Column(name = "qu_id")
	@JsonProperty("qu_id")
	private int quId;// �D�ؽs��

	@Column(name = "quiz_name")
	@JsonProperty("quiz_name")
	private String quizName;// �ݨ��W��

	@Column(name = "quiz_discription")
	@JsonProperty("quiz_discription")
	private String quizDiscription;// �ݨ�����

	@Column(name = "start_date")
	@JsonProperty("start_date")
	private LocalDate startDate;// �ݨ��}�l�ɶ�

	@Column(name = "end_date")
	@JsonProperty("end_date")
	private LocalDate endDate;// �ݨ������ɶ�

	@Column(name = "question")
	@JsonProperty("question_name")
	private String Question;// �D�ذ��D

	@Column(name = "type")
	@JsonProperty("question_type")
	private String type;// �D�س�B�h��B��r�ԭz�D

	@Column(name = "necessary")
	@JsonProperty("is_necessary")
	private boolean necessary;// �D�إ���Ŀ����

	@Column(name = "options")
	@JsonProperty("question_options")
	private String options;// �D�ؿﶵ

	@Column(name = "published")
	@JsonProperty("is_published")
	private boolean published;// �O�_�o��

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

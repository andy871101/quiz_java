package com.example.quiz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "answer")
public class Answer {

	public Answer() {
		super();
	}

	public Answer(String name, String phone, String email, int age, int quizId, int quId, String answer) {
		super();
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.quizId = quizId;
		this.quId = quId;
		this.answer = answer;
	}

//	此欄位在DB是 Ai(Auto incremental)自動增長
//	GenerationType.IDENTITY指主鍵的增長交由資料庫
//	當屬性資料型態是Integer 時，要加
//	當屬性資料型態是int 時，非必需;但若要在新增(Save後)後即時取得新增資訊的流水號就要加
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id")
	private int id;//

	@Column(name = "name")
	private String name;//

	@Column(name = "phone")
	private String phone;//

	@Column(name = "email")
	private String email;//

	@Column(name = "age")
	private int age;//

	@Column(name = "quiz_id")
	private int quizId;//

	@Column(name = "qu_id")
	private int quId;//

	@Column(name = "answer")
	private String answer;//

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

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

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}

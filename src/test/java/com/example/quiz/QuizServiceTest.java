package com.example.quiz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.quiz.entity.Quiz;
import com.example.quiz.service.ifs.QuizService;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;

@SpringBootTest
public class QuizServiceTest {

	@Autowired
	private QuizService quizService;

//	@BeforeEach
//	private void addData() {
//		CreateOrUpdateReq req = new CreateOrUpdateReq();
//		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(2, 1, "test", "test", LocalDate.now().plusDays(2),
//				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
//		quizService.create(req);
//	}

	@Test
	public void creatTest() {
		CreateOrUpdateReq req = new CreateOrUpdateReq();
//		List為空
		BaseRes res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		題號測試
		quizIdTest(req, res);
//		問題測試
		quIdTest(req, res);
//		quizName測試
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		startDate測試
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", null, LocalDate.now().plusDays(9),
				"q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		endDate測試
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2), null,
				"q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		問題名稱測試
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		type測試
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		startDate > endDate測試
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(9),
				LocalDate.now().plusDays(2), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		成功
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 200, "creat test fail!");
//		測試已存在資料
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 200, "creat test fail!");
//		刪除測試資料
	}

	private void quizIdTest(CreateOrUpdateReq req, BaseRes res) {
//		題號測試quizIdTest
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(0, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
	}

	private void quIdTest(CreateOrUpdateReq req, BaseRes res) {
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, -1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
	}

	@Test
	public void answerTest() {
		
		quizService.answer(null);
	}

}

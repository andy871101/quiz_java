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
//		List����
		BaseRes res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		�D������
		quizIdTest(req, res);
//		���D����
		quIdTest(req, res);
//		quizName����
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		startDate����
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", null, LocalDate.now().plusDays(9),
				"q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		endDate����
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2), null,
				"q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		���D�W�ٴ���
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		type����
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		startDate > endDate����
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(9),
				LocalDate.now().plusDays(2), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "creat test fail!");
//		���\
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 200, "creat test fail!");
//		���դw�s�b���
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 200, "creat test fail!");
//		�R�����ո��
	}

	private void quizIdTest(CreateOrUpdateReq req, BaseRes res) {
//		�D������quizIdTest
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

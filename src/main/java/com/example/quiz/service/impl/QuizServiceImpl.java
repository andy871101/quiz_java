package com.example.quiz.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz.constants.RtnCode;
import com.example.quiz.entity.Answer;
import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.AnswerDao;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.service.ifs.QuizService;
import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.AnswerRes;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.DeleteQuizReq;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticsRes;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;
	@Autowired
	private AnswerDao answerDao;

	@Override
	public BaseRes create(CreateOrUpdateReq req) {
		return checkParams(req, true);
	}

	@Override
	public SearchRes search(String quizName, LocalDate startDate, LocalDate endDate, boolean isBackend) {
		if (!StringUtils.hasText(quizName)) {
			quizName = "";// containing 可帶空字串，可撈全部
		}
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);// 將開始時間設定很久以前
		}
		if (endDate == null) {
			endDate = LocalDate.of(9999, 1, 1);// 將結束時間設定很久以後
		}
		if (isBackend == true) {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(quizName,
							startDate, endDate));
		} else {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
							quizName, startDate, endDate));
		}

	}

	@Override
	public BaseRes deleteQuiz(DeleteQuizReq req) {
		if (CollectionUtils.isEmpty(req.getQuizIds())) {// 同時判斷QuizId是否為null或空集合
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		quizDao.deleteAllByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(req.getQuizIds(), req.getQuizIds(),
				LocalDate.now());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes deleteQuestions(int quizId, List<Integer> quIds) {
		if (quizId <= 0 || CollectionUtils.isEmpty(quIds)) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		// 根據 quizId and未發布 or
		List<Quiz> res = quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(//
				quizId, quizId, LocalDate.now());
		if (res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(), RtnCode.QUIZ_NOT_FOUND.getMessage());

		}
		List<Quiz> retainList = new ArrayList<>();
		for (Quiz item : res) {
			if (!quIds.contains(item.getQuId())) {// 保留不刪除的Id
				retainList.add(item);// 放入new出的新List陣列保存
			}
		}
		for (int i = 0; i < retainList.size(); i++) {
			retainList.get(i).setQuId(i + 1);// 更新更正List的Id ，原本ID是舊的保存進來的ID ，
		}
		// 刪除整張問卷
		quizDao.deleteByQuizId(quizId);
		// 將保留的問題存回DB
		if (!retainList.isEmpty()) {
			quizDao.saveAll(retainList);
		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes update(CreateOrUpdateReq req) {
		return checkParams(req, false);
	}

	private BaseRes checkParams(CreateOrUpdateReq req, boolean isCreate) {
		if (CollectionUtils.isEmpty(req.getQuizList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		// 檢查必填項目
		for (Quiz item : req.getQuizList()) {
			if (item.getQuizId() <= 0 || item.getQuId() <= 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
		// 蒐集req 中所有的 quizId
		// 原則上是一個req中所有quizId會相同(一張問卷多個問題)，但也有可能其中一筆資料的quizId是錯的
		// 為保證所有資料正確，就先去蒐集所有quizId
//				List<Integer> quizIdList = new ArrayList<>(); //List 允許重複的值存在
//				for (Quiz item : req.getQuizList()) {
//					if (!quizIdList.contains(item.getQuizId())) {
//						quizIdList.add(item.getQuizId());
//					}
//				List和Set上下兩段是一樣的
		Set<Integer> quizIdList = new HashSet<>(); // set 不會存在相同得值，就是set中如果已存在相同的值，就不會新增
		Set<Integer> quIdList = new HashSet<>();// 檢查問題編號有沒有重複，正常不會重複
		for (Quiz item : req.getQuizList()) {
			quizIdList.add(item.getQuizId());
			quIdList.add(item.getQuId());
		}
		if (quizIdList.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_EXISTS.getCode(), RtnCode.QUIZ_EXISTS.getMessage());
		}
		if (quIdList.size() != req.getQuizList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}
		// 檢查開始時間不能大於結束時間
		for (Quiz item : req.getQuizList()) {
			if (item.getStartDate().isAfter(item.getEndDate())) {
				return new BaseRes(RtnCode.TIME_FORMAT_ARROR.getCode(), RtnCode.TIME_FORMAT_ARROR.getMessage());
			}
		}
		if (isCreate) {
			// 檢查問卷是否已存在
			if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {// 先判斷完有沒有重複，所以可以直接拿隨便一個值來判斷
				return new BaseRes(RtnCode.QUIZ_EXISTS.getCode(), RtnCode.QUIZ_EXISTS.getMessage());
			}
		} else {
			// 確認傳過來的quizId是否真的可以刪除(可以刪除的條件是1.尚未發布或 2.尚未開始)
			if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(req.getQuizList().get(0).getQuizId(),
					req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
				return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(), RtnCode.QUIZ_NOT_FOUND.getMessage());
			}
//				刪除整張問卷
			try {
				quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
			} catch (Exception e) {
				return new BaseRes(RtnCode.SAVE_QUIZ_ERROR.getCode(), RtnCode.SAVE_QUIZ_ERROR.getMessage());
			}

		}

//		根據是否要發布再將傳送過來的 quizList 的值 set到傳送過來的 quizList 中
		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		quizDao.saveAll(req.getQuizList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes answer(AnswerReq req) {
		if (CollectionUtils.isEmpty(req.getAnswerList())) {// 檢查List 有沒有資料
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		for (Answer item : req.getAnswerList()) {// 檢查格式
			if (!StringUtils.hasText(item.getName()) || !StringUtils.hasText(item.getPhone())
					|| !StringUtils.hasText(item.getEmail()) || item.getQuizId() <= 0 || item.getQuId() <= 0
					|| item.getAge() <= 0) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
//		檢查資料列表中所有的quizId都一樣，且quId都不重複
		Set<Integer> quizIdList = new HashSet<>(); // set 不會存在相同得值，就是set中如果已存在相同的值，就不會新增
		Set<Integer> quIdList = new HashSet<>();// 檢查問題編號有沒有重複，正常不會重複
		for (Answer item : req.getAnswerList()) {
			quizIdList.add(item.getQuizId());
			quIdList.add(item.getQuId());
		}
		if (quizIdList.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_ID_DOES_NOT_MATCH.getCode(), RtnCode.QUIZ_ID_DOES_NOT_MATCH.getMessage());
		}
		if (quIdList.size() != req.getAnswerList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}
//		檢查必填問題是否有回答 //撈出必填問題Id
		List<Integer> res = quizDao.findQuIdsByQuizIdAndNecessaryTrue(req.getAnswerList().get(0).getQuizId());
		for (Answer item : req.getAnswerList()) {
			if (res.contains(item.getQuId()) && !StringUtils.hasText(item.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_NO_ANSWER.getCode(), RtnCode.QUESTION_NO_ANSWER.getMessage());
			}
//			for(int item:res) {
//				Answer ans = req.getAnswerList().get(item-1);
//				if(!StringUtils.hasText(ans.)) {
//					
//				}
//			}
		}
//		確認相同email不能重複填寫同一張問卷
		if (answerDao.existsByQuizIdAndEmail(req.getAnswerList().get(0).getQuizId(),
				req.getAnswerList().get(0).getEmail())) {
			return new BaseRes(RtnCode.DUPLICATED_QUIZ_ANSWER.getCode(), RtnCode.DUPLICATED_QUIZ_ANSWER.getMessage());
		}
		answerDao.saveAll(req.getAnswerList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		if (quizId <= 0) {
			return new StatisticsRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
//		撈取問卷後取得問題的 type 是否為簡答題
		List<Quiz> quizList = quizDao.findByQuizId(quizId);
//		qus是非簡答題的題目的集合List
		List<Integer> qus = new ArrayList<>();
//		若是簡答，則option為空
		for (Quiz item : quizList) {
			if (StringUtils.hasText(item.getOptions())) {
				qus.add(item.getQuId());
			}
		}
//		要加OrderBy ，流水號會影響放進順序，我們要依據問題分類
		List<Answer> answerList = answerDao.findByQuizIdOrderByQuId(quizId);
		Map<Integer, String> quIdAnswerMap = new HashMap<>();// 做一個1 對1 讓答案 對上 次數
//		把非簡答題的每題答案串成字串
		for (Answer item : answerList) {
// 			若是包含在qus List中的，則表示是選擇題
			if (qus.contains(item.getQuId())) {
//				若Key存在則進來
				if (quIdAnswerMap.containsKey(item.getQuId())) {
//					取得item 的題目Id 的value值
					String str = quIdAnswerMap.get(item.getQuId());
//					value 加上 這次回答的答案
					str += item.getAnswer();
//					把值設定值
					quIdAnswerMap.put(item.getQuId(), str);
				} else {// 如果key值不存在，則新增key和value
					quIdAnswerMap.put(item.getQuId(), item.getAnswer());
				}
			}
		}
//		計算每題每個選項的次數
//		計算每題每個選項的次數，上面是放題目 : 答案，這邊放選項(答案)與次數的Mapping
//		Map中的 Map<String, Integer>，指的是上面的answerCountMap
		Map<Integer, Map<String, Integer>> quizIdAndAnsCountMap = new HashMap<>();
//		使用foreach 遍歷 map中的每個項目
//		遍歷對象從map 轉成entrySet，好處是可直接取得map 中的key 和value
		for (Entry<Integer, String> item : quIdAnswerMap.entrySet()) {
			Map<String, Integer> answerCountMap = new HashMap<>();
//			非選擇題的options 是null，要跳過
			if(quizList.get(item.getKey() - 1).getOptions() == null) {
				continue;
			}
//		取得問題的每個選項
			String[] optionList = quizList.get(item.getKey() - 1).getOptions().split(";");
//			把問題的選項與次數做Mapping
			for (String option : optionList) {
				String newStr = item.getValue();// AAB
				int lenght1 = newStr.length();// 3
				newStr = newStr.replace(option, "");// """"B
				int lenght2 = newStr.length();// 1
//				要除 option 的原因是 option是選項的內容，而不只是選項編號
				int count = (lenght1 - lenght2) / option.length();// (3-1) / 1
				answerCountMap.put(option, count);
			}
			quizIdAndAnsCountMap.put(item.getKey(), answerCountMap);
		}
		return new StatisticsRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(), quizIdAndAnsCountMap);
	}

	@Override
	public BaseRes objMapper(String str) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Quiz quiz = mapper.readValue(str, Quiz.class);
		} catch (Exception e) {
//			回傳固定錯誤訊息
//			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
//			
			return new StatisticsRes(RtnCode.ERROR_CODE, e.getMessage());

		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public SearchRes showQuiz(String quizName, LocalDate startDate, LocalDate endDate, boolean isBack) {
		if (!StringUtils.hasText(quizName)) {
			quizName = "";// containing 可帶空字串，可撈全部
		}
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);// 將開始時間設定很久以前
		}
		if (endDate == null) {
			endDate = LocalDate.of(9999, 1, 1);// 將結束時間設定很久以後
		}
		return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
				quizDao.showQuiz(quizName, startDate, endDate, isBack));
	}
	@Override
	public SearchRes backShowQuiz(String quizName, LocalDate startDate, LocalDate endDate) {
		if (!StringUtils.hasText(quizName)) {
			quizName = "";// containing 可帶空字串，可撈全部
		}
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);// 將開始時間設定很久以前
		}
		if (endDate == null) {
			endDate = LocalDate.of(9999, 1, 1);// 將結束時間設定很久以後
		}
		return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
				quizDao.backShowQuiz(quizName, startDate, endDate));
	}
	@Override
	public SearchRes replySearch(int quizId) {
		return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),quizDao.findByQuizId(quizId));
	}

	@Override
	public AnswerRes answerSearch(int quizId) {
		return new AnswerRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),answerDao.answerSearch(quizId));
	}
}
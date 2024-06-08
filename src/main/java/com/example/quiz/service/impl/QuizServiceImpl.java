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
			quizName = "";// containing �i�a�Ŧr��A�i������
		}
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);// �N�}�l�ɶ��]�w�ܤ[�H�e
		}
		if (endDate == null) {
			endDate = LocalDate.of(9999, 1, 1);// �N�����ɶ��]�w�ܤ[�H��
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
		if (CollectionUtils.isEmpty(req.getQuizIds())) {// �P�ɧP�_QuizId�O�_��null�ΪŶ��X
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
		// �ھ� quizId and���o�� or
		List<Quiz> res = quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(//
				quizId, quizId, LocalDate.now());
		if (res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(), RtnCode.QUIZ_NOT_FOUND.getMessage());

		}
		List<Quiz> retainList = new ArrayList<>();
		for (Quiz item : res) {
			if (!quIds.contains(item.getQuId())) {// �O�d���R����Id
				retainList.add(item);// ��Jnew�X���sList�}�C�O�s
			}
		}
		for (int i = 0; i < retainList.size(); i++) {
			retainList.get(i).setQuId(i + 1);// ��s��List��Id �A�쥻ID�O�ª��O�s�i�Ӫ�ID �A
		}
		// �R����i�ݨ�
		quizDao.deleteByQuizId(quizId);
		// �N�O�d�����D�s�^DB
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
		// �ˬd���񶵥�
		for (Quiz item : req.getQuizList()) {
			if (item.getQuizId() <= 0 || item.getQuId() <= 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
		// �`��req ���Ҧ��� quizId
		// ��h�W�O�@��req���Ҧ�quizId�|�ۦP(�@�i�ݨ��h�Ӱ��D)�A���]���i��䤤�@����ƪ�quizId�O����
		// ���O�ҩҦ���ƥ��T�A�N���h�`���Ҧ�quizId
//				List<Integer> quizIdList = new ArrayList<>(); //List ���\���ƪ��Ȧs�b
//				for (Quiz item : req.getQuizList()) {
//					if (!quizIdList.contains(item.getQuizId())) {
//						quizIdList.add(item.getQuizId());
//					}
//				List�MSet�W�U��q�O�@�˪�
		Set<Integer> quizIdList = new HashSet<>(); // set ���|�s�b�ۦP�o�ȡA�N�Oset���p�G�w�s�b�ۦP���ȡA�N���|�s�W
		Set<Integer> quIdList = new HashSet<>();// �ˬd���D�s�����S�����ơA���`���|����
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
		// �ˬd�}�l�ɶ�����j�󵲧��ɶ�
		for (Quiz item : req.getQuizList()) {
			if (item.getStartDate().isAfter(item.getEndDate())) {
				return new BaseRes(RtnCode.TIME_FORMAT_ARROR.getCode(), RtnCode.TIME_FORMAT_ARROR.getMessage());
			}
		}
		if (isCreate) {
			// �ˬd�ݨ��O�_�w�s�b
			if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {// ���P�_�����S�����ơA�ҥH�i�H�������H�K�@�ӭȨӧP�_
				return new BaseRes(RtnCode.QUIZ_EXISTS.getCode(), RtnCode.QUIZ_EXISTS.getMessage());
			}
		} else {
			// �T�{�ǹL�Ӫ�quizId�O�_�u���i�H�R��(�i�H�R��������O1.�|���o���� 2.�|���}�l)
			if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(req.getQuizList().get(0).getQuizId(),
					req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
				return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(), RtnCode.QUIZ_NOT_FOUND.getMessage());
			}
//				�R����i�ݨ�
			try {
				quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
			} catch (Exception e) {
				return new BaseRes(RtnCode.SAVE_QUIZ_ERROR.getCode(), RtnCode.SAVE_QUIZ_ERROR.getMessage());
			}

		}

//		�ھڬO�_�n�o���A�N�ǰe�L�Ӫ� quizList ���� set��ǰe�L�Ӫ� quizList ��
		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		quizDao.saveAll(req.getQuizList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes answer(AnswerReq req) {
		if (CollectionUtils.isEmpty(req.getAnswerList())) {// �ˬdList ���S�����
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		for (Answer item : req.getAnswerList()) {// �ˬd�榡
			if (!StringUtils.hasText(item.getName()) || !StringUtils.hasText(item.getPhone())
					|| !StringUtils.hasText(item.getEmail()) || item.getQuizId() <= 0 || item.getQuId() <= 0
					|| item.getAge() <= 0) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
//		�ˬd��ƦC���Ҧ���quizId���@�ˡA�BquId��������
		Set<Integer> quizIdList = new HashSet<>(); // set ���|�s�b�ۦP�o�ȡA�N�Oset���p�G�w�s�b�ۦP���ȡA�N���|�s�W
		Set<Integer> quIdList = new HashSet<>();// �ˬd���D�s�����S�����ơA���`���|����
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
//		�ˬd������D�O�_���^�� //���X������DId
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
//		�T�{�ۦPemail���୫�ƶ�g�P�@�i�ݨ�
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
//		�����ݨ�����o���D�� type �O�_��²���D
		List<Quiz> quizList = quizDao.findByQuizId(quizId);
//		qus�O�D²���D���D�ت����XList
		List<Integer> qus = new ArrayList<>();
//		�Y�O²���A�hoption����
		for (Quiz item : quizList) {
			if (StringUtils.hasText(item.getOptions())) {
				qus.add(item.getQuId());
			}
		}
//		�n�[OrderBy �A�y�����|�v�T��i���ǡA�ڭ̭n�̾ڰ��D����
		List<Answer> answerList = answerDao.findByQuizIdOrderByQuId(quizId);
		Map<Integer, String> quIdAnswerMap = new HashMap<>();// ���@��1 ��1 ������ ��W ����
//		��D²���D���C�D���צꦨ�r��
		for (Answer item : answerList) {
// 			�Y�O�]�t�bqus List�����A�h��ܬO����D
			if (qus.contains(item.getQuId())) {
//				�YKey�s�b�h�i��
				if (quIdAnswerMap.containsKey(item.getQuId())) {
//					���oitem ���D��Id ��value��
					String str = quIdAnswerMap.get(item.getQuId());
//					value �[�W �o���^��������
					str += item.getAnswer();
//					��ȳ]�w��
					quIdAnswerMap.put(item.getQuId(), str);
				} else {// �p�Gkey�Ȥ��s�b�A�h�s�Wkey�Mvalue
					quIdAnswerMap.put(item.getQuId(), item.getAnswer());
				}
			}
		}
//		�p��C�D�C�ӿﶵ������
//		�p��C�D�C�ӿﶵ�����ơA�W���O���D�� : ���סA�o���ﶵ(����)�P���ƪ�Mapping
//		Map���� Map<String, Integer>�A�����O�W����answerCountMap
		Map<Integer, Map<String, Integer>> quizIdAndAnsCountMap = new HashMap<>();
//		�ϥ�foreach �M�� map�����C�Ӷ���
//		�M����H�qmap �নentrySet�A�n�B�O�i�������omap ����key �Mvalue
		for (Entry<Integer, String> item : quIdAnswerMap.entrySet()) {
			Map<String, Integer> answerCountMap = new HashMap<>();
//			�D����D��options �Onull�A�n���L
			if(quizList.get(item.getKey() - 1).getOptions() == null) {
				continue;
			}
//		���o���D���C�ӿﶵ
			String[] optionList = quizList.get(item.getKey() - 1).getOptions().split(";");
//			����D���ﶵ�P���ư�Mapping
			for (String option : optionList) {
				String newStr = item.getValue();// AAB
				int lenght1 = newStr.length();// 3
				newStr = newStr.replace(option, "");// """"B
				int lenght2 = newStr.length();// 1
//				�n�� option ����]�O option�O�ﶵ�����e�A�Ӥ��u�O�ﶵ�s��
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
//			�^�ǩT�w���~�T��
//			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
//			
			return new StatisticsRes(RtnCode.ERROR_CODE, e.getMessage());

		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public SearchRes showQuiz(String quizName, LocalDate startDate, LocalDate endDate, boolean isBack) {
		if (!StringUtils.hasText(quizName)) {
			quizName = "";// containing �i�a�Ŧr��A�i������
		}
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);// �N�}�l�ɶ��]�w�ܤ[�H�e
		}
		if (endDate == null) {
			endDate = LocalDate.of(9999, 1, 1);// �N�����ɶ��]�w�ܤ[�H��
		}
		return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
				quizDao.showQuiz(quizName, startDate, endDate, isBack));
	}
	@Override
	public SearchRes backShowQuiz(String quizName, LocalDate startDate, LocalDate endDate) {
		if (!StringUtils.hasText(quizName)) {
			quizName = "";// containing �i�a�Ŧr��A�i������
		}
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);// �N�}�l�ɶ��]�w�ܤ[�H�e
		}
		if (endDate == null) {
			endDate = LocalDate.of(9999, 1, 1);// �N�����ɶ��]�w�ܤ[�H��
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
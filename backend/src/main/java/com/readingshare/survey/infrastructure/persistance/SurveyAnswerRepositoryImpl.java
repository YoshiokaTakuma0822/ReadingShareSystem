package com.readingshare.survey.infrastructure.persistance;

import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository;
import com.readingshare.common.exception.DatabaseAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ISurveyAnswerRepository の JPA/Hibernate 実装。
 * 担当: 成田
 */
@Repository
public class SurveyAnswerRepositoryImpl implements ISurveyAnswerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public SurveyAnswer save(SurveyAnswer surveyAnswer) {
        try {
            if (surveyAnswer.getId() == null) {
                entityManager.persist(surveyAnswer);
                return surveyAnswer;
            } else {
                return entityManager.merge(surveyAnswer);
            }
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to save survey answer.", e);
        }
    }

    @Override
    public List<SurveyAnswer> findBySurveyId(Long surveyId) {
        try {
            TypedQuery<SurveyAnswer> query = entityManager.createQuery(
                "SELECT sa FROM SurveyAnswer sa WHERE sa.surveyId = :surveyId ORDER BY sa.submittedAt ASC", SurveyAnswer.class);
            query.setParameter("surveyId", surveyId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find survey answers by survey ID: " + surveyId, e);
        }
    }

    @Override
    public Optional<SurveyAnswer> findBySurveyIdAndResponderUserId(Long surveyId, Long responderUserId) {
        try {
            TypedQuery<SurveyAnswer> query = entityManager.createQuery(
                "SELECT sa FROM SurveyAnswer sa WHERE sa.surveyId = :surveyId AND sa.responderUserId = :responderUserId", SurveyAnswer.class);
            query.setParameter("surveyId", surveyId);
            query.setParameter("responderUserId", responderUserId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find survey answer by survey ID and responder user ID: survey=" + surveyId + ", responder=" + responderUserId, e);
        }
    }
}
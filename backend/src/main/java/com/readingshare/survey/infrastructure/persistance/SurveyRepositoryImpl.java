package com.readingshare.survey.infrastructure.persistance;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.common.exception.DatabaseAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ISurveyRepository の JPA/Hibernate 実装。
 * 担当: 成田
 */
@Repository
public class SurveyRepositoryImpl implements ISurveyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Survey save(Survey survey) {
        try {
            if (survey.getId() == null) {
                entityManager.persist(survey);
                return survey;
            } else {
                return entityManager.merge(survey);
            }
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to save survey.", e);
        }
    }

    @Override
    public Optional<Survey> findById(SurveyId id) {
        try {
            return Optional.ofNullable(entityManager.find(Survey.class, id.getValue()));
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find survey by ID: " + id.getValue(), e);
        }
    }

    @Override
    public List<Survey> findByRoomId(Long roomId) {
        try {
            TypedQuery<Survey> query = entityManager.createQuery(
                "SELECT s FROM Survey s WHERE s.roomId = :roomId ORDER BY s.createdAt DESC", Survey.class);
            query.setParameter("roomId", roomId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find surveys by room ID: " + roomId, e);
        }
    }
}
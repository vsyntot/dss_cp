package hrassessment.service;

import hrassessment.model.Assessment;
import hrassessment.repository.AssessmentDAO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class AssessmentService {

    @Inject
    private AssessmentDAO assessmentDAO;

    public void assessCandidate(int candidateId, int score) {
        Assessment a = new Assessment();
        a.setCandidateId(candidateId);
        a.setScore(score);
        a.setAssessmentDate(new Date());
        assessmentDAO.addAssessment(a);
    }

    public List<Assessment> getAssessments(int candidateId) {
        return assessmentDAO.getAssessmentsForCandidate(candidateId);
    }

    public void deleteAssessment(int id) {
        assessmentDAO.deleteAssessment(id);
    }
}

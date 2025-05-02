package hrassessment.controller;

import hrassessment.model.Assessment;
import hrassessment.model.Candidate;
import hrassessment.repository.CandidateDAO;
import hrassessment.service.AssessmentService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class CandidateDetailsBean implements Serializable {

    private Candidate candidate;
    private List<Assessment> assessments;

    @Inject
    private CandidateDAO candidateDAO;

    @Inject
    private AssessmentService assessmentService;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();

        String idParam = params.get("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                candidate = candidateDAO.getCandidateById(id);
                if (candidate == null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Кандидат с ID " + id + " не найден", null));
                    FacesContext.getCurrentInstance().getExternalContext().redirect("error404.xhtml");
                } else {
                    assessments = assessmentService.getAssessments(id);
                }
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при получении кандидата", e.getMessage()));
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("error500.xhtml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void updateCandidate() {
        if (candidate != null) {
            candidateDAO.updateCandidate(candidate);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Кандидат обновлён", null));
        }
    }

    public String deleteCandidate() {
        if (candidate != null && candidate.getId() != null) {
            candidateDAO.deleteCandidate(candidate.getId());
            return "result.xhtml?faces-redirect=true";
        }
        return null;
    }

    public void deleteAssessment(int assessmentId) {
        assessmentService.deleteAssessment(assessmentId);
        this.assessments = assessmentService.getAssessments(candidate.getId());
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Оценка удалена", null));
    }
}

package hrassessment.controller;

import hrassessment.service.AssessmentService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@Named
@RequestScoped
public class AssessmentBean {

    private int candidateId;
    private int score;

    @Inject
    private AssessmentService service;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");
        if (idParam != null) {
            try {
                candidateId = Integer.parseInt(idParam);
                System.out.println("AssessmentBean.init: candidateId = " + candidateId);
            } catch (NumberFormatException e) {
                System.err.println("AssessmentBean.init: invalid candidateId = " + idParam);
            }
        }
    }

    public String submit() {
        service.assessCandidate(candidateId, score);
        return "candidateDetails.xhtml?faces-redirect=true&id=" + candidateId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

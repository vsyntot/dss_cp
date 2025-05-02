package hrassessment.controller;

import hrassessment.model.Candidate;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;

@ManagedBean(name = "candidateInputBean")
@RequestScoped
public class CandidateInputBean implements Serializable {

    private Candidate candidate = new Candidate();

    @ManagedProperty(value = "#{candidateSessionBean}")
    private CandidateSessionBean candidateSessionBean;

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public CandidateSessionBean getCandidateSessionBean() {
        return candidateSessionBean;
    }

    public void setCandidateSessionBean(CandidateSessionBean candidateSessionBean) {
        this.candidateSessionBean = candidateSessionBean;
    }

    public String saveCandidate() {
        if (candidateSessionBean != null && candidateSessionBean.getUser() != null) {
            candidate.setCreatedBy(candidateSessionBean.getUser().getUsername());
        }

        candidateSessionBean.addCandidate(candidate);
        candidate = new Candidate();
        return "result.xhtml?faces-redirect=true";
    }
}

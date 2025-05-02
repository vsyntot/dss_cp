package hrassessment.controller;

import hrassessment.repository.CandidateDAO;
import hrassessment.model.Candidate;
import hrassessment.model.LazyCandidateDataModel;
import hrassessment.model.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import org.primefaces.model.LazyDataModel;

@Named
@SessionScoped
public class CandidateSessionBean implements Serializable {

    @Inject
    private CandidateDAO candidateDAO;

    private LazyDataModel<Candidate> lazyCandidates;
    private Candidate selectedCandidate;

    private User user;

    @PostConstruct
    public void init() {
        lazyCandidates = new LazyCandidateDataModel(candidateDAO);
    }

    public LazyDataModel<Candidate> getLazyCandidates() {
        return lazyCandidates;
    }

    public Candidate getSelectedCandidate() {
        return selectedCandidate;
    }

    public void setSelectedCandidate(Candidate selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }

    public void addCandidate(Candidate candidate) {
        candidateDAO.addCandidate(candidate);
        lazyCandidates = new LazyCandidateDataModel(candidateDAO);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public String getFullName() {
        return isLoggedIn() ? user.getFullName() : "Гость";
    }

    public String getRoleName() {
        return isLoggedIn() ? user.getRoleName() : "unauthorized";
    }

    public void logout() {
        user = null;
    }
}

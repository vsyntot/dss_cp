package hrassessment.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import hrassessment.repository.CandidateDAO;

@ApplicationScoped
public class CandidateService {

    @Inject
    private CandidateDAO candidateDAO;

}

package hrassessment.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import hrassessment.repository.UserDAO;

@ApplicationScoped
public class UserService {

    @Inject
    private UserDAO userDAO;
}

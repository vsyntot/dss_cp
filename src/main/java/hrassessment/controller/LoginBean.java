package hrassessment.controller;

import hrassessment.repository.UserDAO;
import hrassessment.model.User;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Named
@RequestScoped
public class LoginBean {

    private String username;
    private String password;

    @Inject
    private CandidateSessionBean candidateSessionBean;

    @Inject
    private UserDAO userDAO;

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            request.login(username, password);

            User user = userDAO.getUserByUsername(username);
            if (user != null) {
                candidateSessionBean.setUser(user);
                return "dashboard.xhtml?faces-redirect=true";
            } else {
                context.addMessage(null,
                        new javax.faces.application.FacesMessage("Ошибка: пользователь не найден."));
                return null;
            }

        } catch (ServletException e) {
            context.addMessage(null,
                    new javax.faces.application.FacesMessage("Ошибка: неверный логин или пароль."));
            return null;
        }
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            request.logout();
            context.getExternalContext().invalidateSession();
            return "/login.xhtml?faces-redirect=true";
        } catch (ServletException e) {
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package hrassessment.controller;

import hrassessment.model.User;
import hrassessment.repository.UserDAO;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class UserBean implements Serializable {

    private List<User> allUsers;
    private User selectedUser;
    private String password;

    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        allUsers = userDAO.getAllUsers();

        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                selectedUser = userDAO.getUserById(id);
            } catch (NumberFormatException e) {
                selectedUser = new User();
            }
        } else {
            selectedUser = new User();
        }
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String saveUser() {
        if (selectedUser.getId() == null) {
            if (userDAO.isUsernameTaken(selectedUser.getUsername())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пользователь с таким логином уже существует", null));
                return null;
            }
            userDAO.createUser(selectedUser, password);
        } else {
            userDAO.updateUser(selectedUser, password);
        }
        return "userList.xhtml?faces-redirect=true";
    }

    public void deleteUser(int id) {
        userDAO.deleteUser(id);
        allUsers = userDAO.getAllUsers();
    }
}

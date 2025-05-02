package hrassessment.controller;

import hrassessment.model.User;
import hrassessment.repository.UserDAO;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;

@ManagedBean(name = "userEditBean")
@ViewScoped
public class UserEditBean implements Serializable {

    private User user = new User();
    private boolean editMode = false;
    private String password;

    @Inject
    private UserDAO userDAO;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();

        String idParam = params.get("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                User existing = userDAO.getUserById(id);
                if (existing != null) {
                    this.user = existing;
                    this.editMode = true;
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пользователь не найден", null));
                }
            } catch (NumberFormatException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Некорректный ID", null));
            }
        }
    }

    public String saveUser() {
        if (!editMode && (password == null || password.isEmpty())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Пароль обязателен при создании", null));
            return null;
        }

        if (editMode) {
            userDAO.updateUser(user, password);
        } else {
            userDAO.createUser(user, password);
        }

        return "userList.xhtml?faces-redirect=true";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

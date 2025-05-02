package hrassessment.filter;

import hrassessment.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/userList.xhtml", "/userEdit.xhtml"})
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean accessGranted = false;

        if (session != null) {
            Object userObj = session.getAttribute("currentUser");
            if (userObj instanceof User) {
                User user = (User) userObj;
                accessGranted = "admin".equals(user.getRoleName());
            }
        }

        if (accessGranted) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/error403.xhtml");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) { }

    @Override
    public void destroy() { }
}

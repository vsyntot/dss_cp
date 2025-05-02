package hrassessment.servlet;

import hrassessment.repository.CandidateDAO;
import hrassessment.model.Candidate;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/candidate-details")
public class CandidateDetailsServlet extends HttpServlet {

    @Inject
    private CandidateDAO candidateDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Candidate candidate = candidateDAO.getCandidateById(id);

                if (candidate != null) {
                    req.setAttribute("candidate", candidate);
                    req.getRequestDispatcher("candidateDetails.jsp").forward(req, resp);
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }

        resp.sendRedirect("error404.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String idParam = req.getParameter("id");

        if ("Удалить".equals(action) && idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                candidateDAO.deleteCandidate(id);
            } catch (NumberFormatException ignored) {
            }
        }

        resp.sendRedirect("result.xhtml");
    }
}

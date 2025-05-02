package hrassessment.servlet;

import hrassessment.model.Assessment;
import hrassessment.model.Candidate;
import hrassessment.repository.AssessmentDAO;
import hrassessment.repository.CandidateDAO;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

@WebServlet("/export")
public class ExportServlet extends HttpServlet {

    @Resource(lookup = "jdbc/hrassessment")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String type = request.getParameter("type");
        if (type == null) {
            type = "csv";
        }

        CandidateDAO candidateDAO = new CandidateDAO();
        AssessmentDAO assessmentDAO = new AssessmentDAO();

        candidateDAO.setDataSource(dataSource);
        assessmentDAO.setDataSource(dataSource);

        List<Candidate> candidates = candidateDAO.getAllCandidates();

        response.setCharacterEncoding("UTF-8");

        if ("csv".equalsIgnoreCase(type)) {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"candidates.csv\"");
            exportToCSV(response.getWriter(), candidates, assessmentDAO);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Excel export пока не реализован");
        }
    }

    private void exportToCSV(PrintWriter writer, List<Candidate> candidates, AssessmentDAO assessmentDAO) {
        writer.println("Имя,Email,Дата рождения,Последняя оценка");

        for (Candidate c : candidates) {
            List<Assessment> assessments = assessmentDAO.getAssessmentsForCandidate(c.getId());
            String lastScore = assessments.isEmpty() ? "" : String.valueOf(assessments.get(0).getScore());

            String row = String.format("\"%s\",\"%s\",\"%s\",\"%s\"",
                    c.getName(),
                    c.getEmail(),
                    c.getBirthDate() != null ? c.getBirthDate().toString() : "",
                    lastScore);

            writer.println(row);
        }

        writer.flush();
    }
}

package hrassessment.repository;

import hrassessment.model.Assessment;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AssessmentDAO {

    @Resource(lookup = "jdbc/hrassessment")
    private DataSource dataSource;

    public void addAssessment(Assessment a) {
        String sql = "INSERT INTO assessment (candidate_id, score, assessment_date) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, a.getCandidateId());
            stmt.setInt(2, a.getScore());
            stmt.setTimestamp(3, new Timestamp(a.getAssessmentDate().getTime()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Assessment> getAssessmentsForCandidate(int candidateId) {
        List<Assessment> list = new ArrayList<>();
        String sql = "SELECT * FROM assessment WHERE candidate_id = ? ORDER BY assessment_date DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Assessment a = new Assessment();
                a.setId(rs.getInt("id"));
                a.setCandidateId(rs.getInt("candidate_id"));
                a.setScore(rs.getInt("score"));
                a.setAssessmentDate(rs.getTimestamp("assessment_date"));
                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void deleteAssessment(int id) {
        String sql = "DELETE FROM assessment WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении оценки с ID: " + id, e);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}

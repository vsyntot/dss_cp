package hrassessment.repository;

import hrassessment.model.Candidate;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class CandidateDAO {

    @Resource(lookup = "jdbc/hrassessment")
    private DataSource dataSource;

    public List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM candidate");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Candidate candidate = new Candidate();
                candidate.setId(rs.getInt("id"));
                candidate.setName(rs.getString("name"));
                candidate.setEmail(rs.getString("email"));
                candidate.setBirthDate(rs.getDate("birth_date"));
                candidate.setCreatedBy(rs.getString("created_by"));
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return candidates;
    }

    public void addCandidate(Candidate candidate) {
        String sql = "INSERT INTO candidate (name, email, birth_date, created_by) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, candidate.getName());
            stmt.setString(2, candidate.getEmail());

            if (candidate.getBirthDate() != null) {
                stmt.setDate(3, new java.sql.Date(candidate.getBirthDate().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, candidate.getCreatedBy());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCandidate(Candidate candidate) {
        String sql = "UPDATE candidate SET name = ?, email = ?, birth_date = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, candidate.getName());
            stmt.setString(2, candidate.getEmail());

            if (candidate.getBirthDate() != null) {
                stmt.setDate(3, new java.sql.Date(candidate.getBirthDate().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setInt(4, candidate.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCandidate(Integer id) {
        String sql = "DELETE FROM candidate WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Candidate getCandidateById(int id) {
        String sql = "SELECT * FROM candidate WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Candidate candidate = new Candidate();
                    candidate.setId(rs.getInt("id"));
                    candidate.setName(rs.getString("name"));
                    candidate.setEmail(rs.getString("email"));
                    candidate.setBirthDate(rs.getDate("birth_date"));
                    candidate.setCreatedBy(rs.getString("created_by"));
                    return candidate;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении кандидата по ID: " + id, e);
        }

        return null;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}

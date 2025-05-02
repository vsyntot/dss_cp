package hrassessment.repository;

import hrassessment.model.User;

import javax.enterprise.context.Dependent;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class UserDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource(lookup = "jdbc/hrassessment")
    private DataSource dataSource;

    public UserDAO() {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/hrassessment");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserByUsername(String username) {
        User user = null;

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, username, password, full_name, birthdate FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        user = extractUserFromResultSet(rs);
                    }
                }
            }

            if (user != null) {
                user.setRoleName(fetchUserRole(conn, username));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public User getUserById(int id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = extractUserFromResultSet(rs);
                    user.setRoleName(fetchUserRole(conn, user.getUsername()));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                user.setRoleName(fetchUserRole(conn, user.getUsername()));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void addUser(User user) {
        createUser(user, user.getPassword());
    }

    public void createUser(User user, String rawPassword) {
        String sql = "INSERT INTO users (username, password, full_name, birthdate) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, rawPassword);
            ps.setString(3, user.getFullName());

            if (user.getBirthdate() != null) {
                ps.setDate(4, Date.valueOf(user.getBirthdate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    String roleSql = "INSERT INTO user_roles (username, role_name) VALUES (?, ?)";
                    try (PreparedStatement psRole = conn.prepareStatement(roleSql)) {
                        psRole.setString(1, user.getUsername());
                        psRole.setString(2, user.getRoleName());
                        psRole.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user, String newPassword) {
        String sql;
        boolean updatePassword = (newPassword != null && !newPassword.trim().isEmpty());

        if (updatePassword) {
            sql = "UPDATE users SET username = ?, password = ?, full_name = ?, birthdate = ? WHERE id = ?";
        } else {
            sql = "UPDATE users SET username = ?, full_name = ?, birthdate = ? WHERE id = ?";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            ps.setString(idx++, user.getUsername());
            if (updatePassword) {
                ps.setString(idx++, newPassword);
            }
            ps.setString(idx++, user.getFullName());

            if (user.getBirthdate() != null) {
                ps.setDate(idx++, Date.valueOf(user.getBirthdate()));
            } else {
                ps.setNull(idx++, Types.DATE);
            }

            ps.setInt(idx, user.getId());
            ps.executeUpdate();

            String roleSql = "UPDATE user_roles SET role_name = ? WHERE username = ?";
            try (PreparedStatement psRole = conn.prepareStatement(roleSql)) {
                psRole.setString(1, user.getRoleName());
                psRole.setString(2, user.getUsername());
                psRole.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));

        Date birthDate = rs.getDate("birthdate");
        if (birthDate != null) {
            user.setBirthdate(birthDate.toLocalDate());
        }

        return user;
    }

    private String fetchUserRole(Connection conn, String username) throws SQLException {
        String role = null;
        String roleSql = "SELECT role_name FROM user_roles WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(roleSql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    role = rs.getString("role_name");
                }
            }
        }
        return role;
    }

    public void deleteUser(int id) {
        try (Connection conn = dataSource.getConnection()) {
            String getUsernameSql = "SELECT username FROM users WHERE id = ?";
            String username = null;

            try (PreparedStatement ps = conn.prepareStatement(getUsernameSql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            }

            if (username != null) {
                try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM user_roles WHERE username = ?")) {
                    ps1.setString(1, username);
                    ps1.executeUpdate();
                }

                try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

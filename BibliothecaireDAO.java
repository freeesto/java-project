package dao;

import modele.Bibliothecaire;
import util.SingletonConnection;

import java.sql.*;

public class BibliothecaireDAO {
    private Connection connection;

    public BibliothecaireDAO() {
        this.connection = SingletonConnection.getInstance();
    }

    public Bibliothecaire authentifier(String username, String password) throws SQLException {
        String sql = "SELECT u.*, b.matricule FROM utilisateur u " +
                "JOIN bibliothecaire b ON u.id=b.id WHERE u.username=? AND u.password=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Bibliothecaire(
                    rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"),
                    rs.getString("email"), rs.getString("username"), rs.getString("password"),
                    rs.getString("matricule")
            );
        }
        return null;
    }
}

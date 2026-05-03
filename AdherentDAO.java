package dao;

import modele.Adherent;
import util.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdherentDAO implements IAdherentDAO {
    private Connection connection;

    public AdherentDAO() {
        this.connection = SingletonConnection.getInstance();
    }

    @Override
    public void ajouterAdherent(Adherent a) throws SQLException {
        // L'id de la table utilisateur doit être AUTO_INCREMENT dans la BDD
        String sqlU = "INSERT INTO utilisateur (nom, prenom, email, username, password) VALUES (?,?,?,?,?)";
        PreparedStatement psU = connection.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS);
        psU.setString(1, a.getNom());
        psU.setString(2, a.getPrenom());
        psU.setString(3, a.getEmail());
        psU.setString(4, a.getUsername());
        psU.setString(5, a.getPassword());
        psU.executeUpdate();

        ResultSet rs = psU.getGeneratedKeys();
        if (rs.next()) {
            int idGenere = rs.getInt(1);
            String sqlA = "INSERT INTO adherent (id, numAdherent, adresse, telephone) VALUES (?,?,?,?)";
            PreparedStatement psA = connection.prepareStatement(sqlA);
            psA.setInt(1, idGenere);
            psA.setString(2, a.getNumAdherent());
            psA.setString(3, a.getAdresse());
            psA.setString(4, a.getTelephone());
            psA.executeUpdate();
            a.setId(idGenere); // L'objet reçoit son id auto-généré
        }
    }

    @Override
    public void modifierAdherent(Adherent a) throws SQLException {
        String sqlU = "UPDATE utilisateur SET nom=?, prenom=?, email=? WHERE id=?";
        PreparedStatement psU = connection.prepareStatement(sqlU);
        psU.setString(1, a.getNom());
        psU.setString(2, a.getPrenom());
        psU.setString(3, a.getEmail());
        psU.setInt(4, a.getId());
        psU.executeUpdate();

        String sqlA = "UPDATE adherent SET adresse=?, telephone=? WHERE id=?";
        PreparedStatement psA = connection.prepareStatement(sqlA);
        psA.setString(1, a.getAdresse());
        psA.setString(2, a.getTelephone());
        psA.setInt(3, a.getId());
        psA.executeUpdate();
    }

    @Override
    public void supprimerAdherent(int id) throws SQLException {
        // La suppression en cascade supprime aussi la ligne dans adherent
        // (assurez-vous d'avoir ON DELETE CASCADE sur la FK dans la BDD)
        String sql = "DELETE FROM utilisateur WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public Adherent rechercherParId(int id) throws SQLException {
        String sql = "SELECT u.*, a.numAdherent, a.adresse, a.telephone FROM utilisateur u " +
                "JOIN adherent a ON u.id=a.id WHERE u.id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapAdherent(rs);
        return null;
    }

    @Override
    public Adherent rechercherParUsername(String username, String password) throws SQLException {
        String sql = "SELECT u.*, a.numAdherent, a.adresse, a.telephone FROM utilisateur u " +
                "JOIN adherent a ON u.id=a.id WHERE u.username=? AND u.password=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapAdherent(rs);
        return null;
    }

    @Override
    public List<Adherent> listerTous() throws SQLException {
        List<Adherent> liste = new ArrayList<>();
        String sql = "SELECT u.*, a.numAdherent, a.adresse, a.telephone FROM utilisateur u " +
                "JOIN adherent a ON u.id=a.id ORDER BY u.nom";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) liste.add(mapAdherent(rs));
        return liste;
    }

    private Adherent mapAdherent(ResultSet rs) throws SQLException {
        return new Adherent(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("numAdherent"),
                rs.getString("adresse"),
                rs.getString("telephone")
        );
    }
}

package dao;

import modele.Document;
import util.SingletonConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO implements IDocumentDAO {
    private Connection connection;

    public DocumentDAO() {
        this.connection = SingletonConnection.getInstance();
    }

    @Override
    public void ajouterDocument(Document doc) throws SQLException {
        String sql = "INSERT INTO document (titre, auteur, type, categorie, anneePublication, disponible) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, doc.getTitre());
        ps.setString(2, doc.getAuteur());
        ps.setString(3, doc.getType());
        ps.setString(4, doc.getCategorie());
        ps.setInt(5, doc.getAnneePublication());
        ps.setBoolean(6, doc.isDisponible());
        ps.executeUpdate();
    }

    @Override
    public void modifierDocument(Document doc) throws SQLException {
        String sql = "UPDATE document SET titre=?, auteur=?, type=?, categorie=?, anneePublication=?, disponible=? WHERE idDocument=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, doc.getTitre());
        ps.setString(2, doc.getAuteur());
        ps.setString(3, doc.getType());
        ps.setString(4, doc.getCategorie());
        ps.setInt(5, doc.getAnneePublication());
        ps.setBoolean(6, doc.isDisponible());
        ps.setInt(7, doc.getIdDocument());
        ps.executeUpdate();
    }

    @Override
    public void supprimerDocument(int idDocument) throws SQLException {
        String sql = "DELETE FROM document WHERE idDocument=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idDocument);
        ps.executeUpdate();
    }

    @Override
    public Document rechercherParId(int idDocument) throws SQLException {
        String sql = "SELECT * FROM document WHERE idDocument=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idDocument);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapDocument(rs);
        return null;
    }

    @Override
    public List<Document> listerTous() throws SQLException {
        List<Document> liste = new ArrayList<>();
        String sql = "SELECT * FROM document ORDER BY titre";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) liste.add(mapDocument(rs));
        return liste;
    }

    @Override
    public List<Document> rechercherParTitre(String titre) throws SQLException {
        List<Document> liste = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE titre LIKE ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + titre + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) liste.add(mapDocument(rs));
        return liste;
    }

    @Override
    public List<Document> listerDisponibles() throws SQLException {
        List<Document> liste = new ArrayList<>();
        String sql = "SELECT * FROM document WHERE disponible=TRUE ORDER BY titre";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) liste.add(mapDocument(rs));
        return liste;
    }

    @Override
    public void setDisponibilite(int idDocument, boolean disponible) throws SQLException {
        String sql = "UPDATE document SET disponible=? WHERE idDocument=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBoolean(1, disponible);
        ps.setInt(2, idDocument);
        ps.executeUpdate();
    }

    private Document mapDocument(ResultSet rs) throws SQLException {
        return new Document(
                rs.getInt("idDocument"),
                rs.getString("titre"),
                rs.getString("auteur"),
                rs.getString("type"),
                rs.getString("categorie"),
                rs.getInt("anneePublication"),
                rs.getBoolean("disponible")
        );
    }
}

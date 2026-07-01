package com.biblioteca.dao;

import com.biblioteca.model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para la entidad Libro (tabla "libros" en SQLite).
 */
public class LibroDAO {

    /**
     * Inserta un nuevo libro en la base de datos.
     * Retorna false si ya existe un libro con ese ISBN.
     */
    public boolean insertar(Libro libro) {
        if (existeIsbn(libro.getIsbn())) {
            return false;
        }
        String sql = "INSERT INTO libros (isbn, titulo, autor, copias_disponibles) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setInt(4, libro.getCopiasDisponibles());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un libro por su ISBN. Retorna null si no existe.
     */
    public Libro buscarPorIsbn(String isbn) {
        String sql = "SELECT titulo, autor, isbn, copias_disponibles FROM libros WHERE isbn = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLibro(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar libro: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retorna true si ya existe un libro registrado con ese ISBN.
     */
    public boolean existeIsbn(String isbn) {
        return buscarPorIsbn(isbn) != null;
    }

    /**
     * Retorna la lista completa de libros registrados.
     */
    public List<Libro> listarTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT titulo, autor, isbn, copias_disponibles FROM libros ORDER BY titulo";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }
        return libros;
    }

    /**
     * Retorna únicamente los libros que tienen al menos una copia disponible.
     */
    public List<Libro> listarDisponibles() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT titulo, autor, isbn, copias_disponibles FROM libros " +
                "WHERE copias_disponibles > 0 ORDER BY titulo";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar libros disponibles: " + e.getMessage());
        }
        return libros;
    }

    /**
     * Actualiza el número de copias disponibles de un libro.
     */
    public boolean actualizarCopias(String isbn, int nuevasCopias) {
        String sql = "UPDATE libros SET copias_disponibles = ? WHERE isbn = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevasCopias);
            ps.setString(2, isbn);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar copias del libro: " + e.getMessage());
            return false;
        }
    }

    private Libro mapearLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getInt("copias_disponibles")
        );
    }
}

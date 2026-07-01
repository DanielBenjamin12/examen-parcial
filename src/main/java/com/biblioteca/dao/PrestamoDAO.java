package com.biblioteca.dao;

import com.biblioteca.model.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para la entidad Prestamo (tabla "prestamos" en SQLite).
 */
public class PrestamoDAO {

    /**
     * Inserta un nuevo préstamo (fecha_devolucion queda NULL, es decir, activo).
     * Retorna el id autogenerado del préstamo, o -1 si falla.
     */
    public int insertar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (isbn_libro, id_usuario, fecha_prestamo, fecha_devolucion) " +
                "VALUES (?, ?, ?, ?)";
        // Nota: el driver sqlite-jdbc no implementa Statement.RETURN_GENERATED_KEYS,
        // por lo que se obtiene el id autogenerado con la funcion last_insert_rowid()
        // dentro de la MISMA conexion (es un valor por-conexion en SQLite).
        try (Connection con = ConexionDB.obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, prestamo.getIsbnLibro());
                ps.setString(2, prestamo.getIdUsuario());
                ps.setString(3, prestamo.getFechaPrestamo());
                ps.setString(4, prestamo.getFechaDevolucion());
                ps.executeUpdate();
            }
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar prestamo: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Busca el préstamo activo (fecha_devolucion IS NULL) de un libro para un usuario específico.
     * Retorna null si no existe.
     */
    public Prestamo buscarPrestamoActivo(String isbnLibro, String idUsuario) {
        String sql = "SELECT id_prestamo, isbn_libro, id_usuario, fecha_prestamo, fecha_devolucion " +
                "FROM prestamos WHERE isbn_libro = ? AND id_usuario = ? AND fecha_devolucion IS NULL " +
                "ORDER BY id_prestamo DESC LIMIT 1";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbnLibro);
            ps.setString(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPrestamo(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar prestamo activo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Marca un préstamo como devuelto, asignando la fecha de devolución.
     */
    public boolean marcarComoDevuelto(int idPrestamo, String fechaDevolucion) {
        String sql = "UPDATE prestamos SET fecha_devolucion = ? WHERE id_prestamo = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fechaDevolucion);
            ps.setInt(2, idPrestamo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al marcar prestamo como devuelto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna todos los préstamos actualmente activos (no devueltos).
     */
    public List<Prestamo> listarActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT id_prestamo, isbn_libro, id_usuario, fecha_prestamo, fecha_devolucion " +
                "FROM prestamos WHERE fecha_devolucion IS NULL ORDER BY fecha_prestamo";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar prestamos activos: " + e.getMessage());
        }
        return prestamos;
    }

    /**
     * Retorna el historial completo de préstamos (activos e históricos).
     */
    public List<Prestamo> listarTodos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT id_prestamo, isbn_libro, id_usuario, fecha_prestamo, fecha_devolucion " +
                "FROM prestamos ORDER BY fecha_prestamo";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar prestamos: " + e.getMessage());
        }
        return prestamos;
    }

    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        return new Prestamo(
                rs.getInt("id_prestamo"),
                rs.getString("isbn_libro"),
                rs.getString("id_usuario"),
                rs.getString("fecha_prestamo"),
                rs.getString("fecha_devolucion")
        );
    }
}

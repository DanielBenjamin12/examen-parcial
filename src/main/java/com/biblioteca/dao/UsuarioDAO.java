package com.biblioteca.dao;

import com.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos para la entidad Usuario (tabla "usuarios" en SQLite).
 */
public class UsuarioDAO {

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Retorna false si ya existe un usuario con ese ID.
     */
    public boolean insertar(Usuario usuario) {
        if (existeId(usuario.getIdUsuario())) {
            return false;
        }
        String sql = "INSERT INTO usuarios (id_usuario, nombre, tiene_libro_prestado) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getIdUsuario());
            ps.setString(2, usuario.getNombre());
            ps.setInt(3, usuario.isTieneLibroPrestado() ? 1 : 0);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un usuario por su ID. Retorna null si no existe.
     */
    public Usuario buscarPorId(String idUsuario) {
        String sql = "SELECT nombre, id_usuario, tiene_libro_prestado FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retorna true si ya existe un usuario registrado con ese ID.
     */
    public boolean existeId(String idUsuario) {
        return buscarPorId(idUsuario) != null;
    }

    /**
     * Retorna la lista completa de usuarios registrados.
     */
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT nombre, id_usuario, tiene_libro_prestado FROM usuarios ORDER BY nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Actualiza el estado de préstamo (tiene_libro_prestado) de un usuario.
     */
    public boolean actualizarEstadoPrestamo(String idUsuario, boolean tieneLibroPrestado) {
        String sql = "UPDATE usuarios SET tiene_libro_prestado = ? WHERE id_usuario = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tieneLibroPrestado ? 1 : 0);
            ps.setString(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del usuario: " + e.getMessage());
            return false;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getString("nombre"),
                rs.getString("id_usuario"),
                rs.getInt("tiene_libro_prestado") == 1
        );
    }
}

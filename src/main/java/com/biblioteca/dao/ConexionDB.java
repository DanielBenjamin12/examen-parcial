package com.biblioteca.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de gestionar la conexión a la base de datos SQLite
 * y de crear el esquema (tablas) si aún no existe.
 *
 * Se usa un patrón simple: cada método DAO abre y cierra su propia
 * conexión (try-with-resources), lo cual es suficiente para una
 * aplicación de consola de un solo usuario.
 */
public class ConexionDB {

    // Nombre del archivo de la base de datos SQLite (se crea en la raíz del proyecto)
    private static final String NOMBRE_BD = "biblioteca.db";
    private static final String URL_BD = "jdbc:sqlite:" + NOMBRE_BD;

    /**
     * Abre y retorna una nueva conexión a la base de datos SQLite.
     */
    public static Connection obtenerConexion() throws SQLException {
        Connection conexion = DriverManager.getConnection(URL_BD);
        // Habilitar claves foráneas en SQLite (vienen deshabilitadas por defecto)
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conexion;
    }

    /**
     * Crea las tablas necesarias (libros, usuarios, prestamos) si no existen.
     * Debe llamarse una única vez al iniciar la aplicación.
     */
    public static void inicializarBaseDeDatos() {
        String sqlLibros = """
                CREATE TABLE IF NOT EXISTS libros (
                    isbn TEXT PRIMARY KEY,
                    titulo TEXT NOT NULL,
                    autor TEXT NOT NULL,
                    copias_disponibles INTEGER NOT NULL DEFAULT 0
                );
                """;

        String sqlUsuarios = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id_usuario TEXT PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    tiene_libro_prestado INTEGER NOT NULL DEFAULT 0
                );
                """;

        String sqlPrestamos = """
                CREATE TABLE IF NOT EXISTS prestamos (
                    id_prestamo INTEGER PRIMARY KEY AUTOINCREMENT,
                    isbn_libro TEXT NOT NULL,
                    id_usuario TEXT NOT NULL,
                    fecha_prestamo TEXT NOT NULL,
                    fecha_devolucion TEXT,
                    FOREIGN KEY (isbn_libro) REFERENCES libros(isbn),
                    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
                );
                """;

        try (Connection conexion = obtenerConexion();
             Statement stmt = conexion.createStatement()) {
            stmt.execute(sqlLibros);
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlPrestamos);
            System.out.println("Base de datos inicializada correctamente (" + NOMBRE_BD + ").");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

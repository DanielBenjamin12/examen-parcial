package com.biblioteca.service;

import com.biblioteca.dao.LibroDAO;
import com.biblioteca.dao.PrestamoDAO;
import com.biblioteca.dao.UsuarioDAO;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Gestor principal de la biblioteca. Contiene la lógica de negocio
 * (registrar, prestar, devolver, buscar, listar) y delega la persistencia
 * en los DAOs correspondientes (LibroDAO, UsuarioDAO, PrestamoDAO), los
 * cuales a su vez usan SQLite como base de datos.
 */
public class Biblioteca {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LibroDAO libroDAO;
    private final UsuarioDAO usuarioDAO;
    private final PrestamoDAO prestamoDAO;

    public Biblioteca() {
        this.libroDAO = new LibroDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.prestamoDAO = new PrestamoDAO();
    }

    // ---------- Registro ----------

    /**
     * Registra un nuevo libro. Retorna false si el ISBN ya existe.
     */
    public boolean agregarLibro(Libro libro) {
        return libroDAO.insertar(libro);
    }

    /**
     * Registra un nuevo usuario. Retorna false si el ID de usuario ya existe.
     */
    public boolean agregarUsuario(Usuario usuario) {
        return usuarioDAO.insertar(usuario);
    }

    // ---------- Búsquedas ----------

    public Libro buscarLibroPorISBN(String isbn) {
        return libroDAO.buscarPorIsbn(isbn);
    }

    public Usuario buscarUsuarioPorID(String idUsuario) {
        return usuarioDAO.buscarPorId(idUsuario);
    }

    // ---------- Préstamos y devoluciones ----------

    /**
     * Intenta realizar el préstamo de un libro a un usuario.
     *
     * Reglas de negocio:
     *  - El libro debe existir.
     *  - El usuario debe existir.
     *  - El libro debe tener al menos una copia disponible.
     *
     * (No se restringe que un usuario tenga más de un libro prestado a la vez,
     * ya que la biblioteca puede permitir varios préstamos simultáneos; el
     * campo "tieneLibroPrestado" indica si el usuario tiene AL MENOS uno.)
     *
     * @return un mensaje descriptivo del resultado (éxito o el motivo del error).
     */
    public ResultadoOperacion realizarPrestamo(String isbnLibro, String idUsuario) {
        Libro libro = libroDAO.buscarPorIsbn(isbnLibro);
        if (libro == null) {
            return ResultadoOperacion.error("No existe ningun libro registrado con el ISBN '" + isbnLibro + "'.");
        }

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) {
            return ResultadoOperacion.error("No existe ningun usuario registrado con el ID '" + idUsuario + "'.");
        }

        if (libro.getCopiasDisponibles() <= 0) {
            return ResultadoOperacion.error("No hay copias disponibles del libro '" + libro.getTitulo() + "'.");
        }

        // Actualizar copias del libro
        boolean copiasActualizadas = libroDAO.actualizarCopias(isbnLibro, libro.getCopiasDisponibles() - 1);
        if (!copiasActualizadas) {
            return ResultadoOperacion.error("No se pudo actualizar el inventario del libro. Intente de nuevo.");
        }

        // Registrar el prestamo
        String fechaHoy = LocalDate.now().format(FORMATO_FECHA);
        int idPrestamo = prestamoDAO.insertar(new Prestamo(0, isbnLibro, idUsuario, fechaHoy, null));
        if (idPrestamo == -1) {
            // Revertir el cambio en las copias si el prestamo no se pudo registrar
            libroDAO.actualizarCopias(isbnLibro, libro.getCopiasDisponibles());
            return ResultadoOperacion.error("No se pudo registrar el prestamo. Intente de nuevo.");
        }

        // Actualizar estado del usuario
        usuarioDAO.actualizarEstadoPrestamo(idUsuario, true);

        return ResultadoOperacion.exito("Prestamo realizado con exito. '" + libro.getTitulo() +
                "' fue prestado a " + usuario.getNombre() + " el " + fechaHoy + ".");
    }

    /**
     * Intenta realizar la devolución de un libro por parte de un usuario.
     *
     * Reglas de negocio:
     *  - El libro debe existir.
     *  - El usuario debe existir.
     *  - Debe existir un préstamo ACTIVO de ese libro para ese usuario.
     */
    public ResultadoOperacion realizarDevolucion(String isbnLibro, String idUsuario) {
        Libro libro = libroDAO.buscarPorIsbn(isbnLibro);
        if (libro == null) {
            return ResultadoOperacion.error("No existe ningun libro registrado con el ISBN '" + isbnLibro + "'.");
        }

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) {
            return ResultadoOperacion.error("No existe ningun usuario registrado con el ID '" + idUsuario + "'.");
        }

        Prestamo prestamoActivo = prestamoDAO.buscarPrestamoActivo(isbnLibro, idUsuario);
        if (prestamoActivo == null) {
            return ResultadoOperacion.error("El usuario '" + usuario.getNombre() +
                    "' no tiene un prestamo activo del libro '" + libro.getTitulo() + "'.");
        }

        String fechaHoy = LocalDate.now().format(FORMATO_FECHA);
        boolean marcado = prestamoDAO.marcarComoDevuelto(prestamoActivo.getIdPrestamo(), fechaHoy);
        if (!marcado) {
            return ResultadoOperacion.error("No se pudo registrar la devolucion. Intente de nuevo.");
        }

        // Actualizar copias disponibles del libro
        libroDAO.actualizarCopias(isbnLibro, libro.getCopiasDisponibles() + 1);

        // Si el usuario ya no tiene ningun otro prestamo activo, actualizar su estado
        boolean tieneOtroPrestamoActivo = prestamoDAO.listarActivos().stream()
                .anyMatch(p -> p.getIdUsuario().equals(idUsuario));
        usuarioDAO.actualizarEstadoPrestamo(idUsuario, tieneOtroPrestamoActivo);

        return ResultadoOperacion.exito("Devolucion registrada con exito. '" + libro.getTitulo() +
                "' fue devuelto por " + usuario.getNombre() + " el " + fechaHoy + ".");
    }

    // ---------- Listados ----------

    public List<Libro> listarLibrosDisponibles() {
        return libroDAO.listarDisponibles();
    }

    public List<Libro> listarTodosLosLibros() {
        return libroDAO.listarTodos();
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public List<Prestamo> listarPrestamosActivos() {
        return prestamoDAO.listarActivos();
    }

    public List<Prestamo> listarHistorialPrestamos() {
        return prestamoDAO.listarTodos();
    }

    /**
     * Pequeña clase de resultado para comunicar éxito/error junto con un mensaje,
     * sin depender de excepciones para el flujo normal de negocio.
     */
    public static class ResultadoOperacion {
        private final boolean exito;
        private final String mensaje;

        private ResultadoOperacion(boolean exito, String mensaje) {
            this.exito = exito;
            this.mensaje = mensaje;
        }

        public static ResultadoOperacion exito(String mensaje) {
            return new ResultadoOperacion(true, mensaje);
        }

        public static ResultadoOperacion error(String mensaje) {
            return new ResultadoOperacion(false, mensaje);
        }

        public boolean isExito() {
            return exito;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}

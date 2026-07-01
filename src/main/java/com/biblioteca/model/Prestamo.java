package com.biblioteca.model;

/**
 * Representa una transacción de préstamo de un libro a un usuario.
 *
 * Nota: se guarda el ISBN del libro y el ID del usuario (no las referencias
 * completas de objeto) porque así es como se persiste en la base de datos
 * relacional; la clase Biblioteca/Service se encarga de resolver los
 * objetos Libro y Usuario completos cuando se necesitan.
 */
public class Prestamo {

    private int idPrestamo; // 0 si aún no ha sido persistido (autogenerado por la BD)
    private String isbnLibro;
    private String idUsuario;
    private String fechaPrestamo;
    private String fechaDevolucion; // puede ser null si el libro no ha sido devuelto

    public Prestamo(int idPrestamo, String isbnLibro, String idUsuario,
                     String fechaPrestamo, String fechaDevolucion) {
        this.idPrestamo = idPrestamo;
        this.isbnLibro = isbnLibro;
        this.idUsuario = idUsuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public String getIsbnLibro() {
        return isbnLibro;
    }

    public void setIsbnLibro(String isbnLibro) {
        this.isbnLibro = isbnLibro;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(String fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean estaActivo() {
        return fechaDevolucion == null;
    }

    /**
     * Imprime los detalles del préstamo en consola.
     */
    public void mostrarDetalles() {
        System.out.printf("Prestamo #%d | Libro (ISBN): %s | Usuario (ID): %s | Fecha prestamo: %s | Fecha devolucion: %s%n",
                idPrestamo, isbnLibro, idUsuario, fechaPrestamo,
                fechaDevolucion == null ? "Pendiente" : fechaDevolucion);
    }

    @Override
    public String toString() {
        return "Prestamo{idPrestamo=" + idPrestamo + ", isbnLibro='" + isbnLibro +
                "', idUsuario='" + idUsuario + "', fechaPrestamo='" + fechaPrestamo +
                "', fechaDevolucion='" + fechaDevolucion + "'}";
    }
}

package com.biblioteca.model;

/**
 * Representa a un usuario de la biblioteca.
 */
public class Usuario {

    private String nombre;
    private String idUsuario;
    private boolean tieneLibroPrestado;

    public Usuario(String nombre, String idUsuario, boolean tieneLibroPrestado) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.tieneLibroPrestado = tieneLibroPrestado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isTieneLibroPrestado() {
        return tieneLibroPrestado;
    }

    public void setTieneLibroPrestado(boolean tieneLibroPrestado) {
        this.tieneLibroPrestado = tieneLibroPrestado;
    }

    /**
     * Imprime los detalles del usuario en consola.
     */
    public void mostrarDetalles() {
        System.out.printf("ID: %s | Nombre: %s | Tiene libro prestado: %s%n",
                idUsuario, nombre, tieneLibroPrestado ? "Si" : "No");
    }

    @Override
    public String toString() {
        return "Usuario{nombre='" + nombre + "', idUsuario='" + idUsuario +
                "', tieneLibroPrestado=" + tieneLibroPrestado + "}";
    }
}

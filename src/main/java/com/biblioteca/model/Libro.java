package com.biblioteca.model;

/**
 * Representa un libro individual en la biblioteca.
 */
public class Libro {

    private String titulo;
    private String autor;
    private String isbn;
    private int copiasDisponibles;

    public Libro(String titulo, String autor, String isbn, int copiasDisponibles) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.copiasDisponibles = copiasDisponibles;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getCopiasDisponibles() {
        return copiasDisponibles;
    }

    public void setCopiasDisponibles(int copiasDisponibles) {
        this.copiasDisponibles = copiasDisponibles;
    }

    /**
     * Imprime los detalles del libro en consola.
     */
    public void mostrarDetalles() {
        System.out.printf("ISBN: %s | Titulo: %s | Autor: %s | Copias disponibles: %d%n",
                isbn, titulo, autor, copiasDisponibles);
    }

    @Override
    public String toString() {
        return "Libro{titulo='" + titulo + "', autor='" + autor + "', isbn='" + isbn +
                "', copiasDisponibles=" + copiasDisponibles + "}";
    }
}

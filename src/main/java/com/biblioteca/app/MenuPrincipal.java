package com.biblioteca.app;

import com.biblioteca.dao.ConexionDB;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.Biblioteca;

import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada del programa. Muestra el menú de opciones en consola
 * e interactúa con la clase Biblioteca para ejecutar cada operación.
 */
public class MenuPrincipal {

    private final Scanner scanner;
    private final Biblioteca biblioteca;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.biblioteca = new Biblioteca();
    }

    public static void main(String[] args) {
        // Inicializar la base de datos (crea las tablas si no existen)
        ConexionDB.inicializarBaseDeDatos();

        MenuPrincipal app = new MenuPrincipal();
        app.iniciar();
    }

    /**
     * Bucle principal de la aplicación: muestra el menú y ejecuta la opción elegida
     * hasta que el usuario decida salir.
     */
    public void iniciar() {
        boolean salir = false;
        System.out.println("\n=== Bienvenido al Sistema de Gestion de Biblioteca Digital ===");

        while (!salir) {
            mostrarMenu();
            int opcion = pedirInt("Seleccione una opcion: ");
            salir = ejecutarOpcion(opcion);
        }

        System.out.println("\nGracias por usar el Sistema de Gestion de Biblioteca Digital. Hasta pronto!");
        scanner.close();
    }

    /**
     * Muestra las opciones disponibles al usuario.
     */
    private void mostrarMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("1. Registrar nuevo libro");
        System.out.println("2. Registrar nuevo usuario");
        System.out.println("3. Realizar prestamo de un libro");
        System.out.println("4. Devolver un libro");
        System.out.println("5. Listar libros disponibles");
        System.out.println("6. Listar todos los libros");
        System.out.println("7. Listar usuarios");
        System.out.println("8. Listar prestamos activos");
        System.out.println("9. Listar historial de prestamos");
        System.out.println("0. Salir");
        System.out.println("--------------------------------------------------");
    }

    /**
     * Contiene la lógica para llamar a los métodos de Biblioteca según la
     * opción seleccionada. Retorna true si el usuario eligió salir.
     */
    private boolean ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> registrarLibro();
            case 2 -> registrarUsuario();
            case 3 -> prestarLibro();
            case 4 -> devolverLibro();
            case 5 -> listarLibrosDisponibles();
            case 6 -> listarTodosLosLibros();
            case 7 -> listarUsuarios();
            case 8 -> listarPrestamosActivos();
            case 9 -> listarHistorialPrestamos();
            case 0 -> {
                return true;
            }
            default -> System.out.println("Opcion invalida. Por favor seleccione una opcion del menu (0-9).");
        }
        return false;
    }

    // ---------- Opciones del menú ----------

    private void registrarLibro() {
        System.out.println("\n-- Registrar nuevo libro --");
        String titulo = pedirString("Titulo: ");
        String autor = pedirString("Autor: ");
        String isbn = pedirString("ISBN: ");
        int copias = pedirIntNoNegativo("Numero de copias disponibles: ");

        if (biblioteca.buscarLibroPorISBN(isbn) != null) {
            System.out.println("Error: ya existe un libro registrado con el ISBN '" + isbn + "'.");
            return;
        }

        boolean registrado = biblioteca.agregarLibro(new Libro(titulo, autor, isbn, copias));
        if (registrado) {
            System.out.println("Libro registrado exitosamente.");
        } else {
            System.out.println("Error: no se pudo registrar el libro. Intente de nuevo.");
        }
    }

    private void registrarUsuario() {
        System.out.println("\n-- Registrar nuevo usuario --");
        String nombre = pedirString("Nombre: ");
        String idUsuario = pedirString("ID de usuario: ");

        if (biblioteca.buscarUsuarioPorID(idUsuario) != null) {
            System.out.println("Error: ya existe un usuario registrado con el ID '" + idUsuario + "'.");
            return;
        }

        boolean registrado = biblioteca.agregarUsuario(new Usuario(nombre, idUsuario, false));
        if (registrado) {
            System.out.println("Usuario registrado exitosamente.");
        } else {
            System.out.println("Error: no se pudo registrar el usuario. Intente de nuevo.");
        }
    }

    private void prestarLibro() {
        System.out.println("\n-- Realizar prestamo de un libro --");
        String isbn = pedirString("ISBN del libro: ");
        String idUsuario = pedirString("ID del usuario: ");

        Biblioteca.ResultadoOperacion resultado = biblioteca.realizarPrestamo(isbn, idUsuario);
        System.out.println(resultado.isExito() ? "Exito: " + resultado.getMensaje()
                : "Error: " + resultado.getMensaje());
    }

    private void devolverLibro() {
        System.out.println("\n-- Devolver un libro --");
        String isbn = pedirString("ISBN del libro: ");
        String idUsuario = pedirString("ID del usuario: ");

        Biblioteca.ResultadoOperacion resultado = biblioteca.realizarDevolucion(isbn, idUsuario);
        System.out.println(resultado.isExito() ? "Exito: " + resultado.getMensaje()
                : "Error: " + resultado.getMensaje());
    }

    private void listarLibrosDisponibles() {
        System.out.println("\n-- Libros disponibles --");
        List<Libro> libros = biblioteca.listarLibrosDisponibles();
        if (libros.isEmpty()) {
            System.out.println("No hay libros con copias disponibles en este momento.");
            return;
        }
        libros.forEach(Libro::mostrarDetalles);
    }

    private void listarTodosLosLibros() {
        System.out.println("\n-- Todos los libros registrados --");
        List<Libro> libros = biblioteca.listarTodosLosLibros();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados todavia.");
            return;
        }
        libros.forEach(Libro::mostrarDetalles);
    }

    private void listarUsuarios() {
        System.out.println("\n-- Usuarios registrados --");
        List<Usuario> usuarios = biblioteca.listarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados todavia.");
            return;
        }
        usuarios.forEach(Usuario::mostrarDetalles);
    }

    private void listarPrestamosActivos() {
        System.out.println("\n-- Prestamos activos --");
        List<Prestamo> prestamos = biblioteca.listarPrestamosActivos();
        if (prestamos.isEmpty()) {
            System.out.println("No hay prestamos activos en este momento.");
            return;
        }
        prestamos.forEach(Prestamo::mostrarDetalles);
    }

    private void listarHistorialPrestamos() {
        System.out.println("\n-- Historial de prestamos --");
        List<Prestamo> prestamos = biblioteca.listarHistorialPrestamos();
        if (prestamos.isEmpty()) {
            System.out.println("No hay prestamos registrados todavia.");
            return;
        }
        prestamos.forEach(Prestamo::mostrarDetalles);
    }

    // ---------- Métodos auxiliares para entrada de datos ----------

    /**
     * Pide un String no vacío al usuario, repitiendo la pregunta hasta obtener uno válido.
     */
    private String pedirString(String mensaje) {
        String valor;
        do {
            System.out.print(mensaje);
            valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("Este campo no puede estar vacio. Intente de nuevo.");
            }
        } while (valor.isEmpty());
        return valor;
    }

    /**
     * Pide un número entero al usuario, repitiendo la pregunta si la entrada no es válida.
     */
    private int pedirInt(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Por favor ingrese un numero entero.");
            }
        }
    }

    /**
     * Pide un número entero no negativo al usuario (usado para cantidades como copias disponibles).
     */
    private int pedirIntNoNegativo(String mensaje) {
        while (true) {
            int valor = pedirInt(mensaje);
            if (valor < 0) {
                System.out.println("El valor no puede ser negativo. Intente de nuevo.");
                continue;
            }
            return valor;
        }
    }
}

# Sistema de Gestión de Biblioteca Digital

Aplicación de consola en Java para gestionar libros, usuarios y préstamos de
una biblioteca digital, usando **SQLite** como base de datos.

## Arquitectura

El proyecto sigue una separación en capas:

```
src/main/java/com/biblioteca/
├── model/       -> Clases de dominio (POJOs): Libro, Usuario, Prestamo
├── dao/         -> Acceso a datos (JDBC + SQLite): ConexionDB, LibroDAO, UsuarioDAO, PrestamoDAO
├── service/     -> Lógica de negocio: Biblioteca (prestar, devolver, buscar, listar)
└── app/         -> Interfaz de consola: MenuPrincipal (contiene el main)
```

La base de datos (`biblioteca.db`) se crea automáticamente la primera vez que
se ejecuta el programa, con 3 tablas: `libros`, `usuarios` y `prestamos`
(esta última con claves foráneas hacia las otras dos).

## Requisitos

- Java 17 o superior (JDK)
- No requiere Maven ni conexión a internet: el driver JDBC de SQLite
  (`sqlite-jdbc`) y su dependencia (`slf4j-api`) ya vienen incluidos en la
  carpeta `lib/`.

## Cómo compilar y ejecutar

```bash
./compile.sh
./run.sh
```

O manualmente:

```bash
# Compilar
javac -encoding UTF-8 -cp "lib/sqlite-jdbc-3.44.1.0.jar:lib/slf4j-api-1.7.32.jar" \
  -d out $(find src/main/java -name "*.java")

# Ejecutar
java -cp "out:lib/sqlite-jdbc-3.44.1.0.jar:lib/slf4j-api-1.7.32.jar" com.biblioteca.app.MenuPrincipal
```

## Funcionalidades

1. Registrar nuevos libros (título, autor, ISBN, copias disponibles)
2. Registrar nuevos usuarios (nombre, ID de usuario)
3. Realizar préstamos de libros (valida existencia y disponibilidad)
4. Devolver libros (valida que exista un préstamo activo)
5. Listar libros disponibles
6. Listar todos los libros registrados
7. Listar usuarios registrados
8. Listar préstamos activos
9. Listar historial completo de préstamos

## Manejo de errores

- Entradas no numéricas o negativas en el menú se rechazan y se vuelve a
  pedir el dato.
- Operaciones sobre libros/usuarios/préstamos inexistentes muestran un
  mensaje de error claro en lugar de fallar silenciosamente.
- ISBN e ID de usuario duplicados son rechazados al registrar.

## Notas técnicas

- El driver `sqlite-jdbc` no implementa `Statement.RETURN_GENERATED_KEYS`,
  por lo que el ID autogenerado de cada préstamo se obtiene con
  `SELECT last_insert_rowid()` dentro de la misma conexión.
- El archivo `biblioteca.db` se genera en la raíz del proyecto y está
  excluido del control de versiones (ver `.gitignore`).

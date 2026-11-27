| ID | Caso | Pasos | Resultado esperado | Resultado | Evidencia |
|---|---|---|---|---|---|
| CP-01 | Login de administrador | Usuario y contraseña de admin, Ingresar | Abre el menú y se ve el botón Reportes | OK_ | menu-admin-con-reporte.png |
| CP-02 | Login de bibliotecario | Usuario de bibliotecario, Ingresar | Abre el menú sin el botón Reportes | OK | menu-bibliotecario-sin-reporte.png |
| CP-03 | Usuario que no existe | Usuario inventado, Ingresar | "Usuario o contraseña incorrectos." | OK| login-error-prueba.png |
| CP-04 | Contraseña incorrecta | Usuario real con contraseña mala | "Usuario o contraseña incorrectos." | OK | login-error-prueba.png |
| CP-05 | Campos vacíos | Ingresar sin escribir nada | "Por favor ingrese usuario y contraseña." | OK | |
| CP-06 | Sin internet al abrir | Apagar wifi y abrir la app | Pantalla sin conexión con botón Reintentar | OK | sin-conexion-al-iniciar-sesion.png |
| CP-07 | Se cae internet en el login | Abrir con internet, apagar wifi, Ingresar | Alerta "No se pudo conectar con el servidor..." | OK | sin-conexion-login.png |
| CP-08 | Registrar libro válido | ISBN de 13 dígitos y datos completos, Guardar | Libro registrado y aparece en la tabla | _ | Libro-registro-prueba.png |
| CP-09 | ISBN con letras | ISBN 97899ABC0001, Guardar | Mensaje de ISBN inválido | OK| Libro-registro-invalido-prueba.png |
| CP-10 | Año fuera de rango | Año 1200, Guardar | Mensaje de año fuera de rango | OK | |
| CP-11 | Eliminar libro prestado | Elegir libro con préstamo, Eliminar | No deja eliminar | OK | |
| CP-12 | Documento con símbolos | Documento 8-123*45, Guardar | Mensaje de documento inválido | OK| |
| CP-13 | Correo inválido | Correo ana.correo.com, Guardar | Mensaje de correo inválido |OK| |
| CP-14 | Búsqueda mientras se escribe | Escribir parte de un título en Buscar | La tabla se filtra sola | OK | libros-filtrados.png |
| CP-15 | Préstamo válido | Elegir libro y socio, Prestar | Préstamo registrado con fecha de vencimiento | OK | Prestamo_registrado.png |
| CP-16 | Socio con préstamo vencido | Prestar otro libro a un socio con préstamo vencido | No deja prestar | OK | prestamo_vencido_sin_devolver.png |
| CP-17 | Devolución tarde | Devolver el préstamo vencido | Libro devuelto con multa (0.50 por día) | OK | prestamo_devuelto_con_multa.png |
| CP-18 | Devolver sin elegir | Devolver sin seleccionar fila | "Seleccione un préstamo de la tabla" | OK | |
| CP-19 | Generar reporte | Reportes, Libros más prestados, Generar | Tabla con título, autor y veces prestado | OK | reporte-libros-mas-prestados.png |
| CP-20 | Exportar CSV | Generar, Exportar CSV, Guardar, abrir en Excel | "Reporte guardado en..." y acentos bien en Excel | OK | cp20-csv-en-excel.png |
| CP-21 | Exportar sin generar | Entrar a Reportes, Exportar CSV | "Primero genere un reporte" | OK | |
| CP-22 | Se cae internet dentro de una pantalla | En Libros apagar wifi y Guardar; Menú y abrir Libros; prender wifi | Error de base de datos, alerta Sin conexión y luego vuelve a funcionar | OK | cp22-error-sin-internet.png, reconexion-recuperada.png |

Resultado de mvn clean test: 20 pruebas, 0 fallas
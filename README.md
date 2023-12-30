# AirlineApp
# Desarrollo de aplicacion de Aereopuerto (CLIENTE-SERVIDOR) con Java, JPA, Java Servlets

- Aplicación empleando JPA puro, con Entity Manager, aplicacando métodos de JPA como persist, flush, find, update, merge y remove.

- Aplicación hecha con arquitectura con el módelo MVC, separando las clases respectivamente para cada función de la aplicación
  
- Aplicando transacciones en la misma, y aplicando un manejo de errores, mismo hecho especificamente para capturar las excepciones de la aplicaciones, y aplicando un rollback en caso de alguna.

- Haciendo uso de interfaces, implementando estas en el servicio, y así mismo inyectando estos métodos de esta en el controlador.

- La aplicación tiene un controlador del lado del cliente que comprueba que reciba una respuesta con un código 200, si no es así, maneja adecuadamente la excepción.

- La parte de la presentación de la aplicación fue hecha desde cero aplicacion JAVA SWING.

- Esta aplicación te permite crear aereopuertos, crear tipos de aereonaves, crear vuelos, comprar tickets para vuelos (calculando su nivel por medio de un algoritmo 
dependiendo del numero de asientos ocupados, dias restantes para el vuelo, distancia de un aereopuerto a otro), así como cancelar los tickets que hayas comprado,
todo siendo registrado en una base de datos (JPA).

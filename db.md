# Base de datos   
## MySql   
  
Nombre: ecosoftware_bdSpBt   
Usuario: root  
contraseña:      
## Postgres  
Aun no esta configurada  


**Inserts Rol pruebas**  
```
INSERT INTO roles (nombre, descripcion, tipo, estado) VALUES ('Administrador', 'Rol con permisos completos para gestionar la aplicación', 'Administrador', true);
INSERT INTO roles (nombre, descripcion, tipo, estado) VALUES ('Ciudadano', 'Rol para los usuarios ciudadanos que acceden a los servicios', 'Ciudadano', true);
INSERT INTO roles (nombre, descripcion, tipo, estado) VALUES  ('Empresa', 'Rol para empresas que participan en procesos de reciclaje', 'Empresa', true);  
INSERT INTO roles (nombre, descripcion, tipo, estado) VALUES ('Reciclador', 'Rol para recicladores encargados de las recolecciones', 'Reciclador', true);  
```


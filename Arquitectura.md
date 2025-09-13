## Estructura del proyecto  

```  
 🗂️EcoSoftwareSpringboot/  
├── 🗂️.idea/                  # Configuración de IntelliJ  
├── 🗂️.mvn/                   # Wrapper de Maven  
│   └── 🗂️wrapper/  
├── 🗂️src/  
│   ├── 🗂️main/  
│   │   ├── 🗂️java/  
│   │   │   └── 🗂️com/  
│   │   │       └── 🗂️EcoSoftware/  
│   │   │           └── 🗂️Scrum6/
│   │   │               ├── 🗂️Config/            # _Java Class_🔵Clases de configuración (Security, CORS, JPA)  
│   │   │               ├── 🗂️Controller/        # _Java Class_🔵Controladores REST o MVC  
│   │   │               ├── 🗂️DTO/               # _Java Class_🔵Data Transfer Objects  
│   │   │               ├── 🗂️Entity/            # _Java Class_🔵Clases de entidad (JPA)  
│   │   │               ├── 🗂️Exception/         # _Java Class_🔵Clases para manejo de excepciones  
│   │   │               ├── 🗂️Implement/         # _Java Class_🔵Implementaciones de servicios  
│   │   │               ├── 🗂️Repository/        # _Java Interface_🟢Interfaces de repositorio JPA  
│   │   │               ├── 🗂️Service/           # _Java Interface_🟢Interfaces de servicios  
│   │   │               └── 🔵 Scrum6Application.java  # Clase principal con @SpringBootApplication  
│   │   └── 🗂️resources/  
│   │       ├── 🗂️application.properties         # Configuración de Spring Boot  
│   │       └── 🗂️static/                        # Archivos estáticos (CSS, JS, imágenes)  
│   │       └── 🗂️templates/                     # Plantillas Thymeleaf (si aplica)  
│   └── 🗂️test/  
│       └── 🗂️java/...   
├── 🗂️target/                  # Compilados generados por Maven  
├── 📝pom.xml                  # Archivo de dependencias y build de Maven  
├── 📝.gitignore               # Ignorar archivos para Git  
└── 📝README.md  
```
**Controller  -->  Service (interface)  -->  Implement (lógica)  -->  Repository  -->  Entity (BD)
DTO (opcional) circula entre Controller y Service
Exception captura errores en cualquier capa**
---
📁Controller/   
Propósito: Maneja las peticiones HTTP y decide qué hacer con ellas.    
Qué contiene: Clases Java anotadas con @RestController o @Controller.   
GET POST PUT DELETE
---
📂DTO/ (Data Transfer Objects)  
Propósito: Contener objetos que transportan datos entre capas (por ejemplo, de Controller a Service o de Service a Controller).  
Qué contiene: Clases Java simples, normalmente solo con atributos, getters/setters (@Data) y validaciones 
---
📂Entity/  
Propósito: Representar las tablas de la base de datos mediante JPA/Hibernate.  
Qué contiene: Clases anotadas con @Entity y @Table. Cada clase normalmente corresponde a una tabla.  
---
📂Exception/  
Propósito: Centralizar el manejo de errores de la aplicación.  
Qué contiene: Clases que extienden RuntimeException o Exception y clases con @ControllerAdvice para manejar errores globales.  
---
📂Implement/  
Propósito: Contener las implementaciones concretas de las interfaces de servicio.  
Qué contiene: Clases anotadas con @Service que implementan interfaces en Service/.  
---
📂Repository/  
Propósito: Interactuar directamente con la base de datos.   
Qué contiene: Interfaces que extienden JpaRepository o CrudRepository.  
---
📂Service/  
Propósito: Definir contratos de servicios, es decir, qué operaciones de negocio estarán disponibles.  
Qué contiene: Interfaces con métodos como crearUsuario(), actualizarUsuario(), listarUsuarios().  
---

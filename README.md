# EcoSoftware
Sistema de información para la gestión de residuos.

##Estructura del proyecto  

```  
EcoSoftwareSpringboot/  
├── .idea/                  # Configuración de IntelliJ  
├── .mvn/                   # Wrapper de Maven  
│   └── wrapper/  
├── src/  
│   ├── main/  
│   │   ├── java/  
│   │   │   └── com/  
│   │   │       └── EcoSoftware/  
│   │   │           └── Scrum6/
│   │   │               ├── Config/            # Clases de configuración (Security, CORS, JPA)  
│   │   │               ├── Controller/        # Controladores REST o MVC  
│   │   │               ├── DTO/               # Data Transfer Objects  
│   │   │               ├── Entity/            # Clases de entidad (JPA)  
│   │   │               ├── Exception/         # Clases para manejo de excepciones  
│   │   │               ├── Implement/         # Implementaciones de servicios  
│   │   │               ├── Repository/        # Interfaces de repositorio JPA  
│   │   │               ├── Service/           # Interfaces de servicios  
│   │   │               └── Scrum6Application.java  # Clase principal con @SpringBootApplication  
│   │   └── resources/  
│   │       ├── application.properties         # Configuración de Spring Boot  
│   │       └── static/                        # Archivos estáticos (CSS, JS, imágenes)  
│   │       └── templates/                     # Plantillas Thymeleaf (si aplica)  
│   └── test/  
│       └── java/...   
├── target/                  # Compilados generados por Maven  
├── pom.xml                  # Archivo de dependencias y build de Maven  
├── .gitignore               # Ignorar archivos para Git  
└── README.md  
```

---

##Base de datos  
**MySql**  

Nombre: ecosoftware_bdSpBt  
Usuario: root  
contraseña:   
  
**Postgres**  
Aun no esta configurada


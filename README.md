# EcoSoftware
Sistema de información para la gestión de residuos.

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

---

## Base de datos  
**MySql**  

Nombre: ecosoftware_bdSpBt  
Usuario: root  
contraseña:   
  
**Postgres**  
Aun no esta configurada


# Usar imagen oficial de Java 21
FROM eclipse-temurin:21-jdk

# Crear directorio dentro del contenedor
WORKDIR /app

# Copiar el proyecto
COPY . .

# Dar permisos al wrapper de Maven
RUN chmod +x mvnw

# Construir el proyecto
RUN ./mvnw clean package -DskipTests

# Exponer el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/Scrum6-0.0.1-SNAPSHOT.jar"]

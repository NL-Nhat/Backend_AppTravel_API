# Bước 1: Build ứng dụng bằng Maven và Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy file pom.xml và tải các dependency trước để tối ưu cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ mã nguồn và build file JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng với JRE 21 nhẹ hơn
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file jar từ bước build sang (lấy đúng tên file jar được tạo ra)
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 cho ứng dụng
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
# Sử dụng JDK 17 làm môi trường build
FROM openjdk:17-jdk-slim

# Copy file .jar từ thư mục target
COPY target/*.jar app.jar

# Mở cổng 8080 cho ứng dụng
EXPOSE 8080

# Lệnh chạy app
ENTRYPOINT ["java","-jar","/app.jar"]

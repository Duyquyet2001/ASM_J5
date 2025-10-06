# Base image có sẵn JDK
FROM openjdk:17-jdk-slim

# Cài thư viện font cần thiết cho export Excel / PDF
RUN apt-get update && apt-get install -y fontconfig libfreetype6 && rm -rf /var/lib/apt/lists/*

# Copy file JAR từ target
COPY target/*.jar app.jar

# Mở port cho Render
EXPOSE 8080

# Bật chế độ headless (chạy không cần GUI)
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"

# Chạy app
ENTRYPOINT ["java","-jar","/app.jar"]

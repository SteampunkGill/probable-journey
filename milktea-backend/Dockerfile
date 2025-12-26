# 使用 Eclipse Temurin JDK 17 构建阶段
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
# 假设已经有编译好的 jar 包，或者在本地编译
# 如果需要使用 Maven，建议使用国内镜像或确保网络通畅
COPY . .
# 如果本地有 mvnw，可以使用它
# RUN ./mvnw package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app
# 直接复制 target 下的 jar 包（假设本地已经 mvn package 过了）
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
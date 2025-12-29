# 使用 Maven 构建阶段
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
# 使用系统安装的 maven 构建项目
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 从构建阶段复制生成的 jar 包
COPY --from=build /app/target/*.jar app.jar
# 暴露端口
EXPOSE 8080
# 启动命令，使用 docker profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
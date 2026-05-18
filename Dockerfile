# 1. 构建阶段：用 Maven 把你的代码编译打包成 jar
FROM maven:3.8.6-openjdk-17-slim AS build
WORKDIR /app
# 把 Maven 依赖包先复制进来，方便缓存
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. 运行阶段：用 JRE 运行刚才打好的 jar 包
FROM openjdk:17-jdk-slim
WORKDIR /app
# 从上一步的构建结果中复制 jar 包
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# 使用官方轻量级 Java 镜像
FROM eclipse-temurin:17-jdk

# 设置工作目录
WORKDIR /app

# 将编译好的 jar 包复制进来
COPY target/movieServer-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]

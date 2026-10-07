# 基础镜像使用轻量且性能优异的 eclipse-temurin:17-jre-jammy
FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="snowy"

# 设置容器时区为 Asia/Shanghai
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建工作目录
WORKDIR /app

# 拷贝后端 jar 文件到容器中
COPY snowy-web-app.jar /app/app.jar

# 创建文件上传持久化目录
RUN mkdir -p /defaultUploadFolder

# 暴露后端运行端口
EXPOSE 82

# JVM 启动参数配置（适配中小内存服务器）
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"
ENV SPRING_PROFILES_ACTIVE="docker"

# 启动 Spring Boot 应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE"]

FROM nginx:alpine

LABEL maintainer="snowy-front-web"

# 复制自定义 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

# 复制前端打包静态文件
COPY dist /usr/share/nginx/html

# 暴露端口
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

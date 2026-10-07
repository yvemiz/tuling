@echo off
chcp 65001 >nul
echo ==============================================================================
echo [1/4] 开始构建后端项目 (Maven Package)...
echo ==============================================================================
cd /d "%~dp0snowy"
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 后端打包失败，请检查 Maven 编译报错！
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [复制后端可执行 Jar 包到 deploy/backend/...]
if exist "%~dp0snowy\snowy-web-app\target\snowy-web-app.jar" (
    copy /y "%~dp0snowy\snowy-web-app\target\snowy-web-app.jar" "%~dp0deploy\backend\snowy-web-app.jar"
) else if exist "%~dp0snowy\snowy-web-app\target\snowy-web-app-3.0.0.jar" (
    copy /y "%~dp0snowy\snowy-web-app\target\snowy-web-app-3.0.0.jar" "%~dp0deploy\backend\snowy-web-app.jar"
)

echo.
echo ==============================================================================
echo [2/4] 开始构建管理端前端 (snowy-admin-web)...
echo ==============================================================================
cd /d "%~dp0snowy\snowy-admin-web"
call npm run build
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 管理端前端打包失败！
    pause
    exit /b %ERRORLEVEL%
)
echo [复制管理端静态文件到 deploy/admin-web/dist/...]
if exist "%~dp0deploy\admin-web\dist" rd /s /q "%~dp0deploy\admin-web\dist"
xcopy /e /i /y "%~dp0snowy\snowy-admin-web\dist" "%~dp0deploy\admin-web\dist"

echo.
echo ==============================================================================
echo [3/4] 开始构建用户端前台门户 (snowy-front-web)...
echo ==============================================================================
cd /d "%~dp0snowy-front-web"
call npm run build
if %ERRORLEVEL% NEQ 0 (
    echo [错误] 用户端前端打包失败！
    pause
    exit /b %ERRORLEVEL%
)
echo [复制用户端静态文件到 deploy/front-web/dist/...]
if exist "%~dp0deploy\front-web\dist" rd /s /q "%~dp0deploy\front-web\dist"
xcopy /e /i /y "%~dp0snowy-front-web\dist" "%~dp0deploy\front-web\dist"

echo.
echo ==============================================================================
echo [4/4] 导出最新数据库初始化脚本...
echo ==============================================================================
if exist "D:\MariaDB\bin\mysqldump.exe" (
    "D:\MariaDB\bin\mysqldump.exe" -uroot -proot --default-character-set=utf8mb4 --databases st_homework_manage --result-file="%~dp0deploy\db\init.sql"
    echo [数据库导出成功: deploy/db/init.sql]
) else (
    echo [提示] 未找到本地 mysqldump，使用已有 deploy/db/init.sql
)

echo.
echo ==============================================================================
echo [打包完成] 全部构建物已成功归集到 deploy 目录！
echo 您现在只需将 deploy 文件夹压缩或上传至服务器，运行 docker compose up -d 即可！
echo ==============================================================================
cd /d "%~dp0"
pause

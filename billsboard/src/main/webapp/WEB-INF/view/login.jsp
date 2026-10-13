<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html>

<head>

    <title>Вход</title>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body>

<jsp:include page="header.jsp" />

<hr align="center"
    width="100%"
    size="4"
    color="black"
    style="margin-top: 30px;">

<div style="
        display: flex;
        justify-content: center;
        margin-top: 5%;
        margin-bottom: 5%;
     ">

    <div style="
            background-color:#e7e7e7;
            padding:30px;
            width:350px;
            border-radius:10px;
         ">

        <h2>Вход</h2>

        <c:if test="${param.error != null}">
            <p style="color:crimson;">
                Неверный email или пароль.
            </p>
        </c:if>

        <c:if test="${param.logout != null}">
            <p style="color:green;">
                Вы вышли из системы.
            </p>
        </c:if>

        <c:if test="${param.registered != null}">
            <p style="color:green;">
                Регистрация прошла успешно. Войдите в аккаунт.
            </p>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth/login"
              method="post">

            <input type="hidden"
                   name="${_csrf.parameterName}"
                   value="${_csrf.token}"/>

            <p>
                <label for="username">
                    Email:
                </label>
            </p>

            <p>
                <input type="email"
                       id="username"
                       name="username"
                       maxlength="50"
                       required
                       style="width:100%; box-sizing:border-box; padding:8px;">
            </p>

            <p>
                <label for="password">
                    Пароль:
                </label>
            </p>

            <p>
                <input type="password"
                       id="password"
                       name="password"
                       required
                       style="width:100%; box-sizing:border-box; padding:8px;">
            </p>

            <p>
                <input class="buttonstyle"
                       type="submit"
                       value="Войти">
            </p>

        </form>

        <p>
            Нет аккаунта?
            <a href="${pageContext.request.contextPath}/auth/register">
                Зарегистрироваться
            </a>
        </p>

    </div>

</div>

</body>

</html>
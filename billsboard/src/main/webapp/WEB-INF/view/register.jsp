<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html>

<head>

    <title>Регистрация</title>

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

        <h2>Регистрация</h2>

        <c:if test="${error != null}">
            <p style="color:crimson;">
                ${error}
            </p>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth/register"
              method="post">

            <input type="hidden"
                   name="${_csrf.parameterName}"
                   value="${_csrf.token}"/>

            <p>
                <label for="name">Имя:</label>
            </p>
            <p>
                <input type="text"
                       id="name"
                       name="name"
                       maxlength="50"
                       required
                       style="width:100%; box-sizing:border-box; padding:8px;">
            </p>

            <p>
                <label for="secondName">Фамилия:</label>
            </p>
            <p>
                <input type="text"
                       id="secondName"
                       name="secondName"
                       maxlength="50"
                       required
                       style="width:100%; box-sizing:border-box; padding:8px;">
            </p>

            <p>
                <label for="email">Email:</label>
            </p>
            <p>
                <input type="email"
                       id="email"
                       name="email"
                       maxlength="50"
                       required
                       style="width:100%; box-sizing:border-box; padding:8px;">
            </p>

            <p>
                <label for="password">Пароль:</label>
            </p>
            <p>
                <input type="password"
                       id="password"
                       name="password"
                       required
                       style="width:100%; box-sizing:border-box; padding:8px;">
            </p>

            <p>
                <label for="passwordConfirm">Повторите пароль:</label>
            </p>
            <p>
                <input type="password"
                       id="passwordConfirm"
                       name="passwordConfirm"
                       required
                       style="width:100%; box-sizing:border-box; padding:8px;">
            </p>

            <p>
                <input class="buttonstyle"
                       type="submit"
                       value="Зарегистрироваться">
            </p>

        </form>

        <p>
            Уже есть аккаунт?
            <a href="${pageContext.request.contextPath}/auth/login">
                Войти
            </a>
        </p>

    </div>

</div>

</body>

</html>
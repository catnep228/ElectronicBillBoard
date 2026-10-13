<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="UTF-8">
    <title>Создать объявление — Bill's Board</title>

    <style>

        body {
            font-family: 'Times New Roman', serif;
            margin: 0;
            background-color: white;
            color: black;
        }

        /* Header */

        .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin: 20px 10%;
        }

        .logo h1 {
            font-size: 32px;
            text-decoration: underline;
            margin: 0;
        }

        /* Navigation */

        .nav {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .navAction {
            transition: 0.25s;
        }

        .navAction:hover {
            box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.3);
        }

        .navButton {
            border: 2px solid #e7e7e7;
            padding: 12px 30px;
            font-size: 16px;
            font-weight: bold;
            background: white;
            cursor: pointer;
            font-family: 'Times New Roman', serif;
        }

        .navButton:hover {
            background-color: crimson;
            color: white;
        }

        .navUser {
            padding: 12px 15px;
            font-size: 16px;
            font-weight: bold;
        }

        /* Main */

        .create-container {
            width: 70%;
            max-width: 900px;
            margin: 40px auto;
        }

        .page-title {
            font-size: 30px;
            text-decoration: underline;
            margin-bottom: 30px;
        }

        /* Form */

        .ad-form {
            background-color: #e7e7e7;
            padding: 25px;
            border-radius: 10px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-label {
            display: block;
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 8px;
        }

        .required {
            color: crimson;
        }

        .form-input,
        .form-textarea,
        .form-select {
            width: 100%;
            box-sizing: border-box;
            border: 2px solid #d0d0d0;
            padding: 12px;
            font-family: 'Times New Roman', serif;
            font-size: 17px;
            background-color: white;
        }

        .form-input:focus,
        .form-textarea:focus,
        .form-select:focus {
            outline: none;
            border-color: #263e9e;
        }

        .form-textarea {
            min-height: 180px;
            resize: vertical;
        }

        .price-input {
            max-width: 300px;
        }

        .category-select {
            max-width: 500px;
        }

        /* File */

        .file-input {
            width: 100%;
            box-sizing: border-box;
            border: 2px solid #d0d0d0;
            padding: 10px;
            background-color: white;
            font-family: 'Times New Roman', serif;
            font-size: 16px;
        }

        .file-info {
            margin-top: 8px;
            color: #666;
            font-size: 15px;
        }

        /* Buttons */

        .form-buttons {
            display: flex;
            gap: 12px;
            margin-top: 25px;
            flex-wrap: wrap;
        }

        .buttonstyle {
            display: inline-block;
            background-color: #99a9e8;
            border: 2px solid #263e9e;
            padding: 10px 22px;
            cursor: pointer;
            color: black;
            text-decoration: none;
            font-family: 'Times New Roman', serif;
            font-size: 17px;
            font-weight: bold;
        }

        .buttonstyle:hover {
            background-color: crimson;
            color: white;
            border-color: crimson;
        }

        .cancel-button {
            background-color: white;
        }

        /* Messages */

        .error-message {
            background-color: #ffd6d6;
            border: 2px solid crimson;
            color: #900;
            padding: 12px;
            border-radius: 7px;
            margin-bottom: 20px;
            font-size: 17px;
        }

        .success-message {
            background-color: #d9f2d9;
            border: 2px solid #398439;
            color: #246124;
            padding: 12px;
            border-radius: 7px;
            margin-bottom: 20px;
            font-size: 17px;
        }

        /* Responsive */

        @media (max-width: 900px) {

            .header {
                margin: 20px 5%;
                flex-direction: column;
                align-items: flex-start;
                gap: 20px;
            }

            .create-container {
                width: 90%;
            }
        }

        @media (max-width: 600px) {

            .navButton {
                padding: 10px 15px;
            }

            .create-container {
                width: 95%;
            }

            .ad-form {
                padding: 15px;
            }
        }

    </style>
</head>

<body>

<!-- Header -->

<div class="header">

    <div class="logo">
        <h1>Bill's Board</h1>
    </div>

    <nav class="nav">

        <form class="navAction"
              action="${pageContext.request.contextPath}/"
              method="get">

            <input class="navButton"
                   type="submit"
                   value="Доска">

        </form>


        <form class="navAction"
              action="${pageContext.request.contextPath}/FormForAd"
              method="get">

            <input class="navButton"
                   type="submit"
                   value="Создать объявление">

        </form>


        <form class="navAction"
              action="${pageContext.request.contextPath}/about"
              method="get">

            <input class="navButton"
                   type="submit"
                   value="О нас">

        </form>


        <form class="navAction"
              action="${pageContext.request.contextPath}/profile"
              method="get">

            <input class="navButton"
                   type="submit"
                   value="Вы">

        </form>


        <sec:authorize access="isAuthenticated()">

            <span class="navUser">
                <sec:authentication property="name"/>
            </span>

            <form class="navAction"
                  action="${pageContext.request.contextPath}/auth/logout"
                  method="post">

                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}"/>

                <input class="navButton"
                       type="submit"
                       value="Выйти">

            </form>

        </sec:authorize>


        <sec:authorize access="isAnonymous()">

            <form class="navAction"
                  action="${pageContext.request.contextPath}/auth/login"
                  method="get">

                <input class="navButton"
                       type="submit"
                       value="Войти">

            </form>

            <form class="navAction"
                  action="${pageContext.request.contextPath}/auth/register"
                  method="get">

                <input class="navButton"
                       type="submit"
                       value="Регистрация">

            </form>

        </sec:authorize>

    </nav>

</div>


<!-- Создание объявления -->

<div class="create-container">

    <div class="page-title">
        Создание объявления
    </div>


    <!-- Ошибка -->

    <c:if test="${not empty error}">

        <div class="error-message">
            ${error}
        </div>

    </c:if>


    <!-- Сообщение -->

    <c:if test="${not empty message}">

        <div class="success-message">
            ${message}
        </div>

    </c:if>


    <!-- Форма -->

    <form class="ad-form"
          action="${pageContext.request.contextPath}/FormForAd"
          method="post"
          enctype="multipart/form-data">


        <!-- CSRF -->

        <input type="hidden"
               name="${_csrf.parameterName}"
               value="${_csrf.token}">


        <!-- Название -->

        <div class="form-group">

            <label class="form-label"
                   for="title">

                Название
                <span class="required">*</span>

            </label>

            <input class="form-input"
                   type="text"
                   id="title"
                   name="title"
                   maxlength="150"
                   placeholder="Например: Продаю велосипед"
                   required>

        </div>


        <!-- Описание -->

        <div class="form-group">

            <label class="form-label"
                   for="description">

                Описание

            </label>

            <textarea class="form-textarea"
                      id="description"
                      name="description"
                      maxlength="4000"
                      placeholder="Подробно опишите товар или услугу..."></textarea>

        </div>


        <!-- Цена -->

        <div class="form-group">

            <label class="form-label"
                   for="price">

                Цена
                <span class="required">*</span>

            </label>

            <input class="form-input price-input"
                   type="number"
                   id="price"
                   name="price"
                   min="0"
                   step="0.01"
                   placeholder="0.00"
                   required>

        </div>


        <!-- Категория -->

        <div class="form-group">

            <label class="form-label"
                   for="categoryId">

                Категория
                <span class="required">*</span>

            </label>

            <select class="form-select category-select"
                    id="categoryId"
                    name="categoryId"
                    required>

                <option value="">
                    Выберите категорию
                </option>

                <c:forEach var="category" items="${categories}">

                    <option value="${category.id}">
                        ${category.name}
                    </option>

                </c:forEach>

            </select>

        </div>


        <!-- Фотографии -->

        <div class="form-group">

            <label class="form-label"
                   for="photos">

                Фотографии

            </label>

            <input class="file-input"
                   type="file"
                   id="photos"
                   name="photos"
                   accept="image/*"
                   multiple>

            <div class="file-info">
                Можно выбрать несколько фотографий.
            </div>

        </div>


        <!-- Кнопки -->

        <div class="form-buttons">

            <button class="buttonstyle"
                    type="submit">

                Опубликовать объявление

            </button>

            <a class="buttonstyle cancel-button"
               href="${pageContext.request.contextPath}/">

                Отмена

            </a>

        </div>

    </form>

</div>

</body>
</html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>

<html lang="ru">

<head>

    <meta charset="UTF-8">

    <title>Профиль — Bill's Board</title>

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

        .profile-container {
            width: 80%;
            margin: 40px auto;
        }

        .profile-title {
            font-size: 30px;
            text-decoration: underline;
            margin-bottom: 25px;
        }

        /* User information */

        .profile-info {
            background-color: #e7e7e7;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 35px;
        }

        .profile-info h2 {
            margin-top: 0;
            font-size: 25px;
        }

        .profile-row {
            display: flex;
            margin: 10px 0;
            font-size: 18px;
        }

        .profile-label {
            width: 150px;
            font-weight: bold;
        }

        .profile-value {
            word-break: break-word;
        }

        /* Ads */

        .ads-title {
            font-size: 26px;
            margin-bottom: 15px;
        }

        .ads-container {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
        }

        .advertisement {
            width: 240px;
            background-color: #e7e7e7;
            padding: 16px;
            margin: 0;
            border-radius: 10px;
            box-sizing: border-box;
        }

        .advertisement:hover {
            box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.3);
        }

        .ad-photo {
            width: 100%;
            height: 170px;
            object-fit: cover;
            border-radius: 7px;
            background-color: white;
            margin-bottom: 10px;
        }

        .no-photo {
            width: 100%;
            height: 170px;
            background-color: white;
            border-radius: 7px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 10px;
            color: #777;
            font-size: 18px;
        }

        .ad-title {
            font-size: 21px;
            font-weight: bold;
            margin: 8px 0;
        }

        .ad-title a {
            color: black;
            text-decoration: none;
        }

        .ad-title a:hover {
            color: crimson;
            text-decoration: underline;
        }

        .ad-price {
            font-size: 20px;
            font-weight: bold;
            margin: 8px 0;
        }

        .ad-category {
            font-size: 16px;
            margin: 6px 0;
        }

        .ad-description {
            font-size: 16px;
            margin: 8px 0;
            line-height: 1.3;
        }

        .ad-status {
            margin-top: 10px;
            font-weight: bold;
        }

        .ad-date {
            margin-top: 8px;
            color: #666;
            font-size: 14px;
        }

        /* Buttons */

        .buttonstyle {
            display: inline-block;
            background-color: #99a9e8;
            border: 2px solid #263e9e;
            padding: 8px 16px;
            cursor: pointer;
            color: black;
            text-decoration: none;
            font-family: 'Times New Roman', serif;
            font-size: 16px;
            font-weight: bold;
            box-sizing: border-box;
        }

        .buttonstyle:hover {
            background-color: crimson;
            color: white;
            border-color: crimson;
        }

        .edit-button {
            margin-top: 15px;
            width: 100%;
            text-align: center;
        }

        /* Empty state */

        .empty-ads {
            background-color: #e7e7e7;
            border-radius: 10px;
            padding: 25px;
            font-size: 18px;
        }

        /* Responsive */

        @media (max-width: 900px) {

            .header {
                margin: 20px 5%;
                flex-direction: column;
                align-items: flex-start;
                gap: 20px;
            }

            .profile-container {
                width: 90%;
            }

            .nav {
                width: 100%;
            }

        }

        @media (max-width: 600px) {

            .navButton {
                padding: 10px 15px;
            }

            .advertisement {
                width: 100%;
            }

            .profile-row {
                flex-direction: column;
                gap: 3px;
            }

            .profile-label {
                width: auto;
            }

        }

    </style>

</head>

<body>


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


<div class="profile-container">


    <div class="profile-title">

        Профиль пользователя

    </div>


    <!-- Информация о пользователе -->

    <div class="profile-info">

        <h2>

            ${user.name} ${user.secondName}

        </h2>


        <div class="profile-row">

            <div class="profile-label">

                Имя:

            </div>

            <div class="profile-value">

                ${user.name}

            </div>

        </div>


        <div class="profile-row">

            <div class="profile-label">

                Фамилия:

            </div>

            <div class="profile-value">

                ${user.secondName}

            </div>

        </div>


        <div class="profile-row">

            <div class="profile-label">

                Email:

            </div>

            <div class="profile-value">

                ${user.email}

            </div>

        </div>


        <div class="profile-row">

            <div class="profile-label">

                Дата регистрации:

            </div>

            <div class="profile-value">

                ${user.createdAt}

            </div>

        </div>

    </div>


    <!-- Объявления пользователя -->

    <div class="ads-title">

        Мои объявления

    </div>


    <c:choose>


        <c:when test="${empty ads}">

            <div class="empty-ads">

                <p>

                    У вас пока нет объявлений.

                </p>


                <a class="buttonstyle"
                   href="${pageContext.request.contextPath}/FormForAd">

                    Создать объявление

                </a>

            </div>

        </c:when>


        <c:otherwise>

            <div class="ads-container">


                <c:forEach var="ad" items="${ads}">


                    <div class="advertisement">


                        <!-- Фото -->

                        <c:choose>

                            <c:when test="${not empty ad.photos}">

                                <c:set var="firstPhoto"
                                       value="${ad.photos[0]}"/>


                                <img class="ad-photo"
                                     src="${pageContext.request.contextPath}/uploads/${firstPhoto.fileName}"
                                     alt="${ad.title}">

                            </c:when>


                            <c:otherwise>

                                <div class="no-photo">

                                    Нет фото

                                </div>

                            </c:otherwise>

                        </c:choose>


                        <!-- Название -->

                        <div class="ad-title">

                            <a href="${pageContext.request.contextPath}/ads/${ad.id}">

                                ${ad.title}

                            </a>

                        </div>


                        <!-- Цена -->

                        <div class="ad-price">

                            ${ad.price} ₽

                        </div>


                        <!-- Категория -->

                        <c:if test="${not empty ad.category}">

                            <div class="ad-category">

                                Категория:
                                ${ad.category.name}

                            </div>

                        </c:if>


                        <!-- Описание -->

                        <c:if test="${not empty ad.description}">

                            <div class="ad-description">

                                ${ad.description}

                            </div>

                        </c:if>


                        <!-- Статус -->

                        <div class="ad-status">

                            Статус:

                            <c:choose>

                                <c:when test="${ad.status == 'ACTIVE'}">

                                    Активно

                                </c:when>

                                <c:when test="${ad.status == 'SOLD'}">

                                    Продано

                                </c:when>

                                <c:when test="${ad.status == 'ARCHIVED'}">

                                    В архиве

                                </c:when>

                                <c:otherwise>

                                    ${ad.status}

                                </c:otherwise>

                            </c:choose>

                        </div>


                        <!-- Дата -->

                        <div class="ad-date">

                            Создано:
                            ${ad.createdAt}

                        </div>


                        <!-- Кнопки -->

                        <div>

                            <a class="buttonstyle edit-button"
                               href="${pageContext.request.contextPath}/ads/${ad.id}/edit">

                                Редактировать

                            </a>

                        </div>


                    </div>


                </c:forEach>


            </div>

        </c:otherwise>


    </c:choose>


</div>


</body>

</html>
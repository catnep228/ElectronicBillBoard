<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>

<html lang="ru">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Панель администратора — Bill's Board</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

    <style>

        .admin-container {
            width: 90%;
            margin: 40px auto;
        }

        .admin-title {
            font-size: 32px;
            text-decoration: underline;
            margin-bottom: 30px;
        }

        .admin-section {
            background: #e7e7e7;
            padding: 25px;
            margin-bottom: 30px;
            border-radius: 10px;
        }

        .admin-section h2 {
            margin-top: 0;
        }

        .admin-form {
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }

        .admin-form input,
        .admin-form select {
            padding: 10px;
            font-family: 'Times New Roman', serif;
            font-size: 16px;
        }

        .admin-ads {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
        }

        .admin-ad {
            width: 270px;
            background: #e7e7e7;
            padding: 15px;
            border-radius: 10px;
            box-sizing: border-box;
        }

        .admin-ad:hover {
            box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.3);
        }

        .admin-ad-photo {
            width: 100%;
            height: 190px;
            object-fit: cover;
            background: white;
            border-radius: 7px;
            margin-bottom: 10px;
        }

        .admin-no-photo {
            width: 100%;
            height: 190px;
            background: white;
            border-radius: 7px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 10px;
        }

        .admin-ad-title {
            font-size: 21px;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .admin-ad-info {
            margin: 7px 0;
        }

        .admin-actions {
            display: flex;
            gap: 8px;
            margin-top: 15px;
            flex-wrap: wrap;
        }

        .admin-delete {
            background-color: #e57373;
            border-color: #a00000;
        }

        .admin-delete:hover {
            background-color: crimson;
            border-color: crimson;
            color: white;
        }

        .success-message {
            background-color: #b8e6b8;
            border: 2px solid #4c9a4c;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 7px;
        }

        .error-message {
            background-color: #f2b8b8;
            border: 2px solid #a00000;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 7px;
        }

        .category-list {
            margin-top: 20px;
        }

        .category-item {
            padding: 8px 0;
            border-bottom: 1px solid #aaa;
        }

    </style>

</head>

<body>

    <jsp:include page="header.jsp" />


    <div class="admin-container">


        <div class="admin-title">

            Панель администратора

        </div>


        <!-- СООБЩЕНИЯ -->

        <c:if test="${not empty success}">

            <div class="success-message">

                ${success}

            </div>

        </c:if>


        <c:if test="${not empty error}">

            <div class="error-message">

                ${error}

            </div>

        </c:if>


        <!-- ================================ -->
        <!-- СОЗДАНИЕ КАТЕГОРИИ -->
        <!-- ================================ -->

        <div class="admin-section">

            <h2>

                Создать категорию

            </h2>


            <form class="admin-form"
                  action="${pageContext.request.contextPath}/admin/categories/create"
                  method="post">


                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}">


                <input type="text"
                       name="name"
                       placeholder="Название категории"
                       maxlength="100"
                       required>


                <select name="parentId">

                    <option value="">

                        Без родительской категории

                    </option>


                    <c:forEach var="category"
                               items="${categories}">

                        <option value="${category.id}">

                            ${category.name}

                        </option>

                    </c:forEach>

                </select>


                <button class="buttonstyle"
                        type="submit">

                    Создать категорию

                </button>

            </form>


            <!-- Список категорий -->

            <div class="category-list">

                <h3>

                    Существующие категории

                </h3>


                <c:choose>

                    <c:when test="${not empty categories}">

                        <c:forEach var="category"
                                   items="${categories}">

                            <div class="category-item">

                                <strong>

                                    ${category.name}

                                </strong>


                                <c:if test="${not empty category.parentName}">

                                    —

                                    Родитель:

                                    ${category.parentName}

                                </c:if>

                            </div>

                        </c:forEach>

                    </c:when>


                    <c:otherwise>

                        <p>

                            Категорий пока нет.

                        </p>

                    </c:otherwise>

                </c:choose>

            </div>

        </div>


        <!-- ================================ -->
        <!-- ВСЕ ОБЪЯВЛЕНИЯ -->
        <!-- ================================ -->

        <div class="admin-section">

            <h2>

                Все объявления

            </h2>


            <p>

                Всего объявлений:

                <strong>

                    ${advertisements.size()}

                </strong>

            </p>


            <c:choose>


                <c:when test="${not empty advertisements}">


                    <div class="admin-ads">


                        <c:forEach var="advertisement"
                                   items="${advertisements}">


                            <div class="admin-ad">


                                <!-- ФОТО -->

                                <c:choose>

                                    <c:when test="${not empty advertisement.photos}">

                                        <img class="admin-ad-photo"
                                             src="${pageContext.request.contextPath}/uploads/${advertisement.photos[0].fileName}"
                                             alt="${advertisement.title}">

                                    </c:when>


                                    <c:otherwise>

                                        <div class="admin-no-photo">

                                            Нет фотографии

                                        </div>

                                    </c:otherwise>

                                </c:choose>


                                <!-- НАЗВАНИЕ -->

                                <div class="admin-ad-title">

                                    ${advertisement.title}

                                </div>


                                <!-- ЦЕНА -->

                                <div class="admin-ad-info">

                                    <strong>

                                        Цена:

                                    </strong>

                                    ${advertisement.price} ₽

                                </div>


                                <!-- ПРОДАВЕЦ -->

                                <div class="admin-ad-info">

                                    <strong>

                                        Продавец:

                                    </strong>

                                    ${advertisement.sellerName}

                                </div>


                                <!-- ID ПРОДАВЦА -->

                                <div class="admin-ad-info">

                                    <strong>

                                        ID продавца:

                                    </strong>

                                    ${advertisement.sellerId}

                                </div>


                                <!-- КАТЕГОРИЯ -->

                                <div class="admin-ad-info">

                                    <strong>

                                        Категория:

                                    </strong>

                                    ${advertisement.categoryName}

                                </div>


                                <!-- СТАТУС -->

                                <div class="admin-ad-info">

                                    <strong>

                                        Статус:

                                    </strong>


                                    <c:choose>

                                        <c:when test="${advertisement.status == 'ACTIVE'}">

                                            Активно

                                        </c:when>

                                        <c:when test="${advertisement.status == 'SOLD'}">

                                            Продано

                                        </c:when>

                                        <c:when test="${advertisement.status == 'ARCHIVED'}">

                                            В архиве

                                        </c:when>

                                        <c:otherwise>

                                            ${advertisement.status}

                                        </c:otherwise>

                                    </c:choose>

                                </div>


                                <!-- ДАТА -->

                                <div class="admin-ad-info">

                                    <strong>

                                        Создано:

                                    </strong>

                                    ${advertisement.createdAt}

                                </div>


                                <!-- КНОПКИ -->

                                <div class="admin-actions">


                                    <!-- Просмотр -->

                                    <a class="buttonstyle"
                                       href="${pageContext.request.contextPath}/ads/${advertisement.id}">

                                        Просмотреть

                                    </a>


                                    <!-- Редактирование -->

                                    <a class="buttonstyle"
                                       href="${pageContext.request.contextPath}/admin/ads/${advertisement.id}/edit">

                                        Редактировать

                                    </a>


                                    <!-- Удаление -->

                                    <form action="${pageContext.request.contextPath}/admin/ads/${advertisement.id}/delete"
                                          method="post"
                                          onsubmit="return confirm('Вы действительно хотите удалить это объявление?');">


                                        <input type="hidden"
                                               name="${_csrf.parameterName}"
                                               value="${_csrf.token}">


                                        <button type="submit"
                                                class="buttonstyle admin-delete">

                                            Удалить

                                        </button>


                                    </form>


                                </div>


                            </div>


                        </c:forEach>


                    </div>


                </c:when>


                <c:otherwise>

                    <p>

                        Объявлений пока нет.

                    </p>

                </c:otherwise>


            </c:choose>


        </div>


    </div>

</body>

</html>
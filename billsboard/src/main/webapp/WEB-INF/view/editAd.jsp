<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="ru">

<head>

    <meta charset="UTF-8">

    <title>Редактирование объявления — Bill's Board</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body>

<header>

    <div class="header-inner">

        <a href="${pageContext.request.contextPath}/"
           class="logo">
            Bill's Board
        </a>

        <nav>

            <a href="${pageContext.request.contextPath}/">
                Главная
            </a>

            <a href="${pageContext.request.contextPath}/chat">
                Сообщения
            </a>

            <a href="${pageContext.request.contextPath}/profile">
                Профиль
            </a>

            <a href="${pageContext.request.contextPath}/about">
                О нас
            </a>

        </nav>

    </div>

</header>


<main class="container">

    <h1>Редактирование объявления</h1>


    <c:if test="${not empty error}">

        <div class="error-message">
            ${error}
        </div>

    </c:if>


    <form class="ad-form"
          action="${pageContext.request.contextPath}/ads/${ad.id}/edit"
          method="post"
          enctype="multipart/form-data">

        <input type="hidden"
               name="${_csrf.parameterName}"
               value="${_csrf.token}">


        <!-- Название -->

        <div class="form-group">

            <label for="title">
                Название
            </label>

            <input type="text"
                   id="title"
                   name="title"
                   maxlength="150"
                   value="${ad.title}"
                   required>

        </div>


        <!-- Описание -->

        <div class="form-group">

            <label for="description">
                Описание
            </label>

            <textarea id="description"
                      name="description"
                      maxlength="4000"
                      rows="8">${ad.description}</textarea>

        </div>


        <!-- Цена -->

        <div class="form-group">

            <label for="price">
                Цена
            </label>

            <input type="number"
                   id="price"
                   name="price"
                   min="0"
                   step="0.01"
                   value="${ad.price}"
                   required>

        </div>


        <!-- Категория -->

        <div class="form-group">

            <label for="categoryId">
                Категория
            </label>

            <select id="categoryId"
                    name="categoryId"
                    required>

                <c:forEach var="category"
                           items="${categories}">

                    <option value="${category.id}"
                        <c:if test="${category.id == ad.category.id}">
                            selected
                        </c:if>>
                        ${category.name}
                    </option>

                </c:forEach>

            </select>

        </div>


        <!-- Статус -->

        <div class="form-group">

            <label for="status">
                Статус объявления
            </label>

            <select id="status"
                    name="status"
                    required>

                <c:forEach var="status"
                           items="${statuses}">

                    <option value="${status}"
                        <c:if test="${status == ad.status}">
                            selected
                        </c:if>>
                        <c:choose>

                            <c:when test="${status == 'ACTIVE'}">
                                Активно
                            </c:when>

                            <c:when test="${status == 'SOLD'}">
                                Продано
                            </c:when>

                            <c:when test="${status == 'ARCHIVED'}">
                                В архиве
                            </c:when>

                            <c:otherwise>
                                ${status}
                            </c:otherwise>

                        </c:choose>
                    </option>

                </c:forEach>

            </select>

        </div>


        <!-- Существующие фотографии -->

        <div class="form-group">

            <label>
                Текущие фотографии
            </label>

            <c:choose>

                <c:when test="${not empty ad.photos}">

                    <div class="photo-grid">

                        <c:forEach var="photo"
                                   items="${ad.photos}">

                            <div class="photo-item">

                                <img src="${pageContext.request.contextPath}/uploads/${photo.fileName}"
                                     alt="Фото объявления">

                            </div>

                        </c:forEach>

                    </div>

                </c:when>

                <c:otherwise>

                    <p>
                        У объявления пока нет фотографий.
                    </p>

                </c:otherwise>

            </c:choose>

        </div>


        <!-- Добавление новых фотографий -->

        <div class="form-group">

            <label for="photos">
                Добавить фотографии
            </label>

            <input type="file"
                   id="photos"
                   name="photos"
                   accept="image/*"
                   multiple>

            <small>
                Можно выбрать несколько изображений.
            </small>

        </div>


        <!-- Кнопки -->

        <div class="form-actions">

            <button type="submit"
                    class="buttonstyle">
                Сохранить изменения
            </button>

            <a href="${pageContext.request.contextPath}/ads/${ad.id}"
               class="buttonstyle">
                Отмена
            </a>

        </div>

    </form>

</main>

</body>

</html>
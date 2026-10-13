<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>

<html lang="ru">

<head>

    <meta charset="UTF-8">

    <title>${advertisement.title}</title>

    <meta name="viewport"
          content="width=device-width">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

</head>

<body>

    <jsp:include page="header.jsp" />

    <hr align="center"
        width="100%"
        size="4"
        color="black"
        style="margin-top:30px;">


    <div style="
        width:90%;
        max-width:1100px;
        margin:40px auto;
    ">


        <!-- НАЗАД -->

        <p>

            <a href="${pageContext.request.contextPath}/">

                ← Вернуться к объявлениям

            </a>

        </p>


        <!-- ЗАГОЛОВОК -->

        <h1>

            ${advertisement.title}

        </h1>


        <!-- ФОТОГРАФИИ -->

        <div style="
            display:flex;
            flex-wrap:wrap;
            gap:20px;
            margin-top:30px;
        ">

            <c:if test="${not empty advertisement.photos}">

                <c:forEach items="${advertisement.photos}"
                           var="photo">

                    <div style="
                        width:350px;
                        height:280px;
                        overflow:hidden;
                        background:#eeeeee;
                    ">

                        <img
                            src="${pageContext.request.contextPath}/uploads/${photo.fileName}"
                            alt="${advertisement.title}"
                            style="
                                width:100%;
                                height:100%;
                                object-fit:cover;
                            ">

                    </div>

                </c:forEach>

            </c:if>


            <c:if test="${empty advertisement.photos}">

                <div style="
                    width:350px;
                    height:280px;
                    background:#eeeeee;
                    display:flex;
                    align-items:center;
                    justify-content:center;
                ">

                    <span>

                        Нет фотографии

                    </span>

                </div>

            </c:if>

        </div>


        <!-- ЦЕНА -->

        <div style="
            margin-top:30px;
            font-size:28px;
            font-weight:bold;
        ">

            ${advertisement.price} ₽

        </div>


        <!-- ОПИСАНИЕ -->

        <div style="
            margin-top:30px;
        ">

            <h2>

                Описание

            </h2>

            <c:choose>

                <c:when test="${not empty advertisement.description}">

                    <p style="
                        font-size:18px;
                        line-height:1.6;
                    ">

                        ${advertisement.description}

                    </p>

                </c:when>

                <c:otherwise>

                    <p>

                        Описание отсутствует.

                    </p>

                </c:otherwise>

            </c:choose>

        </div>


        <!-- ИНФОРМАЦИЯ -->

        <div style="
            margin-top:30px;
            padding-top:20px;
            border-top:1px solid #cccccc;
        ">

            <h2>

                Информация

            </h2>

            <p>

                <strong>Категория:</strong>

                ${advertisement.categoryName}

            </p>

            <p>

                <strong>Продавец:</strong>

                ${advertisement.sellerName}

            </p>

            <p>

                <strong>Статус:</strong>

                ${advertisement.status}

            </p>

            <p>

                <strong>Дата публикации:</strong>

                ${advertisement.createdAt}

            </p>

        </div>


        <!-- ПЕРВОЕ СООБЩЕНИЕ -->

        <div style="
            margin-top:40px;
            padding-top:25px;
            border-top:1px solid #cccccc;
        ">

            <h2>

                Написать продавцу

            </h2>


            <c:choose>


                <%-- АВТОРИЗОВАННЫЙ ПОЛЬЗОВАТЕЛЬ --%>

                <c:when test="${not empty pageContext.request.userPrincipal}">

                    <form
                        action="${pageContext.request.contextPath}/ads/${advertisement.id}/message"
                        method="post"
                        style="
                            margin-top:20px;
                            max-width:800px;
                        ">


                        <!-- CSRF -->

                        <input
                            type="hidden"
                            name="${_csrf.parameterName}"
                            value="${_csrf.token}">


                        <!-- ПОЛЕ СООБЩЕНИЯ -->

                        <textarea
                            name="text"
                            rows="7"
                            maxlength="2000"
                            required
                            placeholder="Напишите сообщение продавцу..."
                            style="
                                width:100%;
                                box-sizing:border-box;
                                padding:15px;
                                font-size:16px;
                                line-height:1.5;
                                resize:vertical;
                                border:1px solid #cccccc;
                                border-radius:6px;
                            "></textarea>


                        <!-- КНОПКА -->

                        <div style="
                            margin-top:15px;
                        ">

                            <button
                                type="submit"
                                class="buttonstyle">

                                Отправить сообщение

                            </button>

                        </div>

                    </form>

                </c:when>


                <%-- НЕАВТОРИЗОВАННЫЙ ПОЛЬЗОВАТЕЛЬ --%>

                <c:otherwise>

                    <p style="
                        font-size:17px;
                        line-height:1.5;
                    ">

                        Чтобы написать продавцу,
                        необходимо войти в аккаунт.

                    </p>

                    <a
                        href="${pageContext.request.contextPath}/auth/login"
                        class="buttonstyle">

                        Войти, чтобы написать продавцу

                    </a>

                </c:otherwise>

            </c:choose>

        </div>


        <!-- КНОПКА НАЗАД -->

        <div style="
            margin-top:30px;
        ">

            <a
                href="${pageContext.request.contextPath}/"
                class="buttonstyle">

                Вернуться к объявлениям

            </a>

        </div>

    </div>

</body>

</html>
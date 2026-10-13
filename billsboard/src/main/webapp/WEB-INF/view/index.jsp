<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html>

<head>

    <title>Объявления</title>

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

    <div style="display: flex;">

        <!-- ФИЛЬТРЫ -->

        <div style="
                display:inline-block;
                background-color:#e7e7e7;
                margin-top:5%;
                margin-bottom:5%;
                padding:20px;
             ">

            <form action="${pageContext.request.contextPath}/"
                  method="get">

                <!-- СОРТИРОВКА -->

                <h3>Сортировка</h3>

                <select name="sort">

                    <option value=""
                            <c:if test="${empty param.sort}">
                                selected
                            </c:if>>
                        Без сортировки
                    </option>

                    <option value="newest"
                            <c:if test="${param.sort == 'newest'}">
                                selected
                            </c:if>>
                        Новые сначала
                    </option>

                    <option value="oldest"
                            <c:if test="${param.sort == 'oldest'}">
                                selected
                            </c:if>>
                        Старые сначала
                    </option>

                    <option value="priceAsc"
                            <c:if test="${param.sort == 'priceAsc'}">
                                selected
                            </c:if>>
                        Сначала дешёвые
                    </option>

                    <option value="priceDesc"
                            <c:if test="${param.sort == 'priceDesc'}">
                                selected
                            </c:if>>
                        Сначала дорогие
                    </option>

                    <option value="titleAsc"
                            <c:if test="${param.sort == 'titleAsc'}">
                                selected
                            </c:if>>
                        По названию А–Я
                    </option>

                    <option value="titleDesc"
                            <c:if test="${param.sort == 'titleDesc'}">
                                selected
                            </c:if>>
                        По названию Я–А
                    </option>

                </select>


                <!-- КАТЕГОРИЯ -->

                <h3>Категория</h3>

                <select name="categoryId">

                    <option value=""
                            <c:if test="${empty param.categoryId}">
                                selected
                            </c:if>>
                        Все категории
                    </option>

                    <c:forEach items="${categories}"
                               var="category">

                        <option value="${category.id}"
                                <c:if test="${param.categoryId == category.id}">
                                    selected
                                </c:if>>

                            ${category.name}

                        </option>

                    </c:forEach>

                </select>


                <!-- СТАТУС -->

                <h3>Статус</h3>

                <p>

                    <label>

                        <input type="radio"
                               name="status"
                               value=""
                               <c:if test="${empty param.status}">
                                   checked
                               </c:if>>

                        Все

                    </label>

                </p>

                <p>

                    <label>

                        <input type="radio"
                               name="status"
                               value="ACTIVE"
                               <c:if test="${param.status == 'ACTIVE'}">
                                   checked
                               </c:if>>

                        Активные

                    </label>

                </p>

                <p>

                    <label>

                        <input type="radio"
                               name="status"
                               value="SOLD"
                               <c:if test="${param.status == 'SOLD'}">
                                   checked
                               </c:if>>

                        Проданные

                    </label>

                </p>

                <p>

                    <label>

                        <input type="radio"
                               name="status"
                               value="ARCHIVED"
                               <c:if test="${param.status == 'ARCHIVED'}">
                                   checked
                               </c:if>>

                        Архивные

                    </label>

                </p>


                <input class="buttonstyle"
                       type="submit"
                       value="Отфильтровать">

                <br>
                <br>

                <a class="buttonstyle"
                   href="${pageContext.request.contextPath}/">

                    Сбросить фильтры

                </a>

            </form>

        </div>


        <!-- ОБЪЯВЛЕНИЯ -->

        <div style="
                display:inline-block;
                width:100%;
                padding:20px;
             ">

            <div style="
                    display:flex;
                    flex-wrap:wrap;
                    gap:20px;
                 ">


                <c:choose>

                    <c:when test="${not empty advertisements}">


                        <c:forEach items="${advertisements}"
                                   var="advertisement">


                            <div class="advertisement">


                                <!-- ФОТОГРАФИЯ -->

                                <a href="${pageContext.request.contextPath}/ads/${advertisement.id}"
                                   style="text-decoration:none;">

                                    <c:if test="${not empty advertisement.photos}">

                                        <c:forEach items="${advertisement.photos}"
                                                   var="photo"
                                                   begin="0"
                                                   end="0">

                                            <div style="
                                                    width:250px;
                                                    height:200px;
                                                    margin-bottom:15px;
                                                    overflow:hidden;
                                                    background:#eeeeee;
                                                    display:flex;
                                                    align-items:center;
                                                    justify-content:center;
                                                 ">

                                                <img src="${pageContext.request.contextPath}/uploads/${photo.fileName}"
                                                     alt="${advertisement.title}"
                                                     style="
                                                        width:100%;
                                                        height:100%;
                                                        object-fit:cover;
                                                     ">

                                            </div>

                                        </c:forEach>

                                    </c:if>


                                    <!-- ЕСЛИ ФОТО НЕТ -->

                                    <c:if test="${empty advertisement.photos}">

                                        <div style="
                                                width:250px;
                                                height:200px;
                                                margin-bottom:15px;
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

                                </a>


                                <!-- НАЗВАНИЕ -->

                                <h3>

                                    <a href="${pageContext.request.contextPath}/ads/${advertisement.id}"
                                       style="color:inherit; text-decoration:none;">

                                        ${advertisement.title}

                                    </a>

                                </h3>


                                <!-- ЦЕНА -->

                                <p>

                                    Цена:

                                    ${advertisement.price}

                                    ₽

                                </p>


                                <!-- ПРОДАВЕЦ -->

                                <p>

                                    Продавец:

                                    ${advertisement.sellerName}

                                </p>


                                <!-- КАТЕГОРИЯ -->

                                <p>

                                    Категория:

                                    ${advertisement.categoryName}

                                </p>


                                <!-- СТАТУС -->

                                <p>

                                    Статус:

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

                                </p>


                                <!-- ДАТА -->

                                <p>

                                    ${advertisement.createdAt}

                                </p>


                                <!-- ПРОСМОТР -->

                                <form action="${pageContext.request.contextPath}/ads/${advertisement.id}"
                                      method="get">

                                    <input class="buttonstyle"
                                           type="submit"
                                           value="Просмотреть">

                                </form>


                            </div>


                        </c:forEach>


                    </c:when>


                    <c:otherwise>

                        <div style="
                                padding:30px;
                                font-size:20px;
                             ">

                            По заданным параметрам объявления не найдены.

                        </div>

                    </c:otherwise>

                </c:choose>


            </div>

        </div>

    </div>

</body>

</html>
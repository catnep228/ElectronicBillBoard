<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="UTF-8">

    <title>Чат</title>

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
    style="margin-top:30px;">

<div style="
    width:90%;
    max-width:1000px;
    margin:40px auto;
">

    <h1>Чаты</h1>

    <p style="
        font-size:18px;
        margin-top:10px;
        margin-bottom:30px;
    ">
        Здесь отображаются ваши переписки с продавцами.
    </p>


    <c:choose>

        <%-- Если чаты есть --%>
        <c:when test="${not empty chats}">

            <c:forEach items="${chats}" var="chat">

                <a href="${pageContext.request.contextPath}/chat/${chat.adId}"
                   style="
                       display:block;
                       text-decoration:none;
                       color:inherit;
                       margin-bottom:20px;
                   ">

                    <div style="
                        padding:25px;
                        border:1px solid #cccccc;
                        border-radius:8px;
                        background:#fafafa;
                        transition:background 0.2s;
                    ">

                        <%-- Продавец --%>
                        <h2 style="
                            margin-top:0;
                            margin-bottom:10px;
                        ">
                            ${chat.userName}
                        </h2>


                        <%-- Последнее сообщение --%>
                        <c:if test="${not empty chat.lastMessage}">

                            <p style="
                                margin:10px 0;
                                font-size:16px;
                                color:#333333;
                            ">
                                ${chat.lastMessage}
                            </p>

                        </c:if>


                        <%-- Время последнего сообщения --%>
                        <c:if test="${not empty chat.lastMessageTime}">

                            <p style="
                                margin:5px 0 0 0;
                                font-size:14px;
                                color:#777777;
                            ">
                                ${chat.lastMessageTime}
                            </p>

                        </c:if>


                        <div style="
                            margin-top:15px;
                        ">

                            <span class="buttonstyle">
                                Открыть чат
                            </span>

                        </div>

                    </div>

                </a>

            </c:forEach>

        </c:when>


        <%-- Если сообщений нет --%>
        <c:otherwise>

            <div style="
                margin-top:30px;
                padding:40px;
                border:1px solid #cccccc;
                border-radius:8px;
                text-align:center;
                background:#fafafa;
            ">

                <h2>
                    У вас пока нет чатов
                </h2>

                <p style="
                    font-size:17px;
                    margin-top:15px;
                ">
                    Откройте объявление и напишите продавцу,
                    чтобы начать переписку.
                </p>

                <div style="
                    margin-top:25px;
                ">

                    <a href="${pageContext.request.contextPath}/"
                       class="buttonstyle">
                        Перейти на доску
                    </a>

                </div>

            </div>

        </c:otherwise>

    </c:choose>


    <div style="
        margin-top:40px;
    ">

        <a href="${pageContext.request.contextPath}/"
           class="buttonstyle">
            Вернуться на доску
        </a>

    </div>

</div>

</body>
</html>
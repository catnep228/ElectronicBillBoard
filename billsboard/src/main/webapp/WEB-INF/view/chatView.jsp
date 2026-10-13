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
        max-width:900px;
        margin:40px auto;
    ">

    <div style="
            display:flex;
            justify-content:space-between;
            align-items:center;
            margin-bottom:25px;
        ">

        <div>

            <h1 style="margin:0;">
                ${otherUserName}
            </h1>

            <p style="
                    margin-top:8px;
                    color:#777777;
                    font-size:15px;
                ">
                Объявление: ${ad.title}
            </p>

        </div>

        <a href="${pageContext.request.contextPath}/chat"
           class="buttonstyle">
            Все чаты
        </a>

    </div>


    <div style="
            border:1px solid #cccccc;
            border-radius:8px;
            padding:25px;
            background:#fafafa;
            min-height:300px;
        ">

        <c:choose>

            <c:when test="${not empty messages}">

                <c:forEach items="${messages}"
                           var="message">

                    <div style="
                            display:flex;
                            margin-bottom:20px;
                            ${message.sender.id == currentUserId
                                ? 'justify-content:flex-end;'
                                : 'justify-content:flex-start;'}
                        ">

                        <div style="
                                max-width:70%;
                                padding:15px;
                                border-radius:10px;
                                ${message.sender.id == currentUserId
                                    ? 'background:#dff0ff;'
                                    : 'background:#eeeeee;'}
                            ">

                            <div style="
                                    font-size:16px;
                                    line-height:1.5;
                                    word-wrap:break-word;
                                ">
                                ${message.text}
                            </div>

                            <div style="
                                    margin-top:8px;
                                    font-size:12px;
                                    color:#777777;
                                ">
                                ${message.sentAt}
                            </div>

                        </div>

                    </div>

                </c:forEach>

            </c:when>

            <c:otherwise>

                <div style="
                        text-align:center;
                        padding:50px 20px;
                    ">

                    <h2>
                        Сообщений пока нет
                    </h2>

                </div>

            </c:otherwise>

        </c:choose>

    </div>


    <form action="${pageContext.request.contextPath}/chat/${ad.id}/message"
          method="post"
          style="
              margin-top:25px;
          ">

        <input type="hidden"
               name="${_csrf.parameterName}"
               value="${_csrf.token}">


        <textarea name="text"
                  rows="5"
                  maxlength="2000"
                  required
                  placeholder="Напишите сообщение..."
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


        <div style="
                margin-top:15px;
                display:flex;
                gap:15px;
            ">

            <button type="submit"
                    class="buttonstyle">
                Отправить
            </button>

            <a href="${pageContext.request.contextPath}/ads/${ad.id}"
               class="buttonstyle">
                Объявление
            </a>

        </div>

    </form>

</div>

</body>
</html>
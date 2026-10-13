<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>О нас — Bill's Board</title>

    <style>
        .about-container {
            max-width: 800px;
            margin: 40px auto;
            padding: 30px;
            text-align: center;
        }

        .about-container h2 {
            margin-bottom: 20px;
        }

        .about-container p {
            line-height: 1.6;
            margin-bottom: 15px;
        }
    </style>
</head>

<body>

<%@ include file="header.jsp" %>

<div class="about-container">

    <h2>О сайте</h2>

    <p>
        <strong>Bill's Board</strong> — это информационная страница
        электронной доски объявлений.
    </p>

    <p>
        Сайт предназначен для удобного размещения, поиска и просмотра
        объявлений.
    </p>

</div>

</body>
</html>
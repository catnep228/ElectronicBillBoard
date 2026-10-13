<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags" %>


<div class="header">

    <div class="logo">

        <h1>Bill's Board</h1>

    </div>


    <nav class="nav">


        <!-- ДОСКА -->

        <form class="navAction"
              action="${pageContext.request.contextPath}/"
              method="get">

            <input
                class="navButton"
                type="submit"
                value="Доска">

        </form>


        <!-- СОЗДАТЬ ОБЪЯВЛЕНИЕ -->

        <form class="navAction"
              action="${pageContext.request.contextPath}/FormForAd"
              method="get">

            <input
                class="navButton"
                type="submit"
                value="Создать объявление">

        </form>


        <!-- О НАС -->

        <form class="navAction"
              action="${pageContext.request.contextPath}/about"
              method="get">

            <input
                class="navButton"
                type="submit"
                value="О нас">

        </form>


        <!-- ЧАТ -->

        <form class="navAction"
              action="${pageContext.request.contextPath}/chat"
              method="get">

            <input
                class="navButton"
                type="submit"
                value="Чат">

        </form>


        <!-- ПРОФИЛЬ -->

        <form class="navAction"
              action="${pageContext.request.contextPath}/profile"
              method="get">

            <input
                class="navButton"
                type="submit"
                value="Вы">

        </form>


        <!-- ПАНЕЛЬ АДМИНИСТРАТОРА -->

        <sec:authorize access="hasRole('ADMIN')">

            <form class="navAction"
                  action="${pageContext.request.contextPath}/admin"
                  method="get">

                <input
                    class="navButton"
                    type="submit"
                    value="Админ">

            </form>

        </sec:authorize>


        <!-- АВТОРИЗОВАННЫЙ ПОЛЬЗОВАТЕЛЬ -->

        <sec:authorize access="isAuthenticated()">

            <span class="navUser">

                <sec:authentication property="name"/>

            </span>


            <!-- ВЫЙТИ -->

            <form class="navAction"
                  action="${pageContext.request.contextPath}/auth/logout"
                  method="post">

                <input
                    type="hidden"
                    name="${_csrf.parameterName}"
                    value="${_csrf.token}"/>

                <input
                    class="navButton"
                    type="submit"
                    value="Выйти">

            </form>

        </sec:authorize>


        <!-- НЕАВТОРИЗОВАННЫЙ ПОЛЬЗОВАТЕЛЬ -->

        <sec:authorize access="isAnonymous()">

            <!-- ВОЙТИ -->

            <form class="navAction"
                  action="${pageContext.request.contextPath}/auth/login"
                  method="get">

                <input
                    class="navButton"
                    type="submit"
                    value="Войти">

            </form>


            <!-- РЕГИСТРАЦИЯ -->

            <form class="navAction"
                  action="${pageContext.request.contextPath}/auth/register"
                  method="get">

                <input
                    class="navButton"
                    type="submit"
                    value="Регистрация">

            </form>

        </sec:authorize>


    </nav>

</div>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
</head>
<body>
<h2>Регистрация нового пользователя</h2>

<#if error??>
    <p style="color: red;">Ошибка при регистрации. Возможно, имя пользователя уже занято.</p>
</#if>

<form action="/register" method="post">
    <div>
        <label for="username">Логин:</label>
        <input type="text" id="username" name="username" required/>
    </div>
    <div>
        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required/>
    </div>

    <#if _csrf??>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </#if>

    <button type="submit">Зарегистрироваться</button>
</form>

<p>Уже есть аккаунт? <a href="/login">Войти</a></p>
</body>
</html>
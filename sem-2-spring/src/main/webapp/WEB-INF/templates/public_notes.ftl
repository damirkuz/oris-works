<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Публичные заметки</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: #f7f7f7;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            gap: 16px;
            flex-wrap: wrap;
        }

        .btn {
            display: inline-block;
            padding: 10px 14px;
            background: #2563eb;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            border: none;
            cursor: pointer;
        }

        .search-form {
            display: flex;
            gap: 10px;
            margin-bottom: 24px;
        }

        .search-form input[type="text"] {
            flex: 1;
            max-width: 420px;
            padding: 10px 12px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
        }

        .notes-grid {
            display: grid;
            gap: 16px;
        }

        .note-card {
            background: white;
            border-radius: 12px;
            padding: 18px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }

        .note-title {
            margin: 0 0 10px 0;
            font-size: 22px;
        }

        .note-meta {
            color: #6b7280;
            font-size: 14px;
            margin-bottom: 10px;
        }

        .note-content {
            white-space: pre-wrap;
        }

        .empty-block {
            background: white;
            padding: 24px;
            border-radius: 12px;
            color: #4b5563;
        }
    </style>
</head>
<body>

<div class="page-header">
    <div>
        <h1>Публичные заметки</h1>
        <p>Всего: ${(notes?size)!0}</p>
    </div>

    <a class="btn" href="/notes">К моим заметкам</a>
</div>

<form class="search-form" method="get" action="/notes/public">
    <input type="text" name="q" value="${query!''}" placeholder="Поиск по заголовку или тексту">
    <button class="btn" type="submit">Найти</button>
</form>

<#if notes?? && notes?has_content>
    <div class="notes-grid">
        <#list notes as note>
            <div class="note-card">
                <h2 class="note-title">${note.title!'Без названия'}</h2>

                <div class="note-meta">
                    <div>Автор: ${(note.author.username)!'Неизвестно'}</div>
                    <div>Создано: ${note.createdAt!''}</div>
                </div>

                <div class="note-content">${note.content!''}</div>
            </div>
        </#list>
    </div>
<#else>
    <div class="empty-block">
        Публичных заметок пока нет.
    </div>
</#if>

</body>
</html>

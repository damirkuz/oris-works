<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Мои заметки</title>
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

        .btn-danger {
            background: #dc2626;
        }

        .btn-secondary {
            background: #4b5563;
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
            margin-bottom: 14px;
        }

        .note-actions {
            display: flex;
            gap: 10px;
            margin-top: 10px;
            flex-wrap: wrap;
        }

        .empty-block {
            background: white;
            padding: 24px;
            border-radius: 12px;
            color: #4b5563;
        }

        form {
            display: inline;
        }
    </style>
</head>
<body>

<div class="page-header">
    <div>
        <h1>Мои заметки</h1>
        <p>Всего: ${(notes?size)!0}</p>
    </div>

    <div>
        <a class="btn btn-secondary" href="/notes/public">Публичные заметки</a>
        <a class="btn" href="/notes/create">Создать заметку</a>
    </div>
</div>

<#if notes?? && notes?has_content>
    <div class="notes-grid">
        <#list notes as note>
            <div class="note-card">
                <h2 class="note-title">${note.title!'Без названия'}</h2>

                <div class="note-meta">
                    <div>ID: ${note.id!''}</div>
                    <div>Создано: ${note.createdAt!''}</div>
                    <div>Публичная: <#if (note.published!false)>Да<#else>Нет</#if></div>
                    <div>Автор: ${(note.author.username)!'Неизвестно'}</div>
                </div>

                <div class="note-content">${note.content!''}</div>

                <div class="note-actions">
                    <a class="btn" href="/notes/${note.id}/edit">Редактировать</a>

                    <form method="post" action="/notes/${note.id}/delete">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button class="btn btn-danger" type="submit">Удалить</button>
                    </form>
                </div>
            </div>
        </#list>
    </div>
<#else>
    <div class="empty-block">
        У тебя пока нет заметок.
    </div>
</#if>

</body>
</html>

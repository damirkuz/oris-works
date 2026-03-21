<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${formTitle!'Форма заметки'}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: #f7f7f7;
        }

        .container {
            max-width: 760px;
            margin: 0 auto;
            background: white;
            padding: 28px;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }

        .field {
            margin-bottom: 18px;
        }

        label {
            display: block;
            font-weight: bold;
            margin-bottom: 8px;
        }

        input[type="text"],
        textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            box-sizing: border-box;
            font-size: 15px;
        }

        textarea {
            min-height: 180px;
            resize: vertical;
        }

        .checkbox-row {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .checkbox-row label {
            margin-bottom: 0;
        }

        .actions {
            display: flex;
            gap: 12px;
            margin-top: 24px;
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

        .btn-secondary {
            background: #4b5563;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>${formTitle!'Форма заметки'}</h1>

    <form method="post" action="${actionUrl!'/notes/create'}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <#if note?? && note.id??>
            <input type="hidden" name="id" value="${note.id}">
        </#if>

        <div class="field">
            <label for="title">Заголовок</label>
            <input
                    id="title"
                    type="text"
                    name="title"
                    value="${(note.title)!''}"
                    required
            >
        </div>

        <div class="field">
            <label for="content">Содержимое</label>
            <textarea
                    id="content"
                    name="content"
                    required
            >${(note.content)!''}</textarea>
        </div>

        <div class="field checkbox-row">
            <input type="hidden" name="published" value="false"/>

            <input
                    id="published"
                    type="checkbox"
                    name="published"
                    value="true"
                    <#if (note.published!false)>checked</#if>
            >
            <label for="published">Сделать заметку публичной</label>
        </div>


        <div class="actions">
            <button class="btn" type="submit">Сохранить</button>
            <a class="btn btn-secondary" href="/notes">Назад</a>
        </div>
    </form>
</div>

</body>
</html>

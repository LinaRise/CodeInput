## Стек:
 - Kotlin
 - Hilt - да, для этого проекта избыточно, на будущее)
 - RxJava
 - Retrofit
 - SMS Retriever API

## Реализовано:
- Экран подписания
- Запросы
- Таймер в 60 секунд
- Возможность задать возможное количество вводимых цифр кода (не ограничено)
- Автоподстановка кода из SMS с использованием API SMS Retriver
- Тестовый локальный [сервер](https://github.com/LinaRise/OtpTestServer.git). При использовани нужно заменить <em><strong>BASE_URL</strong></em> в приложении
- тесты для custom view

Проверочное сообщение, которое отправляется на устройство пользовател должно:
 - Быть не длиннее 140 байт.
 - Содержать одноразовый код, который клиент отправит обратно на сервер для завершения процесса проверки.
 - Добавить 11-значную хеш-строку, которая идентифицирует ваше приложение. Получить хеш-строку своего приложения с помощью класса AppSignatureHelper. Нужно обязательно удалить его из приложения после получения хэш-строки и/или релиза приложения
 - Подбробнее [тут](http://daringfireball.net](https://developers.google.com/identity/sms-retriever/overview?hl=ru)https://developers.google.com/identity/sms-retriever/overview?hl=ru)


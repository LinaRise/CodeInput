#Стек:
 - Kotlin
 - Hilt - да, для этого проекта избыточно, на будущее)
 - RxJava
 - Retrofit
 - SMS Retriever API

Реализовано:
- Экран подписания
- Запросы
- Таймер в 60 секунд
- Возможность задать возможное количество вводимых цифр кода (не ограничено)
- Автоподастновка кода из SMS с использованием API SMS Retriver
- Тестовый локальный сервер [https://github.com/LinaRise/OtpServer]. При использовани нужно заменить <em><strong>BASE_URL</strong></em> в приложении

Проверочное сообщение, которое отправляется на устройство пользовател должно:
 - Быть не длиннее 140 байт.
 - Содержать одноразовый код, который клиент отправляет обратно на ваш сервер для завершения процесса проверки.
 - Добавить 11-значную хеш-строку, которая идентифицирует ваше приложение (см. Вычисление хеш-строки вашего приложения ). Получить хеш-строку своего приложения с помощью класса AppSignatureHelper. Нужно обязательно удалить его из своего приложения после получения хэш-строки и/или релиза приложения
 - Подбробнее [тут](https://github.com/pandao/editor.md "Heading link](http://daringfireball.net](https://developers.google.com/identity/sms-retriever/overview?hl=ru)https://developers.google.com/identity/sms-retriever/overview?hl=ru)")


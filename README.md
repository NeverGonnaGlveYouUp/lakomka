## Локальный запуск

### Запуск Хранилища S3 и Postgersql

1. Если первый запуск, выполнить создание каталогов для данных S3 И DB в корне проекта

    `mkdir -p docker/db/data`

    `mkdir -p docker/s3/data`

    `mkdir -p docker/s3/config`

    Каталоги data уже добавлены в gitignore

2. Чтобы запустить:

    Оба сервиса `docker-compose up -d` 

    S3 сервис `docker-compose up minio -d` 

    postgres сервис `docker-compose up postgres -d`

3. Чтобы остановить все сервисы `docker-compose down`

4. Bucket указанный в переменной S3_BUCKET, будет создан автоматически при запуске приложения, если еще не существовал. 

5. Web интерфейс S3: http://127.0.0.1:9001/

### Запуск бэкэнда в IDE (idea, openide)

1. В конфигурации запуска LakomkaShopApplication указать файл переменных окружения `.run/dev.env` и нужный спринг-профиль.

2. Если спринг-профиль не указан - режим продакшена, на БД выполняются новые миграции, тестданные не создаются. 
   Запросы sql не отображаются в логе.

3. Запуск приложения со спринг-профилем dev:
   - очищается БД
   - выполняется общая миграция БД через liquibase
   - выполняется миграция БД контекста dev (файл db/changelog/testdata.xml)
   - выполняется бин генерации тест-данных DatabaseInitializer
   
4. Запуск приложения со спринг-профилем jpa-dev (работа с БД без liquibase, для создания таблиц по entity, 
   для последующего создания changeset через liquibase diff):
   - очищается БД (при этом выживут таблицы liquibase: databasechangelog и databasechangeloglock)
   - выполняется миграция БД через jpa
   - выполняется миграция БД контекста dev (файл db/changelog/testdata.xml)
   - выполняется бин генерации тест-данных DatabaseInitializer
   - при останове приложения БД очищается, поэтому liquibase diff делать во время запущенного приложения

### Запуск фронта

1. Установка зависимостей:
   - установить node.js 22
   - в resources\static модуля api выполнить `npm install`, должна быть создана папка node_modules и package-lock.json
2. Запуск:
   - запуск в dev режиме: выполнить в resources\static `npm run watch`, должен быть создан bundle.js
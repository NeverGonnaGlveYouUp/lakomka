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

4. При первом запуске S3, зайти http://127.0.0.1:9001/ и создать bucket "lakomka". Креды смотри в docker-compose.yaml.

### Запуск бэкэнда в IDE (idea, openide)

1. В конфигурации запуска LakomkaShopApplication указать файл переменных окружения `.run/dev.env` и нужный спринг-профиль.

2. Запуск приложения со спринг-профилем dev:
   - очищается БД
   - выполняется общая миграция БД
   - выполняется миграция БД контекста dev (файл db/changelog/testdata.xml)
   - выполняется бин генерации тест-данных DatabaseInitializer
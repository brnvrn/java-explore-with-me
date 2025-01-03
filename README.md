# JAVA-EXPLORE-WITH-ME
Проект представляет собой приложение, которое позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них. С помощью этого приложения пользователи смогут легко планировать свое свободное время, узнавать о предстоящих мероприятиях и организовывать встречи с друзьями.

Пользователи могут предлагать различные события — от выставок до походов в кино — и собирать компанию для участия в этих мероприятиях. Приложение также решает проблему поиска информации о событиях, свободных друзьях и организации встреч.

Проект включает два основных сервиса:

Основной сервис:
API основного сервиса разделен на три части:

Публичная часть: доступна без регистрации любому пользователю сети. Пользователи могут искать события и фильтровать их по различным критериям. Список событий можно сортировать по количеству просмотров (запрашиваемым в сервисе статистики) или по датам событий. Возможность получения всех имеющихся категорий и подборок событий, которые составляют администраторы ресурса.

Закрытая часть: доступна только авторизованным пользователям, чтобы обеспечить безопасность и персонализацию. Авторизованные пользователи могут добавлять новые мероприятия, редактировать их и просматривать после добавления, могут подавать заявки на участие в интересующих мероприятиях. Создатель мероприятия подтверждает заявки, отправленные другими пользователями.

Административная часть: предназначена для администраторов сервиса, позволяющая управлять контентом и пользователями. Настройка добавления, изменения и удаления категорий для событий. Возможность добавлять, удалять и закреплять подборки мероприятий на главной странице. Модерация событий, размещённых пользователями, включая публикацию или отклонение.

Сервис статистики:
Хранит количество просмотров событий и позволяет делать различные выборки для анализа работы приложения, что поможет улучшить функциональность и пользовательский опыт.

Стек технологий: Java, Spring Boot, база данных PostgreSQL, написаны тесты Postman, Docker.

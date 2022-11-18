# java-kanban
## Это репозиторий проекта "Трекер задач".
Наше приложение **умеет**:
1. Создавать новую задачу, задача может быть трёх типов:
* обычные задачи - небольшие задачи не требующие деления на подзадачи;
* эпики - большие задачи, разделённые на несколько подзадач;
* подзадачи - небольшие задачи, входящие в эпик;
2. Сохранять задачи разных типов и их статус, возможны три статуса:
* NEW — задача только создана, но к её выполнению ещё не приступили;
* IN_PROGRESS — над задачей ведётся работа;
* DONE — задача выполнена;
3. Обновление статуса задачи.
4. Получение задачи по её индификатору.
5. Получение списка всех подзадач определённого эпика.
6. Получение списка всех задач.
7. Удаление задачи по её индификатору.
8. Удаление всех задач.

Приложение написано на Java. Пример кода:
```java
public class Practicum {
    public static void main(String[] args) {
    }
}
```
------
О том, как научиться создавать такие приложения, можно узнать в [Яндекс-Практикуме](https://practicum.yandex.ru/java-developer/ "Тут учат Java!")

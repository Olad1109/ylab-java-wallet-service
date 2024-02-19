## Этап №1 - Вход в разработку.
Приветствую всех, кто заглянул на данный проект. Суть проекта в создании электронного кошелька. Перед нами стоит задача: создать сервис, который управляет кредитными/дебетовыми транзакциями от имени пользователей.
### 1. Описание данной задачи
Денежный счет содержит текущий баланс пользователя. Баланс можно изменить, зарегистрировав транзакции на счете, либо дебетовые транзакции (удаление средств), либо кредитные транзакции (добавление средств). Создайте реализацию, которая соответствует описанным ниже требованиям и ограничениям.
### 2. Основные требования к её выполнению
- данные хранятся в памяти приложений
- приложение должно быть консольным (никаких spring-ов, взаимодействий с БД и тд, - только java-core и collections)
- регистрация пользователя
- авторизация пользователя
- Текущий баланс пользователя
- Дебет/снятие средств для каждого пользователя. Дебетовая транзакция будет успешной только в том случае, если на счету достаточно средств (баланс - сумма дебета >= 0).
- Вызывающая сторона предоставит идентификатор транзакции, который должен быть уникальным для всех транзакций. Если идентификатор транзакции не уникален, операция будет завершена ошибкой.
- Кредит на пользователя. Вызывающая сторона предоставит идентификатор транзакции, который должен быть уникальным для всех транзакций. Если идентификатор транзакции не уникален, операция должна завершиться ошибкой.
- Просмотр истории пополнения/снятия средств пользователя
- Аудит действий пользователя (авторизация, завершение работы, пополнения, снятия и тд)
### 3. Нефункциональные требования
- Использование docker и docker-compose
- Unit-тестирование

Ознакомившись с данной задачей, можно приступить к её решению, используя при этом свою любознательность, опыт, а так же источники знаний, которые находятся в интернете, например:
- Onion-архитектура: https://hashdork.com/ru/onion-architecture/, https://medium.com/expedia-group-tech/onion-architecture-deed8a554423
- Maven: https://maven.apache.org/guides/, https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
- Gradle: https://docs.gradle.org/current/userguide/userguide.html, https://docs.gradle.org/current/samples/sample_building_java_applications.html
- Junit5: https://junit.org/junit5/docs/current/user-guide/
- Mockito: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html, https://www.baeldung.com/mockito-series
- AssertJ: https://assertj.github.io/doc/, https://www.baeldung.com/introduction-to-assertj
- GitHub: https://github.com
- Java code conventions: https://www.magnumblog.space/java/translating-java-code-conventions

Используя вышеперечисленное, можно перейти к основному.
### Процесс сборки и запуска данного приложения
- Подготовка своей среды выполнения посредством установки Java Development Kit.
- Моделирование структуры проекта.
- Создание условий для запуска и отладки кода.
- Установка зависимостей и расширений, если об этом попросит среда разработки.
- Использование системы помощи, которая может быть Help Center-ом, внедрённым в среду разработки.

Приступив к сборке приложения, мы создаём директории: "docker", где будет размещаться Dockerfile, а так же "src". В папке "src" создаём путь к создаваемой нами директории "example" - main/java/com/. Так же создадим папку тест, что понадобится для будущих тестов определённых блоков кода приложения. В папке example создадим файлы с исходным кодом: RunWalletService.java, PersonalCabinet.java, RegisterAndAuth.java, PointForCreditTransaction.java и PointForDebitTransaction.java. Помимо этого, понадобятся текстовые файлы для вывода результата работы определённых функций java-приложения, для чего необходимо создать отдельную директорию, которая будет иметь свою внутреннюю иерархию. Исходя из всего этого, представляем примерную структуру того, что находится внутри директории "example":

```
└── example
    ├── data
    |    └── userWallets
    |        ├── login1@example.org
    |        |    ├── auditOfUserActions.txt
    |        |    ├── mobPlus73450091201.txt
    |        |    ├── movementOfFunds.txt
    |        |    ├── requireForAuth.txt
    |        |    ├── credit
    |        |    └── debit
    |        |
    |        └── login2@example.org
    |
    ├── PersonalCabinet.java
    ├── PointForCreditTransaction.java
    ├── PointForDebitTransaction.java
    ├── RegisterAndAuth.java
    └── RunWalletService.java
```
Глядя на эту схему, сначала нужно обратить внимание на класс "RunWalletService", в котором был объявлен метод "startMenu()", который используется в классе "RegisterAndAuth". Сам класс "RegisterAndAuth", в свою очередь, содержит в себе методы для регистрации и авторизации пользователя. Для полноценного функционирования данных методов были импортированы библиотеки для ввода и вывода данных:
```
java.io.BufferedReader;
java.io.File;
java.io.FileOutputStream;
java.io.FileReader;
java.io.IOException;
java.io.InputStreamReader;
java.io.PrintWriter;
```
Помимо этого, был использован "Java Collections Framework" для анализа и сканирования строк,  проверки определенных условий и генерации случайных чисел:
```
java.util.Objects;
java.util.Random;
java.util.Scanner;
```
В самом классе "RegisterAndAuth" объявлены экземпляры таких классов, как "Scanner", "String", "File" и переменная логического типа данных - "boolean". В метод "startMenu()" был помещён экземпляр класса "Scanner" с оператором выбора - "switch case". Суть данного метода в открытии доступа к другим методам. Первыми из них являются "registration()" и "authorization()". В методе "registration()" переменные класса "String" могут получить новые значения посредством чтения данных, введённых пользователем во время работы приложения, для чего используется метод "next()". Далее формируется путь к потенциальному файлу данных авторизации с помощью класса "File". Ниже созданы условия, при которых создаётся директория с помощью метода "mkdir()" и текстовый файл с помощью метода "createNewFile()", как это можно представить:
```
if (password.equals(confirmPassword)) {
    externalBool = folderUser.mkdir();
    externalBool = dataUserTextFile.createNewFile();
    // Открываем файл для записи
    FileOutputStream fileOutputReg = new FileOutputStream
            (folderUser + "/requireForAuth.txt");
    PrintWriter recordOfRegData = new PrintWriter(fileOutputReg);
    // Записываем данные пользователя в файл
    recordOfRegData.println("e-mail: " + email);
    recordOfRegData.println("phone: " + phone);
    recordOfRegData.println("password: " + password);
    // Закрываем файл и поток
    recordOfRegData.flush();
    recordOfRegData.close();
    fileOutputReg.close();
    System.out.println("Вы успешно зарегистрированы!");
} else {
        System.out.println("Пароли не совпадают. Пожалуйста, попробуйте еще раз.");
    }
```
Следующий метод, на который нужно обратить внимание, это метод "authorization()", в котором были объявлены строки "search_Word", "forEmulateSMS", "stringPassword", "confirmPassword", "stringPhone", "confirmPhone" и логическая переменная "fileExists".
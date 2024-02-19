package main.java.com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterAndAuth {

    static Scanner userInput = new Scanner(System.in);
    static String email, phone, password, inputDateLastScore;
    static boolean externalBool;
    static long daysNewScore;
    public static File userActions, dataUserTextFile, pushNumberFile, moveUserOfFunds,
    folderDebit, folderCredit, folderUser;
    static int accrualSensor, lastSearchIndex, percentDayCountDeb, percentDayCountCr;

    public void startMenu() throws IOException, ParseException {

        System.out.println("Добро пожаловать в приложение «Wallet-Service»!");
        System.out.println("""
                -----------------------------------------------------------------
                1. Регистрация пользователя (reg).
                2. Авторизация пользователя (auth).
                3. Покинуть программу (exit).""");
        while (!externalBool) {
            System.out.println("""
                    -----------------------------------------------------------------
                    Ввод необходимой команды:""");
            String command = userInput.next();
            switch (command) {
                case "reg" -> registration();
                case "auth" -> authorization();
                case "exit" -> exitTheApplication();
            }
        }
        
    }

    public void registration() throws IOException {

        // Запускаем цикл регистрации
        System.out.print("Введите ваш адрес электронной почты: ");
        email = userInput.next();
        System.out.print("Введите номер мобильного телефона: ");
        phone = userInput.next();
        System.out.print("Введите ваш пароль: ");
        password = userInput.next();
        System.out.print("Введите ваш пароль еще раз: ");
        String confirmPassword = userInput.next();
        folderUser = new File("src/main/java/com/example/data/userWallets/" + email);
        dataUserTextFile = new File(folderUser + "/requireForAuth.txt");
        userActions = new File(folderUser + "/auditOfUserActions.txt");

        long startTime = System.currentTimeMillis();

        if (!folderUser.exists() && (password != null)) {
            if (password.equals(confirmPassword)) {
                externalBool = folderUser.mkdir();
                externalBool = dataUserTextFile.createNewFile();
                externalBool = userActions.createNewFile();
                // Открываем файл для записи
                FileOutputStream fileOutputReg = new FileOutputStream(folderUser + "/requireForAuth.txt");
                PrintWriter recordOfRegData = new PrintWriter(fileOutputReg);
                // Записываем данные пользователя в файл
                recordOfRegData.println("e-mail: " + email + "\nphone: " + phone
                        + "\npassword: " + password);
                // Закрываем файл и поток
                recordOfRegData.flush();
                recordOfRegData.close();
                fileOutputReg.close();
                System.out.println("Вы успешно зарегистрированы!");
            } else {
                System.out.println("Пароли не совпадают. Пожалуйста, попробуйте еще раз.");
                return;
            }
        } else System.out.println("Пользователь с эл. адресом \"" + email + "\" уже существует."
                    + "\nПожалуйста, попробуйте еще раз.");

        long endTime = System.currentTimeMillis();
        System.out.println("Время, затраченное на проверку данных регистрации: " + (endTime-startTime) + " мс");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date date = new Date();
        String dataString = formatter.format(date);
        String audit = email + " passed registration (" + dataString + ")";
        // Создаем файл
        String filePath = String.valueOf(userActions);
        FileWriter writer = new FileWriter(filePath, true);
        // Записываем строки в файл
        writer.write(audit + "\n");
        writer.flush();
        writer.close();
        externalBool = false;

    }

    public void authorization() throws IOException, ParseException {
        
        System.out.print("Введите ваш адрес электронной почты: ");
        email = userInput.next();
        folderUser = new File("src/main/java/com/example/data/userWallets/" + email);
        System.out.print("Введите пароль от личного кабинета: ");
        password = userInput.next();
        long startTime = System.currentTimeMillis(), startTime2 = 0, endTime2 = 0;
        dataUserTextFile = new File(folderUser + "/requireForAuth.txt");
        moveUserOfFunds = new File (folderUser + "/movementOfFunds.txt");
        userActions = new File(folderUser + "/auditOfUserActions.txt");
        String searchWordPassword = password;
        if (dataUserTextFile.exists()) {
            String confirmPassword, confirmPhone;
            boolean fileExists;
            // Запускаем цикл регистрации
            try (BufferedReader readerDataUser = new BufferedReader(new FileReader(dataUserTextFile))) {
                String lineAuthData;
                StringBuilder buildPass = new StringBuilder(), buildPhone = new StringBuilder();
                while ((lineAuthData = readerDataUser.readLine()) != null) {
                    if (lineAuthData.startsWith("password: ")) buildPass
                    .append(lineAuthData).append("\n");
                    if (lineAuthData.startsWith("phone: ")) buildPhone
                    .append(lineAuthData).append("\n");
                }
                confirmPassword = buildPass.toString()
                        .replace("password: ", "").replace("\n", "");
                confirmPhone = buildPhone.toString()
                        .replace("phone: ", "").replace("\n", "");
            }
            String forEmulateSMS = confirmPhone.replace("+", "mobPlus");
            System.out.println("Пароль из текстового документа: " + confirmPassword);
            System.out.println("Телефон из текстового документа: " + confirmPhone);

            if (Objects.equals(searchWordPassword, confirmPassword)) {
                System.out.println("Авторизация прошла успешно!");
                pushNumberFile = new File(folderUser + "/" + forEmulateSMS + ".txt");
                fileExists = pushNumberFile.exists();
                externalBool = userActions.createNewFile();
                if (!fileExists) {
                    // Генерация случайного числа
                    Random random = new Random();
                    int randomNumber = random.nextInt(9000) + 1000;
                    String input;
                    int codeAccuracy = 0;
                    externalBool = pushNumberFile.createNewFile();
                    try (var writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(pushNumberFile, false)))) {
                        writer.write(String.valueOf(randomNumber));
                        writer.newLine();
                        writer.flush();
                    }
                    System.out.print("Вам было отправлено сообщение с числовым кодом: " + "****" + "\n"
                            + "Введите этот код для подтверждения регистрации: ");
                    do {
                        // Запрос пользователя на ввод числа
                        input = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
                        if (!input.equals(String.valueOf(randomNumber))) {
                            System.out.print("Ошибка ввода! Повторите попытку.\n" + "Введите код: ");
                        } else {
                            System.out.println("Введённый код верен, подтверждение прошло успешно!");
                            codeAccuracy += 1;
                        }
                    } while (codeAccuracy < 1);
                }

                PersonalCabinet personalCabinet = new PersonalCabinet();
                CalculateAndTransact calculateAndTransact = new CalculateAndTransact();
                externalBool = moveUserOfFunds.createNewFile();
                folderDebit = new File(folderUser + "/debit");
                externalBool = folderDebit.mkdir();
                folderCredit = new File(folderUser + "/credit");
                externalBool = folderCredit.mkdir();
                // Объявляем массив строк, используя list() - метод фильтра
                String[] listD = folderDebit.list((dirStr, name)
                        -> name.toLowerCase().startsWith("score"));
                String[] listCr = folderCredit.list((dirStr, name)
                        -> name.toLowerCase().startsWith("score"));

                String strArrD_Scores = Arrays.toString(listD).replace("[", "")
                .replace("]", "");
                String strArrCr_Scores = Arrays.toString(listCr).replace("[", "")
                .replace("]", "");

                List<String> lines = Files.readAllLines(Paths.get(userActions.toURI()));
                String[] listAudit = lines.toArray(String[]::new);

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
                SimpleDateFormat shortFormatter = new SimpleDateFormat("dd-MM-yyyy");
                Date date = new Date();
                String dateString = formatter.format(date);
                String formattedDateString = shortFormatter.format(date);
                System.out.println(dateString);

                String longDateString_1, longDateString_2, truncatedDebitLine, truncatedCreditLine;
                longDateString_1 = email + " debit interest accrual (" + formattedDateString;
                longDateString_2 = email + " accrual of credit interest (" + formattedDateString;
                truncatedDebitLine = email + " debit interest accrual (";
                truncatedCreditLine = email + " accrual of credit interest (";
                String scoreOpenLine = email + " opened a score",
                        arrOutString = Arrays.toString(listAudit)
                        .replace("[", "").replace(", ", "\n")
                        .replace("]", "");

                int debitIndex, creditIndex, lastDebitIndex, lastCreditIndex;
                debitIndex = arrOutString.indexOf(longDateString_1);
                creditIndex = arrOutString.indexOf(longDateString_2);
                System.out.println("Индекс сегодняшних начислений процента по дебету: " + debitIndex
                        + "\nИндекс сегодняшних начислений процента по кредиту: " + creditIndex);
                lastDebitIndex = arrOutString.lastIndexOf(truncatedDebitLine);
                lastCreditIndex = arrOutString.lastIndexOf(truncatedCreditLine);
                System.out.println("Индекс последних начислений процента по дебету: " + lastDebitIndex
                        + "\nИндекс последних начислений процента по кредиту: " + lastCreditIndex);
                lastSearchIndex = arrOutString.indexOf(scoreOpenLine);
                System.out.println("Индекс создания первого счёта: " + lastSearchIndex);
                String rightPartDebitLine = "", rightPartCreditLine = "", rightPartLastLine = "";

                if (lastDebitIndex == -1 && lastCreditIndex != -1 && lastSearchIndex != -1) {
                    rightPartCreditLine = arrOutString.substring(lastCreditIndex);
                } else if (lastDebitIndex != -1 && lastCreditIndex == -1 && lastSearchIndex != -1) {
                    rightPartDebitLine = arrOutString.substring(lastDebitIndex);
                } else if (lastDebitIndex != -1 && lastCreditIndex != -1 && lastSearchIndex != -1) {
                    rightPartDebitLine = arrOutString.substring(lastDebitIndex);
                    rightPartCreditLine = arrOutString.substring(lastCreditIndex);
                } else if (lastDebitIndex == -1 && lastCreditIndex == -1 && lastSearchIndex != -1) {
                    rightPartLastLine = arrOutString.substring(lastSearchIndex);
                } else {
                    rightPartDebitLine = "";
                    rightPartCreditLine = "";
                }

                Matcher matcherDebit = null, matcherCredit = null, matcherLastScore = null;
                Pattern pattern = Pattern.compile("\\d+\\-\\d+\\-\\d+");
                if (!Objects.equals(rightPartDebitLine, "") && !Objects.equals(rightPartCreditLine, "")) {
                    matcherDebit = pattern.matcher(rightPartDebitLine);
                    matcherCredit = pattern.matcher(rightPartCreditLine);
                } else if (!Objects.equals(rightPartDebitLine, "") && Objects.equals(rightPartCreditLine, "")) {
                    matcherDebit = pattern.matcher(rightPartDebitLine);
                } else if (Objects.equals(rightPartDebitLine, "") && !Objects.equals(rightPartCreditLine, "")) {
                    matcherCredit = pattern.matcher(rightPartCreditLine);
                } else if (!Objects.equals(rightPartLastLine, "")) {
                    matcherLastScore = pattern.matcher(rightPartLastLine);
                }
                assert false;

                boolean matchBool_1 = false, matchBool_2 = false, matchBool_3 = false;
                if (matcherDebit != null) matchBool_1 = matcherDebit.find();
                if (matcherCredit != null) matchBool_2 = matcherCredit.find();
                if (matcherLastScore != null) matchBool_3 = matcherLastScore.find();

                String inputDateDebit = "", inputDateCredit = "";
                if (matchBool_1) inputDateDebit = String.valueOf(matcherDebit.group());
                if (matchBool_2) inputDateCredit = String.valueOf(matcherCredit.group());
                if (matchBool_3) inputDateLastScore = String.valueOf(matcherLastScore.group());

                if (matchBool_1 && !inputDateDebit.isEmpty()) {
                    System.out.println("Дата последних начислений по дебетовым счетам: " + inputDateDebit);
                } else System.out.println("Не удалось найти дату по дебетовым начислениям.");
                if (matchBool_2 && !inputDateCredit.isEmpty()) {
                    System.out.println("Дата последнего увеличения долга по кредитам: " + inputDateCredit);
                } else System.out.println("Не удалось найти дату увеличения суммы долга по кредитам.");

                if (!Objects.equals(inputDateDebit, "") && !Objects.equals(inputDateCredit, "")) {
                    Date dateDeb = shortFormatter.parse(inputDateDebit),
                            dateCr = shortFormatter.parse(inputDateCredit);
                    long differenceDeb = Math.abs(System.currentTimeMillis() - dateDeb.getTime()),
                            differenceCr = Math.abs(System.currentTimeMillis() - dateCr.getTime());
                    long daysDeb = differenceDeb / (1000 * 60 * 60 * 24),
                            daysCr = differenceCr / (1000 * 60 * 60 * 24);
                    System.out.println("Количество дней недоплаты по дебету: " + daysDeb);
                    System.out.println("Количество дней неучтённого долга по кредиту: " + daysCr);
                    percentDayCountDeb = (int) daysDeb;
                    percentDayCountCr = (int) daysCr;
                } else if (!Objects.equals(inputDateDebit, "") && Objects.equals(inputDateCredit, "")) {
                    Date dateDeb = shortFormatter.parse(inputDateDebit);
                    long differenceDeb = Math.abs(System.currentTimeMillis() - dateDeb.getTime());
                    long daysDeb = differenceDeb / (1000 * 60 * 60 * 24);
                    System.out.println("Количество дней недоплаты по дебету: " + daysDeb);
                    percentDayCountDeb = (int) daysDeb;
                } else if (Objects.equals(inputDateDebit, "") && !Objects.equals(inputDateCredit, "")) {
                    Date dateCr = shortFormatter.parse(inputDateCredit);
                    long differenceCr = Math.abs(System.currentTimeMillis() - dateCr.getTime());
                    long daysCr = differenceCr / (1000 * 60 * 60 * 24);
                    System.out.println("Количество дней неучтённого долга по кредиту: " + daysCr);
                    percentDayCountCr = (int) daysCr;
                } else if (!Objects.equals(inputDateLastScore, null) && Objects.equals(inputDateCredit, "")
                           && Objects.equals(inputDateDebit, "")) {
                    Date dateLast = shortFormatter.parse(inputDateLastScore);
                    long diffNewScore = Math.abs(System.currentTimeMillis() - dateLast.getTime());
                    daysNewScore = diffNewScore / (1000 * 60 * 60 * 24);
                    percentDayCountDeb = (int) daysNewScore;
                    percentDayCountCr = (int) daysNewScore;
                }

                long endTime = System.currentTimeMillis();
                System.out.println("Время, затраченное на вход в личный кабинет: " + (endTime-startTime) + " мс");
                
                startTime2 = System.currentTimeMillis();

                String auditAuthorization = email + " passed authorization (" + dateString + ")";
                String auditDebit = email + " debit interest accrual (" + dateString + ")";
                String auditCredit = email + " accrual of credit interest (" + dateString + ")";
                // Создаем файл
                String filePath = String.valueOf(userActions);
                FileWriter writer = new FileWriter(filePath, true);
                // Записываем строки в файл с предусловием
                if (!Objects.equals(strArrD_Scores, "") && !Objects.equals(strArrCr_Scores, "")
                        && debitIndex == -1 && creditIndex == -1) {
                    accrualSensor = 3;
                    writer.write(auditAuthorization + "\n" + auditDebit + "\n" + auditCredit + "\n");
                } else if (!Objects.equals(strArrD_Scores, "") && !Objects.equals(strArrCr_Scores, "")
                        && debitIndex != -1 && creditIndex == -1) {
                    accrualSensor = 2;
                    writer.write(auditAuthorization + "\n" + auditCredit + "\n");
                } else if (!Objects.equals(strArrD_Scores, "") && !Objects.equals(strArrCr_Scores, "")
                        && debitIndex == -1 && creditIndex != -1) {
                    accrualSensor = 1;
                    writer.write(auditAuthorization + "\n" + auditDebit + "\n");
                } else if (!Objects.equals(strArrD_Scores, "") && Objects.equals(strArrCr_Scores, "")
                        && debitIndex == -1) {
                    accrualSensor = 1;
                    writer.write(auditAuthorization + "\n" + auditDebit + "\n");
                } else if (Objects.equals(strArrD_Scores, "") && !Objects.equals(strArrCr_Scores, "")
                        && creditIndex == -1) {
                    accrualSensor = 2;
                    writer.write(auditAuthorization + "\n" + auditCredit + "\n");
                } else {
                    accrualSensor = 0;
                    writer.write(auditAuthorization + "\n");
                }

                writer.flush();
                writer.close();
                assert false;
                calculateAndTransact.accrualOfInterest(listD, listCr);
                endTime2 = System.currentTimeMillis();
                System.out.println("Время формирования строк с данными об действиях пользователя: "
                + (endTime2 - startTime2) + " мс");
                externalBool = false;
                personalCabinet.subMenu();
                
            } else {
                System.out.println("Пароли не совпадают. Пожалуйста, попробуйте еще раз.");
                externalBool = false;
            }
        } else {
            System.out.println("Пользователь с такими данными не зарегистрирован!");
            externalBool = false;
        }
        
    }

    public void exitTheApplication() {
        // Выход из среды выполнения кода
        Runtime.getRuntime().exit(0);
        
    }

}
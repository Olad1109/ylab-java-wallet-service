package main.java.com.example;

import static main.java.com.example.PersonalCabinet.*;
import static main.java.com.example.RegisterAndAuth.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class PointForDebitTransaction {

    boolean debitBool;
    File fileDebit;

    public void debitMenu() throws IOException, ParseException {

        System.out.println("""
                    Операции с дебетовыми счетами!
                    -----------------------------------------------------------------
                    1. Информация по счёту (score).
                    2. Завести счёт (createScore).
                    3. Ликвидировать счёт (delete).
                    4. Ввод денежных средств (input).
                    5. Вывод денежных средств (output).
                    6. Выйти в личный кабинет (exitDeb).""");
        while (!debitBool) {
            System.out.println("""
                    -----------------------------------------------------------------
                    Ввод необходимой команды:""");
            String command = userInput.next();
            switch (command) {
                case "score" -> scoreInfo();
                case "createScore" -> createScore();
                case "delete" -> delScore();
                case "input" -> enteringMoney();
                case "output" -> withdrawOfMoney();
                case "exitDeb" -> exitDebit();
            }
        }

    }

    public void scoreInfo() throws IOException {

        long startTime = 0;
        String confirmDebScore;
        if (folderDebit.exists()) {
            System.out.println("Введите номер счёта:");
            String scoreNumber = userInput.next();
            startTime = System.currentTimeMillis();
            String nameScore = "score" + scoreNumber;
            File debitScore = new File(folderDebit + "/" + nameScore + ".txt");
            if (debitScore.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(debitScore))) {
                    String line;
                    StringBuilder theReceivedText = new StringBuilder();
                    while ((line = reader.readLine()) != null) theReceivedText.append(line);
                    confirmDebScore = theReceivedText.toString()
                            .replace("condition", "Условие открытия")
                            .replace("amount", "\nСумма")
                            .replace("percent", "\nПроцент");
                }
            } else confirmDebScore = null;
        } else confirmDebScore = null;
        System.out.println(confirmDebScore);
        long endTime = System.currentTimeMillis();
        System.out.println("Время выявления данных по счёту: "
        + (endTime - startTime) + " мс");
        debitBool = false;

    }

    public void createScore() throws IOException {

        String NUMBERS = "0123456789", amountOfMoney = """
                condition: 10.0 RUB
                amount: 10.0 RUB
                percent: 3 % Y""";
        int LENGTH = 20;
        StringBuilder scoreNumber = new StringBuilder();
        for (int i = 0; i < LENGTH; i ++) {
            int n = (int) (Math.random() * NUMBERS.length());
            scoreNumber.append(NUMBERS.charAt(n));
        }
        String nameScore = "score" + scoreNumber;
        System.out.println (scoreNumber);
        fileDebit = new File(folderDebit + "/" + nameScore + ".txt");
        if (!fileDebit.exists()) {
            System.out.println("Устроит ли вас представленный счёт (yes || no)?");
            String answer = userInput.next();
            while (!Objects.equals(answer, "yes") && !Objects.equals(answer, "no")) {
                System.out.println("Ответ не корректен!");
                answer = userInput.nextLine();
            }
            if (Objects.equals(answer, "yes")) {
                System.out.println("Кладём 10 RUB на счёт (yes || no)?");
                answer = userInput.next();
                if (Objects.equals(answer, "yes")) {
                    externalBool = fileDebit.createNewFile();
                    try (var writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(fileDebit, false)))) {
                        writer.write(amountOfMoney);
                        writer.newLine();
                        writer.flush();
                    }
                    System.out.println("Счёт создан!\n" +
                            "Условия соблюдены, запуск ввода денежных средств!");

                    CalculateAndTransact calculateAndTransact = new CalculateAndTransact();
                    String strCharSeq = calculateAndTransact.generateStrID();

                    double initialDebitAmount = 10.0;
                    initialDebitAmount = (double) Math.round(initialDebitAmount * 100) / 100;

                    SimpleDateFormat formatter = new SimpleDateFormat
                    ("dd-MM-yyyy HH:mm:ss z");
                    Date date = new Date();
                    String dateString = formatter.format(date);
                    String audit = email + " opened a score " + scoreNumber
                    + " (" + dateString + ")";
                    String moveOfFunds = nameScore + ": deposit " + initialDebitAmount
                    + " RUB (" + dateString + ")";
                    // Создаем файлы
                    String filePath = String.valueOf(userActions);
                    String filePath_2 = String.valueOf(moveUserOfFunds);
                    FileWriter writer = new FileWriter(filePath, true);
                    FileWriter writer_2 = new FileWriter(filePath_2, true);
                    // Записываем строки в файлы
                    writer.write(audit + "\n");
                    writer_2.write(moveOfFunds + "\ntransaction ID: " + strCharSeq + "\n");
                    writer.flush();
                    writer_2.flush();
                    writer.close();
                    writer_2.close();
                    externalBool = false;

                } else System.out.println("Аннулируем договор. Отказ одобрен!");
            } else if (Objects.equals(answer, "no")) System.out.println("Отказ одобрен!");
        }
        debitBool = false;

    }

    public void delScore() throws IOException {

        String strAmountDebScore = "", scoreNumber = "", nameScore;
        File debitScore = null, debitScoreDel;

        if (folderDebit.exists()) {
            System.out.println("Введите номер счёта:");
            scoreNumber = userInput.next();
            nameScore = "score" + scoreNumber;
            debitScore = new File(folderDebit + "/" + nameScore + ".txt");
            if (debitScore.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(debitScore))) {
                    String line;
                    StringBuilder theReceivedText = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("amount: ")) theReceivedText.append(line);
                        strAmountDebScore = theReceivedText.toString()
                                .replace("amount: ", "Сумма: ");
                    }
                }
                System.out.println(strAmountDebScore);
            }
        }

        // Удаление файла с определенным значением строки.
        if (Objects.equals(strAmountDebScore, "amount: 0.0 RUB")) {
            debitScoreDel = debitScore;
            assert false;
            if (debitScoreDel.exists()) {
                debitBool = debitScoreDel.delete();
                System.out.println("Данный счёт успешно удалён:\n" + scoreNumber);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
                Date date = new Date();
                String dateString = formatter.format(date);
                String audit = email + " closed a score " + scoreNumber + " (" + dateString + ")";
                // Создаем файл
                String filePath = String.valueOf(userActions);
                FileWriter writer = new FileWriter(filePath, true);
                // Записываем строки в файл
                writer.write(audit + "\n");
                writer.flush();
                writer.close();
            }
        } else {
            assert debitScore != null;
            if (debitScore.exists()) {
                System.out.println("Необходимо опустошить счёт!");
            } else System.out.println("Данный счёт отсутствует!");
        }
        debitBool = false;

    }

    public void enteringMoney() throws IOException {

        String nameInpScore = "", strFirstExtractNum = "";
        File debitInpScore, exDeposFile = null;

        if (folderDebit.exists()) {
            System.out.println("Введите номер счёта:");
            String scoreNumber = userInput.next();
            nameInpScore = "score" + scoreNumber;
            debitInpScore = new File(folderDebit + "/" + nameInpScore + ".txt");
            StringBuilder theReceivedText_1, theReceivedText_2;
            String strAmountInpScore, strPercentInpScore;
            if (debitInpScore.exists()) {
                exDeposFile = debitInpScore;
                try (BufferedReader reader = new BufferedReader(new FileReader(exDeposFile))) {
                    String line;
                    theReceivedText_1 = new StringBuilder();
                    theReceivedText_2 = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("amount: ")) theReceivedText_1.append(line);
                        if (line.startsWith("percent: ")) theReceivedText_2.append(line);
                    }
                    strAmountInpScore = theReceivedText_1.toString();
                    strPercentInpScore = theReceivedText_2.toString();
                }
                strFirstExtractNum = strAmountInpScore
                        .replace("amount: ", "")
                        .replace(" RUB", "");
                System.out.println(strAmountInpScore
                        .replace("amount: ", "Сумма: ")+ "\n"
                        + strPercentInpScore
                        .replace("percent: ", "Процент: "));
            } else System.out.println("Данного счёта не существует!");
        }

        if (!strFirstExtractNum.isEmpty()) {
            double firstExtractNum = Double.parseDouble(strFirstExtractNum);
            System.out.println("Сумма для пополнения счёта:");
            String strAmountToTopUp = userInput.next();
            double amountDouble = Double.parseDouble(strAmountToTopUp);
            amountDouble = (double) Math.round(amountDouble * 100) / 100;
            System.out.println("Поступление денежных средств!");
            CalculateAndTransact calculateAndTransact = new CalculateAndTransact();
            String strCharSeq = calculateAndTransact.generateStrID();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
            Date date = new Date();
            String dateString = formatter.format(date);
            String moveOfFunds = nameInpScore + ": deposit " + amountDouble
                    + " RUB (" + dateString + ")";
            // Создаем файл
            String filePath = String.valueOf(moveUserOfFunds);
            FileWriter writer = new FileWriter(filePath, true);
            // Записываем строки в файл
            writer.write(moveOfFunds + "\ntransaction ID: " + strCharSeq + "\n");
            writer.flush();
            writer.close();

            double scoreBalance = firstExtractNum + amountDouble;
            scoreBalance = (double) Math.round(scoreBalance * 100) / 100;
            String strNewNum = String.valueOf(scoreBalance), oldText, newText;
            oldText = "amount: " + strFirstExtractNum + " RUB";
            newText = "amount: " + strNewNum + " RUB";

            int iDebPercent = 0, iCrPercent = 0, iWithdraw = 0, iDeposit = 1;
            calculateAndTransact.inputStream(iDebPercent, iCrPercent, null, null,
                    iWithdraw, iDeposit, null, exDeposFile, oldText, newText);

            System.out.println("Сумма на счёте: " + scoreBalance + " RUB");
        }
        debitBool = false;

    }

    public void withdrawOfMoney() throws IOException {

        String nameScore = "", strFirstExtractNum = "";
        File debitOutScore, exWithFile = null;

        if (folderDebit.exists()) {
            System.out.println("Введите номер счёта:");
            String scoreNumber = userInput.next();
            nameScore = "score" + scoreNumber;
            debitOutScore = new File(folderDebit + "/" + nameScore + ".txt");
            StringBuilder theReceivedText;
            String strAmountOutScore;
            if (debitOutScore.exists()) {
                exWithFile = debitOutScore;
                try (BufferedReader reader = new BufferedReader(new FileReader(exWithFile))) {
                    String line;
                    theReceivedText = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("amount: ")) theReceivedText.append(line);
                    }
                    strAmountOutScore = theReceivedText.toString();
                }
                strFirstExtractNum = strAmountOutScore
                        .replace("amount: ", "")
                        .replace(" RUB", "");
                System.out.println(strAmountOutScore
                        .replace("amount: ", "Сумма: "));
            } System.out.println("Данного счёта не существует!");
        }

        if (!strFirstExtractNum.isEmpty()) {
            double firstExtractNum, amountDouble;
            firstExtractNum = Double.parseDouble(strFirstExtractNum);
            System.out.println("Сумма, желаемая для вывода?");
            String strAmountToEmpty = userInput.next();
            amountDouble = Double.parseDouble(strAmountToEmpty);
            amountDouble = (double) Math.round(amountDouble * 100) / 100;
            System.out.println("Проверка условий для вывода данных денежных средств:\n"
                    + amountDouble + " RUB");
            if (amountDouble > firstExtractNum) {
                do {
                    System.out.println("Превышение допустимой суммы!"
                            + "Нужно ввести корректное число!");
                    strAmountToEmpty = userInput.next();
                    amountDouble = Double.parseDouble(strAmountToEmpty);
                    amountDouble = (double) Math.round(amountDouble * 100) / 100;
                } while (amountDouble > firstExtractNum);
            }
            System.out.println("Вывод денежных средств!");
            CalculateAndTransact calculateAndTransact = new CalculateAndTransact();
            String strCharSeq = calculateAndTransact.generateStrID();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
            Date date = new Date();
            String dateString = formatter.format(date);
            String moveOfFunds = nameScore + ": withdrawal " + amountDouble
                    + " RUB (" + dateString + ")";
            // Создаем файл
            String filePath = String.valueOf(moveUserOfFunds);
            FileWriter writer = new FileWriter(filePath, true);
            // Записываем строки в файл
            writer.write(moveOfFunds + "\ntransaction ID: " + strCharSeq + "\n");
            writer.flush();
            writer.close();

            double scoreBalance = firstExtractNum - amountDouble;
            scoreBalance = (double) Math.round(scoreBalance * 100) / 100;
            String strNewNum = String.valueOf(scoreBalance), oldText, newText;
            oldText = "amount: " + strFirstExtractNum + " RUB";
            newText = "amount: " + strNewNum + " RUB";

            int iDebPercent = 0, iCrPercent = 0, iWithdraw = 1, iDeposit = 0;
            calculateAndTransact.inputStream(iDebPercent, iCrPercent, null, null,
                    iWithdraw, iDeposit, exWithFile, null, oldText, newText);
        }
        debitBool = false;

    }

    public void exitDebit() throws IOException, ParseException {

        PersonalCabinet personalCabinet = new PersonalCabinet();
        System.out.println("""
                    Произошёл выход из операций с дебетовыми счетами!
                    -----------------------------------------------------------------""");
        personalBool = false;
        personalCabinet.subMenu();

    }

}
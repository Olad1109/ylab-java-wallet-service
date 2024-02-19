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

public class PointForCreditTransaction {

    boolean creditBool;
    File fileCredit;

    public void creditMenu() throws IOException, ParseException {

        System.out.println("""
                    Операции с кредитными счетами!
                    -----------------------------------------------------------------
                    1. Информация по счёту (score).
                    2. Завести счёт (createScore).
                    3. Ликвидировать счёт (delete).
                    4. Ввод денежных средств (input).
                    5. Вывод денежных средств (output).
                    6. Выйти в личный кабинет (exitCr).""");
        while (!creditBool) {
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
                case "exitCr" -> exitCredit();
            }
        }

    }

    public void scoreInfo() throws IOException {

        long startTime = 0;
        String confirmCrScore = "";
        if (folderCredit.exists()) {
            System.out.println("Введите номер счёта:");
            String scoreNumber = userInput.next();
            startTime = System.currentTimeMillis();
            String nameScore = "score" + scoreNumber;
            File creditScore = new File(folderCredit + "/" + nameScore + ".txt");
            if (creditScore.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(creditScore))) {
                    String line;
                    StringBuilder theReceivedText = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        theReceivedText.append(line);
                        confirmCrScore = theReceivedText.toString()
                                .replace("condition", "Условие закрытия")
                                .replace("amount", "\nСумма")
                                .replace("percent", "\nПроцент");
                    }
                }
            } else confirmCrScore = null;
        } else confirmCrScore = null;
        System.out.println(confirmCrScore);
        long endTime = System.currentTimeMillis();
        System.out.println("Время выявления данных по счёту: "
        + (endTime - startTime) + " мс");
        creditBool = false;

    }

    public void createScore() throws IOException {

        String NUMBERS = "0123456789", amountOfMoney, nameScore;
        int LENGTH = 20;
        StringBuilder scoreNumber = new StringBuilder();
        do {
            for (int i = 0; i < LENGTH; i ++) {
                int n = (int) (Math.random() * NUMBERS.length());
                scoreNumber.append(NUMBERS.charAt(n));
            }
            nameScore = "score" + scoreNumber;
            fileCredit = new File(folderCredit + "/" + nameScore + ".txt");
        } while (fileCredit.exists());

        System.out.println (scoreNumber);
        if (!fileCredit.exists()) {
            System.out.println("Устроит ли вас представленный счёт (yes || no)?");
            String answer = userInput.next();
            while (!Objects.equals(answer, "yes") && !Objects.equals(answer, "no")) {
                System.out.println("Ответ не корректен!");
                answer = userInput.nextLine();
            }
            if (Objects.equals(answer, "yes")) {
                System.out.print("""
                        -----------------------------------------------------------------
                        Управление программой передаётся кредитору!
                        -----------------------------------------------------------------
                        Ввод суммы (RUB), которую можно предложить пользователю:\s""");
                answer = userInput.next();
                String ruble = answer;
                double rubleDouble = Double.parseDouble(ruble);
                rubleDouble = (double) Math.round(rubleDouble * 100) / 100;
                ruble = String.valueOf(rubleDouble);
                String percent;
                if (!ruble.equals("\n")) {
                    System.out.print("""
                        Ввод процента (%), под который можно дать кредит:\s""");
                    answer = userInput.next();
                    percent = answer;
                    double percentDouble = Double.parseDouble(percent);
                    int percentInt = (int) percentDouble;
                    percent = String.valueOf(percentInt);
                    amountOfMoney = "condition: " + ruble + " RUB"
                            + "\namount: " + ruble + " RUB"
                            + "\npercent: " + percent + " % Y";
                } else {
                    System.out.println("""
                          Отказываем в создании счёта!
                          -----------------------------------------------------------------
                          Управление программой возвращается пользователю!""");
                    creditBool = false;
                    return;
                }
                externalBool = fileCredit.createNewFile();
                try (var writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(fileCredit, false)))) {
                    writer.write(amountOfMoney);
                    writer.newLine();
                    writer.flush();
                }
                System.out.println("Поступление денежных средств!");

                CalculateAndTransact calculateAndTransact = new CalculateAndTransact();
                String strCharSeq = calculateAndTransact.generateStrID();

                double doubleSecondRubAmount = rubleDouble;
                doubleSecondRubAmount = (double) Math.round(doubleSecondRubAmount * 100) / 100;

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
                Date date = new Date();
                String dateString = formatter.format(date);
                String audit = email + " opened a score " + scoreNumber + " (" + dateString + ")";
                String moveOfFunds = nameScore + ": deposit " + doubleSecondRubAmount
                + " RUB (" +dateString + ")";
                // Создаем файлы
                String filePath = String.valueOf(userActions);
                String filePath_2 = String.valueOf(moveUserOfFunds);
                FileWriter writer = new FileWriter(filePath, true);
                FileWriter writer_2 = new FileWriter(filePath_2, true);
                // Записываем строки в файл
                writer.write(audit + "\n");
                writer_2.write(moveOfFunds + "\ntransaction ID: " + strCharSeq + "\n");
                writer.flush();
                writer_2.flush();
                writer.close();
                writer_2.close();

                System.out.println("""
                     Счёт создан!
                     -----------------------------------------------------------------
                     Управление программой возвращается пользователю!""");
            } else if (Objects.equals(answer, "no")) System.out.println("Отказ одобрен!");
        }
        creditBool = false;

    }

    public void delScore() throws IOException {

        String strConditionCrScore = "", strAmountCrScore = "";
        String scoreNumber = "", nameScore;
        File creditScore = null, creditScoreDel;
        if (folderCredit.exists()) {
            System.out.println("Введите номер счёта:");
            scoreNumber = userInput.next();
            nameScore = "score" + scoreNumber;
            creditScore = new File(folderCredit + "/" + nameScore + ".txt");
            if (creditScore.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(creditScore))) {
                    String line;
                    StringBuilder theReceivedText_1 = new StringBuilder();
                    StringBuilder theReceivedText_2 = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("condition: ")) theReceivedText_1.append(line);
                        if (line.startsWith("amount: ")) theReceivedText_2.append(line);
                    }
                    strConditionCrScore = theReceivedText_1.toString()
                            .replace("condition: ", "Условие закрытия: ");
                    strAmountCrScore = theReceivedText_2.toString()
                            .replace("amount: ", "Сумма: ");
                }
            }
        }
        if (Objects.requireNonNull(creditScore).exists()){
            double numCondition = Double.parseDouble(strConditionCrScore
                    .replace("Условие закрытия: ", "")
                    .replace(" RUB", "")),
                    numAmount = Double.parseDouble(strAmountCrScore
                            .replace("Сумма: ", "").replace(" RUB", ""));
            if (numCondition == numAmount) {
                creditScoreDel = creditScore;
                assert false;
                if (creditScoreDel.exists()) {
                    creditBool = creditScoreDel.delete();
                    System.out.println("Данный счёт успешно закрыт:\n" + scoreNumber);
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
            } else System.out.println(strConditionCrScore + "\n" + strAmountCrScore);
        } else System.out.println("Данный счёт отсутствует!");
        creditBool = false;

    }

    public void enteringMoney() throws IOException {

        String nameInpScore = "", strFirstExtractNum = "", strSecondExtractNum = "";
        File creditInpScore, exDeposFile = null;

        if (folderCredit.exists()) {
            System.out.println("Введите номер счёта:");
            String inpScoreNumber = userInput.next();
            nameInpScore = "score" + inpScoreNumber;
            creditInpScore = new File(folderCredit + "/" + nameInpScore + ".txt");
            StringBuilder theReceivedText_1, theReceivedText_2;
            String strAmountInpScore, strConditionInpScore;
            if (creditInpScore.exists()) {
                exDeposFile = creditInpScore;
                try (BufferedReader reader = new BufferedReader(new FileReader(exDeposFile))) {
                    String line;
                    theReceivedText_1 = new StringBuilder();
                    theReceivedText_2 = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("amount: ")) theReceivedText_1.append(line);
                        if (line.startsWith("condition: ")) theReceivedText_2.append(line);
                    }
                    strAmountInpScore = theReceivedText_1.toString();
                    strConditionInpScore = theReceivedText_2.toString();
                }
                strFirstExtractNum = strAmountInpScore
                        .replace("amount: ", "")
                        .replace(" RUB", "");
                strSecondExtractNum = strConditionInpScore
                        .replace("condition: ", "")
                        .replace(" RUB", "");
                System.out.println(strAmountInpScore
                        .replace("amount: ", "Сумма: ")
                        + "\n" + strConditionInpScore
                        .replace("condition: ", "Условие закрытия: "));
            } else System.out.println("Данного счёта не существует!");
        }

        if (!strFirstExtractNum.isEmpty() && !strSecondExtractNum.isEmpty()) {
            double firstExtractNum = Double.parseDouble(strFirstExtractNum),
                    secondExtractNum = Double.parseDouble(strSecondExtractNum);
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
            System.out.println("Долг по счёту: " + secondExtractNum + " RUB");
        }
        creditBool = false;

    }

    public void withdrawOfMoney() throws IOException {

        String nameScore = "", strFirstExtractNum = "", strSecondExtractNum = "";
        File creditOutScore, exWithFile = null;

        if (folderCredit.exists()) {
            System.out.println("Введите номер счёта:");
            String scoreNumber = userInput.next();
            nameScore = "score" + scoreNumber;
            creditOutScore = new File(folderCredit + "/" + nameScore + ".txt");
            StringBuilder theReceivedText_1, theReceivedText_2;
            String strAmountOutScore, strConditionOutScore;
            if (creditOutScore.exists()) {
                exWithFile = creditOutScore;
                try (BufferedReader reader = new BufferedReader(new FileReader(exWithFile))) {
                    String line;
                    theReceivedText_1 = new StringBuilder();
                    theReceivedText_2 = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("amount: ")) theReceivedText_1.append(line);
                        if (line.startsWith("condition: ")) theReceivedText_2.append(line);
                    }
                    strAmountOutScore = theReceivedText_1.toString();
                    strConditionOutScore = theReceivedText_2.toString();
                }
                strFirstExtractNum = strAmountOutScore
                        .replace("amount: ", "")
                        .replace(" RUB", "");
                strSecondExtractNum = strConditionOutScore
                        .replace("condition: ", "")
                        .replace(" RUB", "");
                System.out.println(strAmountOutScore
                        .replace("amount: ", "Сумма: ") + "\n"
                        + strConditionOutScore
                        .replace("condition: ", "Условие закрытия: "));
            } else System.out.println("Данного счёта не существует!");
        }

        if (!strFirstExtractNum.isEmpty()) {
            double firstExtractNum = Double.parseDouble(strFirstExtractNum),
                    secondExtractNum = Double.parseDouble(strSecondExtractNum);
            assert false;
            System.out.println("Сумма, желаемая для вывода?");
            String strAmountToEmpty = userInput.next();
            double amountDouble = Double.parseDouble(strAmountToEmpty);
            amountDouble = (double) Math.round(amountDouble * 100) / 100;
            System.out.println("Проверка условий для вывода данной суммы:\n"
                    + amountDouble + " RUB");
            if (amountDouble > firstExtractNum) {
                do {
                    System.out.println("Превышение лимита кредитного счёта!" +
                            "Нужно ввести корректное число!");
                    strAmountToEmpty = userInput.next();
                    amountDouble = Double.parseDouble(strAmountToEmpty);
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
            String strNewNum = String.valueOf(scoreBalance);
            String oldText, newText;
            oldText = "amount: " + strFirstExtractNum + " RUB";
            newText = "amount: " + strNewNum + " RUB";

            int iDebPercent = 0, iCrPercent = 0, iWithdraw = 1, iDeposit = 0;
            calculateAndTransact.inputStream(iDebPercent, iCrPercent, null, null,
                    iWithdraw, iDeposit, exWithFile, null, oldText, newText);
            System.out.println("Остаток на счёте: " + scoreBalance + " RUB");
            System.out.println("Долг по счёту: " + secondExtractNum + " RUB");
        }
        creditBool = false;

    }

    public void exitCredit() throws IOException, ParseException {

        PersonalCabinet personalCabinet = new PersonalCabinet();
        System.out.println("""
                    Произошёл выход из операций с кредитными счетами!
                    -----------------------------------------------------------------""");
        personalBool = false;
        personalCabinet.subMenu();

    }

}
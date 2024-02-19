package main.java.com.example;

import static main.java.com.example.RegisterAndAuth.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PersonalCabinet {

    static boolean personalBool;

    public void subMenu() throws IOException, ParseException {

        System.out.println("""
                    Произошёл вход в личный кабинет!
                    -----------------------------------------------------------------
                    1. Текущий баланс (balance).
                    2. Операции с дебетовыми счетами (debit).
                    3. Операции с кредитными счетами (credit).
                    4. Переводы между счетами (transfer).
                    5. Перевод другому пользователю (send).
                    6. Статистика вывода и ввода средств (stat).
                    7. Аудит действий пользователя (auditDo).
                    8. Покинуть личный кабинет (exitCab).""");
        while (!personalBool) {
            System.out.println("""
                    -----------------------------------------------------------------
                    Ввод необходимой команды:""");
            String command = userInput.next();
            switch (command) {
                case "balance" -> currentBalance();
                case "debit" -> withdrawalAndDeposit();
                case "credit" -> borrowAtInterest();
                case "transfer" -> transferOfFunds();
                case "send" -> sendFunds();
                case "stat" -> statWithdrawalAndDeposit();
                case "auditDo" -> actionAudit();
                case "exitCab" -> exitThePersonalAccount();
            }
        }

    }

    // Объявляем метод для просмотра текущего состояния счетов
    public void currentBalance() throws IOException {

        long startTime = System.currentTimeMillis();
        String strTotalAmountOnDeb = "", strTotalAmountOnCr = "";
        double totalAmountOnDebit = 0, totalAmountOnCredit = 0;

        if (folderUser.exists()) {
            File debitScore, creditScore;
            // Объявляем массив строк, используя list() - метод фильтра
            String[] listD = folderDebit.list ((dirStr, name) ->
                    // Проверка на наличие суффикса - последовательности символов
                    // после "score" с преобразованием в нижний регистр
                    name.toLowerCase().startsWith("score"));
            String[] listCr = folderCredit.list ((dirStr, name) ->
                    // Проверка на наличие суффикса - последовательности символов
                    // после "score" с преобразованием в нижний регистр
                    name.toLowerCase().startsWith("score"));
            // Условие вывода AssertionError
            assert listD != null;
            for (String formatTXT : listD) {
                debitScore = new File(folderDebit + "/" + formatTXT);
                if (debitScore.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(debitScore))) {
                        String line;
                        StringBuilder theAmountText = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("amount: ")) {
                                theAmountText.append(line);
                            }
                            strTotalAmountOnDeb = theAmountText.toString()
                                    .replace("amount: ", "")
                                    .replace(" RUB", "");
                        }
                    }
                } else strTotalAmountOnDeb = "0.0";
                assert false;
                totalAmountOnDebit = totalAmountOnDebit + Double.parseDouble(strTotalAmountOnDeb);
            }
            totalAmountOnDebit = (double) Math.round(totalAmountOnDebit * 100) / 100;
            System.out.println("Общая сумма по дебетовым счетам:\n" + totalAmountOnDebit + " RUB");

            assert listCr != null;
            for (String formatTXT : listCr) {
                creditScore = new File(folderCredit + "/" + formatTXT);
                if (creditScore.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(creditScore))) {
                        String line;
                        StringBuilder theReceivedText = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("amount: ")) {
                                theReceivedText.append(line);
                            }
                            strTotalAmountOnCr = theReceivedText.toString()
                                    .replace("amount: ", "")
                                    .replace(" RUB", "");
                        }
                    }
                } else strTotalAmountOnCr = "0.0";
                assert false;
                totalAmountOnCredit = totalAmountOnCredit + Double.parseDouble(strTotalAmountOnCr);
            }
            totalAmountOnCredit = (double) Math.round(totalAmountOnCredit * 100) / 100;
            System.out.println("Общая сумма по кредитным счетам:\n" + totalAmountOnCredit + " RUB");
        }
        long endTime = System.currentTimeMillis();
                System.out.println("Время проверки текущего состояния счетов: "
                + (endTime - startTime) + " мс");
        personalBool = false;

    }

    // Объявляем метод для ввода и вывода средств
    public void withdrawalAndDeposit() throws IOException, ParseException {

        long startTime = System.currentTimeMillis();
        PointForDebitTransaction pointForDebitTransaction = new PointForDebitTransaction();
        long endTime = System.currentTimeMillis();
        System.out.println("Время входа в операции по дебетовым счетам: "
        + (endTime - startTime) + " мс");
        pointForDebitTransaction.debitMenu();

    }

    // Объявляем метод для того, что бы выйти в минус и платить пеню
    public void borrowAtInterest() throws IOException, ParseException {

        long startTime = System.currentTimeMillis();
        PointForCreditTransaction pointForCreditTransaction = new PointForCreditTransaction();
        long endTime = System.currentTimeMillis();
        System.out.println("Время входа в операции по кредитным счетам: "
        + (endTime - startTime) + " мс");
        pointForCreditTransaction.creditMenu();

    }

    public void transferOfFunds() throws IOException {

        long startTime;
        String outScoreNumber, inpScoreNumber, nameOutScore = "", nameInpScore = "";
        File debitOutScore, creditOutScore, exWithFile = null;
        File debitInpScore, creditInpScore, exDeposFile = null;

        String strFirstExtractNum = "0.0", strSecondExtractNum = "0.0";

        if (folderUser.exists()) {
            System.out.println("Введите номер счёта, от куда перевести средства:");
            outScoreNumber = userInput.next();
            System.out.println("Введите номер счёта, куда перевести средства:");
            inpScoreNumber = userInput.next();
            nameOutScore = "score" + outScoreNumber;
            nameInpScore = "score" + inpScoreNumber;
            debitOutScore = new File(folderDebit + "/" + nameOutScore + ".txt");
            creditOutScore = new File(folderCredit + "/" + nameOutScore + ".txt");
            debitInpScore = new File(folderDebit + "/" + nameInpScore + ".txt");
            creditInpScore = new File(folderCredit + "/" + nameInpScore + ".txt");
            if (debitOutScore.exists() && debitInpScore.exists()) {
                exWithFile = debitOutScore;
                exDeposFile = debitInpScore;
            } else if (creditOutScore.exists() && creditInpScore.exists()) {
                exWithFile = creditOutScore;
                exDeposFile = creditInpScore;
            } else if (debitOutScore.exists() && creditInpScore.exists()) {
                exWithFile = debitOutScore;
                exDeposFile = creditInpScore;
            } else if (creditOutScore.exists() && debitInpScore.exists()) {
                exWithFile = creditOutScore;
                exDeposFile = debitInpScore;
            } else {
                System.out.println("Введены неверные данные!");
                personalBool = false;
                return;
            }
            StringBuilder theReceivedText;
            String strAmountInpScore, strAmountOutScore;
            try (BufferedReader reader = new BufferedReader(new FileReader(exWithFile))) {
                String line;
                theReceivedText = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("amount: ")) theReceivedText.append(line);
                }
                strAmountOutScore = theReceivedText.toString();
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(exDeposFile))) {
                String line;
                theReceivedText = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("amount: ")) theReceivedText.append(line);
                }
                strAmountInpScore = theReceivedText.toString();
            }
            strFirstExtractNum = strAmountOutScore
                    .replace("amount: ", "")
                    .replace(" RUB", "");
            strSecondExtractNum = strAmountInpScore
                    .replace("amount: ", "")
                    .replace(" RUB", "");
            System.out.println(strAmountOutScore
                    .replace("amount: ", "сумма на первом счёте: ")
                    + "\n" + strAmountInpScore
                    .replace("amount: ", "сумма на втором счёте: "));
        }

        double firstExtractNum = Double.parseDouble(strFirstExtractNum),
                secondExtractNum = Double.parseDouble(strSecondExtractNum);
        System.out.println("Сумма, желаемая для перевода?");
        String strAmountToTransfer = userInput.next();
        double amountDouble = Double.parseDouble(strAmountToTransfer);
        amountDouble = (double) Math.round(amountDouble * 100) / 100;
        System.out.println("Проверка условий для перевода денежных средств:\n"
                + amountDouble + " RUB");
        if (amountDouble > firstExtractNum) {
            do {
                System.out.println("Превышение допустимой суммы! Нужно ввести корректное число!");
                strAmountToTransfer = userInput.next();
                amountDouble = Double.parseDouble(strAmountToTransfer);
                amountDouble = (double) Math.round(amountDouble * 100) / 100;
            } while (amountDouble > firstExtractNum);
        }

        startTime = System.currentTimeMillis();
        System.out.println("Перевод денежных средств!");

        CalculateAndTransact calculateAndTransact = new CalculateAndTransact();
        String strCharSeq = calculateAndTransact.generateStrID();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date date = new Date();
        String dateString = formatter.format(date);
        String moveOfFunds = nameOutScore + ": withdrawal " + amountDouble + " RUB (" + dateString + ")";
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
        System.out.println("Остаток по счёту: " + scoreBalance + " RUB");
        strCharSeq = calculateAndTransact.generateStrID();

        date = new Date();
        dateString = formatter.format(date);
        moveOfFunds = nameInpScore + ": deposit " + amountDouble + " RUB (" + dateString + ")";
        // Создаем файл
        filePath = String.valueOf(moveUserOfFunds);
        writer = new FileWriter(filePath, true);
        // Записываем строки в файл
        writer.write(moveOfFunds + "\ntransaction ID: " + strCharSeq + "\n");
        writer.flush();
        writer.close();

        scoreBalance = secondExtractNum + amountDouble;
        scoreBalance = (double) Math.round(scoreBalance * 100) / 100;
        System.out.println("Сумма на втором счёте: " + scoreBalance);
        strNewNum = String.valueOf(scoreBalance);
        oldText = "amount: " + strSecondExtractNum + " RUB";
        newText = "amount: " + strNewNum + " RUB";
        iWithdraw = 0;
        iDeposit = 1;
        calculateAndTransact.inputStream(iDebPercent, iCrPercent, null, null,
                iWithdraw, iDeposit, null, exDeposFile, oldText, newText);

        date = new Date();
        dateString = formatter.format(date);
        String audit = email + " made a transfer between scores (" + dateString + ")";
        // Создаем файл
        filePath = String.valueOf(userActions);
        writer = new FileWriter(filePath, true);
        // Записываем строки в файл
        writer.write(audit + "\n");
        writer.flush();
        writer.close();
        long endTime = System.currentTimeMillis();
        System.out.println("Время перемещения средств между счетами: "
        + (endTime - startTime) + " мс");
        personalBool = false;

    }

    public void sendFunds() throws IOException {

        long startTime;
        boolean mobileNumberMatch = false;
        int limitOfCycles = 0;
        String outScoreNumber, inpPhoneNumber, inpScoreNumber, nameOutScore = "",
        nameOutPhone = "", nameInpScore = "", emailSender = email;
        File debitOutScore, creditOutScore, emulateMobile, exWithFile = null;
        File debitInpScore, creditInpScore, exDeposFile = null;
        File otherFolderUser, otherUserActions;

        String strFirstExtractNum = "0.0", strSecondExtractNum = "0.0";

        if (folderUser.exists()) {
            System.out.println("Введите номер счёта, от куда переводить средства:");
            outScoreNumber = userInput.next();
            System.out.printf("Введите номер мобильного, который принадлежит получателю: +");
            inpPhoneNumber = userInput.next();
            System.out.println("Введите номер счёта получателя, куда переводить:");
            inpScoreNumber = userInput.next();
            nameOutScore = "score" + outScoreNumber;
            nameOutPhone = "mobPlus" + inpPhoneNumber;
            nameInpScore = "score" + inpScoreNumber;
            debitOutScore = new File(folderDebit + "/" + nameOutScore + ".txt");
            creditOutScore = new File(folderCredit + "/" + nameOutScore + ".txt");

            do {
                List<String> folderNames = new ArrayList<>();
                File directory = new File("src/main/java/com/example/data/userWallets");
                if (directory.exists() && directory.isDirectory()) {
                    for (File file : directory.listFiles()) {
                        if (file.isDirectory() && !file.isHidden()) {
                            folderNames.add(file.getName());
                        }
                    }
                }
    
                Random random = new Random();
                int randomIndex = random.nextInt(folderNames.size());
                email = folderNames.get(randomIndex);
                
                    emulateMobile = new File("src/main/java/com/example/data/userWallets/" + email + "/" + nameOutPhone + ".txt");
                    if (emulateMobile.exists()) {
                        folderDebit = new File("src/main/java/com/example/data/userWallets/" + email + "/debit");
                        folderCredit = new File("src/main/java/com/example/data/userWallets/" + email + "/credit");
                        System.out.println("Средства готовятся к отправке!");
                        mobileNumberMatch = true;
                    } else {
                        limitOfCycles += 1;
                        if (limitOfCycles > 100) {
                            System.out.println("Пользователь с таким номером не найден!");
                            return;
                        }
                    }
            } while (mobileNumberMatch == false);
            
            System.out.println(email);

            debitInpScore = new File(folderDebit + "/" + nameInpScore + ".txt");
            creditInpScore = new File(folderCredit + "/" + nameInpScore + ".txt");

            if (debitOutScore.exists() && debitInpScore.exists()) {
                exWithFile = debitOutScore;
                exDeposFile = debitInpScore;
            } else if (creditOutScore.exists() && creditInpScore.exists()) {
                exWithFile = creditOutScore;
                exDeposFile = creditInpScore;
            } else if (debitOutScore.exists() && creditInpScore.exists()) {
                exWithFile = debitOutScore;
                exDeposFile = creditInpScore;
            } else if (creditOutScore.exists() && debitInpScore.exists()) {
                exWithFile = creditOutScore;
                exDeposFile = debitInpScore;
            } else {
                System.out.println("Введены неверные данные!");
                personalBool = false;
                return;
            }
            StringBuilder theReceivedText;
            String strAmountInpScore, strAmountOutScore;
            try (BufferedReader reader = new BufferedReader(new FileReader(exWithFile))) {
                String line;
                theReceivedText = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("amount: ")) theReceivedText.append(line);
                }
                strAmountOutScore = theReceivedText.toString();
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(exDeposFile))) {
                String line;
                theReceivedText = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("amount: ")) theReceivedText.append(line);
                }
                strAmountInpScore = theReceivedText.toString();
            }
            strFirstExtractNum = strAmountOutScore
                    .replace("amount: ", "")
                    .replace(" RUB", "");
            strSecondExtractNum = strAmountInpScore
                    .replace("amount: ", "")
                    .replace(" RUB", "");
            System.out.println(strAmountOutScore
                    .replace("amount: ", "сумма на счёте отправителя: ")
                    + "\n" + strAmountInpScore
                    .replace("amount: ", "сумма на счёте получателя: "));
        }

        double firstExtractNum = Double.parseDouble(strFirstExtractNum),
                secondExtractNum = Double.parseDouble(strSecondExtractNum);
        System.out.println("Сумма, желаемая для перевода?");
        String strAmountToTransfer = userInput.next();
        double amountDouble = Double.parseDouble(strAmountToTransfer);
        amountDouble = (double) Math.round(amountDouble * 100) / 100;
        System.out.println("Проверка условий для перевода денежных средств:\n"
                + amountDouble + " RUB");
        if (amountDouble > firstExtractNum) {
            do {
                System.out.println("Превышение допустимой суммы! Нужно ввести корректное число!");
                strAmountToTransfer = userInput.next();
                amountDouble = Double.parseDouble(strAmountToTransfer);
                amountDouble = (double) Math.round(amountDouble * 100) / 100;
            } while (amountDouble > firstExtractNum);
        }

        startTime = System.currentTimeMillis();
        System.out.println("Перевод денежных средств!");

        CalculateAndTransact calculateAndTransact = new CalculateAndTransact();
        String strCharSeq = calculateAndTransact.generateStrID();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date date = new Date();
        String dateString = formatter.format(date);
        String moveOfFunds = nameOutScore + ": withdrawal " + amountDouble + " RUB (" + dateString + ")";
        // Создаем файл
        String filePath = String.valueOf(moveUserOfFunds), otherFilePath;
        FileWriter writer = new FileWriter(filePath, true), writerRecipe;
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
        System.out.println("Остаток по счёту: " + scoreBalance + " RUB");
        strCharSeq = calculateAndTransact.generateStrID();

        otherFolderUser = new File("src/main/java/com/example/data/userWallets/" + email);
        moveUserOfFunds = new File (otherFolderUser + "/movementOfFunds.txt");
        date = new Date();
        dateString = formatter.format(date);
        moveOfFunds = nameInpScore + ": deposit " + amountDouble + " RUB (" + dateString + ")";
        // Создаем файл
        filePath = String.valueOf(moveUserOfFunds);
        writer = new FileWriter(filePath, true);
        // Записываем строки в файл
        writer.write(moveOfFunds + "\ntransaction ID: " + strCharSeq + "\n");
        writer.flush();
        writer.close();

        scoreBalance = secondExtractNum + amountDouble;
        scoreBalance = (double) Math.round(scoreBalance * 100) / 100;
        System.out.println("Сумма на счёте получателя: " + scoreBalance);
        strNewNum = String.valueOf(scoreBalance);
        oldText = "amount: " + strSecondExtractNum + " RUB";
        newText = "amount: " + strNewNum + " RUB";
        iWithdraw = 0;
        iDeposit = 1;
        calculateAndTransact.inputStream(iDebPercent, iCrPercent, null, null,
                iWithdraw, iDeposit, null, exDeposFile, oldText, newText);

        otherUserActions = new File("src/main/java/com/example/data/userWallets/" + email + "/auditOfUserActions.txt");
        date = new Date();
        dateString = formatter.format(date);
        String audit = email + " was used as a recipient (" + dateString + ")";
        String auditSender = emailSender + " was used as a sender (" + dateString + ")";
        // Создаем файл
        filePath = String.valueOf(userActions);
        otherFilePath = String.valueOf(otherUserActions);
        writer = new FileWriter(filePath, true);
        writerRecipe = new FileWriter(otherFilePath, true);
        // Записываем строки в файлы
        writer.write(audit + "\n");
        writerRecipe.write(auditSender + "\n");
        writer.flush();
        writerRecipe.flush();
        writer.close();
        writerRecipe.close();
        long endTime = System.currentTimeMillis();
        System.out.println("Время перемещения средств между счетами: "
        + (endTime - startTime) + " мс");
        personalBool = false;

    }

    // Объявляем метод для статистики ввода и вывода средств
    public void statWithdrawalAndDeposit() throws IOException {

        long startTime = System.currentTimeMillis();
        String strStatWithDep;
        StringBuilder theReceivedText;
        if (folderUser.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(moveUserOfFunds))) {
                String line;
                theReceivedText = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    theReceivedText.append(line).append("\n");
                }
                strStatWithDep = theReceivedText.toString();
            }
            if (!strStatWithDep.isEmpty()) {
                strStatWithDep = strStatWithDep.substring(0, strStatWithDep.length() - 1);
            } else strStatWithDep = null;
            System.out.println(strStatWithDep);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Время определения статистики по движению средств: "
        + (endTime - startTime) + " мс");
        personalBool = false;

    }

    // Объявляем метод для аудита действий
    public void actionAudit() throws IOException {

        long startTime = System.currentTimeMillis();
        String confirmAction;
        StringBuilder theReceivedText;
        if (folderUser.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userActions))) {
                String line;
                theReceivedText = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    theReceivedText.append(line).append("\n");
                }
                confirmAction = theReceivedText.toString();
            }
            confirmAction = confirmAction.substring(0, confirmAction.length() - 1);
            System.out.println("Полученные строки:\n" + confirmAction);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Время определения аудита действий пользователя: "
        + (endTime - startTime) + " мс");
        personalBool = false;

    }

    // Объявляем метод для выхода из личного кабинета
    public void exitThePersonalAccount() throws IOException, ParseException {

        long startTime = System.currentTimeMillis();
        RegisterAndAuth registerAndAuth = new RegisterAndAuth();
        System.out.println("""
                    Произошёл выход из личного кабинета!
                    -----------------------------------------------------------------""");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date date = new Date();
        String dateString = formatter.format(date);
        String audit = email + " left personal cabinet (" + dateString + ")";
        // Создаем файл
        String filePath = String.valueOf(userActions);
        FileWriter writer = new FileWriter(filePath, true);
        // Записываем строки в файл
        writer.write(audit + "\n");
        writer.flush();
        writer.close();
        long endTime = System.currentTimeMillis();
        System.out.println("Время выхода из личного кабинета: "
        + (endTime - startTime) + " мс");
        externalBool = false;
        registerAndAuth.startMenu();

    }

}
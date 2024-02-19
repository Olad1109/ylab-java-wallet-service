package main.java.com.example;

import static main.java.com.example.RegisterAndAuth.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class CalculateAndTransact {

    public void accrualOfInterest (String[] listD, String[] listCr) throws IOException {

        File debitScore, creditScore;
        String strOldAmount, strOldCondition, strOldPercent;
        double calculatePercent, totalAmount, finalBacklog;

        for (String formatTXT : listD) {
            debitScore = new File(folderDebit + "/" + formatTXT);
            try (BufferedReader reader = new BufferedReader(new FileReader(debitScore))) {
                String line;
                StringBuilder theReceivedText_1 = new StringBuilder(),
                        theReceivedText_2 = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("amount: ")) theReceivedText_1.append(line);
                    if (line.startsWith("percent: ")) theReceivedText_2.append(line);
                }
                strOldAmount = theReceivedText_1.toString()
                        .replace("amount: ", "")
                        .replace(" RUB", "");
                strOldPercent = theReceivedText_2.toString()
                        .replace("percent: ", "")
                        .replace(" % Y", "");
            }
            assert false;

            if (accrualSensor == 1 || accrualSensor == 3) {
                double sumNum_stop = Double.parseDouble(strOldAmount);
                double sumPercentDebitNum = 0;
                sumNum_stop = (double) Math.round(sumNum_stop * 100) / 100;
                calculatePercent = Double.parseDouble(strOldPercent);
                calculatePercent = (double) Math.round(calculatePercent * 100) / 100;
                if (lastSearchIndex != -1) {
                    sumPercentDebitNum = ((sumNum_stop * calculatePercent) / 36500)
                    * percentDayCountDeb;
                }
                sumPercentDebitNum = (double) Math.round(sumPercentDebitNum * 100) / 100;
                if (sumPercentDebitNum < 0.01) sumPercentDebitNum = 0.01;
                totalAmount = sumPercentDebitNum + sumNum_stop;
                totalAmount = (double) Math.round(totalAmount * 100) / 100;
                if (inputDateLastScore != null) {
                    System.out.println("Количество дней недоплаты по дебету: " + daysNewScore);
                }
                System.out.println("Начисление к сумме на счёте " +
                        formatTXT.replace("score", "")
                                .replace(".txt", "")
                        + ", исходя из годового процента:\n" + sumPercentDebitNum + " RUB");
                System.out.println("Сумма на счёте " + formatTXT
                        .replace("score", "")
                        .replace(".txt", "")
                        + " до начисления процента:\n" + strOldAmount + " RUB");
                System.out.println("Итоговая сумма на дебетовом счёте "
                        + formatTXT.replace("score", "")
                        .replace(".txt", "")
                        + ":\n" + totalAmount + " RUB");
                String nameScore = formatTXT.replace(".txt", "");
                String oldText = "amount: " + strOldAmount + " RUB";
                String newText = "amount: " + totalAmount + " RUB";
                System.out.println("Поступление денежных средств!");
                double receiveDoubleNumber = sumPercentDebitNum;
                String strCharSeq;
                strCharSeq = generateStrID();
                receiveDoubleNumber = (double) Math.round(receiveDoubleNumber * 100) / 100;

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
                Date date = new Date();
                String dateString = formatter.format(date);
                String moveOfFunds = nameScore + ": deposit " + receiveDoubleNumber + " RUB ("
                + dateString + ")";
                // Создаем файл
                String filePath = String.valueOf(moveUserOfFunds);
                FileWriter writer = new FileWriter(filePath, true);
                // Записываем строки в файл
                writer.write(moveOfFunds + "\ntransaction ID: " + strCharSeq + "\n");
                writer.flush();
                writer.close();

                int iDebPercent = 1, iCrPercent = 0, iWithdraw = 0, iDeposit = 0;
                inputStream(iDebPercent, iCrPercent, debitScore, null,
                        iWithdraw, iDeposit, null, null, oldText, newText);
            }
        }

        for (String formatTXT : listCr) {
            creditScore = new File(folderCredit + "/" + formatTXT);
            try (BufferedReader reader = new BufferedReader(new FileReader(creditScore))) {
                StringBuilder theReceivedText_1 = new StringBuilder(),
                        theReceivedText_2 = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("condition: ")) theReceivedText_1.append(line);
                    if (line.startsWith("percent: ")) theReceivedText_2.append(line);
                }
                strOldCondition = theReceivedText_1.toString()
                        .replace("condition: ", "")
                        .replace(" RUB", "");
                strOldPercent = theReceivedText_2.toString()
                        .replace("percent: ", "")
                        .replace(" % Y", "");
            }
            assert false;
            if (accrualSensor == 2 || accrualSensor == 3) {
                double sumNum_stop = Double.parseDouble(strOldCondition);
                double sumPercentCreditNum = 0;
                sumNum_stop = (double) Math.round(sumNum_stop * 100) / 100;
                calculatePercent = Double.parseDouble(strOldPercent);
                calculatePercent = (double) Math.round(calculatePercent * 100) / 100;
                if (lastSearchIndex != -1) {
                    sumPercentCreditNum = ((sumNum_stop * calculatePercent) / 36500)
                    * percentDayCountCr;
                }
                sumPercentCreditNum = (double) Math.round(sumPercentCreditNum * 100) / 100;
                if (sumPercentCreditNum < 0.01) sumPercentCreditNum = 0.01;
                finalBacklog = sumPercentCreditNum + sumNum_stop;
                finalBacklog = (double) Math.round(finalBacklog * 100) / 100;
                if (inputDateLastScore != null) {
                    System.out.println("Количество дней недоплаты по кредиту: " + daysNewScore);
                }
                System.out.println("Начисление к долгу по счёту " +
                        formatTXT.replace("score", "")
                                .replace(".txt", "")
                        + ", исходя из годового процента:\n" + sumPercentCreditNum + " RUB");
                System.out.println("Сумма долга на счёте " + formatTXT
                        .replace("score", "")
                        .replace(".txt", "")
                        + " до его повышения:\n" + strOldCondition + " RUB");
                System.out.println("Итоговый долг по кредитному счёту "
                        + formatTXT.replace("score", "")
                        .replace(".txt", "")
                        + ":\n" + finalBacklog + " RUB");
                String oldText = "condition: " + strOldCondition + " RUB";
                String newText = "condition: " + finalBacklog + " RUB";

                int iDebPercent = 0, iCrPercent = 1, iWithdraw = 0, iDeposit = 0;
                inputStream(iDebPercent, iCrPercent, null, creditScore,
                        iWithdraw, iDeposit, null, null, oldText, newText);
            }
        }

    }

    public void inputStream(int iDebPercent, int iCrPercent, File debitScore, File creditScore,
                            int iWithdraw, int iDeposit, File exWithFile, File exDeposFile,
                            String oldText, String newText)
            throws IOException {

        File uniFileScore = null;
        if (iDebPercent == 1) uniFileScore = debitScore;
        else if (iCrPercent == 1) uniFileScore = creditScore;
        else if (iWithdraw == 1) uniFileScore = exWithFile;
        else if (iDeposit == 1) uniFileScore = exDeposFile;
        assert uniFileScore != null;
        try (FileInputStream fis = new FileInputStream (uniFileScore);
             InputStreamReader isr = new InputStreamReader (fis);
             BufferedReader br = new BufferedReader(isr)) {
            // Используем метод getProperty(), что бы сохранить конструкцию текста
            String END = System.getProperty ("line.separator");
            // Используем построитель строк
            StringBuilder sb = new StringBuilder();
            String ln;
            while ((ln = br.readLine()) != null) {
                sb.append(ln.replaceAll(oldText, newText)).append(END);
            } br.close();
            // Записываем текст в буферизированный поток вывода
            BufferedWriter bw = new BufferedWriter (new FileWriter(uniFileScore));
            bw.write (sb.toString());
            bw.close();
        }

    }

    public String generateStrID() throws IOException {

        int LENGTH = 20, uniqueID = 0;
        Random randomTransact = new Random();
        StringBuilder buildStrCharSeq = new StringBuilder();
        /*
          Блок кода, который можно скрыть для проверки условия. */
        String strCharSeq;
        do {
            for (int i = 0; i < LENGTH; i ++) {
                char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                        'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                char randomLetter = alphabet[randomTransact.nextInt(62)];
                buildStrCharSeq.append(randomLetter);
            } /*
    Для ручной проверки условия данного цикла, скрываем блок кода выше и открываем две строки кода ниже.
            answer = userInput.next();
            buildCharSeq = new StringBuilder(answer); */
            strCharSeq = buildStrCharSeq.toString().trim();
            String strOldID, strNewID = "transaction ID: " + strCharSeq;
            try (BufferedReader reader = new BufferedReader(new FileReader(moveUserOfFunds))) {
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("transaction ID: " + strCharSeq)) sb.append(line);
                }
                strOldID = sb.toString();
            }
            System.out.println(strNewID
                    .replace("transaction ID: ", "Идентификатор транзакции: "));
            if (Objects.equals(strNewID, strOldID)) {
                System.out.println("Данный идентификатор транзакции уже использовался раньше!");
                System.out.println("Найдено в истории личного кабинета: "
                        + strOldID.replace("transaction ID: ", ""));
            } else uniqueID = 1;
        } while (uniqueID == 0);
        return strCharSeq;
        
    }

}
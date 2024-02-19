package main.java.com.example;

import java.io.IOException;
import java.text.ParseException;

public class RunWalletService {

    public static void main (String[] argCommStr) throws IOException, ParseException {

        RegisterAndAuth registerAndAuth = new RegisterAndAuth();
        registerAndAuth.startMenu();

    }

}
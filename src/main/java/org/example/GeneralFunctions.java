package org.example;

import java.util.Scanner;

public class GeneralFunctions {
    public String[] LoginScreen() {
        Scanner scanner;
        String[] result;

        scanner = new Scanner(System.in);
        System.out.print("User Id (-1 to exit): ");
        String username = scanner.nextLine();
        if (username.equals("-1")) {
            result = new String[] {username, username};
        } else {
            System.out.print("Enter Password : ");
            String password = scanner.nextLine();
            result = new String[] {username, password};
        }

        return result;
    }
}

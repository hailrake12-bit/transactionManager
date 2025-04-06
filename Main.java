package transactionmanagement;

import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args){
        try {
            RealBankOperations obj = new RealBankOperations();

            BankOperations proxy = (BankOperations) Proxy.newProxyInstance(
                    BankOperations.class.getClassLoader(),
                    new Class<?>[]{BankOperations.class},
                    new TransactionHandler(obj));

            Scanner scanner = new Scanner(System.in);

            while(true) {
                System.out.println("mnc - Make new client \n" +
                        "dc - delete client \n" +
                        "mt - make transaction \n" +
                        "wm - withdraw money \n" +
                        "md - make deposit \n" +
                        "end - terminate program");
                String command = scanner.nextLine();

                switch (command) {
                    case ("mnc"):
                        System.out.println("Enter id");
                        int id = scanner.nextInt();
                        System.out.println("Enter balance");
                        int balance = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter name");
                        String name = scanner.nextLine();
                        proxy.MakeNewClient(id, balance, name);
                        break;
                    case ("dc"):
                        System.out.println("Enter id");
                        proxy.DeleteClient(scanner.nextInt());
                        scanner.nextLine();
                        break;
                    case ("mt"):
                        System.out.println("Enter senderID");
                        Integer senderID = scanner.nextInt();
                        System.out.println("Enter receiverID");
                        Integer receiverID = scanner.nextInt();
                        System.out.println("Enter sum of transaction");
                        int sum = scanner.nextInt();
                        proxy.MakeTransaction(senderID, receiverID, sum);
                        scanner.nextLine();
                        break;
                    case("wm"):
                        System.out.println("Enter id");
                        Integer clientID = scanner.nextInt();
                        System.out.println("Enter sum");
                        sum = scanner.nextInt();
                        proxy.WithdrawMoney(clientID, sum);
                        scanner.nextLine();
                        break;
                    case("dm"):
                        System.out.println("Enter id");
                        clientID = scanner.nextInt();
                        System.out.println("Enter sum");
                        sum = scanner.nextInt();
                        proxy.DepositMoney(clientID, sum);
                        scanner.nextLine();
                        break;
                    case("end"):
                        exit(1);
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            System.out.println("Creating Real Object Error");
        }
    }
}

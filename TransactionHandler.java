package transactionmanagement;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static transactionmanagement.FileBackupUtility.backupFile;
import static transactionmanagement.FileBackupUtility.restoreFile;

public class TransactionHandler implements InvocationHandler {
    private final Object target;
    private BufferedWriter clientsLogsWriter, transactionWriter, transactionLogsWriter;
    private static int transactionNum = 0;

    TransactionHandler(Object target)  throws IOException{
        this.target = target;

        try(BufferedReader transactionReader = new BufferedReader(new FileReader("transactions.txt"))){
            String currentLine;
            while((currentLine = transactionReader.readLine())!= null){
                String[] parts = currentLine.split("#");
                transactionNum = Integer.parseInt(parts[0])+1;
            }
        }

        clientsLogsWriter = new BufferedWriter(new FileWriter("clients_logs.txt", true));
        transactionWriter = new BufferedWriter(new FileWriter("transactions.txt", true));
        transactionLogsWriter = new BufferedWriter(new FileWriter("transactions_logs.txt", true));
    }

    private void WriteClientLogs(String event){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate ="[" + now.format(formatter) +"] ";
        try{
            clientsLogsWriter.write( formattedDate + event + "\n");
            clientsLogsWriter.flush();
        } catch(IOException e){
            System.out.println("Client events logging error" + e.getMessage());
        }
    }

    private void WriteTransaction(String info){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "[" + now.format(formatter) +"] ";
        try{
            transactionWriter.write(info + formattedDate + "\n");
            transactionWriter.flush();
        } catch (IOException e){
            System.out.println("Transaction writing error");
        }
    }

    private void WriteTransactionLogs(String event){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "[" + now.format(formatter) +"] ";
        try{
            transactionLogsWriter.write(formattedDate + event + "\n");
            transactionLogsWriter.flush();
        } catch(IOException e){
            System.out.println("Transaction logging error" + e.getMessage());
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        File clientsFile = new File("clients.txt"), temp_clientsFile = new File("temp_clients.txt"),
                transactionsFile = new File("transactions.txt"), temp_transactionsFile = new File("temp_transactions.txt");
        if(method.getName().equals("MakeNewClient")){
            try{
                backupFile(clientsFile, temp_clientsFile);
                Object value = method.invoke(target, args);
                String event = "New client created successfully with name: " + args[2] +
                        " balance: " + args[1] + " id: " + args[0];
                WriteClientLogs(event);

                return value;
            } catch(InvocationTargetException e){
                Throwable cause = e.getCause();
                String event = "Error creating client with name: " + args[2] +
                        " balance: " + args[1] + " id: " + args[0] + " error is " + cause.getMessage();
                WriteClientLogs(event);
                restoreFile(clientsFile, temp_clientsFile);

                return null;
            }
        } else if(method.getName().equals("MakeTransaction")){
            try{
                backupFile(clientsFile, temp_clientsFile);
                backupFile(transactionsFile, temp_transactionsFile);
                Object value = method.invoke(target, args);

                String info = transactionNum++ + "#" + args[0] + "#" + args[1] + "#" + args[2] + "#";
                WriteTransaction(info);

                String event = "New transaction made successfully from user" + args[0] +
                        " id to user" + args[1] + " id for the amount of " + args[2];
                WriteTransactionLogs(event);

                return value;
            } catch (InvocationTargetException e){
                Throwable cause = e.getCause();
                String event = "Error making transaction from user" + args[0] +
                        " id to user" + args[1] + " id for amount of " + args[2] + " error is " + cause.getMessage() ;
                WriteTransactionLogs(event);
                restoreFile(clientsFile, temp_clientsFile);
                restoreFile(transactionsFile, temp_transactionsFile);

                return null;
            }
        } else if(method.getName().equals("DeleteClient")){
            try{
                backupFile(clientsFile, temp_clientsFile);
                Object value = method.invoke(target, args);
                String event =  "Client deleted successfully with id: " + args[0] ;
                WriteClientLogs(event);

                return value;
            } catch (InvocationTargetException e){
                Throwable cause = e.getCause();
                String event = "Error while deleting the client with id: " + args[0] +
                        " error is " + cause.getMessage();
                WriteClientLogs(event);
                restoreFile(clientsFile, temp_clientsFile);

                return null;
            }
        } else if (method.getName().equals("WithdrawMoney")){
            try{
                backupFile(clientsFile, temp_clientsFile);
                backupFile(transactionsFile, temp_transactionsFile);
                Object value = method.invoke(target, args);

                String info = transactionNum++ + "#" + args[0] + "#NULL#" + args[1] + "#";
                WriteTransaction(info);

                String event =  args[1] + " money withdrawn from id: " + args[0];
                WriteTransactionLogs(event);

                return value;
            } catch (InvocationTargetException e){
                Throwable cause = e.getCause();
                String event = "Error making withdrawal from user " + args[1] +
                        "id for amount of " + args[0] + " error is " + cause.getMessage();
                WriteTransactionLogs(event);
                restoreFile(clientsFile, temp_clientsFile);
                restoreFile(transactionsFile, temp_transactionsFile);

                return null;
            }
        } else if (method.getName().equals("DepositMoney")){
            try {
                backupFile(clientsFile, temp_clientsFile);
                backupFile(transactionsFile, temp_transactionsFile);
                Object value = method.invoke(target, args);

                String info = transactionNum++ + "#NULL#" + args[0] + "#" + args[1] + "#";
                WriteTransaction(info);

                String event =  args[1] + " money deposited to id: " + args[0];
                WriteTransactionLogs(event);

                return value;
            } catch (InvocationTargetException e){
                Throwable cause = e.getCause();
                String event = "Error making deposit to user " + args[1] +
                        "id for amount of " + args[0] + " error is " + cause.getMessage();
                WriteTransactionLogs(event);
                restoreFile(clientsFile, temp_clientsFile);
                restoreFile(transactionsFile, temp_transactionsFile);

                return null;
            }
        }
        return method.invoke(target, args);
    }
}

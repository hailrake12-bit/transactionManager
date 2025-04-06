package transactionmanagement;

import javax.swing.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class RealBankOperations implements BankOperations {
    private int droppingTrigger = 0;

    @Override
    public void MakeNewClient(int id, int balance, String name) throws IOException, IllegalArgumentException{
        String line;

        try(RandomAccessFile clientsReader = new RandomAccessFile("clients.txt", "r")){
            while ((line = clientsReader.readLine()) != null) {
                if (line.startsWith(id + "#")) {
                    throw new IllegalArgumentException("Client with ID " + id + " already exists.");
                }
            }
        }

        if(balance < 0) throw new IllegalArgumentException("Client can not have balance less than 0");

        try (BufferedWriter clientsWriter = new BufferedWriter(new FileWriter("clients.txt", true))){
            clientsWriter.write(id + "#" + balance + "#");

//            if(droppingTrigger++==2){
//                throw new IOException("Error while making new client");
//            }

            clientsWriter.write(name + ";\n");
        }
    }

    @Override
    public void DeleteClient(int id) throws IOException{
        File tempFile = new File("temp_clients.txt"),
                inputFile = new File("clients.txt");
        boolean isDeleted = false;

        try (RandomAccessFile clientsReader = new RandomAccessFile(inputFile, "r");
             BufferedWriter tempFileWriter = new BufferedWriter(new FileWriter(tempFile, false))) {
            String currentLine;

            while ((currentLine = clientsReader.readLine()) != null) {
                if (!currentLine.startsWith(id + "#")) {
                    tempFileWriter.write(currentLine + "\n");
                } else {
                    isDeleted = true;
                }
            }
        }

        if(!isDeleted) throw new IllegalArgumentException("No Client with id " + id);

        if (!inputFile.delete()) {
            throw new IOException("Не удалось удалить оригинальный файл");
        }
        if (!tempFile.renameTo(inputFile)) {
            throw new IOException("Не удалось переименовать временный файл");
        }
    }


    @Override
    public void MakeTransaction(Integer senderID, Integer receiverID, int sum) throws IOException{
        File tempFile = new File("temp_clients.txt"),
                inputFile = new File("clients.txt");
        if(senderID.equals(receiverID)) throw new IllegalArgumentException("sender and receiver are the same person");
        try (RandomAccessFile clientsReader = new RandomAccessFile(inputFile, "r");
                BufferedWriter tempFileWriter = new BufferedWriter(new FileWriter(tempFile, false))) {

            String currentLine;
            int resultSum;
            boolean senderExist = false, receiverExist = false;

            while ((currentLine = clientsReader.readLine()) != null) {
                if (currentLine.startsWith(senderID + "#")||currentLine.startsWith(receiverID + "#")) {
                    String[] parts = currentLine.split("#");
                    int currentSum = Integer.parseInt(parts[1]);

                    if(parts[0].equals(senderID.toString()))  {
                        senderExist = true;
                        resultSum = currentSum - sum;
                        if (resultSum < 0) throw new IllegalArgumentException("Insufficient funds");
                    }
                    else {
                        resultSum = currentSum + sum;
                        receiverExist = true;
                    }

                    tempFileWriter.write(parts[0] + "#" + resultSum + "#" + parts[2] + "\n");
                } else{
                    tempFileWriter.write(currentLine + "\n");
                }
            }
            if(!senderExist){
                throw new IllegalArgumentException("Sender is not exist");
            }
            if(!receiverExist){
                throw new IllegalArgumentException("Receiver is not exist");
            }
        }
        if (!inputFile.delete()) {
            throw new IOException("Не удалось удалить оригинальный файл");
        }
        if (!tempFile.renameTo(inputFile)) {
            throw new IOException("Не удалось переименовать временный файл");
        }
    }

    @Override
    public void WithdrawMoney(Integer id, int sum) throws IOException{
        File tempFile = new File("temp_clients.txt"),
                inputFile = new File("clients.txt");
        try (RandomAccessFile clientsReader = new RandomAccessFile(inputFile, "r");
             BufferedWriter tempFileWriter = new BufferedWriter(new FileWriter(tempFile, false))) {

            String currentLine;
            int resultSum;
            boolean clientExist = false;

            while ((currentLine = clientsReader.readLine()) != null) {
                if (currentLine.startsWith(id + "#")) {
                    String[] parts = currentLine.split("#");
                    int currentSum = Integer.parseInt(parts[1]);

                    clientExist = true;
                    resultSum = currentSum - sum;
                    if (resultSum < 0) throw new IllegalArgumentException("Insufficient funds");

                    tempFileWriter.write(parts[0] + "#" + resultSum + "#" + parts[2] + "\n");
                } else{
                    tempFileWriter.write(currentLine + "\n");
                }
            }
            if(!clientExist){
                throw new IllegalArgumentException("Client is not exist");
            }
        }
        if (!inputFile.delete()) {
            throw new IOException("Не удалось удалить оригинальный файл");
        }
        if (!tempFile.renameTo(inputFile)) {
            throw new IOException("Не удалось переименовать временный файл");
        }
    }

    @Override
    public void DepositMoney(Integer id, int sum) throws IOException{
        File tempFile = new File("temp_clients.txt"),
                inputFile = new File("clients.txt");
        try (RandomAccessFile clientsReader = new RandomAccessFile(inputFile, "r");
             BufferedWriter tempFileWriter = new BufferedWriter(new FileWriter(tempFile, false))) {

            String currentLine;
            int resultSum;
            boolean clientExist = false;

            while ((currentLine = clientsReader.readLine()) != null) {
                if (currentLine.startsWith(id + "#")) {
                    String[] parts = currentLine.split("#");
                    int currentSum = Integer.parseInt(parts[1]);

                    clientExist = true;
                    resultSum = currentSum + sum;

                    tempFileWriter.write(parts[0] + "#" + resultSum + "#" + parts[2] + "\n");
                } else{
                    tempFileWriter.write(currentLine + "\n");
                }
            }
            if(!clientExist){
                throw new IllegalArgumentException("Client is not exist");
            }
        }
        if (!inputFile.delete()) {
            throw new IOException("Не удалось удалить оригинальный файл");
        }
        if (!tempFile.renameTo(inputFile)) {
            throw new IOException("Не удалось переименовать временный файл");
        }
    }

    public void GetLastTransactions(int num) {
        try (RandomAccessFile transactionsReader = new RandomAccessFile("transaction.txt", "r")){
            long fileLength = transactionsReader.length();
            int lines = 0;
            StringBuilder builder = new StringBuilder();
            List<String> result = new LinkedList<>();

            for(long pointer = fileLength-1; pointer>= 0; pointer--){
                transactionsReader.seek(pointer);
                int readByte = transactionsReader.readByte();

                if(readByte == 0xA){
                    if(builder.length() > 0){
                        result.add(0, builder.reverse().toString());
                        builder.setLength(0);
                        lines++;
                        if(lines==num) break;
                    }
                } else if(readByte!=0xD){
                    builder.append((char)readByte);
                }
            }

            if (builder.length() > 0 && lines < num) {
                result.add(0, builder.reverse().toString());
            }

            for(String line : result){
                System.out.println(line);
            }

        } catch(IOException e){
            System.out.println("Reading error " + e.getMessage());
        }
    }
}

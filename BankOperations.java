package transactionmanagement;

import java.io.IOException;

public interface BankOperations {
    public void MakeNewClient(int id, int balance, String name) throws IOException, IllegalArgumentException;
    public void DeleteClient(int id) throws IOException;
    public void MakeTransaction(Integer senderID, Integer receiverID, int sum) throws IOException ;
    public void WithdrawMoney(Integer id, int sum) throws IOException;
    public void DepositMoney(Integer id, int sum) throws IOException;

}

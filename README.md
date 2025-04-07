Программа использует паттерн заместитель для реализации атомарности операции транзакций. 

Класс RealBankOperations имеет 5 методов:
MakeNewClient(int id, int balance, string name) - добавляет нового клиента в файл client.txt в формате "id#balance#name;"; метод выбрасывает IllegalArgumentException, если id уже существует файле или если баланс меньше 0;
DeleteClient(int id) - удаляет клиента из файла client.txt; метод выбрасывает IllegalArgumentException, если id не найден в файле;
MakeNewTransaction(int senderId, int receiverId, int sum) - уменьшает баланс клиента с senderId на sum, и увеличивает баланс клиента с receiverId на sum в файле client.txt; метод выбрасиывает IllegalArgumentException, если отправитель/получать не существует, если у отправителя и получателя один id, если сумма перевода меньше нуля и если у отправителя в файле client.txt поле balance меньше поля суммы;
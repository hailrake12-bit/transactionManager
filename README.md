<h1>Программа использует паттерн заместитель для реализации атомарности операции транзакций.</h1>

<h2>Класс RealBankOperations работает с файлом clients.txt, храня и изменяя информацию о текущих клиентах и их балансах. Класс имеет 5 методов:</h2>

<b>MakeNewClient(int id, int balance, string name)</b> - добавляет нового клиента в файл client.txt в формате "id#balance#name;"; метод выбрасывает IllegalArgumentException, если id уже существует файле или если баланс меньше 0;

<b>DeleteClient(int id)</b> - удаляет клиента из файла clients.txt; метод выбрасывает IllegalArgumentException, если id не найден в файле;

<b>MakeNewTransaction(int senderId, int receiverId, int sum)</b> - уменьшает баланс клиента с senderId на sum, и увеличивает баланс клиента с receiverId на sum в файле clients.txt; метод выбрасывает IllegalArgumentException, если отправитель/получать не существует, если у отправителя и получателя один id, если сумма перевода меньше нуля и если у отправителя в файле client.txt поле balance меньше поля суммы;

<b>WithdrawMoney(int id, int sum)</b> - уменьшает баланс клиента с id на sum в файле clients.txt; метод выбрасывает IllegalArgumentException, если sum меньше нуля или если суммы на счете клиента id меньше sum;

<b>DepositMoney(int id, int sum)</b> = увеличивает баланс клиента с id на sum в файле clients.txt; метод выбрасывает IllegalArgumentException, если sum меньше нуля

<h2>Прокси класс TransactionHandler работает с файлами clients.txt, clients_log.txt, transactions.txt и transactions_log.txt. Класс выполняет условие атомарности операций и логгирует данные об ошибочных и успешных операцях</h2>

В случае ошибки во время любой из операций реального класса прокси-класс восстановит файл в состояние, в котором он находлися до выполнения метода. Каждая ошибка выбрасываемая в методе реального класса обрабатывается и записывается в соответсвующий файл для логгов.

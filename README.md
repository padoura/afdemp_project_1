# afdemp_project_1
First individual project of Afdemp's Coding Bootcamp 3. It concerns a very simple, console-based, internal bank account management app.

## Database assumptions ##
1) Column `users`.`username` is a candidate key (defined as `unique`).
2) Column `users`.`password` is stored as `blob`.
3) Column `accounts`.`amount` is stored as `decimal(20,2)` (since we cannot increase the current total amount of 102000 € with external deposits, decimal(8,2) would actually be enough).
4) Column `accounts`.`transaction_date` is stored as `datetime(3)` (since we want to keep the milliseconds).

## Installation guide ##
1) Connect to MySQL Server as administrator. 
2) Install the database by using the `afdemp_project_1/AfdempProject1/reset_database.sql` file.
3) For Windows, run the project by double-clicking the `afdemp_project_1/AfdempProject1/run.bat` file. The batch file changes the code page of command prompt to enable the display of the euro sign (€).


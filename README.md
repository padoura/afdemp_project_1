# [afdemp_project_1](https://github.com/padoura/afdemp_project_1)
First individual project of Afdemp's Coding Bootcamp 3. It concerns a very simple, console-based, internal bank account management app.

## Installation guide ##
1) Connect to MySQL Server as administrator. 
2) Install the database by using the `afdemp_project_1/AfdempProject1/reset_database.sql` file. The given credentials for database and app users have been preserved.
3) For Windows, run the project by double-clicking the `afdemp_project_1/AfdempProject1/run.bat` file. The batch file changes the code page of command prompt to enable the display of the euro sign (€).

## Database assumptions ##
1) Column `users`.`username` is a candidate key (defined as `unique`).
2) Column `users`.`password` is stored as `blob`.
3) Column `accounts`.`amount` is stored as `decimal(20,2)` (since we cannot increase the current total amount of 102000 € with external deposits, decimal(8,2) would actually be enough).
4) Column `accounts`.`transaction_date` is stored as `datetime(3)` (since we want to keep the milliseconds).

## Application logic assumptions ##
1) On top of the required 6 classes, 3 more facilitating classes were created (1 for formatting methods, 1 for console methods, and 1 for logger handling).
2) The main logical units (the 5 classes excluding the facilitating ones) communicate exclusively via the main `BankApp` class. In other words there is no direct association between them.
3) Since there was no specific requirement about how the super admin can view a member's account, the respective menu option simply prompts the user for a member's username.
4) Since no requirement was specified for saving to statement file automatically, transactions are saved to a statement file if and only if the user selects the corresponding menu option.
5) 'x' in the statement file name for simple members is not defined, therefore I am simply using the username in its place (which is a candidate key anyway).
6) Currency format is set with Locale("el-GR") for both displaying/writing and console input cases.
7) Although not required, the application automatically retries to connect if an attempt to connect fails (parameter set to 3 attempts before exiting in this example).
8) Database password, application user passwords and salts should not be hardcoded. Nevertheless, the aforementioned "mistakes" where intentionally made to keep the encryption example in this project simple.


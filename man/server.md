# How to set up a server?
#### A small intro
 * It will be a local server launched on current workstation
 * You should use Linux, but there are also similar command in Windows or mac
 * we will use apache2 and php7.0+
   * install on Linux: 
       ```
       sudo apt update
       sudo apt install apache2
       sudo apt-get install php7.0 php7.0-fpm php7.0-mysql
       ```
## The instructions:
 1) open terminal
    * `Ctrl+Shift+T` on Linux
    * `Win+R` and then type `cmd` in the window on Windows
 2) type `ifconfig` in the terminal on Linux (`ipconfig` on Win)
    in my case I got that: 
    ![](ifconfig.png)
 3) Now we should note that our computer has ip-address `192.168.43.217` in local network
 4) We will run the server on port #1000, so the final url is: `http://192.168.43.217:1000/`
 5) Set up a server (following instructions works only on Linux):
    1. create a file `/etc/apache2/sites-available/hack.conf`
    2. insert following code there:
    ```
    Listen 1000

    <VirtualHost *:1000>
        ServerAdmin admin@site
        ServerName site
        DocumentRoot /var/www/hack/
        ErrorLog /var/www/hack/log/error.log
        CustomLog /var/www/hack/log/access.log combined
        <Directory "/var/www/hack/">
            AddHandler cgi-script .php
            Options +ExecCGI -MultiViews +SymLinksIfOwnerMatch
            Require all granted
            AllowOverride None
        </Directory>
    </VirtualHost>
    ```
    3. Execute following commands:
    ```
    sudo mkdir /var/www/hack
    sudo chmod 777 /var/www/hack
    sudo mkdir /var/www/hack/log 
    sudo chmod 777 /var/www/hack/log
    ```
    4. Execute following commands:
    ```
    sudo a2ensite hack
    sudo service apache2 restart 
    ```
    5. Finally `http://192.168.43.217:1000/` works and if you open it in a browser you will see today's date
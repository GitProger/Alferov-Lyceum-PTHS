# How to set up a client?
You should just build the application with any compiler, keeping its structure.

> We used IntelliJ IDEA and its inline compiler, but you have to make sure the source folder is `client`
>   ```xml
>   <sourceFolder url="file://$MODULE_DIR$/client" isTestSource="false" />
>   ```

- replace `client/ip.txt`'s content with address of the HTTP server
   `client/ip.txt`:
   ```
   http://192.168.43.217:1000/
   ```
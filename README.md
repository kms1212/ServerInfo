# ServerInfo
 Papermc plugin which sends server information(CPU usage, RAM usage, Errors, Exceptions, etc.) to MySQL Server

# Features
- Sends CPU usage, RAM usage to MySQL or MySQL-Compatible server
- Data size limit function for saving disk space
- Pause/Resume function
- Configure plugin ingame (v2.0b+)
- Sample interval configurable (v2.0b+)

# Before you use this plugin...
1. Copy the plugin file to the server plugin directory.
2. Run the server once or load with the "plugin manager" plugin.
3. Edit plugins/ServerInfo/config.yml file and change the file to :
```
sql:
  host: <MySQL hostname>
  port: <MySQL server port>
  database: <MySQL database name>
  username: <MySQL username>
  password: <MySQL password>
  table: <MySQL table name>
```
4. Run command "/si reload"
5. Link with other software whatever works with the MySQL Server

# Command Usage
- /serverinfo|si help **: Prints help message**
- /serverinfo|si reload **: Reloads plugin**
- /serverinfo|si pause|resume **: Pauses/Resumes uploading information*
- /serverinfo|si config \<ConfigurationNode\> [Value] **: Configures the plugin (v2.0b+)**
    - \<ConfigurationNode\> : Node Key
    - [Value] : Node value to set. If this value is not set, the plugin will show the values from node.

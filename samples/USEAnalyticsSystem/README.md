__US ELECTION MONITOR 2016__

Follow the following steps to configure the system on a machine running Ubuntu OS

1. Clone the demos repository from `https://github.com/wso2/demos.git` and find the root folder of the source code in _/samples/USEAnalyticsSystem/_ folder

2. Configure DB 
    
    2.1 If you already installed mysql server in your local machine please ignore the following step	
        Install mysql to your local machine running the following command :`sudo apt-get install mysql-server mysql-client` 
            
    2.2 Login to the mysql server as `mysql -u [username] -p` and Create a new mysql user with privileges as follows
        `CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';`
        `GRANT ALL PRIVILEGES ON * . * TO 'root'@'localhost';`
        `FLUSH PRIVILEGES;`
            
    2.3 Next, change the mysql connection limit
        `SET global max_connections = 10000;`  
           		    
    2.4 Create the following databases
        `CREATE DATABASE use16_cep_bck_db;`
   		`CREATE DATABASE use16_cep_data_db;`
   		`CREATE DATABASE use16_das_bck_db;`
   		`CREATE DATABASE use16_das_data_db;`
   		`CREATE DATABASE use16_electionDAS_db;`
   		`CREATE DATABASE use16_electionProcessDAS_db;`
   		    
    2.5 Logout from mysql using `quit;`
           
    2.6  Using the terminal _cd_ into the _db_configs_ folder and import the databases using following commands 
        `mysql -u root -proot  use16_cep_bck_db < use16_cep_bck_db.sql` 
        `mysql -u root -proot  use16_cep_data_db < use16_cep_data_db.sql`
        `mysql -u root -proot  use16_das_bck_db < use16_das_bck_db.sql`
        `mysql -u root -proot  use16_das_data_db < use16_das_data_db.sql`
        
3. Download required WSO2 products 

	 3.1 Download **WSO2 Enterprise Service Bus** from http://wso2.com/products/enterprise-service-bus/ and extract.
	    Open _ESB_HOME/repository/conf/carbon.xml_ and change the **offset tag** from 0 to _7_
	 3.2 Download **WSO2 Complex Event Processor** from http://wso2.com/products/complex-event-processor/ and extract.
	    Open _CEP_HOME/repository/conf/carbon.xml_ and change the **offset tag** from 0 to _8_
	 3.3 Download **WSO2 Data Analytic Server** from http://wso2.com/products/data-analytics-server/ and extract. 
	    
	 3.4 Download the WSO2 twitter connector from https://storepreview.wso2.com/store/assets/esbconnector/313cbd79-c183-43d2-8a6f-fb2721973ed9
	 and copy the jar to the _ESB_HOME/repository/components/dropins_ directory in ESB.  
	 
4. Deploy cApps  

	 4.1 **ESB cApp**  
	    
	   	4.1.1 Start ESB server using following command: `sh ESB_HOME/bin/wso2server.sh`  
	   	4.1.2 Use this url to access ESB Management Console : https://[host_ip]:9450/carbon/  
	   	4.1.3 Using left navigation pane go to **Main > Manage > Carbon Applications > Add**
	        	Then, Select _/cApp/USE2016_ESB_cApp_1.0.0.car_ and Upload
	        
	 4.2 **CEP cApp**  
	   
	   	4.2.1 Follow the instructions given in _/resources/dropins/CEP_dropins/CEP_Dropins.md_ file
	   	4.2.2 Follow the instructions given in _/resources/lib/CEP_lib/CEP_lib.md_ file 
	   	4.2.3 Start CEP server using following command: `sh CEP_HOME/bin/wso2server.sh ` 
	   	4.2.4 Use this url to access CEP Management Console : https://[host_ip]:9451/carbon/  
	   	4.2.5 Using left navigation pane go to **Configure > Datasources > Add Datasource**
	   		Add two datasources with the following details:
	   
	   		Name:        ELECTIONSYSTEMCEP_DB
	   		Driver:      com.mysql.jdbc.Driver
	   		URL:         jdbc:mysql://localhost:3306/use16_cep_data_db
	   		User Name:   admin
	   		Password:    admin
	   
	   		Name:        ELECTIONSYSTEMCEP_BACKUP_DB
	   		Driver:      com.mysql.jdbc.Driver
	   		URL:         jdbc:mysql://localhost:3306/use16_cep_bck_db
	   		User Name:   admin
	   		Password:    admin
	   
	   	4.2.6 Using left navigation pane go to **Main > Manage > Carbon Applications > Add**
             		Then, Select _/cApp/USE2016_CEP_cApp_1.0.0.car_ and Upload
             	        
	 4.3 **DAS cApp**  
	  
	   	4.3.1 Follow the instructions given in _/resources/dropins/DAS_dropins/DAS_Dropins.md_ file  
	   	4.3.2 Follow the instructions given in _/resources/lib/DAS_lib/DAS_lib.md_ file  
	   	4.3.3 **Unzip** and copy the content found in _/resources/patches.zip_ into the folder _DAS_HOME/repository/components/patches/_  
	   	4.3.4 Open the file _DAS_HOME/repository/conf/analytics/rdbms-config.xml_ Change variable **"ENGINE='MyISAM'"** to **"ENGINE='InnoDB'"** in Line 44  
	   	4.3.5 Copy and replace the **files** _analytics-datasources.xml_ and _master-datasources.xml_ found in _/resources/das_datasources/_ into the folder _DAS_HOME/repository/conf/datasources/_   
	   	4.3.6 Copy the **folder** _us-election-analytics_ found in _WebContent_ into _DAS_HOME/repository/deployment/server/jaggeryapps/_   
	   	4.3.7 Start DAS server using following command:` sh DAS_HOME/bin/wso2server.sh `   
	   	4.3.8 Use this url to access DAS Management Console : https://[host_ip]:9443/carbon/     
	   	4.3.9 Using left navigation pane go to **Configure > Datasources > Add Datasource**
	   
       			Add two datasources with the following details:
     
      		Name:        ELECTIONSYSTEMDAS_DB  
      		Driver:      com.mysql.jdbc.Driver  
      		URL:         jdbc:mysql://localhost:3306/use16_cep_data_db  
      		User Name:   admin  
      		Password:    admin  
             	   
      		Name:        ELECTIONSYSTEMDAS_BACKUP_DB  
      		Driver:      com.mysql.jdbc.Driver  
      		URL:         jdbc:mysql://localhost:3306/use16_cep_bck_db  
      		User Name:   admin  
      		Password:    admin  
       
	   	4.3.10 Using left navigation pane go to **Main > Manage > Carbon Applications > Add**
                    Then, Select _/cApp/USE2016_DAS_cApp_1.0.0.car_ and Upload

5. Customize Extensions(if needed any additional functionalities)  
	 5.1 Download election-siddhi-extensions in src folder  
	 5.2 Make relevent changes using your desired IDE  
	 5.3 Build pom.xml file in election-siddhi-extensions to build a new jar  
	 5.4 Replace the jar file in DASorCEP_HOME/repository/components/dropins with new jar  
	 5.5 For more information use wso2 product documentation  

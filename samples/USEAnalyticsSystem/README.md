1. Configure DB

	1.1 Download db_configs folder which consists .sql files and go to your terminal
	1.2 If you already installed mysql server in your local machine please ignore following step
	
		Install mysql to your local machine using the command : sudo apt-get install mysql-server mysql-client
		   
	1.3 change the mysql connection limit in your database using this command after login as mysql -u root -p
   		 set global max_connections = 10000;
	1.4 Log into mysql server using  mysql -u [username] -p[password] and execute following commands
		CREATE DATABASE use16_cep_bck_db;
		CREATE DATABASE use16_cep_data_db;
		CREATE DATABASE use16_das_bck_db;
		CREATE DATABASE use16_das_data_db;
	1.5 Logout from mysql using quit;
	1.6 Import Databases using following commands
		mysql -u [username] -p[password]  use16_cep_bck_db < use16_cep_bck_db.sql
		mysql -u [username] -p[password]  use16_cep_data_db < use16_cep_data_db.sql
		mysql -u [username] -p[password]  use16_das_bck_db < use16_das_bck_db.sql
		mysql -u [username] -p[password]  use16_das_data_db < use16_das_data_db.sql

2. Download required wso2 products
	2.1 Download WSO2 Enterprise Service Bus from http://wso2.com/products/enterprise-service-bus/ and extract. Open ESB_HOME/repository/conf/carbon.xml and change the offset of the server to 7 as <offset>7<offset> 
	2.2 Download WSO2 Complex Event Processor from http://wso2.com/products/complex-event-processor/ and extract. Open CEP_HOME/repository/conf/carbon.xml and change the offset of the server to 8 as <offset>8<offset>
	2.3 Download WSO2 Data Analytic Server from http://wso2.com/products/data-analytics-server/ and extract.Open DAS_HOME/repository/conf/carbon.xml and change the offset of the server to 0 as <offset>0<offset>
	2.4 Download the WSO2 twitter connector from https://storepreview.wso2.com/store/assets/esbconnector/313cbd79-c183-43d2-8a6f-fb2721973ed9 and copy the jar to the ESB_HOME/repository/components/dropins directory in ESB.
 
3. Deploy cApps
	Download all .car files in cApp folder and resource folder
	2.1 ESB cApp
		2.1.1 Start ESB server using following command: sh ESB_HOME/bin/wso2server.sh
		2.1.2 Use this url to access ESB Management Console : https://[host_ip]:9450/carbon/
		2.1.3 Using left navigation pane go to Main > Manage > Carbon Applications > Add and browse USE2016_ESB_cApp_1.0.0.car > Upload > Refresh the page
	2.2 CEP cApp
		2.2.1 put DOWNLOADED_RESOURCE/dropins/CEP_dropins into CEP_HOME/repository/components/dropins
		2.2.2 put DOWNLOADED_RESOURCE/lib/CEP_lib into CEP_HOME/repository/components/lib
		2.2.3 Start CEP server using following command: sh CEP_HOME/bin/wso2server.sh
		2.2.4 Use this url to access CEP Management Console : https://[host_ip]:9451/carbon/
		2.2.5 Using left navigation pane go to Main > Manage > Carbon Applications > Add and browse USE2016_CEP_cApp_1.0.0.car > Upload > Refresh the page
	2.3 DAS cApp
		2.2.1 put DOWNLOADED_RESOURCE/dropins/DAS_dropins into DAS_HOME/repository/components/dropins
		2.2.2 put DOWNLOADED_RESOURCE/lib/DAS_lib into DAS_HOME/repository/components/lib
		2.3.3 Start DAS server using following command: sh DAS_HOME/bin/wso2server.sh
		2.3.4 Use this url to access DAS Management Console : https://[host_ip]:9443/carbon/
		2.3.5 Using left navigation pane go to Main > Manage > Carbon Applications > Add and browse USE2016_DAS_cApp_1.0.0.car > Upload > Refresh the page
	
4. Deploy web App
	4.1 Download us-election-analytics folder in WebContent folder and copy it into DAS_HOME/repository/deployment/server/jaggeryapps
	4.2 Chnge your relevent mysql username, password and url in config_use16_cep_data_db.json  and config_use16_das_data_db.json files which is in us-election-analytics folder
	
5. Customize Extensions(if needed any additional functionalities)
	5.1 Download election-siddhi-extensions in src folder
	5.2 Make relevent changes using your desired IDE
	5.3 Build pom.xml file in election-siddhi-extensions to build a new jar
	5.4 Replace the jar file in DASORCEP_HOME/repository/components/dropins with new jar
	5.5 For more information use wso2 product documentation

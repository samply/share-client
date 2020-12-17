##Admin Page

### Register at Sample Locator
* To connect to the Sample Locator, login at Connector-UI (../login.xhtml) (default usr=admin, pwd=adminpass)
    * Go to the register page (../admin/broker_list.xhtml) and enter these values:
        * "Address": `https://samplelocator.bbmri.de/broker/`
        * "Your email address": your email address to get an API key
        * "Automatic reply": `Total Size` (default, so you answer automatically with number of samples and donors)
        * Select "Join" to receive an email with the API key to paste under "Status", then select "Activate"
        * At least, **send an email** to `feedback@germanbiobanknode.de` with `your used email address` and `desired biobank name`

### Monitoring
* Activate Monitoring (Icinga will send a test query periodically to send you an email if errors occur)
    * Open the conig page (../admin/configuration.xhtml) to enable three buttons under "Reporting to central services" and scroll down to save with button "Save"

### Credentials
* Under (../admin/credentials_list.xhtml) you can see the credentials which the connector are using.
    * It is possible to add the following credentials:
        * HTTP Proxy
        * Local Data Management authentication
        * Directory Sync (if the feature toggle is enabled)
### User
* To enable a user to access the connector, a new user can be created under "../admin/user_list.xhtml".
  This user then has the possibility to view incoming queries

### Jobs
* The connector are using [Quartz Jobs](http://www.quartz-scheduler.org/) to do things like collect the queries from the searchbroker or execute the queries.
  Under the job page ("../admin/job_list.xhtml") you can see the full list of the jobs.

### Tests
* The connector has connectivity checks which can be found under the test page(../admin/tests.xhtml).



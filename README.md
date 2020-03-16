## Summary
Often there is a need to **profile** your java application, in order to find out various pressure point. Profiling is the process of monitoring various JVM level parameters such as Thread Local Allocation Buffer, Classloader Statistics, Method Execution, String pool Statistics, Thread Execution, Object Creation, Garbage Collection, I/Os, etc. It provides with a finer view of the target application execution and its resource utilisation. It further eliminates the need to spend long hours going through the code, and pinpoints the problems associated with your application.

Java Mission Control (jmc) is a fantastic tool for profiling your java application, however, it provides a comprehensive view of your application and include all kind of system classes if a bottleneck is identified there. Using JMC, even a non-developer or sysadmin can easily get to see and find the bottleneck of your application. Also being a commercial feature, one has to get the license for the same.

**java-agent** is particularly useful for the developer and especially for certain use cases. Let's say you are building a REST API or a Spring Controller. Once the API is finished you may have to perform some performance testing before you actually roll it out to production. There are varioous performance testing tool available in the market, e.g., Apache Benchmark (ab), JMeter etc. But these tools will give you average response time of the API (end point). But as a developer, you must also know which (java) method or piece of code took most of the time, and only then you can make your code more robust and scalable. This is where **java-agent** comes handy.

## How to Use
The **java-agent** uses bytecode injection feature to instrument your code. Let us understand this with a sample application.

Imagine the following classes part of your simple REST application.

AccountController class exposes a REST end point: POST /accounts .

```
@RestController
@RequestMapping(path = "/accounts"
        , produces = MediaType.APPLICATION_JSON_VALUE
        , consumes = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createAccount(@RequestBody Account account) {
        // Create the account first.
        AccountService service = new AccountService();
        service.create(account);
        
        // Send notification too downstream system (via kafka/jms, etc)
        sendNotif(account);
        return ResponseEntity.created(URI.create("/accounts/"  + account.getId())).build();
    }
    
    private void sendNotif(Account account) {
        // Create kafka/jms producer.
        // Send the message.
    }
}
```

AccountService class makes a call to database or other storage to persist the new accounnt details.

```
public class AccountService {

    static {
        Class.forName("your.favorite.rdbms.driver.name");
    }
    
    public void create(Account account) {
        // Make database call to persist the new account details.
        // Let's say, we are using some RDBMS system as a backend.
        Connection conn = DriverManager.getConnection("<url>", "<user>", "<password>");
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ACCOUNTS (ID, TYPE, BALANCE) VALUES (?, ?, ?)");
        pstmt.setString(1, account.getId());
        pstmt.setString(2, account.getType());
        pstmt.setDouble(3, account.getBalance());
        
        int count = pstmt.executeUpdate();
        conn.commit();
        
        pstmt.close();
        conn.close();
    }
}
```

You are trying to profile the folloowing two methods:
1. AccountController.createAccount(Account)
1. AccountService.create(Account)

You have to load the java agent, which in turn will kick start the profiling.

### Steps to load java agent

#### Criteria
Ensure you are trying to load the java agent from the same box where your application is running.

#### Check out/Download java agent
Create a parent directory java-agent. Check out the **deploy** and **lib** directory.

#### Check what all **java** applications are available for profiling.
Run the below command:
```
java -jar deploy/java-agent-1.0-SNAPSHOT.jar --help
```

It will show you various option, plus the java processes running in the current box. See **Appendix A** for more details about the option.

Note the **process id** of your (REST) java application that you wish to monitor.

#### Create Configuration file
Create a configuration file (**/path/to/java-agent/classmap.dat**) to explicitly specify the class and/or method(s) that you want to profile.

Sample entries:
```
# Mapping for controller
<package.name>.AccountController-{createAccount}
# Mapping for service
<package.name>.AccountService-{create}
```

#### Starting Agent
Start the agent by running the following command:
```
java -jar target/java-agent-1.0-SNAPSHOT.jar -a /path/to/java-agent/deploy/java-agent-1.0-SNAPSHOT.jar -p <process_id> -m /path/to/java-agent/classmap.dat -o /path/to/java-agent/temp -v N -s Y -j /path/to/java-agent/lib/javassist-3.12.1.GA.jar
```

You will see the following messages:
```
Uploading agent [/path/to/java-agent/deploy/java-agent-1.0-SNAPSHOT.jar] to remote process [<process_id>]
Detached from remote process [<process_id>]
```

At this point, both of your methods are instrumented.

You can start any performance monitoring tool (e.g., Apache Benchmark) to hit the POST /accounts end point, and the agent will start dumping the profile statistics in the specified directory, as identified by **-o** option, in HTML format.

E.g, of Apache Benchmark command:
```
ab -k -c 2 -n 5 -T application/json -p ./payload.json  http://localhost:8080/accounts
```
Where payload.json is:
```
{
  "id": "ALE191016EMAIL33304100017L5000",
  "type": "Savings Account",
  "balance": 350000
}
```

## Appendix A
1.	-a    Path to java agent library
1.	-p    Remote JVM Process Id
1.	-m    Path to classmap.dat
1.	-d    Specify if dumping of metrics is enabled [default: Y]
1.	-i    Specify the interval (in seconds) the metrics would be dumped [default: 900]
1.	-o    Specify the dump directory, where the metrics (HTML) file will be generated
1.	-v    On|Off the verbose [default: Y]
1.	-l    Specify the log directory where the agent log will be generated (only if verbose is set to Y).
1.	-c    Specify the directory where the instrumented class(s) will be dumped [default: dump directory]
1.	-s    Specify if javaassist library is needed to be loaded by remote vm [default: N]
1.	-j    Path to javaassist library (required if -s option is specified)

## Appendix B
Sample HTML file

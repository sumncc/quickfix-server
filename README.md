spring-boot-starter
=======

<p>
If you are new to FIX, you could have a look at the [industry website](https://www.fixtrading.org/). In particular,
there is a very useful web-based dictionary provided for free that shows each version of FIX and the messages
(fields and components) that make up the version [FIXimate](https://fiximate.fixtrading.org/).
</p>
<p>
In this module, once the client receives a successful Logon, the client sends a MarketDataRequest to the server.
The server responds with a stream of MarketDataIncrementalRefresh me
</p>
This module provides a realistic example of a Marketclient/server interaction using the 
[quickfixj-spring-boot-starter](https://github.com/esanchezros/quickfixj-spring-boot-starter) developed primarily by
[Eduardo Sanchez-Ros](https://www.linkedin.com/in/eduardosanchezros/) who is a member of the [QuickFIX/J LinkedIn Group](https://www.linkedin.com/groups/14156680/). This starter reduces boilerplate code. As an exercise, a dev can compare the total lines of code in
this module to the child modules:
<p>
<strong>quickfixj/client</strong>
<br/>
<strong>quickfixj/server</strong>
</p>


## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)

## General info
This project consists of a Fix client and Server that are able to run as SSL or Non-SSL 
depending on the spring profile chosen. The Client's SSL profile injects mock FIX credentials, 
which have been encrypted in the client's 
[application.yml](quickfixj/client/src/main/resources/application.yml) using Spring Boot/Jaspyt. 
FYI, the credentials will appear in clear text in the FIX messages. Additionally, the Jaspyt
encryption password is in clear text in the client's 
[application.yml](quickfixj/client/src/main/resources/application.yml).
A production version of this application could store it as an environment variable. 
See the following link for more info 
[Jasypt Security](https://github.com/ulisesbocchio/jasypt-spring-boot#demo-app)




## Technologies
Project is created with:
- Spring Boot version: 3.0
- Quickfixj version: 2.3.1


## Setup
 
### IDE
launch the main classes separately: 
- [Client.java](quickfixj/client/src/main/java/org/gershaw/quickfixj/ssl/client/Client.java) 
- [Server.java](quickfixj/server/src/main/java/org/gershaw/quickfixj/server/Server.java)
  - Use `-Dspring.profiles.active=ssl` on both Client and Server to enable SSL

### Maven
- Windows
  - Open 2 cmd prompts.
    - Start Server
      - Non-SSL: `mvn -pl :server spring-boot:run`
      - SSL: `mvn -pl :server spring-boot:run -Dspring-boot.run.profiles=ssl`
    - Start Client 
      - Non-SSL: `mvn -pl :client spring-boot:run`
      - SSL: `mvn -pl :client spring-boot:run -Dspring-boot.run.profiles=ssl`

### Self Signed SSL Cert creation
FYI, this is for testing purposes only. See article [here](https://community.pivotal.io/s/article/Generating-a-self-signed-SSL-certificate-using-the-Java-keytool-command?language=en_US0)


# EXAMPLE FIX MESSAGES

### 1. New Order - Single (Message Type D)
   This message is used to submit a new order.

 Example New Order:

makefile
Copy code
8=FIX.4.2\x019=178\x0135=D\x0149=CLIENT12\x0156=BROKER1\x0134=2\x0152=20230614-18:20:30\x0111=12345\x0155=IBM\x0154=1\x0138=100\x0140=2\x0144=150.50\x0159=0\x0110=176\x01
Explanation:

- 8=FIX.4.2: FIX protocol version 4.2
- 9=178: Body length
- 35=D: Message type is New Order - Single
- 49=CLIENT12: Sender CompID
- 56=BROKER1: Target CompID
- 34=2: Message sequence number
- 52=20230614-18:20:30: Sending time
- 11=12345: Client order ID
- 55=IBM: Symbol
- 54=1: Side (1=Buy)
- 38=100: Order quantity
- 40=2: Order type (2=Limit)
- 44=150.50: Price
- 59=0: Time in force (0=Day)
- 10=176: Checksum

### 2. Execution Report (Message Type 8)
   This message is used to confirm order status.

Example:

makefile
Copy code
8=FIX.4.2\x019=200\x0135=8\x0149=BROKER1\x0156=CLIENT12\x0134=3\x0152=20230614-18:21:00\x0111=12345\x0137=54321\x0150=TRADER1\x0117=1\x0150=20230614-18:20:35\x01150=0\x0154=1\x0155=IBM\x0138=100\x0144=150.50\x0132=100\x0131=150.50\x0148=1\x0157=0\x0150=0\x0110=167\x01
Explanation:

- 8=FIX.4.2: FIX protocol version 4.2
- 9=200: Body length
- 35=8: Message type is Execution Report
- 49=BROKER1: Sender CompID
- 56=CLIENT12: Target CompID
- 34=3: Message sequence number
- 52=20230614-18:21:00: Sending time
- 11=12345: Client order ID
- 37=54321: Order ID
- 150=0: Exec type (0=New)
- 39=0: OrdStatus (0=New)
- 54=1: Side (1=Buy)
- 55=IBM: Symbol
- 38=100: Order quantity
- 44=150.50: Price
- 32=100: Last shares
- 31=150.50: Last price
- 17=1: Exec ID
- 10=167: Checksum
### 3. Order Cancel Request (Message Type F)
   This message is used to cancel an existing order.

Example:


8=FIX.4.2\x019=136\x0135=F\x0149=CLIENT12\x0156=BROKER1\x0134=4\x0152=20230614-18:22:00\x0111=12346\x0137=54321\x0155=IBM\x0154=1\x0138=100\x0141=12345\x0150=TRADER1\x0150=0\x0110=164\x01
Explanation:

- 8=FIX.4.2: FIX protocol version 4.2
- 9=136: Body length
- 35=F: Message type is Order Cancel Request
- 49=CLIENT12: Sender CompID
- 56=BROKER1: Target CompID
- 34=4: Message sequence number
- 52=20230614-18:22:00: Sending time
- 11=12346: Client order ID for cancel request
- 37=54321: Original order ID
- 55=IBM: Symbol
- 54=1: Side (1=Buy)
- 38=100: Order quantity
- 41=12345: Original client order ID
- 10=164: Checksum

### 4. Order Status Request (Message Type H)
   This message is used to request the status of an order.

Example:

makefile
Copy code
8=FIX.4.2\x019=112\x0135=H\x0149=CLIENT12\x0156=BROKER1\x0134=5\x0152=20230614-18:23:00\x0111=12345\x0155=IBM\x0154=1\x010=173\x01
Explanation:

- 8=FIX.4.2: FIX protocol version 4.2
- 9=112: Body length
- 35=H: Message type is Order Status Request
- 49=CLIENT12: Sender CompID
- 56=BROKER1: Target CompID
- 34=5: Message sequence number
- 52=20230614-18:23:00: Sending time
- 11=12345: Client order ID
- 55=IBM: Symbol
- 54=1: Side (1=Buy)
- 10=173: Checksum

- 
### 5.Market Data Request (Message Type V)
   This message is used to request market data.

Example:

makefile
Copy code
8=FIX.4.2\x019=142\x0135=V\x0149=CLIENT12\x0156=BROKER1\x0134=6\x0152=20230614-18:24:00\x01162=1\x0263=1\x0264=0\x0265=0\x0267=2\x0269=0\x0269=1\x0146=1\x055=IBM\x010=178\x01
Explanation:

- 8=FIX.4.2: FIX protocol version 4.2
- 9=142: Body length
- 35=V: Message type is Market Data Request
- 49=CLIENT12: Sender CompID
- 56=BROKER1: Target CompID
- 34=6: Message sequence number
- 52=20230614-18:24:00: Sending time
- 262=1: MDReqID
- 263=1: Subscription request type (1=Snapshot)
- 264=0: Market depth (0=Full Book)
- 265=0: MDUpdateType (0=Full refresh)
- 267=2: NoMDEntryTypes
- 269=0: MDEntryType (0=Bid)
- 269=1: MDEntryType (1=Offer)
- 146=1: NoRelatedSym
- 55=IBM: Symbol
- 10=178: Checksum


 
These examples showcase various types of FIX messages, illustrating how different trading-related activities can be communicated using the FIX protocol. Each message type serves a specific purpose and contains a unique set of fields relevant to its function.



# Test With
http://localhost:8080/send-server/buy?fixVersion=FIXT.1.1&messageType=Quote

http://localhost:8080/send-server/quote?fixVersion=FIXT.1.1&messageType=Quote

dictinary
https://fiximate.fixtrading.org/




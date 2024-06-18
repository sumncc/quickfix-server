package org.raj.quickfixj.springboot.server.component;

import quickfix.*;
import quickfix.field.*;

public class MyApplication implements Application {

    private final quickfix.fix41.MessageCracker messageCracker;

    public MyApplication(quickfix.fix41.MessageCracker messageCracker) {
        this.messageCracker = messageCracker;
    }

    @Override
    public void onCreate(SessionID sessionId) {
        System.out.println("Session created: " + sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("Logon successful: " + sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        System.out.println("Logout successful: " + sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        if (isLogon(message)) {
            // Handle logon if required
        }
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        if (isLogout(message)) {
            System.out.println("Received Logout message: " + message);
        }
    }

    @Override
    public void toApp(Message message, SessionID sessionId) {
        // Process application-level outgoing messages here
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        // Process application-level incoming messages here
        if (isOrderCancelRequest(message)) {
            handleOrderCancelRequest(message, sessionId);
        }
    }

    private boolean isLogon(Message message) {
        try {
            return MsgType.LOGON.equals(message.getHeader().getString(MsgType.FIELD));
        } catch (FieldNotFound e) {
            return false;
        }
    }

    private boolean isLogout(Message message) {
        try {
            return MsgType.LOGOUT.equals(message.getHeader().getString(MsgType.FIELD));
        } catch (FieldNotFound e) {
            return false;
        }
    }

    private boolean isOrderCancelRequest(Message message) {
        try {
            return MsgType.ORDER_CANCEL_REQUEST.equals(message.getHeader().getString(MsgType.FIELD));
        } catch (FieldNotFound e) {
            return false;
        }
    }

    private void handleOrderCancelRequest(Message message, SessionID sessionId) throws FieldNotFound {
        // Extract fields from the order cancel request
        String clOrdID = message.getString(ClOrdID.FIELD);
        String origClOrdID = message.getString(OrigClOrdID.FIELD);
        String symbol = message.getString(Symbol.FIELD);
        char side = message.getChar(Side.FIELD);

        // Create the execution report (order cancel response)
        Message cancelResponse = new quickfix.fix42.ExecutionReport(
                new OrderID("54321"), // OrderID from the system
                new ExecID("12345"),  // ExecID from the system
                new ExecTransType(ExecTransType.NEW),
                new ExecType(ExecType.CANCELED),
                new OrdStatus(OrdStatus.CANCELED),
                new Symbol(symbol),
                new Side(side),
                new LeavesQty(0),
                new CumQty(0),
                new AvgPx(0)
        );

        cancelResponse.setString(ClOrdID.FIELD, clOrdID);
        cancelResponse.setString(OrigClOrdID.FIELD, origClOrdID);

        // Send the cancel response
        try {
            Session.sendToTarget(cancelResponse, sessionId);
            System.out.println("Sent cancel response: " + cancelResponse);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }
}

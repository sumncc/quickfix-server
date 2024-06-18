package org.gershaw.quickfixj.springboot.server.component;

import io.allune.quickfixj.spring.boot.starter.template.QuickFixJTemplate;
import lombok.extern.slf4j.Slf4j;
import org.gershaw.quickfixj.springboot.util.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix50sp2.MarketDataIncrementalRefresh;
import quickfix.fix50sp2.MarketDataRequest;
import quickfix.fix50sp2.Quote;
import quickfix.fix50sp2.QuoteRequest;

import quickfix.fix50sp2.ExecutionReport;
import quickfix.fix50sp2.NewOrderSingle;



import java.util.Arrays;
import java.util.Map;

//@ConditionalOnProperty(prefix = "quickfixj.client.requester", name = "request", havingValue = "true")
@Service("MarketDataRequestService")
@Slf4j
@SuppressWarnings("unused")
public class MarketDataRequestService extends ApplicationAdapter {
    private final QuickFixJTemplate fixJTemplate;
//    private final Character[] mdEntryTypes;
//    private final Map<String, String> instruments;
    private final Acceptor acceptor;

    @SuppressWarnings("unused")
    public MarketDataRequestService(final QuickFixJTemplate fixJTemplate,
//                                    @Value("${quickfixj.client.requester.mdentry}") final Character[] mdEntryTypes,
//                                    @Value("#{${quickfixj.client.requester.instruments}}") final Map<String, String> instruments,
                                    @Lazy final Acceptor acceptor) {
        this.fixJTemplate = fixJTemplate;
//        this.mdEntryTypes = Arrays.copyOf(mdEntryTypes, mdEntryTypes.length);
//        this.instruments = Map.copyOf(instruments);
        this.acceptor = acceptor;
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log.info("Received Logon: {}", sessionId);
//        final MarketDataRequest marketDataRequest = MarketDataRequestor.of(mdEntryTypes, instruments);
//        final Map<SessionID, Boolean>  failures = Utils.send(fixJTemplate, initiator, marketDataRequest);
//        if(!failures.isEmpty()) {
//            log.warn("Failed to send to {}", failures.keySet());
//        }
    }



    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        // Process application-level incoming messages here
        System.out.println("Received message: " + message);
        if (MsgType.ORDER_CANCEL_REQUEST.equals(message.getHeader().getString(MsgType.FIELD))) {
            handleOrderCancelRequest(message, sessionId);

        }else if (MsgType.QUOTE_REQUEST.equals(message.getHeader().getString(MsgType.FIELD)) ){
            handlequote((QuoteRequest) message, sessionId);
        }
       else if (MsgType.ORDER_SINGLE.equals(message.getHeader().getString(MsgType.FIELD)) ){
            handleNewOrderSingle((NewOrderSingle) message, sessionId);
    }
    }

    private void handleNewOrderSingle(NewOrderSingle newOrderSingle, SessionID sessionId) {
        try {
            // Extract order details
            String clOrdID = newOrderSingle.getClOrdID().getValue();
            String symbol = newOrderSingle.getSymbol().getValue();
            char side = newOrderSingle.getSide().getValue();
            double orderQty = newOrderSingle.getOrderQty().getValue();
            double price = newOrderSingle.getPrice().getValue();

            // Create an Execution Report message
            quickfix.fix50sp2.ExecutionReport executionReport = new ExecutionReport(
                    new OrderID("12345"), new ExecID("54321"), new ExecType(ExecType.NEW),
                    new OrdStatus(OrdStatus.FILLED), //newOrderSingle.getSymbol(),
                    newOrderSingle.getSide(), new LeavesQty(0), new CumQty(orderQty)//, new AvgPx(price)
            );

            executionReport.set(new ClOrdID(clOrdID));
            executionReport.set(new OrderQty(orderQty));
            executionReport.set(new Symbol("IBM"));
           // executionReport.set(new AvgPx(price));
            //executionReport.set(new LastShares(1.2d));
            executionReport.set(new LastPx(price));

            // Send the Execution Report
            quickfix.Session.sendToTarget(executionReport, sessionId);
        } catch (FieldNotFound | SessionNotFound e) {
            e.printStackTrace();
        }
    }

    private  void handlequote(QuoteRequest message, SessionID sessionId) throws FieldNotFound {
    try {

        // Create a new Quote message
        Quote quote = new Quote();
        quote.set(new QuoteID("12345")); // Unique ID for the quote
        quote.set(new QuoteReqID(message.getQuoteReqID().getValue()));
        quote.set(new Symbol("IBM"));
        quote.set(new BidPx(145.50));
        quote.set(new OfferPx(146.00));
        quote.set(new BidSize(100));
        quote.set(new OfferSize(100));

        // Send the quote response
        quickfix.Session.sendToTarget(quote, sessionId);
    } catch (FieldNotFound | quickfix.SessionNotFound e) {
        e.printStackTrace();
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

//    @Override
//    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound{
//        String msgType = message.getHeader().getField(new MsgType()).getValue();
//        if(msgType.equals(MarketDataIncrementalRefresh.MSGTYPE)){
//            log.info("Received MD: {}", message);
//        }
//    }
}

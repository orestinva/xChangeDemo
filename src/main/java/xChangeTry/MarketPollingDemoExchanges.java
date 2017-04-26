package xChangeTry;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitcoinde.BitcoindeExchange;
import org.knowm.xchange.bitkonan.BitKonanExchange;
import org.knowm.xchange.bitstamp.Bitstamp;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.livecoin.LivecoinExchange;
import org.knowm.xchange.poloniex.Poloniex;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.internal.Series;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by inva on 12/4/2016.
 */
public class MarketPollingDemoExchanges implements Runnable{

    MarketPollingGUI gui;

    public MarketPollingDemoExchanges(MarketPollingGUI gui){
        this.gui = gui;

    }

    public void run() {
        // Use the factory to get the version 1 Bitstamp and Bitkonan exchange API using default settings
        Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
        Exchange liveCoin = ExchangeFactory.INSTANCE.createExchange(LivecoinExchange.class.getName());

        // Interested in the public market data feed (no authentication)
        MarketDataService bitstampDataService = bitstamp.getMarketDataService();
        MarketDataService liveCoinDataService = liveCoin.getMarketDataService();

        System.out.println("fetching data...");

        // Get the current orderbooks
        OrderBook orderBookBitStamp = null;
        OrderBook orderBookLiveCoin = null;
        try {
            orderBookBitStamp = bitstampDataService.getOrderBook(gui.getCurrencyPair());
            orderBookLiveCoin = liveCoinDataService.getOrderBook(gui.getCurrencyPair());
        } catch (IOException e) {
            gui.getOptionPane().showMessageDialog(gui, "Invalid currency pair");
        }

        System.out.println("received data.");

        System.out.println("plotting...");


        // get BIDS
        ArrayList<BigDecimal> xBidData = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> yBidData = new ArrayList<BigDecimal>();
        getBids(orderBookBitStamp, xBidData, yBidData, 10);

        // Create Chart, add bids
        XYChart chart = QuickChart.getChart("BitStamp Order Book", "USD", "BTC", "BitStamp bids", xBidData, yBidData);

        // get ASKS
        ArrayList<BigDecimal> xAskData = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> yAskData = new ArrayList<BigDecimal>();
        getAsks(orderBookBitStamp, xAskData, yAskData, 10000);

        // add Asks Series to chart
        Series series = chart.addSeries("BitStamp asks", xAskData, yAskData);


        ArrayList<BigDecimal> xBidDataTwo = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> yBidDataTwo = new ArrayList<BigDecimal>();
        getBids(orderBookLiveCoin, xBidDataTwo, yBidDataTwo, 10);

        XYChart chart2 = QuickChart.getChart("LiveCoin Order Book", "USD", "BTC", "LiveCoin bids", xBidDataTwo, yBidDataTwo);

        //other asks Asks
        ArrayList<BigDecimal> xAskdataTwo = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> yAskDataTwo = new ArrayList<BigDecimal>();

        getAsks(orderBookLiveCoin, xAskdataTwo, yAskDataTwo, 10000);

        series = chart2.addSeries("LiveCoin asks", xAskdataTwo, yAskDataTwo);

        //adding charts to list for wrapper
        List<XYChart> charts = new ArrayList<XYChart>();
        charts.add(chart);
        charts.add(chart2);

        XChartPanel chartPanel = new XChartPanel(chart);
        XChartPanel chartPanel2 = new XChartPanel(chart2);
        chartPanel.setVisible(true);
        chartPanel2.setVisible(true);
        gui.getCenterpanel().removeAll();
        gui.getCenterpanel().add(chartPanel).setVisible(true);
        gui.getCenterpanel().add(chartPanel2).setVisible(true);
        gui.getCenterpanel().updateUI();

    }

    public void getBids(OrderBook orderBook, ArrayList<BigDecimal> xData, ArrayList<BigDecimal> yData, int threshold){
        BigDecimal accumulatedUnits = new BigDecimal("0");
        for (LimitOrder limitOrder : orderBook.getBids()) {
            if (limitOrder.getLimitPrice().intValue() > threshold) {
                xData.add(limitOrder.getLimitPrice());
                accumulatedUnits = accumulatedUnits.add(limitOrder.getTradableAmount());
                yData.add(accumulatedUnits);
            }
        }
        Collections.reverse(xData);
        Collections.reverse(yData);
    }

    public void getAsks(OrderBook orderBook, ArrayList<BigDecimal> xData, ArrayList<BigDecimal> yData, int threshold){
        BigDecimal accumulatedUnits = new BigDecimal("0");
        for (LimitOrder limitOrder : orderBook.getAsks()) {
            if (limitOrder.getLimitPrice().intValue() < threshold) {
                xData.add(limitOrder.getLimitPrice());
                accumulatedUnits = accumulatedUnits.add(limitOrder.getTradableAmount());
                yData.add(accumulatedUnits);
            }
        }
    }
}

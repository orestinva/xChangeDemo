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


        // BIDS
        ArrayList<Number> xData = new ArrayList<Number>();
        ArrayList<Number> yData = new ArrayList<Number>();
        BigDecimal accumulatedBidUnits = new BigDecimal("0");
        for (LimitOrder limitOrder : orderBookBitStamp.getBids()) {
            if (limitOrder.getLimitPrice().intValue() > 10) {
                xData.add(limitOrder.getLimitPrice());
                accumulatedBidUnits = accumulatedBidUnits.add(limitOrder.getTradableAmount());
                yData.add(accumulatedBidUnits);
            }
        }
        Collections.reverse(xData);
        Collections.reverse(yData);

        // Create Chart
        XYChart chart = QuickChart.getChart("BitStamp Order Book", "USD", "BTC", "BitStamp bids", xData, yData);

        // ASKS
        ArrayList<Number> xAskData = new ArrayList<Number>();
        ArrayList<Number> yAskData = new ArrayList<Number>();
        BigDecimal accumulatedAskUnits = new BigDecimal("0");
        for (LimitOrder limitOrder : orderBookBitStamp.getAsks()) {
            if (limitOrder.getLimitPrice().intValue() < 10000) {
                xAskData.add(limitOrder.getLimitPrice());
                accumulatedAskUnits = accumulatedAskUnits.add(limitOrder.getTradableAmount());
                yAskData.add(accumulatedAskUnits);
            }
        }

        // Asks Series
        Series series = chart.addSeries("BitStamp asks", xAskData, yAskData);


        List<Number> xDataAnx = new ArrayList<Number>();
        List<Number> yDataAnx = new ArrayList<Number>();
        BigDecimal acumulatedAnxUnits = new BigDecimal("0");
        for (LimitOrder limitOrder : orderBookLiveCoin.getBids()) {
            if (limitOrder.getLimitPrice().intValue() > 10) {
                xDataAnx.add(limitOrder.getLimitPrice());
                acumulatedAnxUnits = acumulatedAnxUnits.add(limitOrder.getTradableAmount());
                yDataAnx.add(acumulatedAnxUnits);
            }
        }
        XYChart chart2 = QuickChart.getChart("LiveCoin Order Book", "USD", "BTC", "LiveCoin bids", xDataAnx, yDataAnx);

        //other asks Asks
        xDataAnx = new ArrayList<Number>();
        yDataAnx = new ArrayList<Number>();
        BigDecimal acumulatedAnxAskUnits = new BigDecimal("0");
        for (LimitOrder limitOrder : orderBookLiveCoin.getAsks()) {
            if (limitOrder.getLimitPrice().intValue() < 10000) {
                xDataAnx.add(limitOrder.getLimitPrice());
                acumulatedAnxAskUnits = acumulatedAnxAskUnits.add(limitOrder.getTradableAmount());
                yDataAnx.add(acumulatedAnxAskUnits);
            }
        }
        series = chart2.addSeries("LiveCoin asks", xDataAnx, yDataAnx);

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
}

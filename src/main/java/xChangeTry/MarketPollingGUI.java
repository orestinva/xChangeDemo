package xChangeTry;


import org.knowm.xchange.currency.CurrencyPair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by inva on 12/4/2016.
 */
public class MarketPollingGUI extends JFrame {

    private JPanel rootpanel = new JPanel(new BorderLayout());
    private JPanel centerpanel = new JPanel(new FlowLayout());
    private JComboBox currencyOneComboBox;
    private JLabel currencyOneLabel = new JLabel("Currency 1: ");
    private JComboBox currencyTwoComboBox;
    private JLabel currencyTwoLabel = new JLabel("Currency 2: ");
    private JPanel currencyPanel = new JPanel(new FlowLayout());
    private JButton goButton = new JButton("Go");
    private JOptionPane optionPane = new JOptionPane();

    public MarketPollingGUI(){
        super("xChange Demo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String[] cryptos = new String[] {"BTC"};
        currencyOneComboBox = new JComboBox(cryptos);
        currencyOneComboBox.setSelectedIndex(-1);
        currencyOneComboBox.setVisible(true);
        currencyOneComboBox.setPreferredSize(new Dimension(60, 20));
        String[] curs = new String[] {"USD", "EUR"};
        currencyTwoComboBox = new JComboBox(curs);
        currencyTwoComboBox.setSelectedIndex(-1);
        currencyTwoComboBox.setVisible(true);
        currencyTwoComboBox.setPreferredSize(new Dimension(60, 20));

        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new MarketPollingDemoExchanges(MarketPollingGUI.this)).start();
            }
        });

        currencyPanel.add(currencyOneLabel);
        currencyPanel.add(currencyOneComboBox);
        currencyPanel.add(currencyTwoLabel);
        currencyPanel.add(currencyTwoComboBox);
        currencyPanel.add(goButton);
        currencyPanel.setVisible(true);

        centerpanel.setVisible(true);
        optionPane.setVisible(false);

        rootpanel.add(currencyPanel, BorderLayout.NORTH);
        rootpanel.setPreferredSize(new Dimension(1350, 450));
        rootpanel.setVisible(true);
        rootpanel.add(centerpanel, BorderLayout.CENTER);
        setContentPane(rootpanel);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }

    public CurrencyPair getCurrencyPair(){

        String currencyOne = currencyOneComboBox.getSelectedItem().toString();
        String currencyTwo = currencyTwoComboBox.getSelectedItem().toString();

        if(currencyOne != null && currencyTwo != null){
            return new CurrencyPair(currencyOne, currencyTwo);
        }

        return null;
    }

    public JOptionPane getOptionPane() {
        return optionPane;
    }

    public JPanel getCenterpanel() {
        return centerpanel;
    }
}

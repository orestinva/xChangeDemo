package xChangeTry;

/**
 * Main Class in program used to start the process.
 * Displays bid-ask charts for two crypto currency exchanges:
 * BitStamp and LiteCoin for currency pairs BTC-USD and BTC-EUR.
 *
 * @author  Orest Reveha
 * @version 1.0
 *
 */
public class xChangeDemo
{
    /**
     * Runs this program.
     * Includes creating of GUI
     */
    public static void main( String[] args )
    {
        MarketPollingGUI gui = new MarketPollingGUI();
    }

}


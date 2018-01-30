/**
 * 
 */
/**
 * @author nizeyu
 *
 */
package demo;


public class Test{
	/** Main method */
	public static void main(String[] args) throws Exception {
		System.out.println("Hello");
        String budgetDataFilePath = "/Users/nizeyu/workspace/project/SearchAds/IndexBuilderTest/IndexBuilder/BudgetData.txt";
        String memcachedServer = "127.0.0.1";
        String mysqlHost = "127.0.0.1:3306";
        String mysqlDb = "searchads";
        String mysqlUser = "root";
        String mysqlPass = "";
        int memcachedPortal = 11211;
        AdsEngine adsEngine = new AdsEngine(budgetDataFilePath, memcachedServer, memcachedPortal,
                mysqlHost, mysqlDb, mysqlUser, mysqlPass);

        adsEngine.initBudget();
        adsEngine.initAd();
    }
}
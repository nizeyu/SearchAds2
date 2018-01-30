/**
 * 
 */
/**
 * @author nizeyu
 *
 */
package demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;

public class QueryRewrite{
	private static int EXP = 0; //0: never expire
	private static MemcachedClient cache;
	
	//construct query rewrite online
	public List<List<String>> OnlineQueryRewrite(List<String> queryTerms, Map<String, List<String>> synonyms_dict) {
		List<List<String>> res = new ArrayList<List<String>>();
		List<List<String>> resTemp = new ArrayList<List<String>>();
		List<List<String>> allSynonymList = new ArrayList<List<String>>();
		try {
			for(String queryTerm:queryTerms) {
				if(synonyms_dict.get(queryTerm) instanceof List) {
					List<String>  synonymList = (List<String>)synonyms_dict.get(queryTerm);
					allSynonymList.add(synonymList);	
				} else {
					List<String>  synonymList = new ArrayList<String>();
					synonymList.add(queryTerm);
					allSynonymList.add(synonymList);
				}	
			}
			
			int len = queryTerms.size();
			//System.out.println("len of queryTerms = " + len);
			ArrayList<String> queryTermsTemp = new ArrayList<String>();
			QueryRewriteHelper(0, len, queryTermsTemp, allSynonymList, resTemp);	
			

			//dedupe
			Set<String> uniquueQuery = new HashSet<String>();
			for(int i = 0;i < resTemp.size();i++) {
				String hash = Utility.strJoin(resTemp.get(i), "_");
				if(uniquueQuery.contains(hash)) {
					continue;
				}
				uniquueQuery.add(hash);
				Set<String> uniquueTerm = new HashSet<String>();
				for(int j = 0;j < resTemp.get(i).size();j++) {
					String term = resTemp.get(i).get(j);
					if(uniquueTerm.contains(term)) {
						break;
					}
					uniquueTerm.add(term);
				}
				if (uniquueTerm.size() == len) {
					res.add(resTemp.get(i));
				}
			}
			//debug
//			for(int i = 0;i < res.size();i++) {
//				System.out.println("synonym");
//				for(int j = 0;j < res.get(i).size();j++) {
//					System.out.println("query term = " + res.get(i).get(j));
//				}			
//			}
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return res;
	}
	
	private void QueryRewriteHelper(int index, int len, ArrayList<String> queryTermsTemp, List<List<String>> allSynonymList, List<List<String>> res) {
		if(index == len) {
			res.add(queryTermsTemp);
			return;
		}
		List<String> synonyms = allSynonymList.get(index);
		for(int i = 0; i < synonyms.size();i++) {			
			ArrayList<String> queryTerms = (ArrayList<String>) queryTermsTemp.clone();
			queryTerms.add(synonyms.get(i));
			QueryRewriteHelper(index + 1, len, queryTerms, allSynonymList, res);
		}	
	}
	
	/** Main method */
	public static void main(String[] args) throws Exception {
		String synonyms_input_file = "/Users/nizeyu/workspace/project/SearchAds/QueryRewriteTest/synonyms_0502.txt";
		String ads_input_file = "/Users/nizeyu/workspace/project/SearchAds/QueryRewriteTest/ads_0502.txt";
		String memcachedServer = "127.0.0.1";
        int memcachedPortal = 11211;
		QueryRewrite qr = new QueryRewrite();
		
		String address = memcachedServer + ":" + memcachedPortal;
		try 
		{
			cache = new MemcachedClient(new ConnectionFactoryBuilder().setDaemon(true).setFailureMode(FailureMode.Retry).build(), AddrUtil.getAddresses(address));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, List<String>> synonyms_dict = new HashMap<>();
		try (BufferedReader brBudget = new BufferedReader(new FileReader(synonyms_input_file))) {
			String line;
			while ((line = brBudget.readLine()) != null) {
				JSONObject json = new JSONObject(line);
				String word = json.getString("word");
				JSONArray arr = json.getJSONArray("synonyms");
				List<String> list = new ArrayList<String>();
				for(int i = 0; i < arr.length(); i++){
				    list.add(arr.optString(i));
				}
				list.add(word);
				synonyms_dict.put(word, list);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<String> querySet = new HashSet<>();
		try (BufferedReader brBudget = new BufferedReader(new FileReader(ads_input_file))) {
			String line;
			while ((line = brBudget.readLine()) != null) {
				JSONObject json = new JSONObject(line);
				String entry = json.getString("query");
				if (!querySet.contains(entry)) {
					querySet.add(entry);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		for (String query: querySet) {
			String[] temp = query.split(" ");
			List<String> queryTerms = new ArrayList<>();
			String str = "";
			for (String s : temp) {
				queryTerms.add(s.toLowerCase());
				str += s.toLowerCase();
			}
			
			List<List<String>> res = qr.OnlineQueryRewrite(queryTerms, synonyms_dict);	
			cache.set(str, EXP, res);
			System.out.println("Store query&queryRewrite to memcached-- " + query + ": " + res);
		} 
    }
}
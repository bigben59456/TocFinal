/*#####################
姓名 : 吳邦宇

學號 : F74006292

簡述 : 
組合的方式照順序，比如說 123 -> 12 13 23
另外，為了減少運算次數(迴圈)，所以像 123 的 2組合 就不可能有 3開頭 的所以找到2之後就不繼續
EX : for迴圈中 index1<=KEY.size()-combination 即是這樣的概念
將每個的值(key)存進 HashMap，若存過則次數(value)+1
用 List 將 Map 依照次數(value)排列後取得要求名次的次數
由該要求名次往後找若次數相同則 index+1
將 0~index 的資料寫到 output.txt

參考 : 檔案的讀取 http://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
#####################*/
import java.io.*;
import java.net.*;
import org.json.*;
import java.util.*;
import java.nio.charset.Charset;

public class TocFinal
{
	public static void main(String[] args) throws IOException, JSONException
	{
		JSONArray json = readJsonFromUrl(args[0]); //web url = args[0]
		int combination=Integer.valueOf(args[2]); //combination L
		FileWriter output=new FileWriter("output.txt");
		
		HashMap<String, Integer> table=new HashMap<String, Integer>(); //the answer table
		
		ArrayList<String> KEY=new ArrayList<String>(); //save every keys
		String [] KeyValue=new String[]{"鄉鎮市區","交易標的","土地區段位置或建物區門牌","土地移轉總面積平方公尺","都市土地使用分區","非都市土地使用分區","非都市土地使用編定","交易年月","交易筆棟數","移轉層次","總樓層數","建物型態","主要用途","主要建材","建築完成年月","建物移轉總面積平方公尺","建物現況格局-房","建物現況格局-廳","建物現況格局-衛","建物現況格局-隔間","有無管理組織","總價元","單價每平方公尺","車位類別","車位移轉總面積平方公尺","車位總價元"}; //all key value
		for(int i=0 ;i<26 ;++i) if(json.getJSONObject(0).has(KeyValue[i])) KEY.add(KeyValue[i]); //use key
		
		for(int i=0 ;i<json.length() ;++i) //every json obj in array
		{
			for(int index1=0 ;index1<=KEY.size()-combination ;++index1) //combine
			{
				if(String.valueOf(json.getJSONObject(i).get(KEY.get(index1))).equals("")) continue; //if "" ignore
				String str1=String.valueOf(KEY.get(index1)); //key
				str1=str1+":"+String.valueOf(json.getJSONObject(i).get(KEY.get(index1))); //value
				for(int index2=index1+1 ;index2<=KEY.size()-combination+1 ;++index2)
				{
					if(String.valueOf(json.getJSONObject(i).get(KEY.get(index2))).equals("")) continue;
					String str2=String.valueOf(KEY.get(index2)); //key
					str2=str2+":"+String.valueOf(json.getJSONObject(i).get(KEY.get(index2))); //value
					if(combination==2)
					{						
						String combine_str=str1+","+str2+";";
						if(table.get(combine_str)!=null) table.put(combine_str ,table.get(combine_str)+1); //has set the data
						else table.put(combine_str ,1); //first time find this kind of data
					}
					else //combination==3 || combination==4
					{
						for(int index3=index2+1 ;index3<=KEY.size()-combination+2 ;++index3)
						{
							if(String.valueOf(json.getJSONObject(i).get(KEY.get(index3))).equals("")) continue;
							String str3=String.valueOf(KEY.get(index3));
							str3=str3+":"+String.valueOf(json.getJSONObject(i).get(KEY.get(index3)));
							if(combination==3)
							{
								String combine_str=str1+","+str2+","+str3+";";
								if(table.get(combine_str)!=null) table.put(combine_str ,table.get(combine_str)+1); //has set the data
								else table.put(combine_str ,1); //first time find this kind of data
							}
							else //combination==4
							{
								for(int index4=index3+1 ;index4<KEY.size() ;++index4)
								{
									if(String.valueOf(json.getJSONObject(i).get(KEY.get(index4))).equals("")) continue;
									String str4=String.valueOf(KEY.get(index4));
									str4=str4+":"+String.valueOf(json.getJSONObject(i).get(KEY.get(index4)));
									String combine_str=str1+","+str2+","+str3+","+str4+";";
									if(table.get(combine_str)!=null) table.put(combine_str ,table.get(combine_str)+1); //has set the data
									else table.put(combine_str ,1); //first time find this kind of data
								}
							}
						}
					}
				}
			}
		}
		List<Map.Entry<String ,Integer>> sort_table=new ArrayList<Map.Entry<String ,Integer>>(table.entrySet()); //sort the table
		Collections.sort //sort the table
		(
			sort_table ,
			new Comparator<Map.Entry<String, Integer>>() //sort function
			{
				public int compare(Map.Entry<String, Integer> value1 ,Map.Entry<String, Integer> value2)
				{
					return (value2.getValue() - value1.getValue());
				}
			}
		);
		
		int EndValue=sort_table.get(Integer.valueOf(args[1])-1).getValue(); //the data with args[1] rank -> value (rank 500 is 499th)
		int output_index=Integer.valueOf(args[1]); //should print to this data at least
		for(int i=output_index ;sort_table.get(i).getValue()==EndValue ;++i ,++output_index); //the index +1
		for(int k=0 ;k<output_index ;++k) output.write(sort_table.get(k).getKey()+sort_table.get(k).getValue()+"\n"); //output the data
		output.flush();
		output.close();
	}
	
	private static String readAll(Reader rd) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while((cp=rd.read())!=-1) sb.append((char)cp); //get one char and put into string
		return sb.toString(); //return a string
	}

	public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException
	{
		InputStream is = new URL(url).openStream(); //string of url
		try
		{
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))); //use UTF-8 to read the url and save in rd
			String jsonText = readAll(rd);
			JSONArray jsonRealPrice = new JSONArray(jsonText); //save in json array format
			return jsonRealPrice; //return the json array
		}
		finally
		{
			is.close(); //close the InputStream is
		}
	}
}

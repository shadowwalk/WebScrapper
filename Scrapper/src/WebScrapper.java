import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * @author WJD
 * Simple WebScrapper class that contains static method to send api request to shopping.com and parse result
 */

public class WebScrapper {
	
	static final int RESULTS_PER_PAGE = 40;
	
	/**
	 * This is the entrance for the class, it just parse the input arguments then decide which method to call
	 * or print message to require correct argument number if get incorrect argument number
	 * @param args  String array for input arguments, should contain product name for 1st argument	
	 * 				with optional page number in 2nd argument. Note that words wrapped in colon count as 1 argument
	 * @return void
	 */
	
	public static void main(String[] args) {
		//do the 1st query for item number
		if(args.length == 1){
			WebScrapper.queryOne(args[0]);
		}
		//do the 2nd query for item info with specific page
		//this search will return a result object and print it, since we cannot using the return value
		//in main method just ignore the return value
		else if (args.length == 2){
			WebScrapper.queryTwo(args[0],args[1]);
		}
		else{
			System.out.println("Please give a valid query with 1 or 2 argument");
		}
			
	}
	
	
	/**
	 * Query 1 that print Total number of results for specific item
	 * it construct request url first then read the content to a string then parse it
	 * here we use regular expression method to parse the result by calling parseForItem() method
	 * 
	 * @param item name in string
	 * @return void
	 */
	private static void queryOne(String item){
		//System.out.println("the query item is:"+item);
		BufferedReader br = null;
		try {
			
			//appended the encoded item name to the end of the api request
	        URL my_url = new URL("http://sandbox.api.shopping.com/publisher/3.0/rest/GeneralSearch?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&trackingId=7000610&keyword="
	        		+ URLEncoder.encode(item,"UTF-8"));
	        //we start read the response and load it into string buffer later pass to parsing method
	        br = new BufferedReader(new InputStreamReader(my_url.openStream()));
	        StringBuffer sb = new StringBuffer();
	        String strTemp = "";
	        while((strTemp = br.readLine()) != null ){	
	        	sb.append(strTemp);
	        }
	        //call the parsing method
	        parseForItem(sb);
	    //process the exception from this method or thrown by the parseForItem() method 
		} catch (MalformedURLException ex) {
	        System.out.println("mal formed url find");
	        System.out.println(ex);
	    } catch (IOException ioe) {
	    	System.out.println("io exception find");
	    	System.out.println(ioe);
		} catch  (IllegalStateException|IndexOutOfBoundsException  ie){
	    	System.out.println("match exception find");
	    	System.out.println(ie);			
		}finally {
			//make sure BufferedReader get closed
	        if (br != null) {
	            try {
					br.close();
				} catch (IOException e) {
					System.out.println(e);
				}
	        }
	    }
	}
	
	/**
	 * This is the parse method for query 1. Since only need to get the item number for the item
	 * we just use regular expression to get that attribute.
	 * the response is xml text, we will get the specific item count attribute by matching. If the 
	 * matchedItemCount is 0 means no item return. Also if the categoryCount is 0 always means no such item
	 * since it's a parent attribute with higher level we just need check this count to decide whether it's empty
	 * 
	 * @param sb response from web site to be parsed, in XML format but process it as simple string by regular expression
	 * @return void
	 * @exception IllegalStateException,IndexOutOfBoundsException
	 */	
	
	public static void parseForItem(StringBuffer sb) throws IllegalStateException,IndexOutOfBoundsException {
		//get pattern and matcher for category count
		Pattern queryCatPattern = Pattern.compile("matchedCategoryCount=\"([0-9]*?)\"");
		Matcher queryMatcher = queryCatPattern.matcher(sb.toString());
		//get pattern and matcher for actual item count
		Pattern queryItemPattern = Pattern.compile("matchedItemCount=\"([0-9]*?)\"");
		Matcher queryItemMatcher = queryItemPattern.matcher(sb.toString());
		//if not find category means this item not valid
		if(queryMatcher.find()){
			if(!queryMatcher.group(1).equals("0")){
				//check how many item only if the category count is not zero
				if(queryItemMatcher.find()){
					System.out.println("total result number is:" + queryItemMatcher.group(1));
				}
				else{
					System.out.println("No Item found");						
				}
			}
			else{
				//if the category count is not zero directly show total number is 0
				System.out.println("The result number is 0");
			}
		}
		else{
			System.out.println("nothing find");
		}	
	}
	
	/**
	 * Query 2 will return a result object and print it. this object will store all items info user requested
	 * The 2 input parameter is user defined which means the keyword to search and the page number
	 * Here we make items/page to a fix number 40 as RESULTS_PER_PAGE
	 * This method will first parse the response from shopping.com to a DOM using jdom
	 * Then it will call the parse method to further parse the DOM object and get the result object
	 * Note if no search result for that keyword or no result on that page(eg. too large pagenumber) it will return null
	 * 
	 * @param item the keyword to be searched
	 * @param pageNum which specific page number user want to see 
	 * @return Result the result object represent the search result
	 */
	private static Result queryTwo(String item, String pageNum){
		//here is just check whether the 2nd argument is a valid integer for page
		try {
			Integer.parseInt(pageNum);
		}
		catch(NumberFormatException ne){
			System.out.println("Please give a valid page number");
			return null;
		}

		try {
			//here construct url to make api request for certain keyword, items per page and page number
	        URL my_url = new URL("http://sandbox.api.shopping.com/publisher/3.0/rest/GeneralSearch?apiKey=78b0db8a-0ee1-4939-a2f9-d3cd95ec0fcc&trackingId=7000610"
	        		+ "&keyword=" + URLEncoder.encode(item,"UTF-8")
	        		+ "&numItems="+Integer.toString(RESULTS_PER_PAGE)
	        		+ "&pageNumber="+pageNum);
	        //begin to parse the returned response XML text form shopping.com to a DOM
	        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = builderFactory.newDocumentBuilder();
	        Document doc = builder.parse(my_url.openStream());
	        Element rootElement = doc.getDocumentElement();
	        //call the parseDOM() method to get the result object from DOM
	        Result resultObj = parseDOM(rootElement);
	        //here print the result
	        if(resultObj != null){
	        	System.out.println(resultObj.toString());
	        	return resultObj;
	        }
	        else{
	        	System.out.println("No result on this item or on this page");
	        	return null;
	        }
	    //process the exception from this method or thrown by the parseDOM() method 
	    } catch (MalformedURLException ex) {
	        System.out.println("incorrect url format");
	        System.out.println(ex);
	        return null;
	    } catch (IOException ex) {
	    	System.out.println("io exception find");
	    	System.out.println(ex);
	    	return null;
		}catch (ParserConfigurationException pe) {
			System.out.println(pe);
			return null;
        }catch(SAXException se){
        	System.out.println(se);
        	return null;
        }
	}
	
	/**
	 * This is the parse method for query 2. The input is DOM object (the root element to be more accurate)
	 * and it will do a further parsing that produce a result object
	 * we basically first go deep in the DOM tree then traverse each item if met certain attribute or element
	 * we put the value of these specific nodes into the result object and return it
	 * 	 
	 * @param rootElement the rootElement of the DOM tree to be parsed
	 * @return Result result object represent the search result
	 * @exception ParserConfigurationException,SAXException
	 */	
	
	public static Result parseDOM(Element rootElement) throws ParserConfigurationException,SAXException{
		//produce the result object
		Result parseResult = new Result(RESULTS_PER_PAGE);
        //go deep into the DOM tree
		Node categories = rootElement.getElementsByTagName("categories").item(0);
        if(categories.getAttributes().getNamedItem("matchedCategoryCount").getNodeValue().equals("0")){
        	return null;
        }
        Node catChild = categories.getFirstChild();
        while(catChild != null){
        	//this category node have items as its child which items is the branch we want to traverse
        	if (catChild.getNodeName().equals("category")){
        		Node items = catChild.getFirstChild();
        		while(items != null){
        			if(items.getNodeName().equals("items")){
        				Node entry = items.getFirstChild();
        				//here we begin to traverse each item, represent by entry
        				//we will advance the each item and result object in this while loop
        				while(entry != null){
        					//check whether this item is product or offer, since the parsing rule is different for them
        					boolean isProduct = entry.getNodeName().equals("product");
        					//set the state for result object
        					if(isProduct){
        						parseResult.setType("Product");
        					}
        					else{
        						parseResult.setType("Offer");
        					}
        					Node entryPart = entry.getFirstChild();
        					//here we will traverse each attribute of that particular item 
        					//and set the value to that correspond result list
        					while(entryPart != null){
        						if(entryPart.getNodeName().equals("name")){
        							parseResult.setName(entryPart.getFirstChild().getNodeValue());
        						}
        						else if(entryPart.getNodeName().equals("minPrice") && isProduct){
        							parseResult.setPrice(entryPart.getFirstChild().getNodeValue());
        						}
        						else if(entryPart.getNodeName().equals("basePrice") &&  !isProduct){
        							parseResult.setPrice(entryPart.getFirstChild().getNodeValue());
        						}
        						else if(entryPart.getNodeName().equals("store") && !isProduct){
        							parseResult.setVender(entryPart.getFirstChild().getFirstChild().getNodeValue());
        						}	        						
        						else if(entryPart.getNodeName().equals("shippingCost") && !isProduct){
        							if(Double.parseDouble(entryPart.getFirstChild().getNodeValue()) == 0){
        								parseResult.setShip("freeShipping");
        							}
        							else{
        								parseResult.setShip(entryPart.getFirstChild().getNodeValue());
        							}
        							
        						}
        						else if(entryPart.getNodeName().equals("numStores") && isProduct){
        							parseResult.setVender(entryPart.getFirstChild().getNodeValue()+" stores");
        						}
        						entryPart = entryPart.getNextSibling();
        					}
        					//since there's no ship price tag for product, we will set it to free
    						if (isProduct){
    							parseResult.setShip("free");
    						}
        					parseResult.advance();
        					entry = entry.getNextSibling();
        				}
        			}
        			items = items.getNextSibling();
        		}
        	}
        	catChild = catChild.getNextSibling();
        }
        return parseResult;
		
	}
	
}

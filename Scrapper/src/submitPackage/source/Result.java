import java.util.ArrayList;
/**
 * @author WJD
 * here is a simple Result class we use to store the information of our search result
 * it has a inner class Item represent each item. We store all item in a ArryList 
 *  
 */

public class Result {
	
	//the list for result
	private ArrayList<Item> itemList;
	//current position, like iterator
	private int currentIndex = 0;
	//maxSize for this itemList, use for boundary check
	private int maxSize;
	
	//initialize the Result to an List full of empty Item
	public Result(int size){
		itemList = new ArrayList<Item>();
		for(int i=0;i<size;i++){
			maxSize = size;
			itemList.add(new Item());
		}
	}
	//method use to print this result object. just append all info to a string buffer
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<maxSize;i++){
			//if this item exist
			if(!itemList.get(i).getType().equals("")){
				sb.append(itemList.get(i).getType() + Integer.toString(i) + ":\n");
				sb.append("----Name:" + itemList.get(i).getName() + "\n");
				sb.append("----Price:$" + itemList.get(i).getPrice() + "\n");
				//here to get rid of the $ sign if it's a free shipping item
				if(itemList.get(i).getShip().equals("free") && itemList.get(i).getShip().equals("freeShipping"))
					sb.append("----Shipping Price:" + itemList.get(i).getShip() + "\n");
				//in case there's no ship price tag for offer,give it as free shipping
				else if(itemList.get(i).getShip().equals("")){
					sb.append("----Shipping Price:freeShipping\n");
				}
				else
					sb.append("----Shipping Price:$" + itemList.get(i).getShip() + "\n");
				sb.append("----Vender:" + itemList.get(i).getVender() + "\n");
			}
		}
		return sb.toString();
	}
	
	//advance or rewind the pointer to the Item object in the itemList
	//if reach boundary, it just won't change
	public int advance(){
		if (currentIndex < maxSize){
			currentIndex++;
		}
		return currentIndex;
	}
	public int back(){
		if (currentIndex > 0){
			currentIndex--;
		}
		return currentIndex;
	}	
	
	//setters to that pointed item object in the itemList
	public void setType(String type){
		itemList.get(currentIndex).setType(type);
	}
	public void setName(String name){
		itemList.get(currentIndex).setName(name);
	}
	public void setPrice(String price){
		itemList.get(currentIndex).setPrice(price);
	}
	public void setVender(String vender){
		itemList.get(currentIndex).setVender(vender);
	}
	public void setShip(String ship){
		itemList.get(currentIndex).setShip(ship);
	}
	//getters to that pointed item object in the itemList
	public String getType(){
		return itemList.get(currentIndex).getType();
	}		
	public String getName(){
		return itemList.get(currentIndex).getName();
	}		
	public String getPrice(){
		return itemList.get(currentIndex).getPrice();
	}		
	public String getVender(){
		return itemList.get(currentIndex).getVender();
	}		
	public String getShip(){
		return itemList.get(currentIndex).getShip();
	}			
	
	
	/**
	 * 
	 * Simple Item class to represent each single item in the items list
	 * use a string array to store info for that item
	 * only have simple constructor to set all field to empty string
	 * and setter/getter to be called from parent class
	 *
	 */
	private class Item{
		static final int ATTR_LENGTH = 5;
		static final int TYPE_INDEX = 0;
		static final int NAME_INDEX = 1;
		static final int PRICE_INDEX = 2;
		static final int VENDER_INDEX = 3;
		static final int SHIP_INDEX = 4;
		
		private String[] infoList;
		private Item(){
			infoList = new String[ATTR_LENGTH];
			for(int i=0;i<ATTR_LENGTH;i++){
				infoList[i] = "";
			}
		}
		private void setType(String type){
			infoList[TYPE_INDEX] = type;
		}	
		private void setName(String name){
			infoList[NAME_INDEX] = name;
		}		
		private void setPrice(String price){
			infoList[PRICE_INDEX] = price;
		}		
		private void setVender(String vender){
			infoList[VENDER_INDEX] = vender;
		}		
		private void setShip(String ship){
			infoList[SHIP_INDEX] = ship;
		}	
		
		private String getType(){
			return infoList[TYPE_INDEX];
		}			
		private String getName(){
			return infoList[NAME_INDEX];
		}		
		private String getPrice(){
			return infoList[PRICE_INDEX];
		}		
		private String getVender(){
			return infoList[VENDER_INDEX];
		}		
		private String getShip(){
			return infoList[SHIP_INDEX];
		}		
	
	}
}


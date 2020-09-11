package logic;

import java.util.ArrayList;
import java.util.List;

public class Cart {                                                   
	private List<ItemSet> itemSetList = new ArrayList<ItemSet>();
	public List<ItemSet> getItemSetList(){
		return itemSetList;
	}
	public void push(ItemSet itemSet) {
		for(ItemSet it : itemSetList) {
			if(it.getItem().getId().equals(itemSet.getItem().getId())) {
				it.setQuantity(it.getQuantity() + itemSet.getQuantity());
				return;
			}
		}
		itemSetList.add(itemSet);		
	}
	public long getTotal() {
		long sum = 0;
		for(ItemSet is : itemSetList) {
			sum += is.getItem().getPrice() * is.getQuantity();
		}
		return sum;
	}

}

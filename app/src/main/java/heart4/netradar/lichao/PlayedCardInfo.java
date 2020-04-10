package heart4.netradar.lichao;

import java.util.ArrayList;

public class PlayedCardInfo {
	boolean isNoCard;
	int[] cards;
	
	public PlayedCardInfo(boolean isNoCard, ArrayList<Integer> cardList) {
		
		this.isNoCard = isNoCard;
		if(!isNoCard)
		{
			cards=new int[cardList.size()];
			for(int i=0;i<cardList.size();i++)
				cards[i]=cardList.get(i);
		}
	}
	
	

}

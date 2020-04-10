package heart4.netradar.lichao;

import java.util.TimerTask;

public class SecondTimerTask extends TimerTask {
	
	private TimeoutCallback callBack;
	
	public SecondTimerTask(TimeoutCallback callBack) {
		
		this.callBack=callBack;
	}

	@Override
	public void run() {
		
		callBack.onSecondTimeout();
	}
	
	public interface TimeoutCallback 
	{
		public void onSecondTimeout();
		
	}


}

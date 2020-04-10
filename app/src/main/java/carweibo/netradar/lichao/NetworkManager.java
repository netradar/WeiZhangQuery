package carweibo.netradar.lichao;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {

	public static boolean IsNetworkOk(Context context){
		NetworkInfo info=((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(info==null||!info.isAvailable())
			return false;
		return true;
	}
}

package carweibo.netradar.lichao;

import java.util.List;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;

import android.app.Application;
import android.util.SparseArray;

public class DBUility extends Application {

	DBhelper DB;

    String CAR_TABLE="car_table";// = "carinfo";  
    String USER_TABLE="user_table";
    String WEIBO_TABLE="weibo_table";
    String DRAFT_TABLE="draft_table";
    String PRIVATE_MSG_TABLE="private_msg_table";
    String AUDIO_LUKUANG_TABLE="audio_lukuang_table";
 //   String WEIZHANG_CODE_TABLE="weizhang_code_table";
    
    String server="http://s-93271.gotocdn.com:8080/first";
    public String webapps="http://s-93271.gotocdn.com:8080";
    String openfire="211.149.163.44";
    
   /* String server="http://192.168.43.147:8080/first";
    String webapps="http://192.168.43.147:8080";*/
  //  String openfire="192.168.43.147";
   
    String openfire_domain="west23544";//zte-20130929cbk west23544
//    String server="http://192.168.1.103:8080/first";
 //   public String webapps="http://192.168.1.103:8080";/**/
 //    String openfire="192.168.1.100";
    
    List<CodeInfo> code_info;

    SparseArray<CodeInfo>  code_map;
    
    ChatManager chatManager;
    XMPPConnection connection_openfire=null;
    
   int[] code={
		   1091,
		   8001,
		   1303,
		   1044,
		   1039,
		   1019,
		   4305,
		   1301,
		   1352,
		   1042,
		   1043,
		   1018,
		   8363,
		   1603,
		   1721,
		   6011,
		   1636,
		   1024,
		   1345,
		   1090,
		   1344,
		   4614,
		   1302,
		   1208,
		   1625,
		   1211,
		   1244,
		   1025,
		   1240,
		   4204,
		   6023,
		   1045,
		   1232,
		   4605,
		   1046,
		   1223,
		   1225,
		   1229,
		   3019,
		   1616,
		   8663,
		   1633,
		   1212,
		   8344,
		   57011,
		   1230,
		   4008,
		   13448,
		   1614,
		   1243,
		   1231,
		   1207,
		   1075,
		   4010,
		   4608,
		   5038,
		   1209,
		   1021,
		   1022,
		   1350,
		   1355,
		   1023,
		   1635,
		   1723,
		   1349,
		   1111,
		   1102,
		   1718,
		   1206,
		   4306,
		   1615,
		   1074,
		   1038,
		   4009,
		   1632,
		   80481,
		   7005,
		   1309,
		   1001,
		   1036,
		   6014,
		   1717,
		   4613,
		   1634,
		   8071,
		   1719,
		   7001,
		   1325,
		   1205,
		   1331,
		   4312,
		   57041,
		   1357,
		   8001,
		   1344,
		   1019,
		   4305,
		   8001,
		   8001,
		   1039,
		   1352,
		   8001,
		   1636,
		   8001,
		   8044,
		   4706,
		   4702,
		   13521

   };

   int[] money=
	   {
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   200,
		   100,
		   100,
		   50,
		   100,
		   100,
		   200,
		   200,
		   50,
		   100,
		   100,
		   100,
		   100,
		   100,
		   200,
		   200,
		   100,
		   200,
		   100,
		   50,
		   100,
		   50,
		   50,
		   100,
		   100,
		   100,
		   200,
		   100,
		   50,
		   100,
		   100,
		   20,
		   50,
		   200,
		   100,
		   200,
		   100,
		   2000,
		   200,
		   200,
		   200,
		   100,
		   200,
		   100,
		   50,
		   100,
		   200,
		   200,
		   200,
		   100,
		   200,
		   200,
		   100,
		   100,
		   200,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   200,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   0,
		   200,
		   1000,
		   100,
		   200,
		   200,
		   200,
		   100,
		   200,
		   100,
		   0,
		   100,
		   100,
		   50,
		   100,
		   2000,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   100,
		   200,
		   100

	   };
   
   int[] score=
	   {
		   0,
		   0,
		   3,
		   0,
		   0,
		   0,
		   3,
		   3,
		   3,
		   0,
		   0,
		   0,
		   3,
		   6,
		   12,
		   0,
		   6,
		   0,
		   3,
		   0,
		   3,
		   6,
		   3,
		   2,
		   6,
		   2,
		   2,
		   0,
		   2,
		   2,
		   0,
		   0,
		   2,
		   6,
		   0,
		   2,
		   2,
		   2,
		   0,
		   6,
		   6,
		   6,
		   2,
		   3,
		   12,
		   2,
		   0,
		   3,
		   6,
		   2,
		   2,
		   2,
		   0,
		   0,
		   6,
		   0,
		   2,
		   0,
		   0,
		   3,
		   3,
		   0,
		   6,
		   12,
		   3,
		   1,
		   1,
		   12,
		   2,
		   3,
		   6,
		   0,
		   0,
		   0,
		   6,
		   0,
		   0,
		   3,
		   0,
		   0,
		   0,
		   12,
		   6,
		   6,
		   0,
		   12,
		   0,
		   3,
		   2,
		   3,
		   3,
		   12,
		   3,
		   0,
		   3,
		   0,
		   3,
		   0,
		   0,
		   0,
		   3,
		   0,
		   6,
		   0,
		   0,
		   12,
		   12,
		   3

	   };
   String[] type=
	   {
		   "违反警告标线指示",
		   "【电子警察】机动车违反标志标线指示",
		   "超速50％以下",
		   "违反规定掉头",
		   "不按规定停车",
		   "机动车违规使用专用车道",
		   "超速50％以下",
		   "逆向行驶",
		   "驾驶中型以上载客载货汽车、危险物品运输车辆以外的其他机动车行驶超过规定时速未达20%的",
		   "不按车道行驶",
		   "变更车道影响其他车辆行驶",
		   "机动车不走机动车道",
		   "超速50％以下",
		   "超速50％以上（9座以下小客车和小型越货车以下）",
		   "驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的机动车行驶超过规定时速50%以上的",
		   "驾驶人未按规定使用安全带的",
		   "驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的其他机动车行驶超过规定时速20%以上未达到50%的",
		   "无设施路口未依次交替行驶",
		   "违反禁止标线指示",
		   "违反警告标志指示",
		   "违反禁令标志指示",
		   "驾驶营运客车以外的机动车非紧急情况下在高速公路应急车道上停车的",
		   "信号路口不按规定行驶",
		   "不按导向车道行驶",
		   "驾驶机动车违反道路交通信号灯通行的",
		   "信号灯路口越停车线停车",
		   "城市快速路上驾驶人未按规定使用安全带的",
		   "交通拥堵处不按规定停车等候",
		   "驾驶人未按规定使用安全带的",
		   "驾驶人未按规定使用安全带的",
		   "驾驶机动车在高速公路、城市快速路以外的道路上不按规定车道行驶的",
		   "危险路段掉头",
		   "违反警告标线指示",
		   "非紧急情况下应急车道停车",
		   "掉头时妨碍车辆或行驶通行",
		   "驾车接拨手持电话",
		   "驾车时有其他妨碍安全行车的",
		   "违反禁令标志指示",
		   "机动车乘坐人未使用安全带",
		   "不按规定安装机动车号牌的",
		   "超速50％以上（9座以下小客车和小型越货车以下）",
		   "驾驶中型以上载货汽车在高速公路、城市快速路以外的道路上行驶超过规定时速20%以上未达到50%的",
		   "强行右转弯",
		   "客、货车违反禁令标志指示",
		   "使用伪造、变造的机动车号牌的",
		   "违反禁止标线指示",
		   "非紧急情况在应急车道行驶",
		   "客、货车违反禁令标志指示",
		   "故意遮挡机动车号牌的",
		   "道超车或者占用对面车道、穿插等候车辆的",
		   "违反警告标志指示",
		   "不戴安全头盔",
		   "在车门、车厢没有关好时行车的",
		   "在高速公路上骑、轧车行道分界线的",
		   "占用应急车道行驶的",
		   "未按规定喷涂放大号",
		   "左转弯不靠路口中心行驶",
		   "交通拥堵处随意穿插",
		   "交通拥堵处随意穿插",
		   "驾驶中型以上载货汽车在高速公路、城市快速路以外的道路上行驶超过规定时速未达20%的",
		   "驾驶机动车在城市快速路上不按规定车道行驶的",
		   "交通拥堵处未依次交替行驶",
		   "驾驶危险物品运输车辆在高速公路、城市快速路以外的道路上行驶超过规定时速20%以上未达到50%的",
		   "驾驶中型以上载货汽车在城市快速路上行驶超过规定时速20％以上未达50%的",
		   "驾驶中型以上载客汽车在高速公路、城市快速路以外的道路上行驶超过规定时速未达20%的",
		   "机动车载货长度、宽度、高度超过规定的",
		   "不按规定使用灯光",
		   "故意遮挡机动车号牌的",
		   "无信号道路不避让行人",
		   "违规低速行驶",
		   "故意污损机动车号牌的",
		   "不按规定倒车的",
		   "不避让道路养护车、工程作业车的",
		   "施工路段不减速行驶",
		   "驾驶中型以上载客汽车在高速公路、城市快速路以外的道路上行驶超过规定时速20%以上未达到50%的",
		   "禁鸣区鸣喇叭",
		   "在变更车道时影响相关车道机动车正常通行，未造成道路都塞或事故的",
		   "超越执行任务车辆",
		   "驾驶拼装车",
		   "货车违规载人",
		   "机动车载物行驶时遗洒、飘散载运物的",
		   "上道路行驶的机动车未悬挂机动车号牌的",
		   "驾驶营运客车以外的机动车在高速公路行车道上停车的",
		   "驾驶校车在高速公路、城市快速路以外的道路上行驶超过规定时速20%以上未达到50%的",
		   "道路交通事故驾驶人应当自行撤离现场而未撤离，造成交通堵塞的。",
		   "故意污损机动车号牌的",
		   "违反规定临时停车，当事人在现场，没有造成交通堵塞，经交警指出立即驶离的",
		   "低速载货汽车牵引挂车",
		   "人行道不停车让行",
		   "不按规定安装号牌",
		   "驾驶机动车在高速公路上不按规定车道行驶的",
		   "使用其他车辆的机动车号牌的",
		   "人行道不停车让行的",
		   "【电子警察】机动车违反标志标线指示",
		   "违反禁令标志指示",
		   "机动车违规使用专用车道",
		   "超速50％以下",
		   "【电子警察】机动车违反标志标线指示",
		   "【电子警察】机动车违反标志标线指示",
		   "不按规定停车",
		   "驾驶中型以上载客载货汽车、危险物品运输车辆以外的其他机动车行驶超过规定时速未达20%的",
		   "【电子警察】机动车违反标志标线指示",
		   "驾驶中型以上载客载货汽车、校车、危险物品运输车辆以外的其他机动车行驶超过规定时速20%以上未达到50%的",
		   "【电子警察】机动车违反标志标线指示",
		   "【非绿标车】违反禁令标志指示",
		   "驾驶中型以上载客汽车在高速公路上行驶超过规定时速20％以上未达50%的",
		   "逆向行驶",
		   "驾驶中型以上载客载货汽车、危险物品运输车辆以外的其他机动车行驶超过规定时速10%未达20%的"

	   };
	@Override
	public void onCreate() {
		
		super.onCreate();
		init();
		initCode();
	}
	private void initCode()
	{
		//code_info=new ArrayList<CodeInfo>();
		
		
		code_map=new SparseArray<CodeInfo>();
		
		
		for(int i=0;i<code.length;i++)
		{
			CodeInfo info=new CodeInfo();
			info.code=code[i];
			info.money=money[i];
			info.score=score[i];
			info.type=type[i];
			
			code_map.put(code[i], info);
			
		}
	
	}
	public void init()
	{
		
		this.DB=new DBhelper(getApplicationContext(),"WeiZhangQuery");
	
	}
	public DBhelper getDB() {
		return DB;
	}

	
}

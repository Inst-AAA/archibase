package utils;

public class Gpoi {
	String[] pinyinList = {"a", "o", "e", "ai", "ei", "ao", "ou", "an", "en", "ang", "eng",
			"ba", "bo", "bi", "bu", "bai", "bei", "bao", "bie", "biao", "ban", "ben", "bin", "bian", "bang", "beng", "bing",
			"pa", "po", "pi", "pu", "pai", "pei", "pao", "pou", "pie", "piao", "pan", "pen", "pin", "pian", "pang", "peng", "ping",
			"ma", "mo", "me", "mi", "mu", "mai", "mei", "mao", "mou", "mie", "miao", "miu", "man", "men", "min", "mian", "mang", "meng", "ming",
			"fa", "fo", "fu", "fei", "nao", "fou", "fan", "fen", "fang", "feng",
			"da", "de", "di", "du", "dai", "dao", "dou", "dia", "die", "duo", "diao", "diu", "dui", "dan", "den", "din", "dian", "duan", "dun", "dang", "deng", "ding", "dong",
			"ta", "te", "ti", "tu", "tai", "tao", "tou", "tie", "tuo", "tiao", "tui", "tan", "tin", "tian", "tuan", "tun", "tang", "teng", "ting", "tong",
			"na", "ne", "ni", "nu", "nai", "nei", "nao", "nou", "nie", "nuo", "nve", "niao", "niu", "nan", "nen", "nin", "nian", "nuan", "nun", "nang", "neng", "ning", "nong", "niang",
			"la", "le", "li", "lu", "lai", "lei", "lao", "lou", "lie", "luo", "lve", "liao", "liu", "lan", "len", "lin", "lian", "luan", "lun", "lang", "leng", "ling", "long", "liang",
			"ga", "ge", "gu", "gai", "gei", "gao", "gou", "gua", "guo", "guai", "gui", "gan", "gen", "guan", "gun", "gang", "geng", "gong", "guang",
			"ka", "ke", "ku", "kai", "kei", "kao", "kou", "kua", "kuo", "kuai", "kui", "kan", "ken", "kuan", "kun", "kang", "keng", "kong", "kuang",
			"ha", "he", "hu", "hai", "hei", "hao", "hou", "hua", "huo", "huai", "hui", "han", "hen", "huan", "hun", "hang", "heng", "hong", "huang",
			"ju", "jiao", "jiu", "jian", "juan", "jun", "jing", "jiang", "jiong", "jia", "jie", "jin",
			"qi", "qu", "qia", "qie", "qiao", "qiu", "qin", "qian", "quan", "qun", "qing", "qiang", "qiong",
			"xi", "xu", "xia", "xie", "xiao", "xiu", "xin", "xian", "xuan", "xun", "xing", "xiang", "xiong",
			"zha", "zhe", "zhi", "zhu", "zhai", "zhao", "zhou", "zhua", "zhuo", "zhuai", "zhui", "zhan", "zhen", "zhuan", "zhun", "zhang", "zheng", "zhong", "zhuang",
			"cha", "che", "chi", "chu", "chai", "chao", "chou", "chuo", "chuai", "chui", "chan", "chen", "chuan", "chun", "chang", "cheng", "chong", "chuang",
			"sha", "she", "shi", "shu", "shai", "shao", "shou", "shua", "shuo", "shuai", "shui", "shan", "shen", "shuan", "shun", "shang", "sheng", "shong", "shuang",
			"re", "ri", "ru", "rao", "rou", "ruo", "rui", "ran", "ren", "ruan", "run", "rang", "reng", "rong",
			"za", "ze", "zi", "zu", "zai", "zei", "zao", "zou", "zuo", "zui", "zan", "zen", "zuan", "zun", "zang", "zeng", "zong",
			"ca", "ce", "ci", "cu", "cai", "cao", "cou", "cuo", "cui", "can", "cen", "cuan", "cun", "cang", "ceng", "cong",
			"sa", "se", "si", "su", "sai", "sao", "sou", "suo", "sui", "san", "sen", "suan", "sun", "sang", "seng", "song",
			"ya", "yo", "ye", "yi", "yu", "yao", "you", "yan", "yin", "yuan", "yun", "yang", "ying", "yong",
			"wo", "wu", "wai", "wei", "wan", "wen", "wang", "weng", "yong"};
	private String placeid;
	private float lat;
	private float lng;
	private float rating;
	private int userRatingsTotal;
	private boolean isChinese;
	private String name;
	private String type;
	private String typeDetail;

	public int judgePinyin(String str) {
		if (str.length() == 0) return 0;
		int total = 0;
//		System.out.println(str);
		for (int i = 0; i < pinyinList.length; ++i) {
			int index = str.lastIndexOf(pinyinList[pinyinList.length - i - 1]);
			int len = pinyinList[pinyinList.length - i - 1].length();
			if (index > -1) {
//				System.out.println("match: " + pinyinList[pinyinList.length-i-1]);
				total += len;
				total += judgePinyin(str.substring(0, index));
				total += judgePinyin(str.substring(index + len));
				break;
			}
		}
//		System.out.println("total = " + total);
		return total;
	}

	public String getPlaceid() {
		return placeid;
	}

	public void setPlaceid(String placeid) {
		this.placeid = placeid;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLng() {
		return lng;
	}

	public void setLng(float lng) {
		this.lng = lng;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public int getUserRatingsTotal() {
		return userRatingsTotal;
	}

	public void setUserRatingsTotal(int userRatingsTotal) {
		this.userRatingsTotal = userRatingsTotal;
	}

	public boolean isChinese() {
		return isChinese;
	}

	public void setChinese(boolean isChinese) {
		this.isChinese = isChinese;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeDetail() {
		return typeDetail;
	}

	public void setTypeDetail(String typeDetail) {
		this.typeDetail = typeDetail;
	}

	public boolean isGreen() {
		return false;
	}

	public boolean isBrown() {
		return false;
	}

}

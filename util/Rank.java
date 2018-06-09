package main.util;

public enum Rank {
	tu("IIIcj"), su("IIcj"), fu("Icj"), 
	tv("IIIc"), sv("IIc"), fv("Ic"),
	KMS("CMS"), MS("MS"), MSMK("MSCM"), ZMS("MMS");
	
	private final String value;
	Rank(String rank){
		this.value = rank;
	}
	@Override
	public String toString() {
		return value;
	}
	
	public static Rank getValue(String val) {
		switch(val){
			case "IIIcj":{
				return tu;
			}
			case "IIcj":{
				return su;
			}
			case "Icj":{
				return fu;
			}
			case "IIIc":{
				return tv;
			}
			case "IIc":{
				return sv;
			}
			case "Ic":{
				return fv;
			}
			case "CMS":{
				return KMS;
			}
			case "MS":{
				return MS;
			}
			case "ÌSCM":{
				return MSMK;
			}
			case "MMS":{
				return ZMS;
			}
			default:{
				return fu;
			}
		}
	}
}

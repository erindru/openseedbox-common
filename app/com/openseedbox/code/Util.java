package com.openseedbox.code;

import com.google.gson.Gson;
import com.openseedbox.mvc.ISelectListItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.Play;

public class Util {
	
	public static String getStackTrace(Throwable t) {
		if (t instanceof MessageException) {
			return t.getMessage();
		}
		if (Play.mode == Play.Mode.DEV) {
			return ExceptionUtils.getStackTrace(t);
		} else {
			//Mails.sendError(t, Http.Request.current.get());
			return "An unhandled exception occured! The developers have been notified.";
		}
	}	
	
	public static String getBestRate(long rateInBytes) {
		return FileUtils.byteCountToDisplaySize(rateInBytes);
	}
	
	public static String formatDate(Date d) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.format(d);				
	}
	
	public static String formatDateTime(Date d) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return df.format(d);		
	}
	
	public static String formatDateTime(DateTime d) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
		return d.toString(dtf);
	}
	
	public static String formatMoney(BigDecimal bd) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
		nf.setRoundingMode(RoundingMode.HALF_EVEN);
		return nf.format(bd.setScale(2, RoundingMode.HALF_EVEN));
	}
	
	public static List<ISelectListItem> toSelectItems(List<String> items) {
		List<ISelectListItem> ret = new ArrayList<ISelectListItem>();
		for (String s : items) {
			SelectItem si = new SelectItem(s, s, false);
			ret.add(si);
		}
		return ret;
	}
	
	public static List<ISelectListItem> toSelectItems(Map<String, String> items) {
		//the key is unique, so its used as the <option> value.
		//The value is not unique, so is the <option> name. Confusing? Good.
		List<ISelectListItem> ret = new ArrayList<ISelectListItem>();
		for (String s : items.keySet()) {
			SelectItem si = new SelectItem(items.get(s), s, false);
			si.name = items.get(s);
			ret.add(si);
		}		
		return ret;		
	}
	/*
	public static DateTime getLocalDate(Date systemDate, User u) {
		DateTimeZone tz = DateTimeZone.forID(u.getTimeZone());
		return new DateTime(systemDate).toDateTime(tz);
	}*/
	
	public static Map<String, String> getUrlParameters(String url) {
		Map<String, String> params = new HashMap<String, String>();
		String[] urlParts = url.split("\\?");
		String query = "";
		if (urlParts.length > 0) {
			query = urlParts[0];
		} else if (urlParts.length > 1) {
			query = urlParts[1];
		}
		if (!query.equals("")) {
			for (String param : query.split("&")) {
				String pair[] = param.split("=");
				try {
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}
					params.put(key, value);				
				} catch (UnsupportedEncodingException ex) {
					//ignore, fuck you java
				}
			}
		}
		return params;
	}
	
	public static String stripHtml(String s) {
		return s.replaceAll("\\<.*?>","");
	}
	
	public static String stripNonNumeric(String s) {
		return s.replaceAll("[^\\d.]", "");
	}
	
	public static <T extends Object> T fromJson(String json, Class<T> c) {
		return new Gson().fromJson(json, c);
	}
	
	public static String toJson(Object o) {
		return new Gson().toJson(o);
	}
	
	public static Map<String, Object> convertToMap(Object[] data) {
		Map<String, Object> m = new HashMap<String, Object>();
		for (int x = 0; x < data.length; x += 2) {
			String key = data[x].toString();
			Object value = null;
			if (x + 1 < data.length) {
				value = data[x + 1];
			}
			m.put(key, value);
		}
		return m;
	}
	
	public static String shellEscape(String s) {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < s.length(); x++) {
			char c = s.charAt(x);
			sb.append("\\");
			sb.append(c);
		}
		return sb.toString();
	}	
	
	public static String executeCommand(String command) {
		try {
			Runtime r = Runtime.getRuntime();
			String[] cmd = {
				"/bin/sh", "-c", command
			};
			Process p = r.exec(cmd);
			p.waitFor();
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			StringBuilder all = new StringBuilder();
			while ((line = b.readLine()) != null) {
			  all.append(line);
			}		
			return all.toString();
		} catch (IOException ex) {
			return ex.toString();
		} catch (InterruptedException ex) {
			return ex.toString();
		}
	}	
	
	
	public static class SelectItem implements ISelectListItem {
		private String name;
		private String value;
		private boolean selected;
		
		public SelectItem(String name, String value, boolean selected) {
			this.name = name; this.value = value; this.selected = selected;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public boolean isSelected() {
			return selected;
		}
	}
	
}
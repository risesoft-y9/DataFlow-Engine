package net.risedata.jdbc.commons.utils;

public class Print {
  public static void print(Object... arr) {
	  
	  for (int i = 0; i < arr.length; i++) {
		System.out.print(arr[i]+"    ");
	  }
	  System.out.println();
  }
  
  public static void print(Object[][] obj) {
	  for (@SuppressWarnings("unused") Object[] objects : obj) {
		System.out.print("[");
		for (int i = 0; i < obj.length; i++) {
			if (i!=0) {
				System.out.print(",");
			}
			System.out.print(obj[i]);
		}
		System.out.println();
	}
  }
}

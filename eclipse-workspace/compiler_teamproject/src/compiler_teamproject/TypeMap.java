package compiler_teamproject;

import java.util.*;

public class TypeMap extends HashMap<Variable, ArrayList<Type>> { 
	public void display()
	{
		System.out.println("map =");
		System.out.println(this.entrySet());
	}
}

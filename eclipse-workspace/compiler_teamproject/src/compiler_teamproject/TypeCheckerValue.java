package compiler_teamproject_save;

import java.util.*;

/*****************************************
 * ERROR가 나는 경우
 * 1. 중복 선언  > 완료
 * 2. 변수가 선언 안된 경우  > 완료
 * 3. 배열의 크기가 다른 경우 (배열을 벗어나는 경우) > 완료
 * 4. 연산시 값이 다른 경우  TypeCheckerOperator.java > 완료
 ****************************************/
public class TypeCheckerValue {
	//각 인덱스에 선언부에 선언된 값이 담긴다.
	String[] identifier;
	String[] type;
	int[][] arrSize;
	int idx;
	
	TypeCheckerValue(){
		identifier = new String[1000];
		type = new String[1000];
		arrSize = new int[1000][2];
		idx = 0;
	}
	
	void isAlready(String iden) {
		int i = 0;
		while(idx >= i) {
			if(iden.equals(identifier[i])) {
				System.err.println("Error: " + iden);
				System.err.println(iden + "란 변수명이 중복 선언되었습니다");
				System.exit(1);
			}
			i++;
		}
	}
	
	void InsertTypeChecker(Variable var, Type tp) {
		isAlready(var.toString());
		identifier[idx] = var.toString();
		type[idx] = tp.toString();
		idx++;
	}
	
	void InsertTypeChecker(Variable var, Type tp, Variable a) {
		isAlready(var.toString());
		identifier[idx] = var.toString();
		type[idx] = tp.toString();
		arrSize[idx][0] = Integer.parseInt(a.toString());
		arrSize[idx][1] = 0;
		idx++;
	}
	
	void InsertTypeChecker(Variable var, Type tp, Variable a, Variable b) {
		isAlready(var.toString());
		identifier[idx] = var.toString();
		type[idx] = tp.toString();
		arrSize[idx][0] = Integer.parseInt(a.toString());
		arrSize[idx][1] = Integer.parseInt(b.toString());
		idx++;
	}
	
	void valCheck(String iden) {
		int i = 0;
		while(true) {
			if(iden.equals(identifier[i])) {
				break;
			}else if(idx == i) {
				System.err.println("Error: " + iden);
				System.err.println(iden + "이란 변수가 선언되지 않았습니다");
				System.exit(1);
			}
			i++;
		}
	}
	
	//1차원 배열 사이즈 비교
	void valCheck(String iden, Expression a) {
		int i = 0;
		int arrA = Integer.parseInt(a.toString());
		
		while(true) {
			if(iden.equals(identifier[i])) {
				break;
			}else if(idx == i) {
				System.err.println("Error: " + iden + '[' + arrA + ']');
				System.err.println(iden + "이란 변수가 선언되지 않았습니다");
				System.exit(1);
			}
			i++;
		}
		if(arrA > arrSize[i][0]) {
			System.err.println("Error: " + iden + '[' + arrA + ']');
			if(arrSize[i][1] == 0) {
				System.err.println(iden + "의 배열 크기는 [" + arrSize[i][0] + "] 입니다");
				System.exit(1);
			}else {
				System.err.println(iden + "의 배열 크기는 [" + arrSize[i][0] + "][" + arrSize[i][1] + "] 입니다");
				System.exit(1);
			}
		}
	}
	
	//2차원 배열 사이즈 비교
	void valCheck(String iden, Expression a, Expression b) {
		int i = 0;	
		int arrA = Integer.parseInt(a.toString());
		int arrB = Integer.parseInt(b.toString());
		
		while(true) {
			if(iden.equals(identifier[i])) {
				break;
			}else if(idx == i) {
				System.err.println("Error: " + iden + '[' + arrA + "][" + arrB + ']');
				System.err.println(iden + "이란 변수가 선언되지 않았습니다");
				System.exit(1);
			}
			i++;
		}
		if(arrA > arrSize[i][0] || arrB > arrSize[i][1]) {
			System.err.println("Error: " + iden + '[' + arrA + "][" + arrB + ']');
			System.err.println(iden + "의 배열 크기는  [" + arrSize[i][0] + "][" + arrSize[i][1] + "] 입니다");
			System.exit(1);
		}
	}
	
	void display() {
		for(int i = 0; i < idx; i++) {
			System.out.println(identifier[i] + ' ' + type[i]);
		}
	}
	
}

package compiler_teamproject_save;

import java.util.*;

/*****************************************
 * ERROR�� ���� ���
 * 1. �ߺ� ����  > �Ϸ�
 * 2. ������ ���� �ȵ� ���  > �Ϸ�
 * 3. �迭�� ũ�Ⱑ �ٸ� ��� (�迭�� ����� ���) > �Ϸ�
 * 4. ����� ���� �ٸ� ���  TypeCheckerOperator.java > �Ϸ�
 ****************************************/
public class TypeCheckerValue {
	//�� �ε����� ����ο� ����� ���� ����.
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
				System.err.println(iden + "�� �������� �ߺ� ����Ǿ����ϴ�");
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
				System.err.println(iden + "�̶� ������ ������� �ʾҽ��ϴ�");
				System.exit(1);
			}
			i++;
		}
	}
	
	//1���� �迭 ������ ��
	void valCheck(String iden, Expression a) {
		int i = 0;
		int arrA = Integer.parseInt(a.toString());
		
		while(true) {
			if(iden.equals(identifier[i])) {
				break;
			}else if(idx == i) {
				System.err.println("Error: " + iden + '[' + arrA + ']');
				System.err.println(iden + "�̶� ������ ������� �ʾҽ��ϴ�");
				System.exit(1);
			}
			i++;
		}
		if(arrA > arrSize[i][0]) {
			System.err.println("Error: " + iden + '[' + arrA + ']');
			if(arrSize[i][1] == 0) {
				System.err.println(iden + "�� �迭 ũ��� [" + arrSize[i][0] + "] �Դϴ�");
				System.exit(1);
			}else {
				System.err.println(iden + "�� �迭 ũ��� [" + arrSize[i][0] + "][" + arrSize[i][1] + "] �Դϴ�");
				System.exit(1);
			}
		}
	}
	
	//2���� �迭 ������ ��
	void valCheck(String iden, Expression a, Expression b) {
		int i = 0;	
		int arrA = Integer.parseInt(a.toString());
		int arrB = Integer.parseInt(b.toString());
		
		while(true) {
			if(iden.equals(identifier[i])) {
				break;
			}else if(idx == i) {
				System.err.println("Error: " + iden + '[' + arrA + "][" + arrB + ']');
				System.err.println(iden + "�̶� ������ ������� �ʾҽ��ϴ�");
				System.exit(1);
			}
			i++;
		}
		if(arrA > arrSize[i][0] || arrB > arrSize[i][1]) {
			System.err.println("Error: " + iden + '[' + arrA + "][" + arrB + ']');
			System.err.println(iden + "�� �迭 ũ���  [" + arrSize[i][0] + "][" + arrSize[i][1] + "] �Դϴ�");
			System.exit(1);
		}
	}
	
	void display() {
		for(int i = 0; i < idx; i++) {
			System.out.println(identifier[i] + ' ' + type[i]);
		}
	}
	
}

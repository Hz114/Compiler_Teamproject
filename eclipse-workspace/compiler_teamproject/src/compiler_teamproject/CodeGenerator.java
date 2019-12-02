package compiler_teamproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

public class CodeGenerator {
	private int ifLabelN = 1;
	private int loopLabelN = 1;
	private String ucodeStr = "";
	int idt = 0;
	int Oplevel = 0;

	CodeGenerator(TypeMap Map, Program out) {
		CodeStartMain(out.decpart);
		CodeDeclaration(Map, out.decpart);
		CodeBody(Map, out.body);
		CodeEndMain();
	}

	public void CodeStartMain(Declarations ds) {
		int size = ds.size();
	}

	public void CodeEndMain() {
		//ucodeStr += "exit       nop\n" + "           end";
	}

	public void CodeDeclaration(TypeMap m, Declarations ds) {
		String key;
		int count = 0;
		LinkedList<Integer> a = new<Integer> LinkedList();
		for (Declaration d : ds) { 
			key = d.v.toString();
			//System.out.println(key+ "======");

			if (d.arrayCheck == 1) {
		//		System.out.println("array!!!!!!!!!!!");
				a.add(1);
			} else
				a.add(0);
		}
		int i = 0;
		int offest = 0, wordleng = 0, index = 0;
		int j=0;
		int end;
		while (a.get(j)==1){
			if(j+1==a.size()) break;
			else j++;
		}
		for (Declaration d : ds) {
			key = d.v.toString();
			//System.out.println(key);
			if (a.get(i) == 0) { // 배열이 아니면
				ucodeStr += "var " + key+ " ;\n";
			}
			else{
				if(d.arrB_integer != -1){
					//System.out.println("////////" + d.arrA_integer + d.arrB_integer);
					ucodeStr += "var " + key + " = " + "new Array(";
					for(int k=0; k<d.arrA_integer; k++){
						if(k == 0) ucodeStr += " new Array(" + d.arrB + ")";
						else ucodeStr += ", new Array(" + d.arrB + ")";
					}
					ucodeStr += " ) ;\n";
				}
			}
			i++;
		}
		
		//var arr = new Array(new Array(4), new Array(4), new Array(4));
	}

	public void CodeBody(TypeMap m, Block b) {
		for (Statement s : b.sts) {
			if (s instanceof Skip)
				;
			if (s instanceof Assignment)
				CodeAssignment(m, (Assignment) s);
			if (s instanceof Conditional)
				CodeConditional(m, (Conditional) s);
			if (s instanceof Input)
				CodeInput(m, (Input) s);
			if (s instanceof Output)
				CodeOutput(m, (Output) s);
			if (s instanceof Loop)
				CodeLoop(m, (Loop) s);
			if (s instanceof Block)
				CodeBlock(m, (Block) s);
		}
	}

	public void CodeStatement(TypeMap m, Statement s) {
		if (s instanceof Skip)
			;
		if (s instanceof Assignment)
			CodeAssignment(m, (Assignment) s);
		if (s instanceof Conditional)
			CodeConditional(m, (Conditional) s);
		if (s instanceof Input)
			CodeInput(m, (Input) s);
		if (s instanceof Output)
			CodeOutput(m, (Output) s);
		if (s instanceof Loop)
			CodeLoop(m, (Loop) s);
		if (s instanceof Block)
			CodeBlock(m, (Block) s);
	}

	public void CodeAssignment(TypeMap m, Assignment a) {
		for(int i=0; i<idt; i++){
			ucodeStr += "\t";
		}
		String key = a.target.toString();
		ucodeStr += key  ;
		if(a.arrayCheck==1){
			if(a.arrB_integer!=-1){
				ucodeStr += "[" + a.arrA_integer + "][" + a.arrB_integer + "]";
			}
			else{
				ucodeStr += "[" + a.arrA_integer + "]";
			}
			
		}
		
		ucodeStr += " = " ;
		CodeExpression(m, a.source);
		ucodeStr += " ;\n";
	}
	
	public void CodeInput(TypeMap m, Input a) {
		
		String key = a.v.toString();
		//CodeExpression(m, a.source);
		ucodeStr += key +  " = prompt();" +   "\n";
	}
	public void CodeOutput(TypeMap m, Output a) { 
		String key = a.exprs.toString();
		//System.out.println(key);
		//CodeExpression(m, a.source);
		String key_edit = key.substring(1, key.length()-1);
		ucodeStr += "console.log(" + key_edit + ")" +   "\n";
	}



	public void CodeConditional(TypeMap m, Conditional c) {
		for(int i=0; i<idt; i++){
			ucodeStr += "\t";
		}
		Expression test = c.test;
		Statement thenBranch = c.thenbranch;
		Statement elseBranch = c.elsebranch;
		String labelName = "if(";
		ifLabelN++;
		ucodeStr += labelName;
		CodeExpression(m, test); // 조건문
		ucodeStr += ") {\n";
		idt++;
		CodeStatement(m, thenBranch);
		CodeStatement(m, elseBranch);
		idt--;
		for(int i=0; i<idt; i++){
			ucodeStr += "\t";
		}
		ucodeStr += "}\n";
	}

	public void CodeLoop(TypeMap m, Loop l) {
		for(int i=0; i<idt; i++){
			ucodeStr += "\t";
		}
		Expression test = l.test;
		Statement body = l.body;
		String labelName = "while(";
		ucodeStr += labelName;
		CodeExpression(m, test);
		ucodeStr += ") {\n";
		idt++;
		CodeStatement(m, body);
		idt--;
		for(int i=0; i<idt; i++){
			ucodeStr += "\t";
		}
		ucodeStr += "}\n";
	}

	public void CodeBlock(TypeMap m, Block b) {
		for (Statement s : b.sts) {
			if (s instanceof Skip)
				;
			if (s instanceof Assignment)
				CodeAssignment(m, (Assignment) s);
			if (s instanceof Conditional)
				CodeConditional(m, (Conditional) s);
			if (s instanceof Input)
				CodeInput(m, (Input) s);
			if (s instanceof Output)
				CodeOutput(m, (Output) s);
			if (s instanceof Loop)
				CodeLoop(m, (Loop) s);
			if (s instanceof Block)
				CodeBlock(m, (Block) s);
		}
	}

	public void CodeExpression(TypeMap m, Expression e) {
		if (e instanceof Value)
			CodeValue(m, (Value) e);
		if (e instanceof Variable)
			CodeVariable(m, (Variable) e);
		if (e instanceof Binary)
			CodeBinary(m, (Binary) e);
		if (e instanceof Unary)
			CodeUnary(m, (Unary) e);

	}

	public void CodeValue(TypeMap m, Value v) {
		if (v instanceof BoolValue)
			CodeBoolValue(m, (BoolValue) v);
		if (v instanceof IntValue)
			CodeIntValue(m, (IntValue) v);
		if (v instanceof CharValue)
			CodeCharValue(m, (CharValue) v);

	}

	public void CodeVariable(TypeMap m, Variable v) {
		String key = v.toString();
		ucodeStr += key  ;
		if(v.arrayCheck==1){
			if(v.arrB_integer!=-1){
				ucodeStr += "[" + v.arrA_integer + "][" + v.arrB_integer + "]";
			}
			else{
				ucodeStr += "[" + v.arrA_integer + "]";
			}
			
		}
		

	}

	public void CodeBinary(TypeMap m, Binary b) {

		String strOp = b.op.toString();
		Oplevel ++;
		if(Oplevel > 1){
			ucodeStr += "(";
		}
	//	System.out.println("operation = ===?????? "+ strOp);
		CodeExpression(m, b.term1);
		

		switch (strOp) {
		case "그리고":
			ucodeStr += " && ";
			break;

		case "또는":
			ucodeStr += " || ";
			break;

		case "작다":
			ucodeStr += " < ";
			break;

		case "작거나같다":
			ucodeStr += " <= ";
			break;

		case "같다":
			ucodeStr += " == " ;
			break;

		case "다르다":
			ucodeStr += " != " ;
			break;

		case "크다":
			ucodeStr += " > " ;
			break;

		case "크거나같다":
			ucodeStr += " >= " ;
			break;

		case "더하기":
			ucodeStr += " + " ;
			break;

		case "빼기":
			ucodeStr += " - ";
			break;

		case "곱하기":
			ucodeStr += " * " ;
			break;

		case "나누기":
			ucodeStr += " / " ;
			break;

		case "나눈나머지":
			ucodeStr += " % ";
			break;

		default:
			break;
		}
		CodeExpression(m, b.term2);
		if(Oplevel > 1){
			ucodeStr += ")" ;
		}
		Oplevel --;
		
		

	}

	public void CodeUnary(TypeMap m, Unary u) {

		String opString = u.op.toString();
		CodeExpression(m, u.term);
		if (opString == "!") {
			ucodeStr += "!";
		} else if (opString == "-") {

			ucodeStr += "-";
		} else {
		}
	}

	public void CodeBoolValue(TypeMap m, BoolValue b) {
		
		ucodeStr += b;
	}

	public void CodeIntValue(TypeMap m,  IntValue i) {
		ucodeStr +=  i ;
	}

	public void CodeCharValue(TypeMap m, CharValue c) {
		ucodeStr += "'" + c + "'";
	}

	public String getCode() {
		return ucodeStr;
	}

	public void flush() {
		ucodeStr = "";
	}
	public static void main(String args[]) {
		Parser parser = new Parser(new Lexer("C:\\Users\\HYEJI\\eclipse-workspace\\compiler_teamproject_save\\src\\compiler_teamproject_save\\test.txt"));
		Program prog = parser.program();
		TypeMap map = TypeCheckerOperator.typing(prog.decpart);
		CodeGenerator G = new CodeGenerator(map, prog);
		String retStr = G.getCode();
		G.flush();
		System.out.println(retStr);
	}
}

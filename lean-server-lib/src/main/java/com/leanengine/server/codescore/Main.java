package com.leanengine.server.codescore;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

	public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		String exampleFile = "./lean-server-lib/src/main/java/com/leanengine/server/codescore/Example.java";
		Java7Lexer lexer = new Java7Lexer(new ANTLRFileStream(exampleFile, "UTF-8"));
		Java7Parser parser = new Java7Parser(new CommonTokenStream(lexer));
		parser.setBuildParseTree(true);

		Method startRule = Java7Parser.class.getMethod("compilationUnit");
		ParserRuleContext tree = (ParserRuleContext)startRule.invoke(parser, (Object[])null);
		System.out.println(tree.toStringTree(parser));
		printRules(tree);
	}

	public static void printRules(ParseTree tree) {

		if (tree.getText() != null)
			System.out.println(tree.getText());

		int childCount = tree.getChildCount();
		for (int i = 0; i < childCount; i++) {
			printRules(tree.getChild(i));
		}

	}

}

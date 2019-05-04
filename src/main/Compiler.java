package main;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;

public class Compiler {
	private String filePath;
	public Compiler(String filePath)
	{
		this.filePath = filePath;
	}
	
	private String getCode()
	{
		StringBuilder fileCode = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			while((line = br.readLine()) != null)
			{
				fileCode.append(line+"\n");
			}
			br.close();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return fileCode.toString();
	}
	
	private void compile(String input) throws IOException, AnalyzerException
	{
		/*
		 * Initialize a new Lexer which will make a list of Tokens
		 */
        Lexer lx = new Lexer();
        lx.tokenize(input);
        
        /*
         * print all tokens
         */
        int i = 0;
	    for(Token t : lx.getFilteredTokens())
	    {
	    	System.out.println(i+": "+t);
	    	i++;
	    }
	    
	    /*
	     * Syntactic analyze the entire program
	     */
	    SyntacticAnalyzer sylyzer = new SyntacticAnalyzer(lx.getFilteredTokens());
	    sylyzer.unit(0);
	    
	    //sylyzer.declCheck();
//	    File grammarFile = new File(System.getProperty("user.dir") + "/src/main/grammar.txt");
//	    Parser ps = new Parser();
//	    ps.parse(grammarFile, lx.getFilteredTokens());
	    
	}
	
	
	
	public static void main(String args[]) throws IOException, AnalyzerException
	{
		Compiler c = new Compiler("C:\\Users\\Robi\\Desktop\\tests\\6.c");
		c.compile(c.getCode());
		
	}
}

package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SyntacticAnalyzerV2 {
	List<Token> tokens = new ArrayList<Token>();
	Map<String, String> variables = new TreeMap<String, String>();
	int i;

	int consume() {
		this.i++;
		return this.i;
	}

	public SyntacticAnalyzerV2(List<Token> tokens) {
		this.tokens = tokens;
	}

	// exprOr: exprOr OR exprAnd | exprAnd  = 
	// Remove left recursion:
	/*
	 * eo -> eo OR ea | ea
	 * eo -> ea eo1
	 * eo1 -> OR ea eo1 | Epsilon
	 * Epsilon means recursion stops... in our case it is exprPrimary
	 */
//	     exprOr: exprAnd exprOr1
//	     exprOr1: OR exprAnd exprOr1
	public boolean exprOr(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(exprAnd(i))
				{
					//i = consume();
					return true;
				}
				else
				{
					state = 1;
				}
				break;
			case 1:
				exprOr1(i);
				return true;
				//break;
			}
		}
	}
	public boolean exprOr1(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(tokens.get(i).getTokenType().equals(TokenType.Or))
				{
					state = 1;
					i = consume();
				}
				else
				{
					return false;
				}
				break;
			case 1:
				if(exprAnd(i))
				{
					exprOr1(i);
				}
				else
					throw new AnalyzerException(
							"Expected exprAnd at: " + tokens.get(i).getBegin() + " and at token: " + i);
			}
		}
	}
	
	
	public boolean exprAnd(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(exprEq(i))
				{
					//i = consume();
					return true;
				}
				else
				{
					state = 1;
				}
				break;
			case 1:
				exprAnd1(i);
				return true;
				//break;
			}
		}
	}
	public boolean exprAnd1(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(tokens.get(i).getTokenType().equals(TokenType.And))
				{
					state = 1;
					i = consume();
				}
				else
				{
					return false;
				}
				break;
			case 1:
				if(exprEq(i))
				{
					exprAnd1(i);
				}
				else
					throw new AnalyzerException(
							"Expected exprEq at: " + tokens.get(i).getBegin() + " and at token: " + i);
			}
		}
	}
	
	
	
	public boolean exprEq(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(exprRel(i))
				{
					//i = consume();
					return true;
				}
				else
				{
					state = 1;
				}
				break;
			case 1:
				exprEq1(i);
				return true;
				//break;
			}
		}
	}
	public boolean exprEq1(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(tokens.get(i).getTokenType().equals(TokenType.Equal))
				{
					state = 1;
					i = consume();
				}
				else if(tokens.get(i).getTokenType().equals(TokenType.ExclameEqual))
				{
					state = 1;
					i = consume();
				}
				else
				{
					return false;//nu prea influenteaza ca nu verificam nimic cu return true/false din exprEq1, exprOr1 etc...
				}
				break;
			case 1:
				if(exprRel(i))
				{
					exprEq1(i);
				}
				else
					throw new AnalyzerException(
							"Expected exprRel at: " + tokens.get(i).getBegin() + " and at token: " + i);
			}
		}
	}
	
	
	public boolean exprRel(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(exprAdd(i))
				{
					//i = consume();
					return true;
				}
				else
				{
					state = 1;
				}
				break;
			case 1:
				exprRel1(i);
				return true;
				//break;
			}
		}
	}
	public boolean exprRel1(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(tokens.get(i).getTokenType().equals(TokenType.Less))
				{
					state = 1;
					i = consume();
				}
				else if(tokens.get(i).getTokenType().equals(TokenType.LessEqual))
				{
					state = 1;
					i = consume();
				}
				else if(tokens.get(i).getTokenType().equals(TokenType.Greater))
				{
					state = 1;
					i = consume();
				}
				else if(tokens.get(i).getTokenType().equals(TokenType.GreaterEqual))
				{
					state = 1;
					i = consume();
				}
				else
				{
					return false;//nu prea influenteaza ca nu verificam nimic cu return true/false din exprEq1, exprOr1 etc...
				}
				break;
			case 1:
				if(exprAdd(i))
				{
					exprRel1(i);
				}
				else
					throw new AnalyzerException(
							"Expected exprAdd at: " + tokens.get(i).getBegin() + " and at token: " + i);
			}
		}
	}
	
	
	public boolean exprAdd(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(exprMul(i))
				{
					//i = consume();
					return true;
				}
				else
				{
					state = 1;
				}
				break;
			case 1:
				exprAdd1(i);
				return true;
				//break;
			}
		}
	}
	public boolean exprAdd1(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(tokens.get(i).getTokenType().equals(TokenType.Plus))
				{
					state = 1;
					i = consume();
				}
				else if(tokens.get(i).getTokenType().equals(TokenType.Minus))
				{
					state = 1;
					i = consume();
				}
				else
				{
					return false;//nu prea influenteaza ca nu verificam nimic cu return true/false din exprEq1, exprOr1 etc...
				}
				break;
			case 1:
				if(exprMul(i))
				{
					exprAdd1(i);
				}
				else
					throw new AnalyzerException(
							"Expected exprMul at: " + tokens.get(i).getBegin() + " and at token: " + i);
			}
		}
	}
	
	public boolean exprMul(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(exprCast(i))
				{
					//i = consume();
					return true;
				}
				else
				{
					state = 1;
				}
				break;
			case 1:
				exprMul1(i);
				return true;
				//break;
			}
		}
	}
	public boolean exprMul1(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(tokens.get(i).getTokenType().equals(TokenType.Multiply))
				{
					state = 1;
					i = consume();
				}
				else if(tokens.get(i).getTokenType().equals(TokenType.Divide))
				{
					state = 1;
					i = consume();
				}
				else
				{
					return false;//nu prea influenteaza ca nu verificam nimic cu return true/false din exprEq1, exprOr1 etc...
				}
				break;
			case 1:
				if(exprCast(i))
				{
					exprMul1(i);
				}
				else
					throw new AnalyzerException(
							"Expected exprCast at: " + tokens.get(i).getBegin() + " and at token: " + i);
			}
		}
	}
	// exprOr: exprOr OR exprAnd | exprAnd  = 
		// Remove left recursion:
		/*
		 * eo -> eo OR ea | ea
		 * eo -> ea eo1
		 * eo1 -> OR ea eo1 | Epsilon
		 * Epsilon means recursion stops... in our case it is exprPrimary
		 */
	/*
	 exprPostfix: exprPostfix LBRACKET expr RBRACKET | exprPostfix DOT ID  | exprPrimary ; 
	 
	 A-> A alfa1 | A alfa2 | B
	 
	 A -> A alfa1 | B				A -> A alfa2 | B
	 
	 A -> B A'						A -> B A'
	 A'-> alfa1  A'|Eps				A'->alfa2 A'|Eps
	 
	 ->

	 */
	/*
	 * exprPo -> exprPr exprPo1
	 * exprPo1 -> alfa1 else alfa2 exprPr exprPo1
	 */
	
	public boolean exprPostfix(int i) throws AnalyzerException
	{
		int state = 0;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(exprPrimary(i))
				{
					//i = consume();
					return true;
				}
				else
				{
					state = 1;
				}
				break;
			case 1:
				exprPostfix1(i);
				return true;
				//break;
			}
		}
	}
	public boolean exprPostfix1(int i) throws AnalyzerException
	{
		int state = 0;
		//int i_backup;
		while(true)
		{
			switch(state)
			{
			case 0:
				if(tokens.get(i).getTokenType().equals(TokenType.OpenStraightBrace))
				{
					//i_backup = i;
					state = 1;
					i = consume();
				}
				else if(tokens.get(i).getTokenType().equals(TokenType.Dot))
				{
					state = 3;
					i =  consume();
				}
				else
				{
					return false;
				}
				break;
			case 1:
				if(expr(i))
				{
					i = consume();
				}
				else
				{
					throw new AnalyzerException(
							"Expected expr at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 2:
				if(tokens.get(i).getTokenType().equals(TokenType.CloseStraightBrace))
				{
					state = 6;
					i = consume();
				}
			case 3:
				if(tokens.get(i).getTokenType().equals(TokenType.Identifier))
				{
					state = 6;
					i = consume();
				}
				else
				{
					throw new AnalyzerException(
							"Expected expr at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
					
			case 6:
				if(exprPrimary(i))
				{
					exprPostfix1(i);
				}
				else
					throw new AnalyzerException(
							"Expected exprAnd at: " + tokens.get(i).getBegin() + " and at token: " + i);
			}
		}
	}
	
	
}

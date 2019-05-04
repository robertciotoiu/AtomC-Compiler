package main;

public class Variable implements Comparable{
	public int status;// 0 -undeclared, 1-declared
	public TokenType type;
	public String name;
	
	public Variable(int status, TokenType type, String name)
	{
		this.status = status;
		this.type = type;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		final Variable other = (Variable) obj;
		if(this.name == other.name)
		{
			return true;
		}
		return false;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if(o.getClass().equals(Variable.class))
		{
			Variable v1 = (Variable) o ;
			System.out.println(v1.name + " " + this.name);
			if(this.name.equals(v1.name))
			{
				return 0;
			}	
			return 1;
		}
		return 0;
	}
}

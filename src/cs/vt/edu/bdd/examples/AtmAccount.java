package cs.vt.edu.bdd.examples;

public class AtmAccount{

	private double balance;
	
	public AtmAccount(double _balance){
		balance = _balance;
	}
	
	public double getBalance(){
		return balance;
	}
	
	public void setBalance(double _balance){
		balance = _balance;
	}
	
	public void deposit(double amount) {
		balance = balance + amount;
	}
	
	public void withdraw(double amount) {		
		balance = balance - amount;
	}
}
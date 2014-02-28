
public class Product {
	public int ProductID;
	public int InStock;
	public String Name;
	public double Price;
	public String Description;
	public String [] UserComments;
	
	public double Relavance; // Used for fulltext searching.
	
	public String toString(){
		return Name;
	}
}

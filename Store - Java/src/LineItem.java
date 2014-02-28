
public class LineItem {
	public Product Product;
	public Order Order;
	public int Quantity;
	public double PricePaid;
	
	public String toString (){
		return Product.Name + " (Qty " + Integer.toString(Quantity) + ")";
	}
}

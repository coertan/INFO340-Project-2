import java.util.Date;


public class Order {
	
	public int OrderID;
	
	public Date OrderDate;
	public String Status;
	
	public Customer Customer;
	
	public double TotalCost;
	
	public LineItem [] LineItems;
	
	public String ShippingAddress;
	public String BillingAddress;
	public String BillingInfo; 		// Like Credit Card #, etc...	
	
	public String toString ()
	{
		return Status;
	}
}

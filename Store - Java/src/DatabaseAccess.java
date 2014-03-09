import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;


public class DatabaseAccess {
	
	private final static String url = "jdbc:sqlserver://is-fleming.ischool.uw.edu";
    private final static String user = "store_1";
    private final static String pass = "info340#jc";
    
	public static Order [] GetPendingOrders()
	{		
		try{
		   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		   Connection conn = DriverManager.getConnection(url, user, pass);
	       conn.setCatalog("store_1");
	       Statement stmt = conn.createStatement();
	       ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS RowTotal FROM OrderInfo WHERE DateShipped IS Null");
	       int rowcount = 0;
	       if(rs.next()){
	    	   rowcount = rs.getInt("RowTotal");
	       }
	       
	       rs = stmt.executeQuery("SELECT * FROM OrderInfo WHERE DateShipped IS NULL");
	      
	       Order[] orderList = new Order[rowcount];
	       int count = 0;
	       while (rs.next()) {	    	   
	    	   Order o = new Order();
	    	   o.Customer = new Customer();
	    	   o.OrderID = rs.getInt("OrderID");
	    	   Statement innerStmt = conn.createStatement();
	    	   ResultSet rsInner = innerStmt.executeQuery("SELECT * FROM Customer WHERE CustomerID = " + rs.getString("CustomerID"));
	    	   rsInner.next();
	    	   
	    	   o.Customer.Name = rsInner.getString("FirstName") + " " + rsInner.getString("LastName");
	    	   o.Customer.Email = rsInner.getString("EmailAddress");
	    	   o.Customer.CustomerID = rs.getInt("CustomerID");
	    	   o.OrderDate = rs.getTimestamp("DateOrdered");
	    	   o.Status = rs.getString("OrderStatus");
	    	   //now build the lineitems
	    	   int innerRowCount = 0;
	    	   rsInner = innerStmt.executeQuery("SELECT COUNT(*) AS RowTotal FROM LineItem WHERE OrderID = " + rs.getString("OrderID"));
	    	   if(rsInner.next()){
	    		   innerRowCount=rsInner.getInt("RowTotal");
	    	   }
	    	   rsInner = innerStmt.executeQuery("SELECT * FROM LineItem WHERE OrderID = " + rs.getString("OrderID"));
	    	   double total = 0;
	    	   
	    	   o.LineItems = new LineItem[innerRowCount];
	    	   innerRowCount = 0;
	    	   while(rsInner.next()){

	    		   LineItem item = new LineItem();
	    		   item.Order = o;
	    		   item.Quantity = rsInner.getInt("QuantityOrdered");
	    		   item.PricePaid = rsInner.getDouble("SellPrice");
	    		   total = (item.PricePaid * item.Quantity) + total;
	    		   item.Product = new Product();
	    		   Statement productStmt = conn.createStatement();
	    		   ResultSet rsProduct = productStmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + rsInner.getString("ProductID"));
	    		   rsProduct.next();
	    		   item.Product.Description = rsProduct.getString("Description");
	    		   item.Product.ProductID = rsProduct.getInt("ProductID");
	    		   item.Product.Name = rsProduct.getString("ProductName");
	    		   item.Product.Price = rsProduct.getDouble("SellPrice");
	    		   item.Product.InStock = rsProduct.getInt("Qty_In_Stock");
	    		   o.LineItems[innerRowCount] = item;
	    		   innerRowCount++;
	    	   }
	    	   o.TotalCost = Math.round(total*100.0)/100.0;
	    	   rsInner = innerStmt.executeQuery("SELECT * FROM Address WHERE AddressID = " + rs.getString("ShippingAddressID"));
	    	   rsInner.next();
	    	   if(rsInner.getString("LineTwo") == null){
		    	   o.ShippingAddress = rsInner.getString("LineOne")  + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
	    	   }else{
		    	   o.ShippingAddress = rsInner.getString("LineOne") + " " + rsInner.getString("LineTwo") + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
	    	   }
	    	   rsInner = innerStmt.executeQuery("SELECT * FROM Address WHERE AddressID = "+  rs.getString("BillingAddressID"));
	    	   rsInner.next();	   
	    	   if(rsInner.getString("LineTwo") == null){
		    	   o.BillingAddress = rsInner.getString("LineOne")  + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
	    	   }else{
		    	   o.BillingAddress = rsInner.getString("LineOne") + " " + rsInner.getString("LineTwo") + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
	    	   }
	    	   orderList[count] = o;
	    	   count++;
	       }
	       return orderList;

		}
		catch(ClassNotFoundException ex) {
		   System.out.println("Error: unable to load driver class!");
		   System.exit(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Order[1];
	}
	
	
	public static Product[] GetProducts()
	{
			try {
			   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			   
		       Connection conn = DriverManager.getConnection(url, user, pass);
		       conn.setCatalog("store_1");
		       Statement stmt = conn.createStatement();
		       int count = 0;
		       ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS Total FROM Product");
		       if(rs.next()){
		    	   count= rs.getInt("Total");
		       }
		       Product[] products = new Product[count];
		       count = 0;
		       rs = stmt.executeQuery("SELECT * FROM Product");
		       while (rs.next()) {
		    	  
		    	   Product individualProduct = new Product();
		    	   individualProduct.ProductID = rs.getInt("ProductID");
		    	   individualProduct.Description = rs.getString("Description");
		    	   individualProduct.Name = rs.getString("ProductName");
		    	   individualProduct.Price = rs.getDouble("SellPrice");
		    	   individualProduct.InStock = rs.getInt("Qty_In_Stock");
		    	   //NEED TO ADD RELEVANCE/COMMENTS HERE
		    	   
		    	   products[count] = individualProduct;
		    	   count++;
		       }
		       return products;

			}
			catch(ClassNotFoundException ex) {
			   System.out.println("Error: unable to load driver class!");
			   return null;
			   //System.exit(1);
			} catch (SQLException e) {
				System.out.println("SQL ERROR!");
				return null;
				//e.printStackTrace();
			}
		
	
	}

	public static Order GetOrderDetails(int OrderID)
	{
		
		try {
			   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		       Connection conn = DriverManager.getConnection(url, user, pass);
		       conn.setCatalog("store_1");
		       Statement stmt = conn.createStatement();
		       ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as ErrorCheck FROM OrderInfo WHERE OrderID = " + OrderID);
		       rs.next();
		       
		       //neither of these should ever trigger, but just in case...
		       if(rs.getInt("ErrorCheck") > 1){
		    	   System.out.println("Error!  More than one order with that name!");
		    	   System.exit(1);
		       }else if(rs.getInt("ErrorCheck") == 0){
		    	   System.out.println("Error!  No such item!");
		    	   System.exit(1);
		       }else{
		    	   rs = stmt.executeQuery("SELECT * FROM OrderInfo WHERE OrderID = "+OrderID);
			       rs.next();
			       Order o = new Order();
			       o.Customer = new Customer();
		    	   Statement innerStmt = conn.createStatement();
		    	   ResultSet rsInner = innerStmt.executeQuery("SELECT * FROM Customer WHERE CustomerID = " + rs.getString("CustomerID"));
		    	   rsInner.next();
		    	   
		    	   o.Customer.Name = rsInner.getString("FirstName") + " " + rsInner.getString("LastName");
		    	   o.Customer.Email = rsInner.getString("EmailAddress");
		    	   o.Customer.CustomerID = rs.getInt("CustomerID");
		    	   o.OrderDate = rs.getTimestamp("DateOrdered");
		    	   o.Status = rs.getString("OrderStatus");
		    	   //now build the lineitems
		    	   int innerRowCount = 0;
		    	   rsInner = innerStmt.executeQuery("SELECT COUNT(*) AS RowTotal FROM LineItem WHERE OrderID = " + rs.getString("OrderID"));
		    	   if(rsInner.next()){
		    		   innerRowCount=rsInner.getInt("RowTotal");
		    	   }
		    	   rsInner = innerStmt.executeQuery("SELECT * FROM LineItem WHERE OrderID = " + rs.getString("OrderID"));
		    	   double total = 0;
		    	   
		    	   o.LineItems = new LineItem[innerRowCount];
		    	   innerRowCount = 0;
		    	   while(rsInner.next()){

		    		   LineItem item = new LineItem();
		    		   item.Order = o;
		    		   item.Quantity = rsInner.getInt("QuantityOrdered");
		    		   item.PricePaid = rsInner.getDouble("SellPrice");
		    		   total = (item.PricePaid * item.Quantity) + total;
		    		   item.Product = new Product();
		    		   Statement productStmt = conn.createStatement();
		    		   ResultSet rsProduct = productStmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + rsInner.getString("ProductID"));
		    		   rsProduct.next();
		    		   item.Product.Description = rsProduct.getString("Description");
		    		   item.Product.ProductID = rsProduct.getInt("ProductID");
		    		   item.Product.Name = rsProduct.getString("ProductName");
		    		   item.Product.Price = rsProduct.getDouble("SellPrice");
		    		   item.Product.InStock = rsProduct.getInt("Qty_In_Stock");
		       		   o.LineItems[innerRowCount] = item;
		    		   innerRowCount++;
		    	   }
		    	   
		    	   o.TotalCost = Math.round(total*100.0)/100.0;
		    	   rsInner = innerStmt.executeQuery("SELECT * FROM Address WHERE AddressID = " + rs.getString("ShippingAddressID"));
		    	   rsInner.next();
		    	   if(rsInner.getString("LineTwo") == null){
			    	   o.ShippingAddress = rsInner.getString("LineOne")  + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
		    	   }else{
			    	   o.ShippingAddress = rsInner.getString("LineOne") + " " + rsInner.getString("LineTwo") + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
		    	   }
		    	   rsInner = innerStmt.executeQuery("SELECT * FROM Address WHERE AddressID = "+  rs.getString("BillingAddressID"));
		    	   rsInner.next();	   
		    	   if(rsInner.getString("LineTwo") == null){
			    	   o.BillingAddress = rsInner.getString("LineOne")  + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
		    	   }else{
			    	   o.BillingAddress = rsInner.getString("LineOne") + " " + rsInner.getString("LineTwo") + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
		    	   }
		    	   //need to add address stuff here - billingaddress, shippingaddress, billin
		    	   
		    	   
		    	   return o;
		       }
		       
		       

			}
			catch(ClassNotFoundException ex) {
			   System.out.println("Error: unable to load driver class!");
			   System.exit(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return new Order();
	
	}

	public static Product GetProductDetails (int ProductID)
	{
		
		try {
			   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		       Connection conn = DriverManager.getConnection(url, user, pass);
		       conn.setCatalog("store_1");
		       Statement stmt = conn.createStatement();
		       ResultSet rs = stmt.executeQuery("SELECT * from Product WHERE ProductID = "+ProductID);
		       Product p = new Product();
		       if (rs.next()) {  //Should return 1 or 0 products - either the ID exists or it does not - add error handling
		    	   p.ProductID = rs.getInt("ProductID");
		    	   p.Description = rs.getString("Description");
		    	   p.Name = rs.getString("ProductName");
		    	   p.Price = rs.getDouble("SellPrice");
		    	   
		    	   //NEED TO ADD RELEVANCE/COMMENTS HERE
		       }
		       return p;

			}
			catch(ClassNotFoundException ex) {
			   System.out.println("Error: unable to load driver class!");
			   System.exit(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return new Product();
	}
	
	public static Customer [] GetCustomers () //Done!  I think
	{
		
		try {
			   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		       Connection conn = DriverManager.getConnection(url, user, pass);
		       conn.setCatalog("store_1");
		       Statement stmt = conn.createStatement();
		       ResultSet rs = stmt.executeQuery("SELECT * from Customer");
		       Customer[] customers = new Customer[10];
		       int count = 0;
		       while (rs.next()) {
		    	   if(count > (customers.length-1)){
		    		   Customer[] temp = new Customer[customers.length*2];
		    		   for(int i = 0; i < customers.length; i++){
		    			   temp[i] = customers[i];
		    		   }
		    		   customers = temp;
		    	   }
		    	   Customer c = new Customer();
		    	   c.CustomerID = rs.getInt("CustomerID");
		    	   c.Name = rs.getString("FirstName") + " " + rs.getString("LastName");
		    	   c.Email = rs.getString("EmailAddress");
		    	   customers[count] = c;
		    	   count++;
		       }
		       return customers;

			}
			catch(ClassNotFoundException ex) {
			   System.out.println("Error: unable to load driver class!");
			   System.exit(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Error: SQL error!");
				System.exit(1);
			}
		return new Customer[1]; //if errors, return an empty set
	}
	
	public static Order [] GetCustomerOrders (Customer c)
	{
		
		
		
		
		try{
		   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		   Connection conn = DriverManager.getConnection(url, user, pass);
	       conn.setCatalog("store_1");
	       Statement stmt = conn.createStatement();
	       ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS RowTotal FROM OrderInfo WHERE CustomerID = " + c.CustomerID);
	       int rowcount = 0;
	       if(rs.next()){
	    	   rowcount = rs.getInt("RowTotal");
	       }
	       
	       rs = stmt.executeQuery("SELECT * FROM OrderInfo WHERE CustomerID = " + c.CustomerID);
	      
	       Order[] orderList = new Order[rowcount];
	       int count = 0;
	       while (rs.next()) {
	    	   
	    	   Order o = new Order();
	    	   o.Customer = new Customer();
	    	   o.OrderID = rs.getInt("OrderID");
	    	   Statement innerStmt = conn.createStatement();
	    	   ResultSet rsInner = innerStmt.executeQuery("SELECT * FROM Customer WHERE CustomerID = " + rs.getString("CustomerID"));
	    	   rsInner.next();

	    	   
	    	   o.Customer.Name = rsInner.getString("FirstName") + " " + rsInner.getString("LastName");
	    	   o.Customer.Email = rsInner.getString("EmailAddress");
	    	   o.Customer.CustomerID = rs.getInt("CustomerID");
	    	   o.OrderDate = rs.getTimestamp("DateOrdered");
	    	   o.Status = rs.getString("OrderStatus");
	    	   //now build the lineitems
	    	   int innerRowCount = 0;
	    	   rsInner = innerStmt.executeQuery("SELECT COUNT(*) AS RowTotal FROM LineItem WHERE OrderID = " + rs.getString("OrderID"));
	    	   if(rsInner.next()){
	    		   innerRowCount=rsInner.getInt("RowTotal");
	    	   }
	    	   rsInner = innerStmt.executeQuery("SELECT * FROM LineItem WHERE OrderID = " + rs.getString("OrderID"));
	    	   double total = 0;
	    	   
	    	   o.LineItems = new LineItem[innerRowCount];
	    	   innerRowCount = 0;
	    	   while(rsInner.next()){

	    		   LineItem item = new LineItem();
	    		   item.Order = o;
	    		   item.Quantity = rsInner.getInt("QuantityOrdered");
	    		   item.PricePaid = rsInner.getDouble("SellPrice");
	    		   total = (item.PricePaid * item.Quantity) + total;
	    		   item.Product = new Product();
	    		   Statement productStmt = conn.createStatement();
	    		   ResultSet rsProduct = productStmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + rsInner.getString("ProductID"));
	    		   rsProduct.next();
	    		   item.Product.Description = rsProduct.getString("Description");
	    		   item.Product.ProductID = rsProduct.getInt("ProductID");
	    		   item.Product.Name = rsProduct.getString("ProductName");
	    		   item.Product.Price = rsProduct.getDouble("SellPrice");
	    		   item.Product.InStock = rsProduct.getInt("Qty_In_Stock");
	    		   item.Product.Description = "Temppp";
	    		   //NEED TO ADD DESCRIPTION STUFF HERE
	    		   o.LineItems[innerRowCount] = item;
	    		   innerRowCount++;
	    	   }
	    	   o.TotalCost = Math.round(total*100)/100;
	    	   rsInner = innerStmt.executeQuery("SELECT * FROM Address WHERE AddressID = " + rs.getString("ShippingAddressID"));
	    	   rsInner.next();
	    	   if(rsInner.getString("LineTwo") == null){
		    	   o.ShippingAddress = rsInner.getString("LineOne")  + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
	    	   }else{
		    	   o.ShippingAddress = rsInner.getString("LineOne") + " " + rsInner.getString("LineTwo") + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
	    	   }
	    	   rsInner = innerStmt.executeQuery("SELECT * FROM Address WHERE AddressID = "+  rs.getString("BillingAddressID"));
	    	   rsInner.next();	   
	    	   if(rsInner.getString("LineTwo") == null){
		    	   o.BillingAddress = rsInner.getString("LineOne")  + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
	    	   }else{
		    	   o.BillingAddress = rsInner.getString("LineOne") + " " + rsInner.getString("LineTwo") + " " + rsInner.getString("City") + " " + rsInner.getString("State") + " " + rsInner.getString("Zip");
	    	   }	    	   o.BillingInfo = " ";
	    	   orderList[count] = o;
	    	   count++;
	       }
	       return orderList;

		}
		catch(ClassNotFoundException ex) {
		   System.out.println("Error: unable to load driver class!");
		   System.exit(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Order[1];
	}
	
	public static Product [] SearchProductReviews(String query)
	{
		//TODO: Fix Me
		// DUMMY VALUES
		Product p = new Product();
		p.Description = "A great monitor";
		p.Name = "Monitor, 19 in";
		p.InStock = 10;
		p.Price = 196;
		p.ProductID = 1;
		p.Relavance = 0.7;
		return new Product [] { p} ;
	}
	                    
	public static void MakeOrder(Customer c, LineItem [] LineItems)
	{
		if(LineItems.length == 0){
			JOptionPane.showMessageDialog(null, "An order must have at least one item!");
			return;
		}

		try {
			   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		       Connection conn = DriverManager.getConnection(url, user, pass);
		       conn.setCatalog("store_1");
		       
		       

		       conn.setAutoCommit(false);
		       Statement stmt = conn.createStatement();
		       Statement updateStmt = conn.createStatement();
		       ResultSet rs;		       
		       PreparedStatement orderStmt = conn.prepareStatement("INSERT INTO OrderInfo(DateOrdered, CustomerID, ShippingAddressID, "
		       		+ "BillingAddressID, OrderStatus) VALUES(?,?,?,?,?)");
		       
		       rs = stmt.executeQuery("SELECT  * FROM Address WHERE CustomerID="+c.CustomerID+" AND WasMostRecentShippingAddress=1");
		       rs.next();
		       
		       java.sql.Timestamp orderDate = new java.sql.Timestamp(System.currentTimeMillis());
		       orderStmt.setTimestamp(1, orderDate);
		       orderStmt.setInt(2, c.CustomerID);
		       orderStmt.setInt(3, rs.getInt("AddressID"));
		       orderStmt.setInt(4, rs.getInt("AddressID"));
		       orderStmt.setString(5, "Order Received");
		       orderStmt.execute();
		       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		       rs = stmt.executeQuery("SELECT OrderID FROM OrderInfo WHERE CustomerID="+c.CustomerID+" AND DateOrdered='"+df.format(orderDate)+"'");
		       rs.next();
		       int orderID = rs.getInt("OrderID");
		       
		       
		       
		       for(LineItem item : LineItems){
			       rs = stmt.executeQuery("SELECT * from Product WITH (ROWLOCK) WHERE ProductID = " + item.Product.ProductID);
			       rs.next();
			       if(rs.getInt("Qty_In_Stock") < item.Quantity){
			    	   JOptionPane.showMessageDialog(null, "Cannot create order, only " + rs.getInt("Qty_In_Stock") + " left in stock of " +item.Product.Name);
			    	   conn.rollback();
			    	   return;
			       }
			       updateStmt.executeUpdate("UPDATE Product SET Qty_In_Stock="+(rs.getInt("Qty_In_Stock") - item.Quantity) 
			    		   + " WHERE ProductID="+item.Product.ProductID);	
			       double profit = (rs.getDouble("SellPrice") - rs.getDouble("PriceFromSeller")) * item.Quantity;
			       ResultSet rsCheck = updateStmt.executeQuery("SELECT * FROM LineItem WHERE OrderID="+orderID+" AND ProductID="+item.Product.ProductID);
			       if(rsCheck.next()){
			    	   updateStmt.executeUpdate("UPDATE LineItem SET QuantityOrdered="+(rsCheck.getInt("QuantityOrdered")
			    			   +item.Quantity) + ", ProductProfitSubtotal="+(rsCheck.getDouble("ProductProfitSubtotal") + profit) + " WHERE "
			    			   + " ProductID=" + item.Product.ProductID + " AND OrderID="+orderID);
			       }else{
				       PreparedStatement lineStmt = conn.prepareStatement("INSERT INTO LineItem VALUES(?,?,?,?,?);");
				       
				       lineStmt.setDouble(1, profit);
				       lineStmt.setInt(2, orderID);
				       lineStmt.setInt(3, item.Product.ProductID);
				       lineStmt.setInt(4, item.Quantity);
				       lineStmt.setDouble(5, item.PricePaid);
				       lineStmt.executeUpdate();	
			       }

			       
			   }
		       conn.commit();	
			   JOptionPane.showMessageDialog(null, "Created order for " + c.Name + " for " + Integer.toString(LineItems.length) + " items.");

		    }
			catch(ClassNotFoundException ex) {
			   System.out.println("Error: unable to load driver class!");
			   System.exit(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	
	}
}

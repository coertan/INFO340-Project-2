import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

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
					item.Product = GetProductDetails(rsInner.getInt("ProductID"));
/*					item.Product = new Product();
					Statement productStmt = conn.createStatement();
					ResultSet rsProduct = productStmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + rsInner.getString("ProductID"));
					rsProduct.next();
					item.Product.Description = rsProduct.getString("Description");
					item.Product.ProductID = rsProduct.getInt("ProductID");
					item.Product.Name = rsProduct.getString("ProductName");
					item.Product.Price = rsProduct.getDouble("SellPrice");
					item.Product.InStock = rsProduct.getInt("Qty_In_Stock");*/
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
				//Grab reviews
				
				Statement reviewStmt = conn.createStatement();
				ResultSet rsReviews = reviewStmt.executeQuery("SELECT *, count(*) OVER() AS 'Count' FROM ProductReview WHERE ProductID = " + individualProduct.ProductID);
				int reviewCount = 0;
				
				if(rsReviews.next()){ //if there are reviews
					reviewCount = rsReviews.getInt("Count");
					individualProduct.UserComments = new String[reviewCount];
					//process first review
					individualProduct.UserComments[0] = rsReviews.getString("ReviewText");
					int innerCount = 1;
					while(rsReviews.next()){
						individualProduct.UserComments[innerCount] = rsReviews.getString("ReviewText");
						innerCount++;
					}					
				}else{
					individualProduct.UserComments = new String[0];
				}
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
					item.Product = GetProductDetails(rsInner.getInt("ProductID"));
/*					item.Product = new Product();
					Statement productStmt = conn.createStatement();
					ResultSet rsProduct = productStmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + rsInner.getString("ProductID"));
					rsProduct.next();
					item.Product.Description = rsProduct.getString("Description");
					item.Product.ProductID = rsProduct.getInt("ProductID");
					item.Product.Name = rsProduct.getString("ProductName");
					item.Product.Price = rsProduct.getDouble("SellPrice");
					item.Product.InStock = rsProduct.getInt("Qty_In_Stock");*/
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
				p.InStock = rs.getInt("Qty_In_Stock");
				Statement reviewStmt = conn.createStatement();
				ResultSet rsReviews = reviewStmt.executeQuery("SELECT *, count(*) OVER() AS 'Count' FROM ProductReview WHERE ProductID = " + p.ProductID);
				int reviewCount = 0;
				
				if(rsReviews.next()){ //if there are reviews
					reviewCount = rsReviews.getInt("Count");
					p.UserComments = new String[reviewCount];
					//process first review
					p.UserComments[0] = rsReviews.getString("ReviewText");
					int innerCount = 1;
					while(rsReviews.next()){
						p.UserComments[innerCount] = rsReviews.getString("ReviewText");
						innerCount++;
					}					
				}else{
					p.UserComments = new String[0];
				}
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

	
	
	public static Product [] SearchProductReviews(String query){
		//let's do this using sql server full-text searching
		try {
			   Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		       Connection conn = DriverManager.getConnection(url, user, pass);
		       conn.setCatalog("store_1");
		       Statement stmt = conn.createStatement();
		       

		       ResultSet rs = stmt.executeQuery("SELECT TOP 10 ReviewID, ProductID, KEY_TBL.RANK, count(ReviewID) OVER() as 'Count' " + 
		    		   "FROM ProductReview JOIN FREETEXTTABLE(ProductReview, ReviewText, '" + query + "') AS KEY_TBL " + 
		    		   "ON ProductReview.ReviewID = KEY_TBL.[KEY] ORDER BY rank DESC");
		       int rowCount = 0;
		       if(rs.next()){
		    	   rowCount = rs.getInt("Count");
		    	   System.out.println("Count="+rowCount);
		       }
		       Product[] results = new Product[rowCount];
		       for(int i = 0; i < rowCount; i++){
		    	   Product temp = GetProductDetails(rs.getInt("ProductID"));
		    	   if(rs.getInt("RANK") > temp.Relavance){
		    		   temp.Relavance = rs.getInt("RANK");
		    	   }
		    	   results[i] = temp;	
		    	   if(i < rowCount - 1){
		    		   rs.next();
		    	   }
		       }
		       return results;
			}
			catch(ClassNotFoundException ex) {
			   System.out.println("Error: unable to load driver class!");
			   System.exit(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
		
	}

	
	
	
/*	public static Product [] SearchProductReviews(String query)
	{	
		
		//first, let's tokenize and process the query
		HashMap<String, Double> queryMap = new HashMap<String, Double>();
		query = query.replace(".", "");
		query = query.replace("!", "");
		query = query.replace("'s", "");
		query = query.toLowerCase();
		String[] queryTokens = query.split(" ");
		int queryTotal = queryTokens.length;
		for(String token : queryTokens){
			if(!queryMap.containsKey(token)){
				queryMap.put(token, (double) 1);
			}else{
				queryMap.put(token, queryMap.get(token)+1);
			}
		}
		for(String token : queryMap.keySet()){
			double tf = queryMap.get(token);
			tf = tf / queryTotal;
			queryMap.put(token, tf);
		}
		//queryMap now contains TF values for each term in the query
		
		//now process the reviews
		Product[] products = GetProducts();
		HashMap<String, Double> corpus = new HashMap<String, Double>();
		int documentTotal = 0;
		for(Product product: products){
			String[] reviews = product.UserComments;
			int[] wordTotals = new int[reviews.length];
			documentTotal = documentTotal + reviews.length;
			HashMap<String, Double>[] wordMaps = new HashMap[reviews.length];
			for(int i = 0; i < reviews.length; i++){ //for each review, tokenize and process
				String review = reviews[i];
				review = review.replace(".", "");
				review = review.replace("!", "");
				review = review.replace("'s", "");
				review = review.toLowerCase();
				String[] tokens = review.split(" ");
				wordTotals[i] = tokens.length;
				HashMap<String, Double> thisDoc = new HashMap<String, Double>();
				for(String token : tokens){		 //for each token...			
					//if we haven't already seen this word for this document, add it
					if(!thisDoc.containsKey(token)){
						if(corpus.containsKey(token)){ //if this token has been seen in other documents, just update the count
							double a = corpus.get(token);
							corpus.put(token, a++);
						}else{ //otherwise start the count at 1
							corpus.put(token, (double) 1);
						}
						thisDoc.put(token, (double)1);
					}else{ //otherwise we've already seen this word in this document, so don't update the corpus again,
						//just the map for this particular document
						double a = thisDoc.get(token);
						thisDoc.put(token, a++);
					}
				}//done processing the individual review
				wordMaps[i] = thisDoc;
			}//done processing ALL reviews for the product
			product.wordMaps = wordMaps;
			product.wordTotals = wordTotals;			

		} //done building the corpus/wordMaps/wordTotals.
		
		
		//now let's process the corpus and figure out idf values
		for(String token : corpus.keySet()){
			double idf = corpus.get(token);
			idf = Math.log10(documentTotal/idf);
			corpus.put(token, idf);		
		}
		
		//now let's turn the tf values in queryMap into tf-idf values
		
		for(String token : queryMap.keySet()){
			if(corpus.containsKey(token)){
				double tf = queryMap.get(token);
				double tfidf = tf * corpus.get(token);
				queryMap.put(token, tfidf);
			}else{
				queryMap.put(token, (double)0);
			}
		}
		
		//NOW we can process the reviews product-by-product and calculate tf-idf values for each term in each review
		//we can also calculate the relevance for each product
		for(Product product: products){
			for(int i = 0; i < product.wordMaps.length; i++){
				HashMap<String, Double> wordMap = product.wordMaps[i];
				int wordTotal = product.wordTotals[i];
				for(String token : wordMap.keySet()){
					double tf = wordMap.get(token) / wordTotal;
					double tfidf = tf * corpus.get(token);
					wordMap.put(token, tfidf);
				}
			}
		}		
	}*/

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
				rs = stmt.executeQuery("SELECT * from Product WITH (ROWLOCK XLOCK) WHERE ProductID = " + item.Product.ProductID);
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

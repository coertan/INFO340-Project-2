import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class StoretMainWindow {
	static private JTable jTable1;
	static private JPanel jPanel1;
	static private JScrollPane jScrollPane1;
	static private JButton jButton2;
	static private JButton jButton5;
	static private JButton jButton4;
	static private JPanel jPanel2;
	static private JButton jButton1;
	static private DefaultTableModel jTable1Model ;
	
    public static void addComponentsToPane(Container pane) {
        
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        
        DateFormat df = DateFormat.getDateTimeInstance();
    	//Create and set up the window.
        JFrame frame = new JFrame("Store system");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        Container pane = frame.getContentPane();
        {
        	jScrollPane1 = new JScrollPane();
        	pane.add(jScrollPane1, BorderLayout.CENTER);
        	jScrollPane1.setPreferredSize(new java.awt.Dimension(346, 46));
        	{        		
        		jTable1Model = 
        			new DefaultTableModel(
        					null,
        					new String[] { "Date","Customer","Status","Total Price","Ship To" });
        		jTable1 = new JTable();
        		jScrollPane1.setViewportView(jTable1);
        		jTable1.setModel(jTable1Model);
        		Order [] orders = DatabaseAccess.GetPendingOrders();
        		if (orders != null)
        		{
	        		for (int i=0;i<orders.length;i++)
	        		{
	        			Order o = orders[i];
	        			jTable1Model.addRow(new Object[] {
	        					df.format(o.OrderDate),
	        					o.Customer.Name,
	        					o,
	        					Double.toString(o.TotalCost),
	        					o.ShippingAddress
	        				});
	        		}
        		}
        	}
        }
        
        //Make the center component big, since that's the
        //typical usage of BorderLayout.
        {
        	jPanel1 = new JPanel();
        	BoxLayout jPanel1Layout = new BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS);
        	jPanel1.setLayout(jPanel1Layout);

        	pane.add(jPanel1, BorderLayout.EAST);
        	{
        		jButton1 = new JButton();
        		jPanel1.add(jButton1);
        		jButton1.setText("Order Details");
        		jButton1.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent evt) {
        				jButton1ActionPerformed(evt);
        			}
        		});
        	}
        	{
        		jButton4 = new JButton();
        		jPanel1.add(jButton4);
        		jButton4.setText("New Order");
        		jButton4.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent evt) {
        				jButton4ActionPerformed(evt);
        			}
        		});
        	}
        }
        {
        	jPanel2 = new JPanel();
        	FlowLayout jPanel2Layout = new FlowLayout();
        	jPanel2Layout.setAlignment(FlowLayout.LEFT);
        	pane.add(jPanel2, BorderLayout.NORTH);
        	jPanel2.setLayout(jPanel2Layout);
        	jPanel2.setPreferredSize(new java.awt.Dimension(664, 33));
        	{
        		jButton2 = new JButton();
        		jPanel2.add(jButton2);
        		jButton2.setText("Customers");
        		jButton2.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent evt) {
        				jButton2ActionPerformed(evt);
        			}
        		});
        	}
        	{
        		jButton5 = new JButton();
        		jPanel2.add(jButton5);
        		jButton5.setText("Search Reviews");
        		jButton5.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent evt) {
        				jButton5ActionPerformed(evt);
        			}
        		});
        	}
        }

        //Use the content pane's default BorderLayout. No need for
        //setLayout(new BorderLayout());
        //Display the window.
        frame.pack();
        frame.setSize(800, 504);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	private static void jButton1ActionPerformed(ActionEvent evt) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			Order o = (Order) jTable1Model.getValueAt(row, 2);
			if (o != null)
			{
				Order oDetails = DatabaseAccess.GetOrderDetails(o.OrderID);
				new OrderDetailsWindow(oDetails);
			}	
		}
	}
	
		
	private static void jButton2ActionPerformed(ActionEvent evt) {
		new CustomersWindow();
	}
	
	private static void jButton5ActionPerformed(ActionEvent evt) {
		new NotesSearchWindow();
	}
	
	private static void jButton4ActionPerformed(ActionEvent evt) {
		new CreateOrderWindow();
	}

}

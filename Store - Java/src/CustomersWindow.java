import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import javax.swing.BoxLayout;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
public class CustomersWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1242405414797815636L;
	private JPanel jPanel1;
	private JButton jButton1;
	private JPanel jPanel3;
	private JPanel jPanel2;
	private JTable jTable1;
	private JComboBox jComboBox1;
	private JLabel jLabel1;
	private JScrollPane jScrollPane1;
	private DefaultTableModel jTable1Model ;

	public CustomersWindow ()
	{
		initGUI();
		pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		{
			jPanel3 = new JPanel();
			getContentPane().add(jPanel3, BorderLayout.WEST);
			jPanel3.setPreferredSize(new java.awt.Dimension(93, 185));
			{
				jButton1 = new JButton();
				jPanel3.add(jButton1);
				jButton1.setText("Order Details");
				jButton1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						jButton1ActionPerformed(evt);
					}
				});
			}
		}
	}
	private void initGUI() {
		try {
			{
				jPanel1 = new JPanel();
				FlowLayout jPanel1Layout = new FlowLayout();
				jPanel1Layout.setAlignment(FlowLayout.LEFT);
				jPanel1.setLayout(jPanel1Layout);
				getContentPane().add(jPanel1, BorderLayout.NORTH);
				{
					jLabel1 = new JLabel();
					jPanel1.add(jLabel1);
					jLabel1.setText("Choose Customer:");
				}
				{
					ComboBoxModel jComboBox1Model = 
						new DefaultComboBoxModel( DatabaseAccess.GetCustomers());
					jComboBox1 = new JComboBox();
					jPanel1.add(jComboBox1);
					jComboBox1.setModel(jComboBox1Model);
					jComboBox1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jComboBox1ActionPerformed(evt);
						}
					});
				}
			}
			{
				jPanel2 = new JPanel();
				BoxLayout jPanel2Layout = new BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS);
				jPanel2.setLayout(jPanel2Layout);
				getContentPane().add(jPanel2, BorderLayout.CENTER);
				jPanel2.setPreferredSize(new java.awt.Dimension(792, 187));
				{
					jScrollPane1 = new JScrollPane();
					jPanel2.add(jScrollPane1);
					{
						jTable1Model = 
							new DefaultTableModel(
									new String[] { "Order Date", "Status", "Total", "Ship To", "Billing To"},0);
						jTable1 = new JTable();
						jScrollPane1.setViewportView(jTable1);
						jTable1.setModel(jTable1Model);
						jTable1.setPreferredSize(new Dimension(800,100));
					}
				}
			}
			{
				this.setSize(800, 250);
				this.setTitle("Customers");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void jComboBox1ActionPerformed(ActionEvent evt) {
		Customer c = (Customer) jComboBox1.getSelectedItem();
		
		if (c != null)
		{
			this.jTable1Model.setRowCount(0);
		
			DateFormat df = DateFormat.getDateTimeInstance();
			Order [] orders = DatabaseAccess.GetCustomerOrders(c);
			if (orders!= null)
			{
				for (int i=0;i<orders.length;i++)
				{
					Order o = orders[i];
					jTable1Model.addRow(
								new Object[] { 
									df.format(o.OrderDate),
									o,
									o.TotalCost,
									o.ShippingAddress,
									o.BillingAddress}
							);
				}
			}
		}
	}
	
	private void jButton1ActionPerformed(ActionEvent evt) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			Order o = (Order) jTable1Model.getValueAt(row, 1);
			Order oDetail = DatabaseAccess.GetOrderDetails(o.OrderID);
			new OrderDetailsWindow(oDetail);
		}
	}

}

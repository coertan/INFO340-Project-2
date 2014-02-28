import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
public class NotesSearchWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1242405414797815636L;
	private JPanel jPanel1;
	private JButton jButton2;
	private JPanel jPanel2;
	private JButton jButton1;
	private JTextField jTextField1;
	private JTable jTable1;
	private JLabel jLabel1;
	private JScrollPane jScrollPane1;
	private DefaultTableModel jTable1Model ;

	public NotesSearchWindow ()
	{
		initGUI();
		pack();
		this.setLocationRelativeTo(null);
        setSize(800, 504);
		this.setVisible(true);
	}
	private void initGUI() {
		try {
			{
				jPanel1 = new JPanel();
				FlowLayout jPanel1Layout = new FlowLayout();
				jPanel1Layout.setAlignment(FlowLayout.LEFT);
				jPanel1.setLayout(jPanel1Layout);
				getContentPane().add(getJPanel2(), BorderLayout.WEST);
				getContentPane().add(jPanel1, BorderLayout.NORTH);
				{
					jLabel1 = new JLabel();
					jPanel1.add(jLabel1);
					jLabel1.setText("Enter keywords:");
				}
				{
					jTextField1 = new JTextField();
					jPanel1.add(jTextField1);
					jTextField1.setPreferredSize(new java.awt.Dimension(192, 23));
				}
				{
					jButton1 = new JButton();
					jPanel1.add(jButton1);
					jButton1.setText("Search");
					jButton1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							jButton1ActionPerformed(evt);
						}
					});
				}
			}
			{
				jScrollPane1 = new JScrollPane();
				getContentPane().add(jScrollPane1, BorderLayout.CENTER);
				{
					jTable1Model = 
						new DefaultTableModel(
								new String[] { "Rank","Product","Price" },0);
					jTable1 = new JTable();
					jScrollPane1.setViewportView(jTable1);
					jTable1.setModel(jTable1Model);
					jTable1.setPreferredSize(new java.awt.Dimension(789, 100));
				}
			}
			{
				this.setSize(800, 250);
				this.setTitle("Search Product Reviews");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void jButton1ActionPerformed(ActionEvent evt) {
		this.jTable1Model.setRowCount(0);
			
		Product [] products  = DatabaseAccess.SearchProductReviews(jTextField1.getText());
		if (products != null)
		{
			for (int i=0;i<products.length;i++)
			{
				Product p = products[i];
				jTable1Model.addRow(
							new Object[] { 
								p.Relavance,
								p,
								Double.toString(p.Price)
							}
						);
			}
		}
	}
	
	private JPanel getJPanel2() {
		if(jPanel2 == null) {
			jPanel2 = new JPanel();
			BoxLayout jPanel2Layout = new BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS);
			jPanel2.setLayout(jPanel2Layout);
			jPanel2.setPreferredSize(new java.awt.Dimension(108, 442));
			jPanel2.add(getJButton2());
		}
		return jPanel2;
	}
	
	private JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Show Details");
			jButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton2ActionPerformed(evt);
				}
			});
		}
		return jButton2;
	}
	
	private void jButton2ActionPerformed(ActionEvent evt) {
		int row = jTable1.getSelectedRow();
		if (row >= 0)
		{
			Product p = (Product) jTable1Model.getValueAt(row, 1);
			Product pDetails = DatabaseAccess.GetProductDetails(p.ProductID);
			new ProductDetailsWindow(pDetails);
		}
	}

}

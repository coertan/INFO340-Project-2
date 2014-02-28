import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

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
public class ProductDetailsWindow  {

	/**
	 * 
	 */
	private JFrame jFrame1;
	private JPanel jPanel1;
	private JLabel jLabel4;
	private JScrollPane jScrollPane1;
	private JTextField jTextField4;
	private JTextField jTextField3;
	private JLabel jLabel3;
	private JTextField jTextField2;
	private JLabel jLabel2;
	private JTextField jTextField1;
	private JLabel jLabel1;
	private JTable jTable1;
	private DefaultTableModel jTable1Model;
	
	public ProductDetailsWindow (Product theProduct )
	{
		JFrame frame = getJFrame1();
		jFrame1.setTitle("Product Details");
		jTextField1.setText(theProduct.Name);
		jTextField2.setText(theProduct.Description);
		jTextField3.setText(Double.toString(theProduct.Price));
		jTextField4.setText(Double.toString(theProduct.InStock));
		
		if (theProduct.UserComments!=null)
		{
			for (int i=0;i<theProduct.UserComments.length;i++)
			{
				jTable1Model.addRow(
						new Object[] 
						           {
									theProduct.UserComments[i]
								}
						);
			}
		}

		frame.pack();
		frame.setLocationRelativeTo(null);
		jFrame1.setSize(new Dimension(600,400));

		frame.setVisible(true);
	}
	
	private JFrame getJFrame1() {
		if(jFrame1 == null) {
			jFrame1 = new JFrame();
			jFrame1.getContentPane().add(getJScrollPane1(), BorderLayout.CENTER);
			jFrame1.getContentPane().add(getJPanel1(), BorderLayout.NORTH);
			jFrame1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}
		return jFrame1;
	}
	
	private JTable getJTable1() {
		if(jTable1 == null) {
			jTable1Model = 
				new DefaultTableModel(
						new String[] { "User Comment" },0);
			jTable1 = new JTable();
			
			jTable1.setModel(jTable1Model);
		}
		return jTable1;
	}
	
	private JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new JPanel();
			GridLayout jPanel1Layout = new GridLayout(74, 2);
			jPanel1Layout.setColumns(2);
			jPanel1Layout.setHgap(5);
			jPanel1Layout.setVgap(5);
			jPanel1Layout.setRows(4);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.add(getJLabel1());
			jPanel1.add(getJTextField1());
			jPanel1.add(getJLabel2());
			jPanel1.add(getJTextField2());
			jPanel1.add(getJLabel3());
			jPanel1.add(getJTextField3());
			jPanel1.add(getJLabel4());
			jPanel1.add(getJTextField4());
		}
		return jPanel1;
	}
	
	private JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Name:");
		}
		return jLabel1;
	}
	
	private JTextField getJTextField1() {
		if(jTextField1 == null) {
			jTextField1 = new JTextField();
		}
		return jTextField1;
	}
	
	private JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Description:");
		}
		return jLabel2;
	}
	
	private JTextField getJTextField2() {
		if(jTextField2 == null) {
			jTextField2 = new JTextField();
		}
		return jTextField2;
	}
	
	private JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Price:");
		}
		return jLabel3;
	}
	
	private JTextField getJTextField3() {
		if(jTextField3 == null) {
			jTextField3 = new JTextField();
		}
		return jTextField3;
	}
	
	private JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Stock On Hand:");
		}
		return jLabel4;
	}
	
	private JTextField getJTextField4() {
		if(jTextField4 == null) {
			jTextField4 = new JTextField();
		}
		return jTextField4;
	}

	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJTable1());
		}
		return jScrollPane1;
	}

}

package gui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cfmv.App;
import cfmv.Comparator;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;

/**
 * Graphic interface.
 */
public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	/** CVS with original data set */
	private File originalDS;
	/** CVS with incomplete data set */
	private File incompleteDS;
	/** Output folder */
	private File output;
	/** k for k-means */
	private int k;
	/** Flag */
	private boolean runned;
	/** To show messages the console of the GUI */
	MessageConsole mc;

	private JPanel contentPane;
	private JButton btnOpenIncompleteDS;
	private JButton btnOutput;
	private JFileChooser fc1;
	private JFileChooser fc2;
	private JFileChooser fc3;
	private JTextField originalDSinput;
	private JTextField outputInput;
	private JTextField incompleteDSinput;
	private JSpinner kInput;
	private JButton btnRun;
	private JTextPane messagesArea;
	private JButton btnCompare;
	private JButton btnOriginalDS;

	/**
	 * Launch the application with GUI.
	 *
	 * @param args
	 *            arguments (none)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		contentPane = new JPanel();
		setContentPane(contentPane);
		setTitle("Finding missed values in a data set");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 445, 433);

		String desktop = (System.getProperty("user.home") + "\\Desktop")
				.replace("\\", "/");
		fc1 = new JFileChooser(desktop);
		fc2 = new JFileChooser(desktop);
		fc3 = new JFileChooser(desktop);

		// Data set with missed values
		JLabel lblIncompleteDSinput = new JLabel(
				"Data set with missed values:");
		lblIncompleteDSinput.setBounds(15, 10, 278, 14);
		lblIncompleteDSinput.setLabelFor(incompleteDSinput);

		incompleteDSinput = new JTextField();
		incompleteDSinput.setBounds(15, 25, 199, 20);
		incompleteDSinput.setColumns(10);
		incompleteDSinput.setText(desktop + "/dataset.csv");
		incompleteDS = new File(incompleteDSinput.getText());

		btnOpenIncompleteDS = new JButton("Open");
		btnOpenIncompleteDS.setBounds(224, 25, 70, 20);
		btnOpenIncompleteDS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fc1.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"CSV files", "csv");
				fc1.setFileFilter(filter);
				fc1.setDialogTitle("Select data set");
				int result = fc1.showSaveDialog(GUI.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					incompleteDS = fc1.getSelectedFile();
					incompleteDSinput.setText(
							incompleteDS.toString().replace("\\", "/"));
				}
			}
		});

		// Output folder
		JLabel lblOutputInput = new JLabel("Output folder:");
		lblOutputInput.setBounds(15, 59, 199, 14);
		lblOutputInput.setLabelFor(outputInput);

		outputInput = new JTextField();
		outputInput.setBounds(15, 74, 199, 20);
		outputInput.setColumns(10);
		outputInput.setText(desktop);
		output = new File(outputInput.getText().replace("\\", "/"));

		btnOutput = new JButton("Select");
		btnOutput.setBounds(224, 74, 70, 20);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc2.setDialogTitle("Select data set");
				int result = fc2.showSaveDialog(GUI.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					output = fc2.getSelectedFile();
					outputInput.setText(output.toString().replace("\\", "/"));
				}
			}
		});

		// Run
		JLabel lblKmean = new JLabel("k-means");
		lblKmean.setLabelFor(kInput);
		lblKmean.setBounds(312, 25, 50, 20);

		kInput = new JSpinner();
		kInput.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1),
				null, new Integer(1)));
		kInput.setBounds(372, 25, 40, 20);

		btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
		btnRun.setBounds(313, 54, 99, 40);

		// Messages
		JLabel lblMessagesArea = new JLabel("Messages:");
		lblMessagesArea.setBounds(15, 105, 69, 14);
		lblMessagesArea.setLabelFor(messagesArea);

		messagesArea = new JTextPane();
		messagesArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(15, 120, 397, 193);
		scrollPane.setViewportView(messagesArea);

		mc = new MessageConsole(messagesArea);
		mc.redirectOut();
		mc.redirectErr(Color.RED, null);

		JSeparator separator = new JSeparator();
		separator.setBounds(15, 326, 395, 2);

		// Compare
		JLabel lblOriginalDSinput = new JLabel("Original data set:");
		lblOriginalDSinput.setBounds(15, 343, 173, 14);
		lblOriginalDSinput.setLabelFor(originalDSinput);

		originalDSinput = new JTextField();
		originalDSinput.setBounds(15, 358, 199, 20);
		originalDSinput.setColumns(10);
		originalDSinput.setText(desktop + "/original.csv");
		originalDS = new File(originalDSinput.getText());

		btnOriginalDS = new JButton("Select");
		btnOriginalDS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc3.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"CSV files", "csv");
				fc3.setFileFilter(filter);
				fc3.setDialogTitle("Select original data set");
				int result = fc3.showSaveDialog(GUI.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					originalDS = fc3.getSelectedFile();
					originalDSinput
							.setText(originalDS.toString().replace("\\", "/"));
				}
			}
		});
		btnOriginalDS.setBounds(224, 358, 70, 20);

		btnCompare = new JButton("Compare");
		btnCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compare();
			}
		});
		btnCompare.setBounds(313, 339, 99, 40);

		// Layout
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.add(lblIncompleteDSinput);
		contentPane.add(incompleteDSinput);
		contentPane.add(btnOpenIncompleteDS);
		contentPane.add(lblOutputInput);
		contentPane.add(outputInput);
		contentPane.add(btnOutput);
		contentPane.add(lblKmean);
		contentPane.add(kInput);
		contentPane.add(btnRun);
		contentPane.add(lblMessagesArea);
		contentPane.add(scrollPane);
		contentPane.add(separator);
		contentPane.add(lblOriginalDSinput);
		contentPane.add(originalDSinput);
		contentPane.add(btnOriginalDS);
		contentPane.add(btnCompare);
	}

	/**
	 * Run the aplication with the data sets selected in the GUI.
	 */
	private void run() {
		messagesArea.setText("");
		try {
			if (incompleteDS != null && output != null) {
				k = (int) kInput.getValue();
				App.run(incompleteDS, output, k);
				runned = true;
			} else {
				System.err.println("ERROR: program not configured");
			}
		} catch (IOException e) {
			System.err.println(
					"ERROR: at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			System.err.println("ERROR: wrong value. " + e.getMessage());
		} catch (Exception e) {
			System.err.println("ERROR. " + e.getMessage());
		}
	}

	/**
	 * Compare the original data set with the output data set and show the
	 * results in the GUI.
	 */
	private void compare() {
		messagesArea.setText("");
		try {
			if (originalDS != null && incompleteDS != null && output != null
					&& runned) {
				Comparator.compare(originalDS, incompleteDS, output);
			} else {
				System.err.println("ERROR: program not configured");
			}
		} catch (IOException e) {
			System.err.println(
					"ERROR: at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			System.err.println("ERROR: wrong value. " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("ERROR. " + e.getMessage());
		}
	}
}
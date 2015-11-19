package cfmv;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File originalDS;
	private File incompleteDS;
	private File output;
	private int k;
	private boolean runned;
	MessageConsole mc;

	private JPanel contentPane;
	private JButton btnOpenIncompleteDS;
	private JButton btnOutput;
	private JFileChooser fc;
	private JTextField originalDSinput;
	private JTextField outputInput;
	private JTextField incompleteDSinput;
	private JSpinner kInput;
	private JButton btnRun;
	private JTextPane messagesArea;
	private JButton btnCompare;
	private JButton btnOriginalDS;

	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(GUI.class);

	/**
	 * Launch the application.
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
		setBounds(100, 100, 445, 326);

		String desktop = System.getProperty("user.home") + "\\Desktop";
		fc = new JFileChooser(desktop);

		// Data set with missed values
		JLabel lblIncompleteDSinput = new JLabel(
				"Data set with missed values:");
		lblIncompleteDSinput.setBounds(15, 10, 278, 14);
		lblIncompleteDSinput.setLabelFor(incompleteDSinput);

		incompleteDSinput = new JTextField();
		incompleteDSinput.setBounds(15, 25, 199, 20);
		incompleteDSinput.setColumns(10);
		incompleteDSinput.setText(desktop + "\\dataset.csv");
		incompleteDS = new File(incompleteDSinput.getText());

		btnOpenIncompleteDS = new JButton("Open");
		btnOpenIncompleteDS.setBounds(224, 25, 70, 20);
		btnOpenIncompleteDS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"CSV files", "csv");
				fc.setFileFilter(filter);
				fc.setDialogTitle("Select data set");
				int result = fc.showSaveDialog(GUI.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					incompleteDS = fc.getSelectedFile();
					incompleteDSinput.setText(incompleteDS.toString());
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
		output = new File(outputInput.getText());

		btnOutput = new JButton("Select");
		btnOutput.setBounds(224, 74, 70, 20);
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Select data set");
				int result = fc.showSaveDialog(GUI.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					output = fc.getSelectedFile();
					outputInput.setText(output.toString());
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
		scrollPane.setBounds(15, 120, 397, 92);
		scrollPane.setViewportView(messagesArea);

		mc = new MessageConsole(messagesArea);
		mc.redirectOut();
		mc.redirectErr(Color.RED, null);

		JSeparator separator = new JSeparator();
		separator.setBounds(15, 221, 395, 2);

		// Compare
		JLabel lblOriginalDSinput = new JLabel("Original data set:");
		lblOriginalDSinput.setBounds(15, 236, 173, 14);
		lblOriginalDSinput.setLabelFor(originalDSinput);

		originalDSinput = new JTextField();
		originalDSinput.setBounds(15, 251, 199, 20);
		originalDSinput.setColumns(10);
		originalDSinput.setText(desktop + "\\original.csv");
		originalDS = new File(originalDSinput.getText());

		btnOriginalDS = new JButton("Select");
		btnOriginalDS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"CSV files", "csv");
				fc.setFileFilter(filter);
				fc.setDialogTitle("Select original data set");
				int result = fc.showSaveDialog(GUI.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					originalDS = fc.getSelectedFile();
					originalDSinput.setText(originalDS.toString());
				}			
			}
		});
		btnOriginalDS.setBounds(224, 251, 70, 20);

		btnCompare = new JButton("Compare");
		btnCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compare();
			}
		});
		btnCompare.setBounds(313, 232, 99, 40);

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

	private void run() {
		messagesArea.setText("");
		try {
			if (incompleteDS != null && output != null) {
				k = (int) kInput.getValue();
				App.run(incompleteDS, output, k);
				runned = true;
			} else {
				logger.info("ERROR: program not configured");
			}
		} catch (IOException e) {
			logger.info("Error at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			logger.info("Wrong value. " + e.getMessage());
		}
	}

	private void compare() {
		messagesArea.setText("");
		try {
			if (originalDS != null && incompleteDS != null && output != null
					&& runned) {
				App.similarity(originalDS, incompleteDS, output);
			} else {
				logger.info("ERROR: program not configured");
			}
		} catch (IOException e) {
			logger.info("Error at processing data set. " + e.getMessage());
		} catch (NumberFormatException e) {
			logger.info("Wrong value. " + e.getMessage());
		}
	}
}

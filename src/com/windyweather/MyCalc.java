package com.windyweather;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import javax.swing.SwingConstants;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MyCalc extends JFrame {

	private JPanel contentPane;
	private JTextField txtNumber1;
	private JTextField txtNumber2;
	private JTextField tfTimerTics;
	
	private long nTimerTics;
	private boolean bTimerRunning;
	private Timer aTimer;
	private MyTimerTask timerTask;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyCalc frame = new MyCalc();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//
	// a timer task to tick down the time
	//
	private class MyTimerTask extends TimerTask {
		
		public void run()
		{
			nTimerTics++;
		}
	}
	
	/**
	 * Create the frame.
	 */
	public MyCalc() {
		
		bTimerRunning = false;
		aTimer = new Timer();
		timerTask = new MyTimerTask();
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 543, 335);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtNumber1 = new JTextField();
		txtNumber1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		txtNumber1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtNumber1.setBounds(41, 35, 107, 29);
		txtNumber1.setText("0");
		contentPane.add(txtNumber1);
		txtNumber1.setColumns(10);
		
		txtNumber2 = new JTextField();
		txtNumber2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		txtNumber2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtNumber2.setBounds(41, 85, 107, 29);
		txtNumber2.setText("0");
		contentPane.add(txtNumber2);
		txtNumber2.setColumns(10);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnAdd.setBounds(41, 129, 107, 35);
		contentPane.add(btnAdd);
		
		JLabel lblTotal = new JLabel("Total");
		lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotal.setBounds(10, 219, 175, 37);
		lblTotal.setForeground(new Color(30, 144, 255));
		lblTotal.setFont(new Font("Tahoma", Font.BOLD, 30));
		contentPane.add(lblTotal);
		
		JSpinner spnMillisecondsPerTick = new JSpinner();
		spnMillisecondsPerTick.setModel(new SpinnerNumberModel(new Long(100), new Long(100), new Long(999), new Long(50)));
		spnMillisecondsPerTick.setFont(new Font("Tahoma", Font.PLAIN, 20));
		spnMillisecondsPerTick.setBounds(321, 40, 77, 24);
		contentPane.add(spnMillisecondsPerTick);
		
		JLabel lblNewLabel = new JLabel("M Secs");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel.setBounds(422, 42, 77, 21);
		contentPane.add(lblNewLabel);
		
		JLabel lblTimer = new JLabel("Timer");
		lblTimer.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTimer.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTimer.setBounds(229, 42, 69, 21);
		contentPane.add(lblTimer);
		
		JButton btnStartTimer = new JButton("Start Timer");
		btnStartTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Long msecsPerTic = (Long) spnMillisecondsPerTick.getValue();
				System.out.println("btnStartTimer for "+String.valueOf(msecsPerTic) );
				aTimer.schedule(timerTask, msecsPerTic, msecsPerTic);
			}
		});
		btnStartTimer.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnStartTimer.setBounds(290, 85, 138, 29);
		contentPane.add(btnStartTimer);
		
		JButton btnStopTimer = new JButton("Stop Timer");
		btnStopTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timerTask.cancel();
			}
		});
		btnStopTimer.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnStopTimer.setBounds(290, 126, 138, 29);
		contentPane.add(btnStopTimer);
		
		JButton btnClearTics = new JButton("Clear Tics");
		btnClearTics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nTimerTics = 0;
				tfTimerTics.setText(String.valueOf(nTimerTics));
			}
		});
		btnClearTics.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnClearTics.setBounds(290, 227, 138, 29);
		contentPane.add(btnClearTics);
		
		JLabel lblTimerTics = new JLabel("Timer Tics");
		lblTimerTics.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTimerTics.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTimerTics.setBounds(203, 181, 107, 21);
		contentPane.add(lblTimerTics);
		
		tfTimerTics = new JTextField();
		tfTimerTics.setEditable(false);
		tfTimerTics.setHorizontalAlignment(SwingConstants.RIGHT);
		tfTimerTics.setText("0");
		tfTimerTics.setFont(new Font("Tahoma", Font.PLAIN, 20));
		tfTimerTics.setBounds(329, 177, 135, 29);
		contentPane.add(tfTimerTics);
		tfTimerTics.setColumns(10);
		
		JLabel lblSumOfNumbers = new JLabel("Sum");
		lblSumOfNumbers.setHorizontalAlignment(SwingConstants.CENTER);
		lblSumOfNumbers.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblSumOfNumbers.setBounds(58, 188, 90, 21);
		contentPane.add(lblSumOfNumbers);
		
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int num1 = Integer.parseInt( txtNumber1.getText() );
				int num2 = Integer.parseInt( txtNumber2.getText() );
				int answer = num1 + num2;
				lblTotal.setText( Integer.toString(answer));
			}
		});
	}
}

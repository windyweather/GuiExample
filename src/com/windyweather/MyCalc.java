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
import java.awt.Robot;

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
import java.awt.event.InputEvent;
import java.awt.event.ActionEvent;

public class MyCalc extends JFrame {
	
	static final long serialVersionUID = 123456L;

	private JPanel contentPane;
	private JTextField txtNumber1;
	private JTextField txtNumber2;
	private JTextField tfTimerTics;
	
	private long nTimerTics;
	private boolean bTimerRunning;
	private Timer aTimer;
	private MyTimerTask timerTask;
	private JTextField tfImpressPath;
	private JTextField txtOptions;
	private JTextField txtShowPath;
	
	private boolean bShowRunning;
	private Process pShowProcess;
	

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
	// up the tics and click the mouse
	//
	private class MyTimerTask extends TimerTask {
		@Override
		public void run()
		{
			nTimerTics++;
			tfTimerTics.setText(String.valueOf(nTimerTics));
			// try to click the mouse here
			try {
				Robot bot = new Robot();
				int mask = InputEvent.BUTTON1_DOWN_MASK;
				// don't move the mouse in case the user wants to click on Stop Show or
				// something else. It will be fine, the show will stop on the click
				// if it's at the end.
				//bot.mouseMove(100, 100);           
				bot.mousePress(mask);     
			
				try {
					// hang for a bit before release
				    Thread.sleep(100);
				}
				catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				    System.out.println("MyTimerTask interrupted");
				}
				bot.mouseRelease(mask);
			}
			catch (Exception e ) {
				System.out.println("MyTimerTask Robot exception");
			}
			
			// if the show is running, watch for it to end in a strange way
			
			if ( bShowRunning ) {
				
				try {
					// so, rather than a wait, we check for exit value
					// and if that tosses an exception, the process is
					// still running. Ooooooookkkkkaaaaaayyyyyy No Problem
					int exitValue = pShowProcess.exitValue();
					// guess we don't do this to get rid of the not referened warning
					//(void)exitValue;
					// we don't care what the exit value was
					exitValue = 0;
					// but if we get here, then the show stopped, so
					// if we stop it now, it won't need to wait, it will be fine
					// we think.
					stopShow();
				} catch (Exception ex) {
					// Process is still running. So just keep going until
					// mouse clicks or something else stops the show
				}
			}
		}
	}
	
	public void startTimer( long msecsPerTic ) {
		try {
			if ( bTimerRunning ) {
				System.out.println("startTimer already running" );
				return;
			}
			aTimer = new Timer();
			timerTask = new MyTimerTask();
			aTimer.schedule(timerTask, msecsPerTic, msecsPerTic);
			bTimerRunning = true;
			System.out.println("startTimer started for "+String.valueOf(msecsPerTic) );
		} catch (Exception ex)
		{
			// just ignore any exceptions
			System.out.println("startTimer exception" );
		}

	}
	
	public void stopTimer() {
		try {
		if ( !bTimerRunning ) {
			System.out.println("stopTimer not running" );
			return;
		}
		timerTask.cancel();
		bTimerRunning = false;
		System.out.println("stopTimer cancelled" );
		} catch (Exception ex) {
			System.out.println("stopTimer exception" );
		}
	}
	
	public void startShowPlaying( String sImpress, String sOptions, String sShowPath ) {
		
		if ( bShowRunning ) {
			
			System.out.println("startShowPlaying already running");
			return;
		}
		String cmdString = sImpress +" "+sOptions+" "+sShowPath;
		try {
			pShowProcess = Runtime.getRuntime().exec( cmdString );
			System.out.println("startShowPlaying show started");
			bShowRunning = true;
			if ( bTimerRunning ) {
				stopTimer();
			}
			startTimer( 5000 );
		} catch (Exception ex ) {
			System.out.println("startShowPlaying exception");
		}

	}
	
	public void stopShow() {
		if ( !bShowRunning ) {
			System.out.println("stopShow show not running");
			return;
		}
		// we cannot use destroy() since that would leave the Impress show
		// in a bad state. So all we can do is wait on the user to stop the
		// show and then clean up.
		
		try {
			// stop the mouse clicks
			stopTimer();
			System.out.println("stopShow waiting for you to stop the show");
			pShowProcess.waitFor();
			System.out.println("stopShow show not running");
			bShowRunning = false;
		} catch (Exception ex ) {
			System.out.println("stopShow exception");
			bShowRunning = false; // try to clean up
		}
	}
	
	/**
	 * Create the frame.
	 */
	@SuppressWarnings("deprecation")
	public MyCalc() {
		
		bTimerRunning = false;
		bShowRunning = false;
	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 543, 527);
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
		
		JSpinner spnSecsPerTic = new JSpinner();
		spnSecsPerTic.setModel(new SpinnerNumberModel(new Long(5), new Long(1), new Long(99), new Long(1)));
		spnSecsPerTic.setFont(new Font("Tahoma", Font.PLAIN, 20));
		spnSecsPerTic.setBounds(321, 40, 77, 24);
		contentPane.add(spnSecsPerTic);
		
		JLabel lblNewLabel = new JLabel("Secs");
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
				Long msecsPerTic = (Long) spnSecsPerTic.getValue() * 1000;
				startTimer( msecsPerTic );
			}
		});
		btnStartTimer.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnStartTimer.setBounds(290, 85, 138, 29);
		contentPane.add(btnStartTimer);
		
		JButton btnStopTimer = new JButton("Stop Timer");
		btnStopTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTimer();
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
		
		JLabel lblImpress = new JLabel("Impress");
		lblImpress.setHorizontalAlignment(SwingConstants.RIGHT);
		lblImpress.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblImpress.setBounds(22, 301, 107, 21);
		contentPane.add(lblImpress);
		
		JLabel lblOptions = new JLabel("Options");
		lblOptions.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOptions.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblOptions.setBounds(22, 332, 107, 21);
		contentPane.add(lblOptions);
		
		JLabel lblShowpaths = new JLabel("ShowPath");
		lblShowpaths.setHorizontalAlignment(SwingConstants.RIGHT);
		lblShowpaths.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblShowpaths.setBounds(22, 382, 107, 21);
		contentPane.add(lblShowpaths);
		
		tfImpressPath = new JTextField();
		tfImpressPath.setText("C:\\Program Files\\LibreOffice\\program\\soffice.exe");
		tfImpressPath.setHorizontalAlignment(SwingConstants.LEFT);
		tfImpressPath.setFont(new Font("Tahoma", Font.PLAIN, 20));
		tfImpressPath.setColumns(10);
		tfImpressPath.setBounds(149, 293, 338, 29);
		contentPane.add(tfImpressPath);
		
		txtOptions = new JTextField();
		txtOptions.setText("--impress --show");
		txtOptions.setHorizontalAlignment(SwingConstants.LEFT);
		txtOptions.setFont(new Font("Tahoma", Font.PLAIN, 20));
		txtOptions.setColumns(10);
		txtOptions.setBounds(149, 337, 149, 29);
		contentPane.add(txtOptions);
		
		txtShowPath = new JTextField();
		txtShowPath.setText("D:\\aaArtHarvesting\\zzLibreOffice\\ChainTests\\ShowTestOne.odp");
		txtShowPath.setHorizontalAlignment(SwingConstants.LEFT);
		txtShowPath.setFont(new Font("Tahoma", Font.PLAIN, 20));
		txtShowPath.setColumns(10);
		txtShowPath.setBounds(149, 378, 338, 29);
		contentPane.add(txtShowPath);
		
		JButton btnStartShow = new JButton("Start Show");
		btnStartShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startShowPlaying( tfImpressPath.getText(), txtOptions.getText(), txtShowPath.getText() );
			}
		});
		btnStartShow.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnStartShow.setBounds(149, 421, 138, 29);
		contentPane.add(btnStartShow);
		
		JButton btnStopShow = new JButton("Stop Show");
		btnStopShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopShow();
			}
		});
		btnStopShow.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnStopShow.setBounds(306, 421, 138, 29);
		contentPane.add(btnStopShow);
		
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

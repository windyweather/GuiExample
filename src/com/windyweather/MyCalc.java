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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
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
	private JButton btnWhere;
	private JLabel lblStatus;
	
	private boolean bShowRunning;
	private Process pShowProcess;
	
	private final String sImpressOnWindows = "C:\\Program Files\\LibreOffice\\program\\soffice.exe";
	private final String sImpressOnLinux = "soffice";
	private final String sOptionsOnWindows = "--impress --show";
	private final String sOptionsOnLinux = "--impress --show";
	private final String sShowPathOnWindows = "D:\\aaArtHarvesting\\zzLibreOffice\\ChainTests\\ShowTestOne.odp";
	private final String sShowPathOnLinux = "/home/darrell/ImpressTests/ChainTests/ShowTestOne.odp";
	// /home/darrell/ImpressTests/ChainTests/ShowTestOne.odp
	
	
	void setStatus( String str ) {
		
		lblStatus.setText(str);
	}
	
	
	private boolean isOsWindows()
	{
		String osName = System.getProperty ("os.name");
		if ( osName.contains("Windows") ) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean isOsLinux()
	{
		String osName = System.getProperty ("os.name");
		if ( osName.contains("Linux") ) {
			return true;
		}
		else {
			return false;
		}
	}
	//
	// Do this in one place so we can easily turn it off later
	//
	private void printSysOut( String str ) {
		System.out.println(str);
	}

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
				    printSysOut("MyTimerTask interrupted");
				}
				bot.mouseRelease(mask);
			}
			catch (Exception e ) {
				printSysOut("MyTimerTask Robot exception");
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
				printSysOut("startTimer already running" );
				return;
			}
			aTimer = new Timer();
			timerTask = new MyTimerTask();
			aTimer.schedule(timerTask, msecsPerTic, msecsPerTic);
			bTimerRunning = true;
			printSysOut("startTimer started for "+String.valueOf(msecsPerTic) );
		} catch (Exception ex)
		{
			// just ignore any exceptions
			printSysOut("startTimer exception" );
		}

	}
	
	public void stopTimer() {
		try {
		if ( !bTimerRunning ) {
			printSysOut("stopTimer not running" );
			return;
		}
		timerTask.cancel();
		bTimerRunning = false;
		printSysOut("stopTimer cancelled" );
		} catch (Exception ex) {
			printSysOut("stopTimer exception" );
		}
	}
	
	/*
	 * example from https://www.javaworld.com/article/2071275/when-runtime-exec---won-t.html?page=2
	 */
	class StreamGobbler extends Thread
	{
	    InputStream is;
	    String type;
	    OutputStream os;
	    
	    StreamGobbler(InputStream is, String type)
	    {
	        this(is, type, null);
	    }
	    StreamGobbler(InputStream is, String type, OutputStream redirect)
	    {
	        this.is = is;
	        this.type = type;
	        this.os = redirect;
	    }
	    
	    public void run()
	    {
	        try
	        {
	            PrintWriter pw = null;
	            if (os != null)
	                pw = new PrintWriter(os);
	                
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	            {
	                if (pw != null)
	                    pw.println(line);
	                System.out.println(type + ">" + line);    
	            }
	            if (pw != null)
	                pw.flush();
	        } catch (IOException ioe)
	            {
	            ioe.printStackTrace();  
	            }
	    }
	}
	
	public void launchProgram( String cmdString ) {
	    try
	    {            
	        PrintStream fos = System.out;
	        Runtime rt = Runtime.getRuntime();
	        Process proc = rt.exec("java jecho 'Hello World'");
	        // any error message?
	        StreamGobbler errorGobbler = new 
	            StreamGobbler(proc.getErrorStream(), "ERROR");            
	        
	        // any output?
	        StreamGobbler outputGobbler = new 
	            StreamGobbler(proc.getInputStream(), "OUTPUT", fos);
	            
	        // kick them off
	        errorGobbler.start();
	        outputGobbler.start();
	                                
	        // any error???
	        int exitVal = proc.waitFor();
	        System.out.println("ExitValue: " + exitVal);
	        fos.flush();
	        fos.close();        
	    } catch (Throwable t)
	      {
	        t.printStackTrace();
	      }
	}
	
	
	/*
	 * Getting around error on Linux. Need to launch by writing a script and launching that.
	 */
	public void startShowPlayingLinux( String sImpress, String sOptions, String sShowPath ) {
		
		if ( bShowRunning ) {
			
			printSysOut("startShowPlayingLinux already running");
			return;
		}
		String cmdString = sImpress +" "+sOptions+" "+sShowPath;

		String scriptPath = "";
		try {
			scriptPath = pathToOurJarFile();
			if ( scriptPath.isEmpty() ) {
				printSysOut("startShowPlayingLinux we can't find ourselves");
				return;
			}
			scriptPath = scriptPath + "launchImpressShow.bash";
			printSysOut("startShowPlayingLinux to "+scriptPath);
			
			/*
			 * write the script, since arguments don't appear to work.
			 */
			File scriptFile = new File(scriptPath);
			
			try {
				if (scriptFile.exists()) {
					scriptFile.delete();
				}
			} catch (Exception ex) {
				printSysOut("startShowPlayingLinux Error deleting launchscript "+scriptFile);
				return;
			}
			/*
			 * Write the script file here
			 */
			try {

			    FileWriter fileWriter = new FileWriter(scriptFile);
			    PrintWriter printWriter = new PrintWriter(fileWriter);
			    printWriter.println("#!/bin/bash");
			    printWriter.println("echo \"Start impress with args\"");
			    printWriter.println(cmdString + " > /dev/null 2>&1");
			    printWriter.println("echo \"Impress is done now\"");
			    printWriter.println("exit 0");
			    printWriter.println("");
			    printWriter.close();

	             boolean bval = scriptFile.setExecutable(true,false);
	             printSysOut("startShowPlayingLinux setExecutable "+ bval+"  "+scriptPath);
			} catch (Exception ex) {
				
			}
			/*
			 * Launch the script to cover the impress error
			 */
			//pShowProcess = Runtime.getRuntime().exec( scriptPath );
			String [] cmdAry = new String[] {System.getenv("SHELL"),"-c",scriptPath};
			pShowProcess = Runtime.getRuntime().exec( cmdAry );
			printSysOut("startShowPlayingLinux show started "+cmdString);
			bShowRunning = true;
			if ( bTimerRunning ) {
				stopTimer();
			}
			startTimer( 5000 );
		} catch (Exception ex ) {
			printSysOut("startShowPlayingLinux exception "+ex.getMessage() );
			printSysOut(scriptPath);
			printSysOut(cmdString);
		}

	}
	
	
	/*
	 * much easier on windows since it just works
	 */
	public void startShowPlayingWindows( String sImpress, String sOptions, String sShowPath ) {
		
		if ( bShowRunning ) {
			
			printSysOut("startShowPlayingWindows already running");
			return;
		}
		String cmdString = sImpress +" "+sOptions+" "+sShowPath;

		try {
			pShowProcess = Runtime.getRuntime().exec( cmdString );
			printSysOut("startShowPlayingWindows show started");
			bShowRunning = true;
			if ( bTimerRunning ) {
				stopTimer();
			}
			startTimer( 5000 );
		} catch (Exception ex ) {
			printSysOut("startShowPlayingWindows exception "+ex.getMessage() );
			printSysOut(cmdString);
		}

	}
	
	/*
	 * Two launch strategies to get around error on Linux
	 */
	public void startShowPlaying( String sImpress, String sOptions, String sShowPath ) {
		if ( isOsLinux() ) {
		//if ( false ) {
			startShowPlayingLinux( sImpress, sOptions, sShowPath );
		} else {
			startShowPlayingWindows( sImpress, sOptions, sShowPath );
		}
	}
	
	public void stopShow() {
		if ( !bShowRunning ) {
			printSysOut("stopShow show not running");
			return;
		}
		// we cannot use destroy() since that would leave the Impress show
		// in a bad state. So all we can do is wait on the user to stop the
		// show and then clean up.
		
		try {
			// stop the mouse clicks
			stopTimer();
			printSysOut("stopShow waiting for you to stop the show");
			pShowProcess.waitFor();
			printSysOut("stopShow show not running");
			bShowRunning = false;
		} catch (Exception ex ) {
			printSysOut("stopShow exception");
			bShowRunning = false; // try to clean up
		}
	}
	
	/*
	 * get a path to where we are. That is where is our JAR file?
	 * So we can call a script to launch our impress and hide the errors.
	 */
	
	public String pathToOurJarFile() {
		
		String jarDir = "";

		try {
		CodeSource codeSource = MyCalc.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());
		jarDir = jarFile.getParentFile().getPath();
		} catch (Exception ex) {
			// ignore this, just return the empty jarDir
			printSysOut( "pathToOurJarFile is unhappy "+ex.getMessage() );
			jarDir = "";
			return jarDir; // don't add separator on error
		}
		return jarDir+File.separator;
	}
	
	
	public void whereAreWe()
	{
		try {
			String pathToUs;
			
			pathToUs = pathToOurJarFile();
			
			setStatus("We are "+pathToUs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			printSysOut("whereAreWe - exception "+e.getMessage() );
			setStatus("whereAreWe exception");
			//e.printStackTrace();
		};
	}
	/**
	 * Create the frame.
	 */
	@SuppressWarnings("deprecation")
	public MyCalc() {
		setResizable(false);
		
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
		btnAdd.setFont(new Font("Dialog", Font.BOLD, 16));
		btnAdd.setBounds(41, 129, 107, 35);
		contentPane.add(btnAdd);
		
		JLabel lblTotal = new JLabel("Total");
		lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotal.setBounds(22, 176, 135, 37);
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
		btnStartTimer.setFont(new Font("Dialog", Font.BOLD, 16));
		btnStartTimer.setBounds(290, 85, 138, 29);
		contentPane.add(btnStartTimer);
		
		JButton btnStopTimer = new JButton("Stop Timer");
		btnStopTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTimer();
			}
		});
		btnStopTimer.setFont(new Font("Dialog", Font.BOLD, 16));
		btnStopTimer.setBounds(290, 126, 138, 29);
		contentPane.add(btnStopTimer);
		
		JButton btnClearTics = new JButton("Clear Tics");
		btnClearTics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nTimerTics = 0;
				tfTimerTics.setText(String.valueOf(nTimerTics));
			}
		});
		btnClearTics.setFont(new Font("Dialog", Font.BOLD, 16));
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
		tfImpressPath.setText("");
		tfImpressPath.setHorizontalAlignment(SwingConstants.LEFT);
		tfImpressPath.setFont(new Font("Dialog", Font.PLAIN, 16));
		tfImpressPath.setColumns(10);
		tfImpressPath.setBounds(149, 293, 338, 29);
		contentPane.add(tfImpressPath);
		
		txtOptions = new JTextField();
		txtOptions.setText("--impress --show");
		txtOptions.setHorizontalAlignment(SwingConstants.LEFT);
		txtOptions.setFont(new Font("Dialog", Font.PLAIN, 16));
		txtOptions.setColumns(10);
		txtOptions.setBounds(149, 337, 338, 29);
		contentPane.add(txtOptions);
		
		txtShowPath = new JTextField();
		txtShowPath.setText("");
		txtShowPath.setHorizontalAlignment(SwingConstants.LEFT);
		txtShowPath.setFont(new Font("Dialog", Font.PLAIN, 16));
		txtShowPath.setColumns(10);
		txtShowPath.setBounds(149, 378, 338, 29);
		contentPane.add(txtShowPath);
		
		if ( isOsWindows() ) {
			tfImpressPath.setText(sImpressOnWindows);
			txtOptions.setText(sOptionsOnWindows);
			txtShowPath.setText(sShowPathOnWindows);
		} else {
			tfImpressPath.setText(sImpressOnLinux);
			txtOptions.setText(sOptionsOnLinux);
			txtShowPath.setText(sShowPathOnLinux);
		}
		
		JButton btnStartShow = new JButton("Start Show");
		btnStartShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startShowPlaying( tfImpressPath.getText(), txtOptions.getText(), txtShowPath.getText() );
			}
		});
		btnStartShow.setFont(new Font("Dialog", Font.BOLD, 16));
		btnStartShow.setBounds(149, 421, 138, 29);
		contentPane.add(btnStartShow);
		
		JButton btnStopShow = new JButton("Stop Show");
		btnStopShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopShow();
			}
		});
		btnStopShow.setFont(new Font("Dialog", Font.BOLD, 16));
		btnStopShow.setBounds(306, 421, 138, 29);
		contentPane.add(btnStopShow);
		
		btnWhere = new JButton("Where");
		btnWhere.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whereAreWe();
			}
		});
		btnWhere.setFont(new Font("Dialog", Font.BOLD, 16));
		btnWhere.setBounds(41, 238, 138, 29);
		contentPane.add(btnWhere);
		
		lblStatus = new JLabel("Status");
		lblStatus.setFont(new Font("Dialog", Font.BOLD, 14));
		lblStatus.setBounds(22, 462, 509, 23);
		contentPane.add(lblStatus);
		
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

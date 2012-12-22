import graphs.*;
import dmc.*;
import rvg.VariateGenerator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.ArithmeticException;
import java.lang.NumberFormatException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Double;
import java.lang.Math;
import java.lang.System;
import java.util.Vector;
import java.util.Iterator;

/**
 * This class implements a graphical user interface to a Diffusion
 * Monte Carlo simulation, complete with parameter input and graphical
 * visualization of certain outputs.
 * <p>
 * Be warned:  I've tried to keep the code as clean as possible, but I
 * am still learning Swing, and this design is far from perfect.  I feel
 * that it has become a bit bloated, and could use some modularization.
 * In fact, with all the private variables, it's a bit like programming
 * in C again with globals. O:-)
 * <p>
 * Suggestions for future improvement:
 *    <li>Modularize all aspects of each type of graph into another class,
 *        and get the graph specific members out of this high up GUI.
 *    <li>Modularize all aspects of each potential into another class, etc.
 *    <li>Remove all unnecessary data members, perhaps through using action
 *        commands in the event handling instead of the event source.
 *    <li>Modularize the event handling into multiple classes that each handle
 *        events from a certain type of object.  As it is, the event handling
 *        is fairly bloated.
 *
 * @author Ian Terrell (itterr@wm.edu)
 * @version 0.1
 */
public class GUI extends JApplet implements ActionListener, 
					    ChangeListener, 
					    FocusListener
{
    /*************
     * CONSTANTS *
     *           **************************************
     * The following variables are used as constants. *
     **************************************************/

    /**
     * The default number of histogram bins to use in a simulation.
     */
    private final int DEFAULT_NUM_BINS = 200;

    /**
     * The default delay (in milliseconds) between simulation events.
     */
    private final int DEFAULT_DELAY = 50;

    /**
     * The default number of iterations (for graph updates, etc) to run
     * on each simulation event.
     */
    private final int DEFAULT_N_ITERATIONS = 1;

    /**
     * The default number of points to display on the number of walkers
     * and reference energy graphs.
     */
    private final int DEFAULT_MAX_POINTS = 100;

    /**
     * The default number of iterations to warm up with.
     */
    private final int DEFAULT_WARMUP = 0;

    /**
     * The default number of iterations to record E0.
     */
    private final int DEFAULT_E0_ITERATIONS = 500;

    /**
     * The string representation of the Histogram, for use in combo
     * boxes, etc.
     */
    private final String HISTOGRAM = "Histogram";

    /**
     * The string representation of the number of walkers graph, 
     * for use in combo boxes, etc.
     */
    private final String REFENERGY = "Ref. Energy";

    /**
     * The string representation of the reference energy graph, 
     * for use in combo boxes, etc.
     */
    private final String NUMWALKERS = "Num Walkers";

    /**
     * The string representation of the E0 estimate graph.
     */
    private final String E0ESTIMATE = "E0 Estimate";
    
    /**
     * The string representation of the Phi0 Estimate graph.
     */
    private final String PHI0ESTIMATE = "Phi0 Estimate";
    
    /**
     * The string array telling the choices for the graph selection
     * combo boxes.
     */
    private final String[] graphChoices = { HISTOGRAM, 
					    NUMWALKERS,
					    REFENERGY,
					    E0ESTIMATE, 
					    PHI0ESTIMATE };

    /**
     * The string representation of the SHO potential, for use in
     * combo boxes.
     */
    private final String SHO = "Simple Harmonic Oscillator";

    /**
     * The string representation of the delta function, for use in
     * the combo box for walker initialization.
     */
    private final String DELTAFNC = "Delta Function";

    /**
     * The string representation of the uniform distribution, for use in
     * the combo box for walker initialization.
     */
    private final String UNIFORM = "Uniform Distribution";

    /**
     * The string representation of the gaussian distribution, for use in
     * the combo box for walker initialization.
     */
    private final String GAUSSIAN = "Gaussian Distribution";

    /**************************
     * GUI EDITABLE VARIABLES *
     *                        ***********************************
     * The following variables are editable by the user through *
     * the GUI, to use as simulation parameters, etc.           *
     ************************************************************/

    /**
     * The millisecond delay between executing sets of
     * nIterations simulation iterations.
     */
    private int delay;

    /**
     * The number of simulation iterations to run at a time.
     */
    private int nIterations;

    /**
     * The number of simulation iterations to record e0 estimate.
     */
    private int e0Iterations;

    /**
     * The number of simulation iterations to warm up with.
     */
    private int warmup;

    /**
     * The reference energy used in the simulation.
     */ 
    private double refEnergy;

    /**
     * Whether or not the reference energy should be held
     * constant in the simulation.
     */
    private boolean refEnergyConstant;

    /**
     * The number of walkers to start the simulation with.
     */
    private int numWalkers;

    /**
     * The timestep used in the simulation.
     */
    private double dTau;

    /**
     * The feedback parameter alpha used in the simulation.
     */
    private double alpha;

    /**
     * The random variate generator seed used in the simulation.
     */
    private long seed;
    
    /**
     * The x_0 parameter of the Delta Function used for
     * walker initialization.
     */
    private double initDeltaFncX0;

    /**
     * The a parameter of the Uniform(a,b) distribution used
     * for walker initialization.
     */
    private double initUniformA;

    /**
     * The b parameter of the Uniform(a,b) distribution used
     * for walker initialization.
     */
    private double initUniformB;

    /**
     * The mu parameter of the Gaussian(mu,sigma) distribution used
     * for walker initialization.
     */
    private double initGaussianMu;

    /**
     * The sigma parameter of the Gaussian(mu,sigma) distribution used
     * for walker initialization.
     */
    private double initGaussianSigma;
    
    /**
     * Whether or not the ground state energy for the SHO
     * potential is graphed on the reference energy graph.
     */
    private boolean shoGraphEnergy;

    /**
     * The color of the SHO ground state energy on the
     * reference energy graph.
     */
    private Color shoEnergyColor;
    
    /**
     * Whether or not the ground state wavefunction for the SHO
     * potential is graphed on the histogram.
     */
    private boolean shoGraphPhi;
    
    /**
     * The color of the SHO ground state wavefunction on
     * the histogram.
     */
    private Color shoPhiColor;

    /**
     * The display mode of the E0 Estimate graph.
     */
    private int e0EstimateDisplayMode;

    /**
     * The color of the E0 Estimate graph.
     */
    private Color e0EstimateColor;

    /**
     * The color of the PHI0 Estimate graph.
     */
    private Color phi0EstimateColor;

    /**
     * The display mode of the histogram.
     */
    private int histogramDisplayMode;

    /**
     * The color of the histogram.
     */
    private Color histogramColor;

    /**
     * The number of bins per histogram.
     */
    private int numBins;

    /**
     * The xMin for the histograms.
     */
    private double histogramXMin;

    /**
     * The xMax for the histograms.
     */
    private double histogramXMax;

    /**
     * The display mode of the number of walkers graph.
     */
    private int numWalkersDisplayMode;

    /**
     * The maximum number of points to display on the number
     * of walkers graph.
     */
    private int numWalkersMaxPoints;    

    /**
     * The color of the number of walkers graph.
     */
    private Color numWalkersColor;

    /**
     * The display mode of the reference energy graph.
     */
    private int refEnergyDisplayMode;

    /**
     * The maximum number of points to display on the reference
     * energy graph.
     */
    private int refEnergyMaxPoints;    

    /**
     * The color of the reference energy graph.
     */
    private Color refEnergyColor;

    /**
     * Whether or not the histogram is normalized
     */
    private boolean normalizeHistogram;
    
    /******************************
     * SIMULATION RELATED MEMBERS *
     *                            *****************************************
     * These variables have to do with running the simulation in the gui. *
     **********************************************************************/

    /**
     * The DMC Simulation that we are currently running.  Is the type DMC so
     * that any class derived from DMC can be used.
     */
    private DMC simulation;

    /**
     * The timer that fires simulation events so that the output 
     * can be graphed.
     */
    private Timer simulationTimer;
    
    /**
     * Whether or not the current simulation is paused.
     */
    private boolean isPaused;

    /**
     * The current number of iterations run.
     */
    private int iterations;

    /**
     * The cumulative reference energy.
     */
    private double cumulativeEnergy;

    /*************************
     * GRAPH RELATED MEMBERS *
     *                       **********************************
     * These members have to do with the graphing in the GUI. *
     **********************************************************/

    /**
     * Contains the upper small graph's panel.
     */
    private GraphPanel topLittleGraph;

    /**
     * Contains the lower small graph's panel.
     */
    private GraphPanel bottomLittleGraph;

    /**
     * Contains the big graph's panel.
     */
    private GraphPanel bigGraph;

    /**
     * Vector containing all the visible histograms.
     */
    private Vector histogramGraphVector;

    /**
     * Vector containing all the visible numWalker graphs
     */
    private Vector numWalkersGraphVector;

    /**
     * Vector containing all the visible refEnergy graphs
     */
    private Vector refEnergyGraphVector;

    /**
     * Contains the data for the Phi_0 Estimate graphs.
     */
    private Phi0EstimateData phi0EstimateData;

    /**
     * Vector containing all of the visible Phi_0 Estimate graphs.
     */
    private Vector phi0EstimateGraphVector;
    
    /**
     * Vector containing all the visible E0 Estimate graphs
     */
    private Vector e0EstimateGraphVector;

    /**
     * Contains the data for the E0 Estimate graph.
     */
    private GraphData e0EstimateData;

    /**
     * Contains the data for the numWalkers graph.
     */
    private GraphData numWalkersData;

    /**
     * Contains the data for the refEnergy graph.
     */
    private GraphData refEnergyData;

    /***************
     * GUI MEMBERS * 
     *             *************************************
     * These members are all part of the glorious GUI. *
     * *************************************************/

    // BUTTONS /////

    /**
     * The button to start the simulation.
     */
    private JButton startButton;

    /**
     * The button to reset the simulation to its empty state.
     */
    private JButton resetButton;

    /**
     * The button to pause the simulation.
     */
    private JButton pauseButton;

    /**
     * The button to continue a paused simulation.
     */ 
    private JButton continueButton;

    /**
     * Sets all variables to defaults.
     */
    private JButton useDefaultsButton;

    /**
     * Radio button that sets histogram mode to boxes.
     */
    private JRadioButton histogramBoxes;

    /**
     * Radio button that sets histogram mode to points.
     */ 
    private JRadioButton histogramPoints;

    /**
     * Check box to set if you want the histogram normalized or not.
     */
    private JCheckBox normalizeHistogramCheckBox;

    /**
     * Radio button that sets numWalkers graph mode to lines.
     */
    private JRadioButton numWalkersLines;

    /**
     * Radio button that sets numWalkers mode to points.
     */ 
    private JRadioButton numWalkersPoints;

    /**
     * Radio button that sets e0Estimate graph mode to lines.
     */
    private JRadioButton e0EstimateLines;

    /**
     * Radio button that sets e0Estimate mode to points.
     */ 
    private JRadioButton e0EstimatePoints;

    /**
     * Radio button that sets refEnergy graph mode to lines.
     */
    private JRadioButton refEnergyLines;

    /**
     * Radio button that sets refEnergy mode to points.
     */ 
    private JRadioButton refEnergyPoints;

    /**
     * Button to select the color of the E_0 function
     * on the reference energy graph.
     */
    private JButton shoEnergyColorButton;

    /**
     * Button to select the color of the Phi_0 function
     * on the histogram.
     */
    private JButton shoPhiColorButton;
    
    /**
     * Button to select the color of the histogram.
     */
    private JButton histogramColorButton;
    
    /**
     * Button to select the color of the e0Estimate.
     */
    private JButton e0EstimateColorButton;
    
    /**
     * Button to select the color of the phi0Estimate.
     */
    private JButton phi0EstimateColorButton;

    /**
     * Button to select the color of the number of walkers graph.
     */
    private JButton numWalkersColorButton;

    /**
     * Button to select the color of the reference energy graph.
     */
    private JButton refEnergyColorButton;

    /**
     * Checkbox to select whether or not to graph SHO's Phi_0
     */
    private JCheckBox shoGraphPhiCheckBox;

    /**
     * Checkbox to select whether or not to graph SHO's E_0
     */
    private JCheckBox shoGraphEnergyCheckBox;

    /**
     * Checkbox to select whether or not the reference energy
     * should be held constant in the simulation.
     */
    private JCheckBox refEnergyConstantCheckBox;

    // PANELS /////    

    /**
     * This panel displays the color of the SHO's graphed E_0.
     */
    private JPanel shoEnergyColorPanel;

    /**
     * This panel displays the color of the E0 Estimate graph.
     */
    private JPanel e0EstimateColorPanel;

    /**
     * This panel displays the color of the Phi0 Estimate graph.
     */
    private JPanel phi0EstimateColorPanel;

    /**
     * This panel displays the color of the SHO's graphed Phi_0.
     */
    private JPanel shoPhiColorPanel;

    /**
     * This panel displays the color of the reference energy graph.
     */
    private JPanel refEnergyColorPanel;

    /**
     * This panel displays the color of the number of walkers graph.
     */
    private JPanel numWalkersColorPanel;

    /**
     * This panel displays the color of the histogram.
     */
    private JPanel histogramColorPanel;

    /**
     * Card Layout'd panel that contains panels to edit
     * potential (V) information.
     */
    private JPanel potentialChoiceOptionsPanel;

    /**
     * Card Layout'd panel that contains panels to edit
     * graph options.
     */
    private JPanel graphOptionsChoiceOptionsPanel;

    /**
     * Card Layout'd panel that contains panels to edit
     * walker initialization options.
     */
    private JPanel walkerInitChoiceOptionsPanel;

    // TEXT FIELDS /////

    /**
     * Text field to enter the desired number of walkers.
     */
    private JTextField numWalkersTextField;

    /**
     * Text field to enter the timestep dTau.
     */
    private JTextField dTauTextField;

    /**
     * Text field to enter the feedback parameter alpha.
     */ 
    private JTextField alphaTextField;

    /**
     * Text field to enter the number of warmup iterations.
     */ 
    private JTextField warmupTextField;

    /**
     * Text field to enter the simulation's random variate generator seed.
     */
    private JTextField seedTextField;

    /**
     * Text field to enter the starting reference energy.
     */
    private JTextField refEnergyTextField;

    /**
     * Text field to enter the delay between simulation events.
     */
    private JTextField delayTextField;

    /**
     * Text field to enter the number of iterations to be run each sim event.
     */
    private JTextField nIterationsTextField;

    /**
     * Text field to enter the x_0 parameter of the delta function
     * for walker initialization.
     */
    private JTextField initDeltaFncX0TextField;

    /**
     * Text field to enter the A parameter of the uniform(a,b) distribution
     * for walker initialization.
     */
    private JTextField initUniformATextField;

    /**
     * Text field to enter the B paramter of the uniform(a,b) distribution
     * for walker initialization.
     */
    private JTextField initUniformBTextField;

    /**
     * Text field to enter the mu paramter of the gaussian(mu,sigma)
     * distribution for walker initialization.
     */
    private JTextField initGaussianMuTextField;

    /**
     * Text field to enter the sigma paramter of the gaussian(mu,sigma)
     * distribution for walker initialization.
     */
    private JTextField initGaussianSigmaTextField;

    /**
     * Text field to enter the minimum x value to display on the histogram.
     */
    private JTextField histogramXMinTextField;

    /**
     * Text field to enter the maximum x value to display on the histogram.
     */
    private JTextField histogramXMaxTextField;

    /**
     * Text field to enter the maximum number of points to display on
     * the number of walkers graph.
     */ 
    private JTextField numWalkersMaxPointsTextField;

    /**
     * Text field to enter the maximum number of points to display on
     * the reference energy graph.
     */ 
    private JTextField refEnergyMaxPointsTextField;

    /**
     * Text field to enter the maximum number of iterations to display
     * on the E0 estimate graph.
     */ 
    private JTextField e0IterationsTextField;

    // COMBO BOXES /////

    /**
     * Contains graph choices for top little graph.
     */
    private JComboBox topLittleGraphComboBox;
    
    /**
     * Contains graph choices for bottom little graph.
     */
    private JComboBox bottomLittleGraphComboBox;
    
    /**
     * Contains graph choices for big graph.
     */
    private JComboBox bigGraphComboBox;
    
    /**
     * Combo Box to select panel with options for walker
     * initialization.
     */
    private JComboBox walkerInitComboBox;

    /**
     * Combo Box to select potential.
     */
    private JComboBox potentialComboBox;

    /**
     * Combo Box to select panel with options for 
     * different graphs.
     */
    private JComboBox graphOptionsComboBox;
    
    // OTHER /////

    /**
     * Slider to select number of bins in histograms.
     */
    private JSlider numBinsSlider;

    /***********
     * METHODS *
     *         **********************
     * Here's where the fun starts. *
     ********************************/

    /**
     * Specifies the preferred size of this instance.
     *
     * @return the preferred size as a
     *         <code>java.awt.Dimension</code> object.
     */
    public Dimension getPreferredSize()
    {
    	return new Dimension(600,485);
    }

    /**
     * Necessary to be an applet - does nothing.
     */
    public void init() { }

    /**
     * This method acts as the main function for the application.
     * The GUI is set up and everything is initialized.  When run as
     * an applet the JVM starts execution here, when run as an application
     * the main function will call it.
     */
    public void start() 
    { 
	isPaused = true;
	setupGUI();
	assignDefaultValues();	
	resetSimulation();
    }
    
    /**
     * This function resets the simulation to its empty state.
     * It sets the simulation to a blank simulation, empties the current
     * graphs and their data, resets the graphs to blank data, and disables
     * all the main buttons save "start."  Should be called at the beginning
     * of the GUI setup and when the "reset" button is pressed.
     */
    private void resetSimulation()
    {
	isPaused = true;
	setupBlankSimulation();

	iterations = 0;
	cumulativeEnergy = 0.0;

	histogramGraphVector = new Vector();
	numWalkersGraphVector = new Vector();
	refEnergyGraphVector = new Vector();    
	e0EstimateGraphVector = new Vector();
	phi0EstimateGraphVector = new Vector();

	numWalkersData = new GraphData(0,1,numWalkers-1,numWalkers+1,
				       numWalkersMaxPoints);
	refEnergyData = new GraphData(0,1,0,1,refEnergyMaxPoints);
	e0EstimateData = new GraphData(0,1,0,1,-1);
	phi0EstimateData = new Phi0EstimateData(histogramXMin,histogramXMax,200);
	
	resetTimer();

	// Setup default values, etc:
	topLittleGraphComboBox.setSelectedItem(NUMWALKERS);
	bottomLittleGraphComboBox.setSelectedItem(E0ESTIMATE);
	bigGraphComboBox.setSelectedItem(PHI0ESTIMATE);

	pauseButton.setEnabled(false);
	continueButton.setEnabled(false);
	resetButton.setEnabled(false);
    }

    /**
     * This function sets up a blank simulation of the type selected
     * by the potential combo box.
     */
    private void setupBlankSimulation()
    {
	if ((String) potentialComboBox.getSelectedItem() == SHO)
	    simulation = new DMC_SHO(0,0.0,0);
	else
	    simulation = new DMC(0,0.0,0);
    }
    
    /**
     * This function uses the event handling mechanisms, among others,
     * to assign the default values to the GUI's input boxes, etc.  Should
     * be called at the beginning of a simulation and when the "Use Defaults"
     * button is pressed.
     */
    private void assignDefaultValues()
    {
	// Some values/displays depend on others, 
	// so this is slightly order specific.
	
	shoEnergyColor = Color.blue;
	shoEnergyColorPanel.setBackground(shoEnergyColor);
	shoPhiColor = Color.blue;
	shoPhiColorPanel.setBackground(shoPhiColor);
	
	histogramColor = Color.red;
	histogramColorPanel.setBackground(histogramColor);
	numWalkersColor = Color.red;
	numWalkersColorPanel.setBackground(numWalkersColor);
	refEnergyColor = Color.red;
	refEnergyColorPanel.setBackground(refEnergyColor);
	e0EstimateColor = Color.red;
	e0EstimateColorPanel.setBackground(e0EstimateColor);
	phi0EstimateColor = Color.red;
	phi0EstimateColorPanel.setBackground(phi0EstimateColor);
	updatePhi0EstimateGraphs();
	
	stateChanged(new ChangeEvent(numBinsSlider)); 
	resetTextField(warmupTextField);
	resetTextField(numWalkersTextField);
	resetTextField(e0IterationsTextField);
	resetTextField(dTauTextField);
	resetTextField(alphaTextField);
	resetTextField(seedTextField);
	resetTextField(refEnergyTextField);
	resetTextField(delayTextField);
	resetTextField(nIterationsTextField);
	resetTextField(initDeltaFncX0TextField);
	resetTextField(initUniformATextField);
	resetTextField(initUniformBTextField);
	resetTextField(initGaussianMuTextField);
	resetTextField(initGaussianSigmaTextField);
	resetTextField(histogramXMinTextField);
	resetTextField(histogramXMaxTextField);
	resetTextField(numWalkersMaxPointsTextField);
	resetTextField(refEnergyMaxPointsTextField);
	histogramBoxes.doClick();
	numWalkersLines.doClick();
	refEnergyLines.doClick();
	e0EstimateLines.doClick();
	if (!normalizeHistogram)
	    normalizeHistogramCheckBox.doClick();
	if (!shoGraphEnergy)
	    shoGraphEnergyCheckBox.doClick();
	if (!shoGraphPhi)
	    shoGraphPhiCheckBox.doClick(); 
	refEnergyConstantCheckBox.setSelected(false);
	actionPerformed(new ActionEvent(refEnergyConstantCheckBox,0,""));
    }
    
    /**
     * Resets a text field to its default value.
     */
    private void resetTextField(JTextField tf)
    {
	tf.setText("");
	focusLost(new FocusEvent(tf,0));
    }

    /**
     * This function resets the timer with the delay given by the
     * GUI editable variable.  Should be called at the beginning of a
     * simulation, and whenever the value of delay changes.
     */
    private void resetTimer() 
    {
	simulationTimer = new Timer(delay,this);
	simulationTimer.setInitialDelay(0);
    }

    /**
     * This method sets certain components to be editable or enabled,
     * depending on the parameter.  This should be called by the start
     * button to disable the components at the beginning of a simulation,
     * and by the reset button to enable them after the end of a simulation.
     *
     * @param b The boolean flag to set the components' states to.
     */
    private void componentsSetEditable(boolean b)
    {
	numWalkersTextField.setEditable(b);
	dTauTextField.setEditable(b);
	alphaTextField.setEditable(b);
	seedTextField.setEditable(b);
	initDeltaFncX0TextField.setEditable(b);
	initUniformATextField.setEditable(b);
	initUniformBTextField.setEditable(b);
	initGaussianMuTextField.setEditable(b);
	initGaussianSigmaTextField.setEditable(b);
	refEnergyTextField.setEditable(b);
	warmupTextField.setEditable(b);
	walkerInitComboBox.setEnabled(b);
	useDefaultsButton.setEnabled(b);
	refEnergyConstantCheckBox.setEnabled(b);
    }

    /**
     * Necessary to be an applet - does nothing.
     */
    public void stop() 
    { 
	// Nothing to clean up.
    }

    /**
     * Necessary to be an applet - does nothing.
     */
    public void destroy() 
    { 
	// Nothing to clean up.
    }
    
    /**
     * Creates the <code>JFrame</code> needed to contain the
     * instance of this class for running as an application.
     */
    public static void main(String [] args)
    {
	/**
	 * GUI extends JApplet, which is really just 
	 * a Panel, so it can be added to a JFrame.  Here, in 
	 * case the program is run as an application instead
	 * of directly as an applet, we create the JFrame to
	 * act as the heavyweight panel for the program.
	 */

	// Set up the GUI:
	GUI gui = new GUI();
	gui.start();

	// Set up the JFrame and go:
	JFrame f = new JFrame("DMC Visualization");
	WindowMonitor w = new WindowMonitor();
	f.addWindowListener(w);
	f.setResizable(false);
	f.getContentPane().add(gui);
	f.pack();
	f.setVisible(true);
    }

    /*********************
     * GUI SETUP METHODS *
     *                   **************************************************
     * The following methods set up the GUI visually, add listeners, etc. *
     **********************************************************************/

    /**
     * This method is the main setup method, which sets up the tabbed pane
     * seen at the top level, and then calls functions to initialize each
     * of the tabs.
     */
    private void setupGUI()
    {
	getContentPane().setBackground(Color.white);
	
	JTabbedPane tp = new JTabbedPane();
	tp.add("Graphs",setupGraphsPanel());
	tp.add("Settings",setupSettingsPanel());
	getContentPane().add(tp);
    }
    
    /**
     * This method sets up the panel seen under the "Graphs" tab.
     */
    private JPanel setupGraphsPanel()
    {    
	// Setup graphs panel:
	JPanel graphsPanel = new JPanel(new BorderLayout());

	// Add little graphs:
	JPanel littleGraphsPanel = new JPanel(new BorderLayout());
	topLittleGraph = new GraphPanel("Top Graph");
	topLittleGraph.setToolTipText("Top Graph");
	topLittleGraph.setPreferredSize(new Dimension(200,169));
	littleGraphsPanel.add(topLittleGraph, BorderLayout.NORTH);
	bottomLittleGraph = new GraphPanel("Bottom Graph");
	bottomLittleGraph.setToolTipText("Bottom Graph");
	bottomLittleGraph.setPreferredSize(new Dimension(200,168));
	littleGraphsPanel.add(bottomLittleGraph, BorderLayout.SOUTH);
	graphsPanel.add(littleGraphsPanel, BorderLayout.WEST);

	// Add big graph:
	bigGraph = new GraphPanel("Big Graph");
	bigGraph.setToolTipText("Big Graph");
	bigGraph.setPreferredSize(new Dimension(395,350));
	graphsPanel.add(bigGraph, BorderLayout.EAST);

	// Add bottomPanel:
	JPanel bottomPanel = new JPanel(new BorderLayout());
	bottomPanel.setPreferredSize(new Dimension(600,115));
	// Setup and add a graph selection panel:
	bottomPanel.add(setupOptionsPanel(), BorderLayout.WEST);
	// Setup and add control panel:
	JPanel controlPanel = new JPanel(new BorderLayout());
	controlPanel.setBorder(BorderFactory.createTitledBorder("Control"));
	startButton = new JButton("Start Simulation"); 
	startButton.setToolTipText("Starts a simulation.");
	startButton.addActionListener(this);
	controlPanel.add(startButton, BorderLayout.NORTH);
	pauseButton = new JButton("Pause");
	pauseButton.setToolTipText("Pauses a running simulation.");
	pauseButton.addActionListener(this);
	controlPanel.add(pauseButton, BorderLayout.WEST);
	continueButton = new JButton("Continue");
	continueButton.setToolTipText("Continues a paused simulation.");
	continueButton.addActionListener(this);
	controlPanel.add(continueButton, BorderLayout.CENTER);
	resetButton = new JButton("Reset Simulation"); 
	resetButton.setToolTipText("Resets the running simulation.");
	resetButton.addActionListener(this);
	controlPanel.add(resetButton, BorderLayout.SOUTH);
	bottomPanel.add(controlPanel, BorderLayout.CENTER);
	
	graphsPanel.add(bottomPanel, BorderLayout.SOUTH);
	return graphsPanel;
    }

    /**
     * This method sets up the panel seen as "Options" in the "Graphs" tab.
     */
    private JPanel setupOptionsPanel()
    {
	// Set up a graph selection panel:
	JPanel optionsPanel = new JPanel(new BorderLayout());
	optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
	optionsPanel.setPreferredSize(new Dimension(420,115));

	JPanel optionsLeftPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	optionsLeftPanel.setPreferredSize(new Dimension(195,110));
	optionsLeftPanel.add(new JLabel("Top Graph:"));
	topLittleGraphComboBox = new JComboBox(graphChoices);
	topLittleGraphComboBox.setToolTipText("Selects the graph type to be displayed on the top little graph.");
	topLittleGraphComboBox.addActionListener(this);
	optionsLeftPanel.add(topLittleGraphComboBox);
	optionsLeftPanel.add(new JLabel("Bot. Graph:"));
	bottomLittleGraphComboBox = new JComboBox(graphChoices);
	bottomLittleGraphComboBox.setToolTipText("Selects the graph type to be displayed on the bottom little graph.");
	bottomLittleGraphComboBox.addActionListener(this);
	optionsLeftPanel.add(bottomLittleGraphComboBox);
	optionsLeftPanel.add(new JLabel("Big Graph:"));
	bigGraphComboBox = new JComboBox(graphChoices);
	bigGraphComboBox.setToolTipText("Selects the graph type to be displayed on the big graph.");
	bigGraphComboBox.addActionListener(this);
	optionsLeftPanel.add(bigGraphComboBox);
	optionsPanel.add(optionsLeftPanel, BorderLayout.WEST);
	
	JPanel optionsRightPanel = new JPanel(new BorderLayout());
	optionsRightPanel.setPreferredSize(new Dimension(210,100));
	JPanel numBinsPanel = new JPanel();
	numBinsPanel.setPreferredSize(new Dimension(207,50));
	numBinsPanel.setBorder(BorderFactory.createTitledBorder("Number of Histogram Bins"));
	numBinsSlider = new JSlider(0,300,150);
	numBinsSlider.setToolTipText("Selects the number of bins used in the histogram.");
	numBinsSlider.addChangeListener(this);
	numBinsSlider.setMajorTickSpacing(100);
	numBinsSlider.setMinorTickSpacing(50);
	numBinsSlider.setPaintTicks(true);
	numBinsSlider.setPaintLabels(true);
	numBinsPanel.add(numBinsSlider);
	optionsRightPanel.add(numBinsPanel, BorderLayout.CENTER);
	optionsPanel.add(optionsRightPanel, BorderLayout.EAST);		
	return optionsPanel;
    }

    /**
     * This method sets up the panel seen under the "Settings" tab.
     */
    private JPanel setupSettingsPanel()
    {
	// Setup settings panel:
	JPanel settingsPanel = new JPanel(new BorderLayout());

	int leftPanelWidth = 230;
	JPanel leftPanel = new JPanel();
	leftPanel.setPreferredSize(new Dimension(leftPanelWidth,480));
	leftPanel.add(setupInputParametersPanel(leftPanelWidth));
	leftPanel.add(setupWalkerInitPanel(leftPanelWidth));
	leftPanel.add(setupRefEnergyPanel(leftPanelWidth));
	settingsPanel.add(leftPanel, BorderLayout.WEST);

	int rightPanelWidth = 360;
	JPanel rightPanel = new JPanel();
	rightPanel.setPreferredSize(new Dimension(rightPanelWidth,480));
	rightPanel.add(setupPotentialPanel(rightPanelWidth));
	rightPanel.add(setupGraphOptionsPanel(rightPanelWidth));
	settingsPanel.add(rightPanel, BorderLayout.EAST);

	JPanel bottomPanel = new JPanel(new BorderLayout());
	bottomPanel.setPreferredSize(new Dimension(590,100));
	bottomPanel.add(setupDisplayOptionsPanel(420), BorderLayout.WEST);
	JPanel bottomBottomPanel = new JPanel();
	bottomBottomPanel.setPreferredSize(new Dimension(590,35));
	useDefaultsButton = new JButton("Use Defaults");
	useDefaultsButton.setToolTipText("Resets all modifiable values to their defaults.");
	useDefaultsButton.addActionListener(this);
	bottomBottomPanel.add(useDefaultsButton);
	bottomPanel.add(bottomBottomPanel, BorderLayout.SOUTH);
	settingsPanel.add(bottomPanel, BorderLayout.SOUTH);
	
	return settingsPanel;
    }

    /**
     * This method sets up the panel seen as "Display Options" under
     * the "Settings" tab.
     *
     * @param width The preferred integer width of the panel.
     */
    private JPanel setupDisplayOptionsPanel(int width)
    {
	JPanel displayOptionsPanel = new JPanel();
	displayOptionsPanel.setPreferredSize(new Dimension(width,55));
	displayOptionsPanel.setBorder 
	    (BorderFactory.createTitledBorder("Display Options"));
	displayOptionsPanel.add(new JLabel("Update display every"));
	nIterationsTextField = new JTextField("",3);
	nIterationsTextField.setToolTipText("Edits the number of iterations to be run at a time.");
	nIterationsTextField.addFocusListener(this);
	displayOptionsPanel.add(nIterationsTextField);
	displayOptionsPanel.add(new JLabel("iterations every"));
	delayTextField = new JTextField("",3);
	delayTextField.setToolTipText("Edits the millisecond delay between running simulation events.");
	delayTextField.addFocusListener(this);
	displayOptionsPanel.add(delayTextField);
	displayOptionsPanel.add(new JLabel("milliseconds."));
	
	return displayOptionsPanel;
    }

    /**
     * This method sets up the panel seen as "Graph Options" under
     * the "Settings" tab.
     *
     * @param rightPanelWidth The preferred width of the panel.
     */
    private JPanel setupGraphOptionsPanel(int rightPanelWidth)
    {	
	int subPanelHeight = 70;

	JPanel graphOptionsPanel = new JPanel();
	graphOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth,130));
	graphOptionsPanel.setBorder 
	    (BorderFactory.createTitledBorder("Graph Options"));

	graphOptionsComboBox = new JComboBox(graphChoices);
	graphOptionsComboBox.setToolTipText("Selects the type of graph to edit the options for.");
	graphOptionsComboBox.addActionListener(this);
	graphOptionsPanel.add(new JLabel("Graph: "));
	graphOptionsPanel.add(graphOptionsComboBox);
	
	// Phi0Estimate options:
	JPanel phi0EstimateOptionsPanel = new JPanel();
	phi0EstimateOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								 subPanelHeight));
	JPanel phi0EstimateOptionsTopPanel = new JPanel();
	phi0EstimateOptionsTopPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,30));
	phi0EstimateColorPanel = new JPanel();
	phi0EstimateColorPanel.setPreferredSize(new Dimension(10,10));
	phi0EstimateColorPanel.setBackground(Color.red);
	phi0EstimateOptionsTopPanel.add(phi0EstimateColorPanel);
	phi0EstimateColorButton = new JButton("Color");
	phi0EstimateColorButton.setToolTipText("Pops up a window to select the color of the data.");
	phi0EstimateColorButton.addActionListener(this);
	phi0EstimateOptionsTopPanel.add(phi0EstimateColorButton);
	phi0EstimateOptionsPanel.add(phi0EstimateOptionsTopPanel);

	JPanel phi0EstimateOptionsBottomPanel = new JPanel();
	phi0EstimateOptionsBottomPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								   30));
	phi0EstimateOptionsPanel.add(phi0EstimateOptionsBottomPanel);
	
	// E0 estimate options:
	JPanel e0EstimateOptionsPanel = new JPanel();
	e0EstimateOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								 subPanelHeight));
	JPanel e0EstimateOptionsTopPanel = new JPanel();
	e0EstimateOptionsTopPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,30));
	e0EstimateLines = new JRadioButton("Lines");
	e0EstimateLines.setToolTipText("Sets the graph to display the data as lines.");
	e0EstimateLines.addActionListener(this);
	e0EstimatePoints = new JRadioButton("Points");
	e0EstimatePoints.setToolTipText("Sets the graph to display the data as points.");
	e0EstimatePoints.addActionListener(this);
	ButtonGroup e0EstimateTypeGroup = new ButtonGroup();
	e0EstimateTypeGroup.add(e0EstimateLines);
	e0EstimateTypeGroup.add(e0EstimatePoints);
	e0EstimateOptionsTopPanel.add(new JLabel("Display:"));
	e0EstimateOptionsTopPanel.add(e0EstimateLines);
	e0EstimateOptionsTopPanel.add(e0EstimatePoints);
	e0EstimateOptionsTopPanel.add(new JLabel("     "));
	e0EstimateColorPanel = new JPanel();
	e0EstimateColorPanel.setPreferredSize(new Dimension(10,10));
	e0EstimateColorPanel.setBackground(Color.red);
	e0EstimateOptionsTopPanel.add(e0EstimateColorPanel);
	e0EstimateColorButton = new JButton("Color");
	e0EstimateColorButton.setToolTipText("Pops up a window to select the color of the data.");
	e0EstimateColorButton.addActionListener(this);
	e0EstimateOptionsTopPanel.add(e0EstimateColorButton);
	e0EstimateOptionsPanel.add(e0EstimateOptionsTopPanel);

	JPanel e0EstimateOptionsBottomPanel = new JPanel();
	e0EstimateOptionsBottomPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								   30));
	e0EstimateOptionsBottomPanel.add(new JLabel("Stop recording after"));
	e0IterationsTextField = new JTextField("",4);
	e0IterationsTextField.addFocusListener(this);
	e0IterationsTextField.setToolTipText("Edits the number of iterations to record the E0 Estimate.");
	e0EstimateOptionsBottomPanel.add(e0IterationsTextField);
	e0EstimateOptionsBottomPanel.add(new JLabel("iterations."));
	e0EstimateOptionsPanel.add(e0EstimateOptionsBottomPanel);

	// Histogram options:
	JPanel histogramOptionsPanel = new JPanel();
	histogramOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								 subPanelHeight));
	JPanel histogramOptionsTopPanel = new JPanel();
	histogramOptionsTopPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,30));
	histogramBoxes = new JRadioButton("Boxes");
	histogramBoxes.setToolTipText("Sets the histogram to display bin data as boxes.");
	histogramBoxes.addActionListener(this);
	histogramPoints = new JRadioButton("Points");
	histogramPoints.setToolTipText("Sets the histogram to display bin data as centered points.");
	histogramPoints.addActionListener(this);
	ButtonGroup histogramTypeGroup = new ButtonGroup();
	histogramTypeGroup.add(histogramBoxes);
	histogramTypeGroup.add(histogramPoints);
	histogramOptionsTopPanel.add(new JLabel("Display:"));
	histogramOptionsTopPanel.add(histogramBoxes);
	histogramOptionsTopPanel.add(histogramPoints);
	histogramOptionsTopPanel.add(new JLabel("     "));
	histogramColorPanel = new JPanel();
	histogramColorPanel.setPreferredSize(new Dimension(10,10));
	histogramColorPanel.setBackground(Color.red);
	histogramOptionsTopPanel.add(histogramColorPanel);
	histogramColorButton = new JButton("Color");
	histogramColorButton.setToolTipText("Pops up a window to select the color of the data.");
	histogramColorButton.addActionListener(this);
	histogramOptionsTopPanel.add(histogramColorButton);
	histogramOptionsPanel.add(histogramOptionsTopPanel);

	JPanel histogramOptionsBottomPanel = new JPanel();
	histogramOptionsBottomPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								   30));
	histogramOptionsBottomPanel.add(new JLabel("xMin ="));
	histogramXMinTextField = new JTextField("",4);
	histogramXMinTextField.setToolTipText("Edits the minimum x value displayed.");
	histogramXMinTextField.addFocusListener(this);    
	histogramOptionsBottomPanel.add(histogramXMinTextField);
	histogramOptionsBottomPanel.add(new JLabel("xMax ="));
	histogramXMaxTextField = new JTextField("",4);
	histogramXMaxTextField.setToolTipText("Edits the maximum x value displayed.");
	histogramXMaxTextField.addFocusListener(this);   
	histogramOptionsBottomPanel.add(histogramXMaxTextField);
	normalizeHistogramCheckBox = new JCheckBox("Normalize?");
	normalizeHistogramCheckBox.setToolTipText("Sets whether or not the histogram should be normalized.");
	normalizeHistogramCheckBox.addActionListener(this);
	histogramOptionsBottomPanel.add(normalizeHistogramCheckBox);
	histogramOptionsPanel.add(histogramOptionsBottomPanel);

	// NumWalkers options:
	JPanel numWalkersOptionsPanel = new JPanel();
	numWalkersOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								 subPanelHeight));
	JPanel numWalkersOptionsTopPanel = new JPanel();
	numWalkersOptionsTopPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,30));
	numWalkersLines = new JRadioButton("Lines");
	numWalkersLines.setToolTipText("Sets the graph to display the data as lines.");
	numWalkersLines.addActionListener(this);
	numWalkersPoints = new JRadioButton("Points");
	numWalkersPoints.setToolTipText("Sets the graph to display the data as points.");
	numWalkersPoints.addActionListener(this);
	ButtonGroup numWalkersTypeGroup = new ButtonGroup();
	numWalkersTypeGroup.add(numWalkersLines);
	numWalkersTypeGroup.add(numWalkersPoints);
	numWalkersOptionsTopPanel.add(new JLabel("Display:"));
	numWalkersOptionsTopPanel.add(numWalkersLines);
	numWalkersOptionsTopPanel.add(numWalkersPoints);
	numWalkersOptionsTopPanel.add(new JLabel("     "));
	numWalkersColorPanel = new JPanel();
	numWalkersColorPanel.setPreferredSize(new Dimension(10,10));
	numWalkersColorPanel.setBackground(Color.red);
	numWalkersOptionsTopPanel.add(numWalkersColorPanel);
	numWalkersColorButton = new JButton("Color");
	numWalkersColorButton.setToolTipText("Pops up a window to select the color of the data.");
	numWalkersColorButton.addActionListener(this);
	numWalkersOptionsTopPanel.add(numWalkersColorButton);
	numWalkersOptionsPanel.add(numWalkersOptionsTopPanel);

	JPanel numWalkersOptionsBottomPanel = new JPanel();
	numWalkersOptionsBottomPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								   30));
	numWalkersOptionsBottomPanel.add(new JLabel("Max Points = "));
	numWalkersMaxPointsTextField = new JTextField("",4);
	numWalkersMaxPointsTextField.setToolTipText("Edits the maximum number of points displayed.  Use -1 for no maximum.");
	numWalkersMaxPointsTextField.addFocusListener(this);    
	numWalkersOptionsBottomPanel.add(numWalkersMaxPointsTextField);
	numWalkersOptionsPanel.add(numWalkersOptionsBottomPanel);

	// RefEnergy options:
	JPanel refEnergyOptionsPanel = new JPanel();
	refEnergyOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								 subPanelHeight));
	JPanel refEnergyOptionsTopPanel = new JPanel();
	refEnergyOptionsTopPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,30));
	refEnergyLines = new JRadioButton("Lines");
	refEnergyLines.setToolTipText("Sets the graph to display the data as lines.");
	refEnergyLines.addActionListener(this);
	refEnergyPoints = new JRadioButton("Points");
	refEnergyPoints.setToolTipText("Sets the graph to display the data as points.");
	refEnergyPoints.addActionListener(this);
	ButtonGroup refEnergyTypeGroup = new ButtonGroup();
	refEnergyTypeGroup.add(refEnergyLines);
	refEnergyTypeGroup.add(refEnergyPoints);
	refEnergyOptionsTopPanel.add(new JLabel("Display:"));
	refEnergyOptionsTopPanel.add(refEnergyLines);
	refEnergyOptionsTopPanel.add(refEnergyPoints);
	refEnergyOptionsTopPanel.add(new JLabel("     "));
	refEnergyColorPanel = new JPanel();
	refEnergyColorPanel.setPreferredSize(new Dimension(10,10));
	refEnergyColorPanel.setBackground(Color.red);
	refEnergyOptionsTopPanel.add(refEnergyColorPanel);
	refEnergyColorButton = new JButton("Color");
	refEnergyColorButton.setToolTipText("Pops up a window to select the color of the data.");
	refEnergyColorButton.addActionListener(this);
	refEnergyOptionsTopPanel.add(refEnergyColorButton);
	refEnergyOptionsPanel.add(refEnergyOptionsTopPanel);

	JPanel refEnergyOptionsBottomPanel = new JPanel();
	refEnergyOptionsBottomPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								   30));
	refEnergyOptionsBottomPanel.add(new JLabel("Max Points = "));
	refEnergyMaxPointsTextField = new JTextField("",4);
	refEnergyMaxPointsTextField.setToolTipText("Edits the maximum number of points displayed.  Use -1 for no maximum.");
	refEnergyMaxPointsTextField.addFocusListener(this);    
	refEnergyOptionsBottomPanel.add(refEnergyMaxPointsTextField);
	refEnergyOptionsPanel.add(refEnergyOptionsBottomPanel);

	// Card Layout'd Options Panel:
	graphOptionsChoiceOptionsPanel = new JPanel(new CardLayout());
	graphOptionsChoiceOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								      subPanelHeight));
	graphOptionsChoiceOptionsPanel.add(histogramOptionsPanel, 
					   HISTOGRAM, 0);
	graphOptionsChoiceOptionsPanel.add(numWalkersOptionsPanel, 
					   NUMWALKERS, 1);
	graphOptionsChoiceOptionsPanel.add(refEnergyOptionsPanel, 
					   REFENERGY, 2);
	graphOptionsChoiceOptionsPanel.add(e0EstimateOptionsPanel,
					   E0ESTIMATE, 3);
	graphOptionsChoiceOptionsPanel.add(phi0EstimateOptionsPanel,
					   PHI0ESTIMATE, 4);
	graphOptionsPanel.add(graphOptionsChoiceOptionsPanel);
	
	return graphOptionsPanel;
    }

    /**
     * This method sets up the panel seen as "Potential" under
     * the "Settings" tab.
     *
     * @param rightPanelWidth The preferred width of the panel.
     */
    private JPanel setupPotentialPanel(int rightPanelWidth)
    {	
	int subPanelHeight = 100;

	JPanel potentialPanel = new JPanel();
	potentialPanel.setPreferredSize(new Dimension(rightPanelWidth,165));
	potentialPanel.setBorder 
	    (BorderFactory.createTitledBorder("Potential"));

	String[] potentialChoices = { SHO };
	potentialComboBox = new JComboBox(potentialChoices);
	potentialComboBox.setToolTipText("Selects the potential to use.");
	potentialComboBox.addActionListener(this);
	potentialPanel.add(new JLabel("Potential: "));
	potentialPanel.add(potentialComboBox);
	
	// Panel that contains all the info for SHO:
	JPanel shoOptionsPanel = new JPanel();
	shoOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								 subPanelHeight));

	JPanel shoOptionsEquationPanel = new JPanel();
	shoOptionsEquationPanel.setPreferredSize(new Dimension(rightPanelWidth - 20, 25));
	shoOptionsEquationPanel.add(new JLabel("<html>V(x) = 1/2 * omega<sup>2</sup> * x<sup>2</sup></html>"));
	shoOptionsPanel.add(shoOptionsEquationPanel);

	JPanel shoOptionsEnergyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	shoOptionsEnergyPanel.setPreferredSize(new Dimension(rightPanelWidth - 20, 30));
	shoOptionsEnergyPanel.add(new JLabel("<html>E<sub>0</sub> = 0.5</html>"));
	shoOptionsEnergyPanel.add(new JLabel("     "));
	shoGraphEnergyCheckBox = new JCheckBox("Graph?");
	shoGraphEnergyCheckBox.setToolTipText("Selects whether or not to graph E_0.");
	shoGraphEnergyCheckBox.addActionListener(this);
	shoOptionsEnergyPanel.add(shoGraphEnergyCheckBox);
	shoEnergyColorPanel = new JPanel();
	shoEnergyColorPanel.setPreferredSize(new Dimension(10,10));
	shoEnergyColorPanel.setBackground(Color.blue);
	shoOptionsEnergyPanel.add(shoEnergyColorPanel);
	shoEnergyColorButton = new JButton("Color");
	shoEnergyColorButton.setToolTipText("Pops up a window to choose the color of the E_0 graphed.");
	shoEnergyColorButton.addActionListener(this);
	shoOptionsEnergyPanel.add(shoEnergyColorButton);
	shoOptionsPanel.add(shoOptionsEnergyPanel);

	JPanel shoOptionsPhiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	shoOptionsPhiPanel.setPreferredSize(new Dimension(rightPanelWidth - 20, 30));
	shoOptionsPhiPanel.add(new JLabel("<html>Phi<sub>0</sub>(x) = Pi<sup>-1/4</sup> e<sup>-x^2/2</sup></html>"));
	shoOptionsPhiPanel.add(new JLabel("     "));
	shoGraphPhiCheckBox = new JCheckBox("Graph?");
	shoGraphPhiCheckBox.setToolTipText("Selects whether or not to graph Phi_0.");
	shoGraphPhiCheckBox.addActionListener(this);
	shoOptionsPhiPanel.add(shoGraphPhiCheckBox);
	shoPhiColorPanel = new JPanel();
	shoPhiColorPanel.setPreferredSize(new Dimension(10,10));
	shoPhiColorPanel.setBackground(Color.green);
	shoOptionsPhiPanel.add(shoPhiColorPanel);
	shoPhiColorButton = new JButton("Color");
	shoPhiColorButton.setToolTipText("Pops up a window to choose the color of the graphed Phi_0.");
	shoPhiColorButton.addActionListener(this);
	shoOptionsPhiPanel.add(shoPhiColorButton);
	shoOptionsPanel.add(shoOptionsPhiPanel);

	// Card Layout'd Options Panel:
	potentialChoiceOptionsPanel = new JPanel(new CardLayout());
	potentialChoiceOptionsPanel.setPreferredSize(new Dimension(rightPanelWidth - 20,
								    subPanelHeight));
	potentialChoiceOptionsPanel.add(shoOptionsPanel, SHO, 0);
	
	potentialPanel.add(potentialChoiceOptionsPanel);
	return potentialPanel;
    }

    /**
     * This method sets up the panel seen as "Walker Initialization"
     * under the "Settings" tab.
     *
     * @param leftPanelWidth The preferred integer width of the panel.
     */
    private JPanel setupWalkerInitPanel(int leftPanelWidth)
    {	
	int subPanelHeight = 25;

	JPanel walkerInitPanel = new JPanel();
	walkerInitPanel.setPreferredSize(new Dimension(leftPanelWidth,90));
	walkerInitPanel.setBorder 
	    (BorderFactory.createTitledBorder("Walker Initialization"));

	String[] walkerInitChoices = {DELTAFNC,UNIFORM,GAUSSIAN};
	walkerInitComboBox = new JComboBox(walkerInitChoices);
	walkerInitComboBox.setToolTipText("Selects how the walkers should be initialized.");
	walkerInitComboBox.addActionListener(this);
	walkerInitPanel.add(new JLabel("Use a"));
	walkerInitPanel.add(walkerInitComboBox);
	
	JPanel deltaFunctionOptionsPanel = new JPanel();
	deltaFunctionOptionsPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
								 subPanelHeight));
	deltaFunctionOptionsPanel.add(new JLabel("x_0 ="));
	initDeltaFncX0TextField = new JTextField("",4);
	initDeltaFncX0TextField.setToolTipText("Edits the x_0 parameter of the delta function delta(x-x_0).");
	initDeltaFncX0TextField.addFocusListener(this);
	deltaFunctionOptionsPanel.add(initDeltaFncX0TextField);
		
	JPanel uniformDistOptionsPanel = new JPanel();
	uniformDistOptionsPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
							       subPanelHeight));
	uniformDistOptionsPanel.add(new JLabel("a ="));
	initUniformATextField = new JTextField("",4);
	initUniformATextField.setToolTipText("Edits the a parameter of the Uniform(a,b) distribution.");
	initUniformATextField.addFocusListener(this);
	uniformDistOptionsPanel.add(initUniformATextField);
	uniformDistOptionsPanel.add(new JLabel("b ="));
	initUniformBTextField = new JTextField("",4);
	initUniformBTextField.setToolTipText("Edits the b parameter of the Uniform(a,b) distribution.");
	initUniformBTextField.addFocusListener(this);
	uniformDistOptionsPanel.add(initUniformBTextField);
			
	JPanel gaussianDistOptionsPanel = new JPanel();
	gaussianDistOptionsPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
								subPanelHeight));
	gaussianDistOptionsPanel.add(new JLabel("mu ="));
	initGaussianMuTextField = new JTextField("",4);
	initGaussianMuTextField.setToolTipText("Edits the mu parameter of the Gaussian(mu,sigma) distribution.");
	initGaussianMuTextField.addFocusListener(this);
	gaussianDistOptionsPanel.add(initGaussianMuTextField);
	gaussianDistOptionsPanel.add(new JLabel("sigma ="));
	initGaussianSigmaTextField = new JTextField("",4);
	initGaussianSigmaTextField.setToolTipText("Edits the sigma parameter of the Gaussian(mu,sigma) distribution.");
	initGaussianSigmaTextField.addFocusListener(this);
	gaussianDistOptionsPanel.add(initGaussianSigmaTextField);

	walkerInitChoiceOptionsPanel = new JPanel(new CardLayout());
	walkerInitChoiceOptionsPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
								    subPanelHeight));
	walkerInitChoiceOptionsPanel.add(deltaFunctionOptionsPanel, DELTAFNC, 0);
	walkerInitChoiceOptionsPanel.add(uniformDistOptionsPanel, UNIFORM, 1);
	walkerInitChoiceOptionsPanel.add(gaussianDistOptionsPanel, GAUSSIAN, 2);
	
	walkerInitPanel.add(walkerInitChoiceOptionsPanel);
	return walkerInitPanel;
    }

    /**
     * This method sets up the panel seen as "Reference Energy"
     * under the "Settings" tab.
     *
     * @param leftPanelWidth The preferred integer width of the panel.
     */
    private JPanel setupRefEnergyPanel(int leftPanelWidth)
    {	
	JPanel refEnergyPanel = new JPanel();
	refEnergyPanel.setPreferredSize(new 
					Dimension(leftPanelWidth,60));
	refEnergyPanel.setBorder 
	    (BorderFactory.createTitledBorder("Reference Energy"));
	refEnergyPanel.add(new JLabel("E_R ="));
	refEnergyTextField = new JTextField("",4);
	refEnergyTextField.setToolTipText("Edits the reference energy used to start the simulation.  Use -1.0 to use the average potential energy of the walkers.");
	refEnergyTextField.addFocusListener(this);
	refEnergyPanel.add(refEnergyTextField);
	refEnergyConstantCheckBox = new JCheckBox("Constant?");
	refEnergyConstantCheckBox.setToolTipText("Sets whether or not the reference energy is held constant in the simulation.");
	refEnergyConstantCheckBox.addActionListener(this);
	refEnergyPanel.add(refEnergyConstantCheckBox);
	
	return refEnergyPanel;
    }

    /**
     * This method sets up the panel seen as "InputParameters"
     * under the "Settings" tab.
     *
     * @param leftPanelWidth The preferred integer width of the panel.
     */
    private JPanel setupInputParametersPanel(int leftPanelWidth)
    {
	int subPanelHeight = 25;

	JPanel inputParametersPanel = new JPanel();
	inputParametersPanel.setPreferredSize(new 
				      Dimension(leftPanelWidth,180));
	inputParametersPanel.setBorder 
	    (BorderFactory.createTitledBorder("Input Parameters"));
	
	JPanel numWalkersPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	numWalkersPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
						       subPanelHeight));
	JLabel numWalkersLabel = new JLabel("Num. Walkers =");
	numWalkersTextField = new JTextField("",8);
	numWalkersTextField.setToolTipText("Edits the number of walkers desired for the simulation.");
	numWalkersTextField.addFocusListener(this);
	numWalkersPanel.add(numWalkersLabel);
	numWalkersPanel.add(numWalkersTextField);
	inputParametersPanel.add(numWalkersPanel);

	JPanel warmupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	warmupPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
						       subPanelHeight));
	JLabel warmupLabel = new JLabel("Warmup It. =");
	warmupTextField = new JTextField("",8);
	warmupTextField.setToolTipText("Edits the number of iterations to warm the simulation up.");
	warmupTextField.addFocusListener(this);
	warmupPanel.add(warmupLabel);
	warmupPanel.add(warmupTextField);
	inputParametersPanel.add(warmupPanel);

	JPanel dTauPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	dTauPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
						       subPanelHeight));
	JLabel dTauLabel = new JLabel("dTau =");
	dTauTextField = new JTextField("",8);
	dTauTextField.setToolTipText("Edits the timestep of the simulation.");
	dTauTextField.addFocusListener(this);
	dTauPanel.add(dTauLabel);
	dTauPanel.add(dTauTextField);
	inputParametersPanel.add(dTauPanel);

	JPanel alphaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	alphaPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
						       subPanelHeight));
	JLabel alphaLabel = new JLabel("Alpha =");
	alphaTextField = new JTextField("",8);
	alphaTextField.setToolTipText("Edits the feedback parameter of the simulation.  Use -1.0 to use 1/dTau.");
	alphaTextField.addFocusListener(this);
	alphaPanel.add(alphaLabel);
	alphaPanel.add(alphaTextField);
	inputParametersPanel.add(alphaPanel);

	JPanel seedPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	seedPanel.setPreferredSize(new Dimension(leftPanelWidth - 20,
						       subPanelHeight));
	JLabel seedLabel = new JLabel("Seed =");
	seedTextField = new JTextField("",8);
	seedTextField.setToolTipText("Edits the seed used for the random variate generator.");
	seedTextField.addFocusListener(this);
	seedPanel.add(seedLabel);
	seedPanel.add(seedTextField);
	inputParametersPanel.add(seedPanel);
	
	return inputParametersPanel;
    }

    /**************************
     * EVENT HANDLING METHODS *
     *                        ************************************************
     * This class implements most of its own event handlers, so here you go. *
     *************************************************************************/
    
    /**
     * This method handles the action events thrown by various GUI members,
     * including the simulation timer, all of the buttons (button buttons,
     * check boxes, and radio buttons), and the combo boxes.
     * <p>
     * It is important that the simulationTimer is the first one in the list
     * of checked for events, as it needs to run quickly to finish before
     * the next simulation event is thrown.
     * <p>
     * This should be modularized into multiple classes in the future.
     * Although I use good naming practices and the individual parts of 
     * this function are readable, it is getting bulky.
     *
     * @param e The ActionEvent to handle.
     */
    public void actionPerformed(ActionEvent e) 
    {
	Object source = e.getSource();

	if (source == simulationTimer) {
	    try {
		for (int i = 0; i < nIterations; i++) {
		    simulation.Iterate();
		    iterations++;
		    cumulativeEnergy += simulation.refEnergy;
		    if (iterations >= warmup)
			phi0EstimateData.addData(simulation.walkers);
		}
	    }
	    catch (ArithmeticException ae) {
		simulationTimer.stop();
		startButton.setEnabled(false);
		pauseButton.setEnabled(false);
		continueButton.setEnabled(false);
		resetButton.setEnabled(true);
	    }
	    refEnergyData.addCoordinate(new Coordinate(simulation.tau,
						       simulation.refEnergy));
	    numWalkersData.addCoordinate(new Coordinate(simulation.tau,
							simulation.walkers.size()));
	    if (iterations <= e0Iterations)
		    e0EstimateData.addCoordinate(new Coordinate(simulation.tau,
								cumulativeEnergy/iterations));
	    repaint();
	    return;
	}
	if (source == startButton) {
	    isPaused = false;
	    int mode = DMC.INIT_DELTA_FNC;
	    double p1 = 0.0,p2 = 0.0;
	    String s = ((String) walkerInitComboBox.getSelectedItem());
	    if (s == DELTAFNC){
		mode = DMC.INIT_DELTA_FNC;
		p1 = initDeltaFncX0;
	    }
	    else if (s == UNIFORM) {
		mode = DMC.INIT_UNIFORM;
		p1 = initUniformA;
		p2 = initUniformB;
	    }
	    else if (s == GAUSSIAN) {
		mode = DMC.INIT_GAUSSIAN;
		p1 = initGaussianMu;
		p2 = initGaussianSigma;
	    }
	    if ((String) potentialComboBox.getSelectedItem() == SHO)
		simulation = new DMC_SHO(numWalkers, refEnergy, refEnergyConstant,
					 dTau, alpha, seed, mode, p1, p2);
	    else
		simulation = new DMC(numWalkers, refEnergy, refEnergyConstant,
				     dTau, alpha, seed, mode, p1, p2);
	    Iterator i = histogramGraphVector.iterator();
	    while (i.hasNext()) {
		Histogram h = (Histogram) i.next();
		h.theData = simulation.walkers;
	    }
	    simulationTimer.start();
	    startButton.setEnabled(false);
	    pauseButton.setEnabled(true);
	    componentsSetEditable(false);
	}
	else if (source == pauseButton) {
	    isPaused = true;
	    simulationTimer.stop();
	    pauseButton.setEnabled(false);
	    continueButton.setEnabled(true);
	    resetButton.setEnabled(true);
	}
	else if (source == continueButton) {
	    isPaused = false;
	    simulationTimer.restart();
	    continueButton.setEnabled(false);
	    resetButton.setEnabled(false);
	    pauseButton.setEnabled(true);
	}
	else if (source == resetButton) {
	    resetSimulation();
	    startButton.setEnabled(true);
	    continueButton.setEnabled(false);
	    resetButton.setEnabled(false);
	    componentsSetEditable(true);
	}
	else if (source == useDefaultsButton)
	    assignDefaultValues();
	else if (source == normalizeHistogramCheckBox) {
	    normalizeHistogram = normalizeHistogramCheckBox.isSelected();
	    updateHistograms();
	}
	else if (source == refEnergyConstantCheckBox) {
	    refEnergyConstant = refEnergyConstantCheckBox.isSelected();
	}
	else if (source == shoGraphEnergyCheckBox) {
	    shoGraphEnergy = shoGraphEnergyCheckBox.isSelected();
	    updateRefEnergyGraphs();
	    updateE0EstimateGraphs();
	}
	else if (source == shoGraphPhiCheckBox) {
	    shoGraphPhi = shoGraphPhiCheckBox.isSelected();
	    updatePhi0EstimateGraphs();
	}
	else if (source == shoEnergyColorButton) {
	    Color tempColor = JColorChooser.showDialog(this,"Choose SHO Energy Color",
						       shoEnergyColor);
	    if (tempColor != null) {
		shoEnergyColor = tempColor;
		shoEnergyColorPanel.setBackground(shoEnergyColor);
	    }
	    updateRefEnergyGraphs();
	    updateE0EstimateGraphs();
	}
	else if (source == shoPhiColorButton) {
	    Color tempColor = JColorChooser.showDialog(this,"Choose SHO Phi Color",
						       shoPhiColor);
	    if (tempColor != null) {
		shoPhiColor = tempColor;
		shoPhiColorPanel.setBackground(shoPhiColor);
	    }
	    updatePhi0EstimateGraphs();
	}
	else if (source == histogramColorButton) {
	    Color tempColor = JColorChooser.showDialog(this,"Choose SHO Phi Color",
						       histogramColor);
	    if (tempColor != null) {
		histogramColor = tempColor;
		histogramColorPanel.setBackground(histogramColor);
	    }
	    updateHistograms();
	}
	else if (source == numWalkersColorButton) {
	    Color tempColor = JColorChooser.showDialog(this,"Choose SHO Phi Color",
						       numWalkersColor);
	    if (tempColor != null) {
		numWalkersColor = tempColor;
		numWalkersColorPanel.setBackground(numWalkersColor);
	    }
	    updateNumWalkersGraphs();
	}
	else if (source == refEnergyColorButton) {
	    Color tempColor = JColorChooser.showDialog(this,"Choose SHO Phi Color",
						       refEnergyColor);
	    if (tempColor != null) {
		refEnergyColor = tempColor;
		refEnergyColorPanel.setBackground(refEnergyColor);
	    }
	    updateRefEnergyGraphs();
	}
	else if (source == e0EstimateColorButton) {
	    Color tempColor = JColorChooser.showDialog(this,"Choose SHO Phi Color",
						       e0EstimateColor);
	    if (tempColor != null) {
		e0EstimateColor = tempColor;
		e0EstimateColorPanel.setBackground(e0EstimateColor);
	    }
	    updateE0EstimateGraphs();
	}
	else if (source == phi0EstimateColorButton) {
	    Color tempColor = JColorChooser.showDialog(this,"Choose SHO Phi Color",
						       phi0EstimateColor);
	    if (tempColor != null) {
		phi0EstimateColor = tempColor;
		phi0EstimateColorPanel.setBackground(phi0EstimateColor);
	    }
	    updatePhi0EstimateGraphs();
	}
	else if (source == histogramBoxes) {
	    histogramDisplayMode = Histogram.BOXES;
	    updateHistograms();
	}
	else if (source == histogramPoints) {
	    histogramDisplayMode = Histogram.POINTS;
	    updateHistograms();
	}
	else if (source == numWalkersLines) {
	    numWalkersDisplayMode = DataGraph.LINES;
	    updateNumWalkersGraphs();
	}
	else if (source == numWalkersPoints) {
	    numWalkersDisplayMode = DataGraph.POINTS;
	    updateNumWalkersGraphs();
	}
	else if (source == refEnergyLines) {
	    refEnergyDisplayMode = DataGraph.LINES;
	    updateRefEnergyGraphs();
	}
	else if (source == refEnergyPoints) {
	    refEnergyDisplayMode = DataGraph.POINTS;
	    updateRefEnergyGraphs();
	}
	else if (source == e0EstimateLines) {
	    e0EstimateDisplayMode = DataGraph.LINES;
	    updateE0EstimateGraphs();
	}
	else if (source == e0EstimatePoints) {
	    e0EstimateDisplayMode = DataGraph.POINTS;
	    updateE0EstimateGraphs();
	}
	else if (source == walkerInitComboBox)
	    changeCardPanel(walkerInitChoiceOptionsPanel,((JComboBox) source));	
	else if (source == potentialComboBox)
	    changeCardPanel(potentialChoiceOptionsPanel,((JComboBox) source));
	else if (source == graphOptionsComboBox)
	    changeCardPanel(graphOptionsChoiceOptionsPanel,((JComboBox) source));
	else if (source == topLittleGraphComboBox)
	    changeGraphs(topLittleGraph, (String) (((JComboBox) source).getSelectedItem()),
			 185, 136);
	else if (source == bottomLittleGraphComboBox)
	    changeGraphs(bottomLittleGraph, (String) (((JComboBox) source).getSelectedItem()),
			 185, 135);
	else if (source == bigGraphComboBox)
	    changeGraphs(bigGraph, (String) (((JComboBox) source).getSelectedItem()),
			 380, 310);
    }

    /**
     * This function handles the change events.  Well, there's only one so
     * far, so this function handles the numBins slider.
     *
     * @param e The ChangeEvent to handle.
     */
    public void stateChanged(ChangeEvent e) 
    {
	Object source = e.getSource();
	if (source == numBinsSlider) {
	    numBins = ((JSlider) source).getValue();
	    if (numBins == 0)
		numBins = 1;
	    updateHistograms();
	}
    }

    /**
     * This function doesn't do anything, as I don't care when a user
     * clicks on something (that doesn't generate an action event, that is.
     *
     * @param e The FocusEvent to ignore.
     */
    public void focusGained(FocusEvent e) { }
    
    /**
     * This method handles the focus events generated by the focus leaving
     * a particular element.  This is used to update the values of variables
     * when the focus leaves their text box.  If the value is not valid, 
     * this method returns it to its default.
     *
     * @param e The FocusEvent to handle.
     */
    public void focusLost(FocusEvent e)
    {
	Object source = e.getSource();

	// If it's not editable, don't update:
	if (!((JTextField) source).isEditable())
	    return;

	// Possible improvement:  
	// Write overloaded updateTextField(tf,var,default)
	// methods to handle most of the repetitive try/catch work.
	if (source == numWalkersTextField) {
	    try {
		numWalkers = Integer.parseInt(numWalkersTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		numWalkersTextField.setText(Integer.toString(simulation.DEFAULT_NUM_WALKERS));
		numWalkers = simulation.DEFAULT_NUM_WALKERS;
	    }
	}
	else if (source == e0IterationsTextField) {
	    try {
		e0Iterations = Integer.parseInt(e0IterationsTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		e0IterationsTextField.setText(Integer.toString(DEFAULT_E0_ITERATIONS));
		e0Iterations = DEFAULT_E0_ITERATIONS;
	    }
	}
	else if (source == warmupTextField) {
	    try {
		warmup = Integer.parseInt(warmupTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		warmupTextField.setText(Integer.toString(DEFAULT_WARMUP));
		warmup = DEFAULT_WARMUP;
	    }
	    if (!isPaused)
		pause();
	    resetTimer();
	}
	else if (source == delayTextField) {
	    try {
		delay = Integer.parseInt(delayTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		delayTextField.setText(Integer.toString(DEFAULT_DELAY));
		delay = DEFAULT_DELAY;
	    }
	    if (!isPaused)
		pause();
	    resetTimer();
	}
	else if (source == nIterationsTextField) {
	    try {
		nIterations = Integer.parseInt(nIterationsTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		nIterationsTextField.setText(Integer.toString(DEFAULT_N_ITERATIONS));
		nIterations = DEFAULT_N_ITERATIONS;
	    }
	    if (!isPaused)
		pause();
	}
	else if (source == dTauTextField) {
	    try {
		dTau = Double.parseDouble(dTauTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		dTauTextField.setText(Double.toString(simulation.DEFAULT_DTAU));
		dTau = simulation.DEFAULT_DTAU;
	    }
	}
	else if (source == seedTextField) {
	    try {
		seed = Long.parseLong(seedTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		seedTextField.setText(Long.toString(simulation.DEFAULT_SEED));
		seed = simulation.DEFAULT_SEED;
	    }
	}
	else if (source == refEnergyTextField) {
	    try {
		refEnergy = Double.parseDouble(refEnergyTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		refEnergyTextField.setText(Double.toString(simulation.DEFAULT_REF_ENERGY));
		refEnergy = simulation.DEFAULT_REF_ENERGY;
	    }
	}
	else if (source == alphaTextField) {
	    try {
		alpha = Double.parseDouble(alphaTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		alphaTextField.setText(Double.toString(simulation.DEFAULT_ALPHA));
		alpha = simulation.DEFAULT_ALPHA;
	    }
	}
	else if (source == initDeltaFncX0TextField) {
	    try {
		initDeltaFncX0 = Double.parseDouble(initDeltaFncX0TextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		initDeltaFncX0TextField.setText(Double.toString(simulation.DEFAULT_DELTA_FNC_X0));
		initDeltaFncX0 = simulation.DEFAULT_DELTA_FNC_X0;
	    }
	}
	else if (source == initUniformATextField) {
	    try {
		initUniformA = Double.parseDouble(initUniformATextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		initUniformATextField.setText(Double.toString(simulation.DEFAULT_UNIFORM_A));
		initUniformA = simulation.DEFAULT_UNIFORM_A;
	    }
	}
	else if (source == initUniformBTextField) {
	    try {
		initUniformB = Double.parseDouble(initUniformBTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		initUniformBTextField.setText(Double.toString(simulation.DEFAULT_UNIFORM_B));
		initUniformB = simulation.DEFAULT_UNIFORM_B;
	    }
	}
	else if (source == initGaussianMuTextField) {
	    try {
		initGaussianMu = Double.parseDouble(initGaussianMuTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		initGaussianMuTextField.setText(Double.toString(simulation.DEFAULT_GAUSSIAN_MU));
		initGaussianMu = simulation.DEFAULT_GAUSSIAN_MU;
	    }
	}
	else if (source == initGaussianSigmaTextField) {
	    try {
		initGaussianSigma = Double.parseDouble(initGaussianSigmaTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		initGaussianSigmaTextField.setText(Double.toString(simulation.DEFAULT_GAUSSIAN_SIGMA));
		initGaussianSigma = simulation.DEFAULT_GAUSSIAN_SIGMA;
	    }
	}
	else if (source == histogramXMinTextField) {
	    try {
		histogramXMin = Double.parseDouble(histogramXMinTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		histogramXMinTextField.setText(Double.toString(simulation.DEFAULT_X_MIN));
		histogramXMin = simulation.DEFAULT_X_MIN;
	    }
	    updateHistograms();
	}
	else if (source == histogramXMaxTextField) {
	    try {
		histogramXMax = Double.parseDouble(histogramXMaxTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		histogramXMaxTextField.setText(Double.toString(simulation.DEFAULT_X_MAX));
		histogramXMax = simulation.DEFAULT_X_MAX;
	    }
	    updateHistograms();
	}
	else if (source == numWalkersMaxPointsTextField) {
	    try {
		numWalkersMaxPoints = Integer.parseInt(numWalkersMaxPointsTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		numWalkersMaxPointsTextField.setText(Integer.toString(DEFAULT_MAX_POINTS));
		numWalkersMaxPoints = DEFAULT_MAX_POINTS;
	    }
	    updateNumWalkersGraphs();
	}
	else if (source == refEnergyMaxPointsTextField) {
	    try {
		refEnergyMaxPoints = Integer.parseInt(refEnergyMaxPointsTextField.getText());
	    }
	    catch (NumberFormatException nfe) {
		refEnergyMaxPointsTextField.setText(Integer.toString(DEFAULT_MAX_POINTS));
		refEnergyMaxPoints = DEFAULT_MAX_POINTS;
	    }
	    updateRefEnergyGraphs();
	}
    }
    
    /*********************************
     * EVENT HANDLING HELPER METHODS *
     *                               *****************************************
     * These are some of the methods the event handlers call to lend a hand. *
     *************************************************************************/

    /**
     * This method updates all of the visible histograms, including changing
     * the number of bins, xMin, xMax, the color, whether or not to normalize,
     * the mode, as well as whether or not to graph SHO's Phi_0 function.
     */
    private void updateHistograms() 
    {
	if (histogramGraphVector != null) {
	    Iterator i = histogramGraphVector.iterator();
	    while (i.hasNext()) {
		Histogram h = (Histogram) i.next();
		h.numBins = numBins;
		h.xMin = histogramXMin;
		h.xMax = histogramXMax;
		h.mode = histogramDisplayMode;
		h.color = histogramColor;
		h.normalize = normalizeHistogram;
	    }
	    repaint();
	}
    }

    /**
     * This method updates all of the visible phi0Estimate graphs.
     */
    private void updatePhi0EstimateGraphs() 
    {
	if (phi0EstimateGraphVector != null) {
	    Iterator i = phi0EstimateGraphVector.iterator();
	    while (i.hasNext()) {
		Phi0Histogram h = (Phi0Histogram) i.next();
		h.color = phi0EstimateColor;
		h.functionList.clear();
		if (shoGraphPhi)
		    h.functionList.add(new SHOPhiFunction(shoPhiColor));
	    }
	    repaint();
	}
    }

    /**
     * This method updates the mode, color, and max points of the visible
     * number of walkers graphs.
     */
    private void updateNumWalkersGraphs() 
    {
	if (numWalkersGraphVector != null) {
	    Iterator i = numWalkersGraphVector.iterator();
	    while (i.hasNext()) {
		DataGraph dg = (DataGraph) i.next();
		dg.mode = numWalkersDisplayMode;
		dg.color = numWalkersColor;
		dg.theData.maxPoints = numWalkersMaxPoints;
	    }
	    repaint();
	}
    }

    /**
     * This method updates the mode, color, and max points of the visible
     * E0 estimate graphs.
     */
    private void updateE0EstimateGraphs() 
    {
	if (e0EstimateGraphVector != null) {
	    Iterator i = e0EstimateGraphVector.iterator();
	    while (i.hasNext()) {
		DataGraph dg = (DataGraph) i.next();
		dg.mode = e0EstimateDisplayMode;
		dg.color = e0EstimateColor;
		dg.functionList.clear();
		if (shoGraphEnergy)
		    dg.functionList.add(new SHOEnergyFunction(shoEnergyColor));
	    }
	    repaint();
	}
    }

    /**
     * This method updates the mode, color, max points, as well as whether
     * or not to graph SHO's E_0 function on all of the visible
     * reference energy graphs.
     */
    private void updateRefEnergyGraphs() 
    {
	if (refEnergyGraphVector != null) {
	    Iterator i = refEnergyGraphVector.iterator();
	    while (i.hasNext()) {
		DataGraph dg = (DataGraph) i.next();
		dg.mode = refEnergyDisplayMode;
		dg.color = refEnergyColor;
		dg.theData.maxPoints = refEnergyMaxPoints;
		dg.functionList.clear();
		if (shoGraphEnergy)
		    dg.functionList.add(new SHOEnergyFunction(shoEnergyColor));
	    }
	    repaint();
	}
    }

    /**
     * This method presses the Pause button.
     */
    private void pause()
    {
	actionPerformed(new ActionEvent(pauseButton,0,""));
    }

    /**
     * This method changes the graph displayed on one of the graph panels.
     *
     * @param gp The GraphPanel of which the graph is to be changed.
     * @param graph The string representation of the graph to be displayed.
     * @param width The preferred width of the new graph.
     * @param height The preferred height of the new graph.
     */
    private void changeGraphs(GraphPanel gp, String graph, 
			      int width, int height)
    {
	// Not sure what it had before, so remove from each (won't hurt):
	histogramGraphVector.remove(gp.content);
	numWalkersGraphVector.remove(gp.content);
	refEnergyGraphVector.remove(gp.content);
	e0EstimateGraphVector.remove(gp.content);
	phi0EstimateGraphVector.remove(gp.content);

	if (graph == HISTOGRAM) {
	    Histogram h = new Histogram(width,height,histogramXMin,
					histogramXMax,numBins);
	    h.theData = simulation.walkers;
	    h.mode = histogramDisplayMode;
	    histogramGraphVector.add(h);
	    h.color = histogramColor;
	    h.normalize = normalizeHistogram;
	    gp.setContent(h);
	    gp.setBorder(BorderFactory.createTitledBorder("Histogram"));
	}
	else if (graph == NUMWALKERS) {
	    DataGraph g = new DataGraph(width,height);
	    g.setToolTipText("Number of Walkers Graph");
	    g.theData = numWalkersData;
	    g.mode = numWalkersDisplayMode;
	    g.color = numWalkersColor;
	    numWalkersGraphVector.add(g);
	    gp.setContent(g);
	    gp.setBorder(BorderFactory.createTitledBorder("Number of Walkers"));
	}
	else if (graph == REFENERGY) {
	    DataGraph g = new DataGraph(width,height);
	    g.setToolTipText("Reference Energy Graph");
	    g.theData = refEnergyData;
	    g.mode = refEnergyDisplayMode;
	    g.color = refEnergyColor;
	    refEnergyGraphVector.add(g);
	    if (shoGraphEnergy)
		g.functionList.add(new SHOEnergyFunction(shoEnergyColor));
	    gp.setContent(g);
	    gp.setBorder(BorderFactory.createTitledBorder("Reference Energy"));
	}
	else if (graph == E0ESTIMATE) {
	    DataGraph g = new DataGraph(width,height);
	    g.setToolTipText("E0 Estimate Graph");
	    g.theData = e0EstimateData;
	    g.mode = e0EstimateDisplayMode;
	    g.color = e0EstimateColor;
	    e0EstimateGraphVector.add(g);
	    if (shoGraphEnergy)
		g.functionList.add(new SHOEnergyFunction(shoEnergyColor));
	    gp.setContent(g);
	    gp.setBorder(BorderFactory.createTitledBorder("E0 Estimate"));	    
	}
	else if (graph == PHI0ESTIMATE) {
	    Phi0Histogram h = new Phi0Histogram(width,height,phi0EstimateData);
	    phi0EstimateGraphVector.add(h);
	    h.setToolTipText("PHI0 Estimate Graph");
	    h.color = phi0EstimateColor;
	    if (shoGraphPhi)
		h.functionList.add(new SHOPhiFunction(shoPhiColor));
	    gp.setContent(h);
	    gp.setBorder(BorderFactory.createTitledBorder("Phi0 Estimate"));
	}
	repaint();
    }

    /**
     * This function changes the visible panel in a CardLayout'd panel
     * to what was selected by a combo box.
     *
     * @param p The JPanel with the CardLayout in which we want to change
     *          the visible panel.
     * @param cb The JComboBox which contains the string of the panel we
     *           want to change to.
     */
    private void changeCardPanel(JPanel p, JComboBox cb)
    {
	CardLayout l = (CardLayout) p.getLayout();
	String s = (String) cb.getSelectedItem();
	l.show(p,s);
    }
}

/*****************
 * EXTRA CLASSES *
 *               *********************************************************
 * Here are some extra classes, which could probably be put in their own *
 * files, some in other packages, but for now aren't.                    *
 *************************************************************************/

/**
 * Here is a class that acts as a WindowListener for the GUI when it is run
 * as an application.  It just exits the program when the window is closed,
 * so no further JavaDoc is given.
 */
class WindowMonitor implements WindowListener
{
    public void windowClosing(WindowEvent e) { 
	System.exit(0);
    }
    public void windowClosed(WindowEvent e) { }
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowOpened(WindowEvent e) {  }
}

/**
 * This class is probably one of the only well designed things in this
 * GUI.  It is a panel with a title'd border, that contains another panel
 * that will be a graph at one point or another.
 */
class GraphPanel extends JPanel
{
    /**
     * The panel contained within this GraphPanel.
     */
    public JPanel content;

    /**
     * This constructor constructs this panel with the given title and
     * content.
     *
     * @param title The String title of the panel, to be used in a
     *              titled border.
     * @param p The JPanel contained within this GraphPanel.
     */
    public GraphPanel(String title, JPanel p)
    {
	content = p;
	add(content);
	setBorder(BorderFactory.createTitledBorder(title));
    }
    
    /**
     * This constructor creates this panel with the given title and
     * a new JPanel for content.
     *
     * @param title The String title of the panel, to be used in a
     *              titled border.
     */
    public GraphPanel(String title) 
    {
	this(title, new JPanel());	
    }

    /**
     * This method clears the existing content and adds the given content.
     *
     * @param c The JPanel to set the content to.
     */
    public void setContent(JPanel c)
    {
	removeAll();
	content = c;
	add(content);
    }
}

/**
 * This class extends Function to implement the function for
 * the SHO's ground state wavefunction, Phi_0.
 */
class SHOPhiFunction extends Function
{
    /**
     * Creates the function with the given color.
     *
     * @param c The color to graph the function.
     */
    public SHOPhiFunction(Color c)
    {
	super(c);
    }

    /**
     * Returns the value of Phi_0 at the x value given.
     * <p>
     * The function for Phi_0 is:
     *     phi_0(x) = pi^(-1/4) * exp(-x^2/2)
     *
     * @param x The x value at which the function value should be returned.
     */
    public double f(double x)
    {
	return 0.7511255445 * Math.exp(-0.5*x*x);
    }
}

/**
 * This class extends Function to implement the function for
 * the SHO's ground state energy, E_0, which is a constant.
 */
class SHOEnergyFunction extends Function
{    
    /**
     * Creates the function with the given color.
     *
     * @param c The color to graph the function.
     */
    public SHOEnergyFunction(Color c)
    {
	super(c);
    }

    /**
     * Returns E_0.
     *
     * @param x The x value at which the function value should be returned.
     */
    public double f(double x)
    {
	return 0.5;
    }
}

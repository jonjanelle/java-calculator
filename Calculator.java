/**
 * CSCIE-10B Problem Set 6
 * Calculator GUI application using swing classes
 * 
 * 
 * Roughly mimics the basic functions of the built-in Windows calculator.
 * Allows the four basic arithmetic operations to be performed. Also includes 
 * buttons to find the square root of a number, square a number, and change
 * the sign of a number. The display is set to show only 9 digits to prevent 
 * 
 * 
 * Note: This version shows binary operators on screen to partially mimic the behavior of the windows calculator,
 * which indicates which operator was pushed in a separate row of the display 
 * 
 *  @author Jon Janelle
 *  @version Last modified on 4/8/16
 */
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class Calculator extends JFrame{

	private JButton[] buttons;
	private JPanel buttonPanel; 	//Panel to organize calculator buttons
	private JTextArea display;		//Main display area for calculator
	private String[] buttonLabels = {"C","\u221A","/","*",
									 "7","8","9","-",
									 "4","5","6","+",
									 "1","2","3","x^2",
									 ".","0","\u00B1", "="};
	private Font buttonFont;		
	private Font displayFont;

	private double operLeft;			//left operand, updated after each new operator press
	private String lastOperator;		//most recent operator pressed whose evaluation has not been finalized
	private String lastPushed;			//last button that was pressed
	private final int displaySize = 10; 	
	/**
	 * Default constructor.
	 * Initializes buttons and display area, adds panels to frame, and makes frame visible
	 */
	public Calculator()
	{
		//Initialize fonts
		buttonFont = new Font ("Helvetica", Font.BOLD, 28);
		displayFont = new Font ("Helvetica", Font.BOLD, 54);

		//Setup buttons
		buttonPanel = new JPanel(new GridLayout(5,4));
		buttons = new JButton[20];
		initButtons();

		//Initialize main display
		display = new JTextArea(1,10);
		display.setEditable(false); //prevent user from manually typing in display
		
		
		clear();
		display.setFont(displayFont);
		display.setBorder(LineBorder.createBlackLineBorder());

		//Add display and button panel to frame
		add(display, BorderLayout.NORTH);
		add(buttonPanel,BorderLayout.CENTER);

		pack();
		setTitle("Janelle Calculator");
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 *  Label the calculator buttons and add them to the panel
	 */
	private void initButtons()
	{
		for (int i = 0; i < buttons.length; i++){
			buttons[i]=new JButton(buttonLabels[i]);
			buttons[i].addActionListener(new ButtonListener());
			buttons[i].setPreferredSize(new Dimension(90,90));
			buttons[i].setFont(buttonFont);
			buttonPanel.add(buttons[i]);
		}
	}

	/**
	 * Specify how to respond to button presses.
	 */
	private class ButtonListener implements ActionListener 
	{ 
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand(); //Command holds the text of the button pushed

			//Check for the push of a numeric digit or a decimal point
			if (isNumericDigit(command)||command.equals(".")){
				processNumeric(command); //
			}
			//Next check for and react to operator button press
			else if (isOperator(command)){
				processOperator(command);
			}
			
			else if (command.equals("C")){
				clear();
			}
			lastPushed = command; 
		}
	} 

	/**
	 * Reset calculator to beginning state 
	 */
	private void clear(){
		display.setText("0");
		lastPushed = "0";
		lastOperator = null;
		operLeft = 0;	
	}
	
	/**
	 * Determine whether a string represents a single integer digit
	 * @param s A string
	 * @return true if s is an integer digit and false otherwise
	 */
	private static boolean isNumericDigit(String s)
	{
		String[] digits = {"0","1","2","3","4","5","6","7","8","9","0"};
		for (String digit:digits){
			if (digit.equals(s))
				return true;
		}
		return false;
	}

	/**
	 * Determine whether a string represents a calculator operator.
	 * Notes: U+221A is the unicode square root character
	 * 		  U+00B1 is the unicode plus or minus character
	 * @param s A string
	 * @return true if s is an integer digit and false otherwise
	 */
	private static boolean isOperator(String s)
	{
		String[] ops = {"+","-","*","/","\u221A","\u00B1","=","x^2"};
		for (String op:ops){
			if (op.equals(s))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if a string represents a unary operator, false otherwise
	 * @param op A string containing a representation of an operator
	 * @return True if op is unary, false otherwise.
	 */
	public static boolean isUnaryOp(String op){
		return (op.equals("\u00B1")||op.equals("x^2")||op.equals("\u221A"));
	}
	
	/**
	 * Inspect new digit or decimal point and update main display appropriately 
	 * @param command A digit or decimal point 
	 */
	private void processNumeric(String command)
	{
		if (isOperator(lastPushed)) { //begin new number construction operator pressed last
			display.setText("0");
		}
		//If a digit is entered immediately after "=", then start a new expression
		if (lastPushed.equals("=")||display.getText().equals("0")){
			display.setText(command);
		}

		//Only add a decimal point to the display if it does not already contain one.
		else if (command.equals(".")){
			if (display.getText().indexOf(".")==-1)
				display.setText(display.getText()+command);
			else
				return;
		}

		//If the display contains only 0, then keep 0 if decimal button pushed, discard otherwise
		else if (display.getText().equals("0"))
		{
			if (display.getText().indexOf(".")==-1)
				display.setText(command);
			else
				display.setText(display.getText()+command);
		}

		//Otherwise concatenate existing display string and new digit 
		else if (display.getText().length()<displaySize&&(isNumericDigit(lastPushed)||(lastPushed.equals(".")))){
			display.setText(display.getText()+command);
		}
	}


	/**
	 * React appropriately to a new operator press and call the appropriate processing method if needed
	 * @param op A String containing a single operator 
	 */
	private void processOperator(String op)
	{	
		if (display.getText().indexOf("Error")!=-1){
			clear();
		}
		else if (op.equals("\u221A")){ //process square root push
			processSqrt();
		}
		else if (op.equals("x^2")){  //process square button push
			processSquare();
		}
		
		else if (op.equals("\u00B1")) { //process plus-or-minus button push
			processPlusMinus();
		}
		
		
		//if last button pushed was an non-unary operator, replace previous operator with new operator
		else if (isOperator(lastPushed)&&!isUnaryOp(lastPushed)) {
			if (op.equals("=")==false) { //don't change operator to equals
				if (display.getText().indexOf("E")!=-1){
					operLeft = Double.parseDouble(display.getText().substring(0,display.getText().length()));	
				}
				else {
					operLeft = Double.parseDouble(display.getText().substring(0,display.getText().length()-1));
				}
				display.setText(""+operLeft+op);
			}
		}

		//If no previous operators waiting for right operand, then set operator and left operand
		else if (lastOperator == null || isUnaryOp(lastOperator)|| lastOperator.equals("")){ 
			if (op.equals("=")){ //If "=" button pushed and no operator is waiting for evaluation, then do nothing.
				return;
			}
		
			operLeft = Double.parseDouble(display.getText());	//set left operand
			display.setText(display.getText()+op);
		}

		//Evaluate expression if last operator not empty and equals pushed
		else if (op.equals("=")||op.equals("+")||op.equals("-")||op.equals("*")||op.equals("/")){
			processEquals(op);
		}

		//if command was "=", set lastOperator to null to begin new expression
		if (op.equals("=") )
			lastOperator = null;
		else if (!isUnaryOp(op)) { //if not unary, then must be binary operator.
			lastOperator = op; // set new binary operator waiting to be completed
		}
			
	}

	/**
	 * Process a completed binary expression after a push of the equals sign or after
	 * more than one binary operator has been encountered. Updates the display with the 
	 * result of operating upon leftOper and the currently displayed value.
	 * 
	 * @param op A string containing the operator that prompted expression evaluation
	 */
	private void processEquals(String op)
	{
		if (lastOperator.equals("+")){
			operLeft+= Double.parseDouble(display.getText());
			display.setText(""+operLeft);
		}
		else if (lastOperator.equals("-")){
			operLeft-=Double.parseDouble(display.getText());
			display.setText(""+operLeft);
		}
		else if (lastOperator.equals("*")){
			operLeft*=Double.parseDouble(display.getText());
			display.setText(""+operLeft);
		}

		else if (lastOperator.equals("/")){
			if (Double.parseDouble(display.getText())==0){
				display.setText("Error: / by 0");
			}
			else {
				operLeft/=Double.parseDouble(display.getText());
				display.setText(""+operLeft);
			}
		}
		if (op.equals("=")==false)
			display.setText(display.getText()+op); //if here because of part of a chain of non-equals, show operator
	}

	/**
	 * Process press of the square root key. Computes the square root of the 
	 * currently displayed value and rounds the answer to 6 decimal places. If
	 * the currently displayed value contained an incomplete binary operator expression,
	 * then do nothing. If a negative number is currently displayed, show an error message.
	 */
	private void processSqrt(){
		try {
			double temp =  Double.parseDouble(display.getText());
			if (temp >= 0) {
				display.setText(String.format("%.9g", Math.sqrt(temp))); 
				
			}
			else {
				display.setText("Error: Undefined");
			}
		}
		catch (NumberFormatException e){
			//String must contain operator, do nothing.
		}
	}
	
	/**
	 * Process press of the square key. Squares the  currently displayed value and rounds
	 * the answer to 6 decimal places. If the currently displayed value contained an incomplete
	 * binary operator expression, then do nothing.
	 */
	private void processSquare(){
		try {
			double temp =  Double.parseDouble(display.getText());
			display.setText(String.format("%.9g", temp*temp)); //%g allows for scientific notation
		}
		catch (NumberFormatException e){
			//String must contain operator, do nothing.
		}
	}
	
	/**
	 * React to the push of the plus or minus button.
	 * If only a numeric value is shown on screen, negate it.
	 * If a binary operator is present and waiting for a right operand, then do nothing
	 */
	private void processPlusMinus(){
		try {
			double temp =  Double.parseDouble(display.getText());
			display.setText(""+-1*temp);
		}
		catch (NumberFormatException e){
			//String must contain operator, do nothing.
		}
	}

		
	/**
	 * Create a new Calculator object 
	 */
	public static void main(String[] args) {

		new Calculator();
	}

}

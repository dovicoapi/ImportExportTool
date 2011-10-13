package com.dovico.importexporttool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.JButton;


public class CUpDownButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	private boolean m_bUpButton = true;
	private int m_iTriangleHeight = 4; 
	private int m_iTriangleWidth = 8;
	
	
	// Overloaded constructor
	public CUpDownButton(boolean bUpButton) { m_bUpButton = bUpButton; }
	
	
	@Override
	protected void paintComponent(Graphics g) {
		// Let the parent class do any drawing that it needs to (I don't want to handle all of the visual aspects of the button, I just want an Up/Down arrow on
		// the button without having to pull in images)
		super.paintComponent(g);
		
		// Get the drawing surface size of our panel		
		Rectangle rcMaxSize = super.getVisibleRect();
		Graphics2D g2d = (Graphics2D)g;
		
		// Determine the X/Y position of the triangle on the button
		int iLeft = ((int)rcMaxSize.getCenterX() - (m_iTriangleWidth / 2)); // half the width of the triangle so that it's drawn horizontally centered
		int iTop = ((int)rcMaxSize.getCenterY() - (m_iTriangleHeight / 2)); // half the height of the triangle so that it's drawn vertically centered 
		
		// Get the proper polygon based on if this button needs an Up or Down arrow
		Polygon pTriangle = null;
		if(m_bUpButton) { pTriangle = getUpArrowPolygon(iLeft, iTop); }
		else { pTriangle = getDownArrowPolygon(iLeft, iTop); }
				
		// Fill the triangle's background
		g2d.setColor(Color.black);
		g2d.fillPolygon(pTriangle);		
	}
	
	
	// Returns the polygon for the up arrow
	private Polygon getUpArrowPolygon(int iLeft, int iTop){
		Polygon pTriangle = new Polygon();
		
		// Doesn't make sense to me but I had to tweak the calculations slightly to have this Up arrow match the Down arrow in size and location
		pTriangle.addPoint((iLeft - 1), (iTop + m_iTriangleHeight)); // Bottom-left point
		pTriangle.addPoint((iLeft + m_iTriangleWidth + 1), (iTop + m_iTriangleHeight)); // Bottom-right point
		pTriangle.addPoint((iLeft + (m_iTriangleWidth / 2)), (iTop - 1)); // Top point
		pTriangle.addPoint((iLeft - 1), (iTop + m_iTriangleHeight)); // Back to the bottom-left point
			
		return pTriangle;
	}
	
	
	// Returns the polygon for the down arrow 
	private Polygon getDownArrowPolygon(int iLeft, int iTop){
		Polygon pTriangle = new Polygon();		
		
		pTriangle.addPoint(iLeft, iTop); // Top-left point
		pTriangle.addPoint((iLeft + (m_iTriangleWidth / 2)), (iTop + m_iTriangleHeight)); // Bottom point
		pTriangle.addPoint((iLeft + m_iTriangleWidth), iTop); // Top-right point		
		pTriangle.addPoint(iLeft, iTop); // Back to the top-left point
		
		return pTriangle;
	}
}

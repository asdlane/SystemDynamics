package de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph;

import java.awt.*;

import org.jgraph.graph.*;

public class SharedNodeVertexRenderer extends VertexRenderer{
	private static final long serialVersionUID = 1L;
	private String nodeTypeLocal;
	private static SharedNodeVertexRenderer instance;

	/**
	 * Constructor.
	 * @param nodeType 
	 */
	private SharedNodeVertexRenderer(String nodeType) {
		nodeTypeLocal = nodeType;
	}

	/**
	 * Gets the <code>ConstantNodeVertexRenderer</code instance.
	 * @param nodeType 
	 * 
	 * @return instance
	 */
	public static SharedNodeVertexRenderer getInstance(String nodeType) {
		
		instance = new SharedNodeVertexRenderer(nodeType);
		
		return instance;
	}

	/**
	 * Renders the constant node vertex.
	 * 
	 * @param g graphics
	 */
	@Override
	public void paint(Graphics g) {
		if(nodeTypeLocal == "Constant") {
			int b = borderWidth;
			Graphics2D g2 = (Graphics2D) g;
			Dimension d = getSize();
			boolean tmp = selected;
			if (super.isOpaque()) {
				g.setColor(super.getBackground());
				if (gradientColor != null && !preview) {
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(),
							getWidth(), getHeight(), gradientColor, true));
				}
				g.fillOval(d.height / 4 + b - 1, b - 1, d.height - b, d.height - b);

				g.drawRect(d.height / 4 + b - 5, b - 1, d.height - b +8, d.height - b);

			}
			try {
				setBorder(null);
				setOpaque(false);
				selected = false;
				super.paint(g);
			} finally {
				selected = tmp;
			}
			if (bordercolor != null) {
				g.setColor(bordercolor);
				g2.setStroke(new BasicStroke(b));
				// draw circle
				g.drawOval(d.height / 4 + b - 1, b - 1, d.height - b, d.height - b);
				g.drawRect(d.height / 4 + b - 5, b - 1, d.height - b +8, d.height - b);

			}
			if (selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(highlightColor);
				// draw circle
				g.drawOval(d.height / 4 + b - 1, b - 1, d.height - b, d.height - b);
				g.drawRect(d.height / 4 + b - 5, b - 1, d.height - b +8, d.height - b);
			}
		}
		if(nodeTypeLocal == "Auxiliary") {
			 int b = borderWidth;
		      Graphics2D g2 = (Graphics2D) g;
		      Dimension d = getSize();
		      boolean tmp = selected;
		      if (super.isOpaque()) {
		         g.setColor(super.getBackground());
		         if (gradientColor != null && !preview) {
		            setOpaque(false);
		            g2.setPaint(new GradientPaint(0, 0, getBackground(),
		                  getWidth(), getHeight(), gradientColor, true));
		         }
		         g.fillOval(b - 1, b - 1, d.width - b, d.height - b);
		      }
		      try {
		         setBorder(null);
		         setOpaque(false);
		         selected = false;
		         super.paint(g);
		      } finally {
		         selected = tmp;
		      }
		      if (bordercolor != null) {
		         g.setColor(bordercolor);
		         g2.setStroke(new BasicStroke(b));
		         g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
		      }
		      if (selected) {
		         g2.setStroke(GraphConstants.SELECTION_STROKE);
		         g.setColor(highlightColor);
		         g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
		      }   
		}
		else {
			System.out.println(nodeTypeLocal);
		}
		
		
	}
}

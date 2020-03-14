package blue.endless.wtrader.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthConstants;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public class ColorblockStyle extends SynthStyle {
	public Color foreground = color("494374");
	public Color background = null;
	public Color border = null;
	public Color text = null;
	
	public Color disabledFG = color("494374");
	public Color enabledBG = color("221e3e");
	public Color disabledBG = color("191632");
	public Color textAreaBG = Color.BLACK;
	
	public Font defaultFont = makeNormalFont(20);
	
	
	public ColorblockStyle() {
		setPrimaryColor(color("494374"), true);
		//text = color("958dd2");
		//background = color("221e3e");
		
		double foregroundIntensity = getIntensity(foreground);
		double textIntensity = getIntensity(text);
		double textRatio = textIntensity / foregroundIntensity;
		System.out.println("textRatio: "+textRatio);
		
		double backgroundIntensity = getIntensity(background);
		double backgroundRatio = backgroundIntensity / foregroundIntensity;
		System.out.println("backgroundRatio: "+backgroundRatio);
	}
	
	public ColorblockStyle setPrimaryColor(Color c, boolean darkTheme) {
		this.foreground = c;
		
		double fgIntensity = getIntensity(c);
		if (darkTheme) {
			this.background = scale(c, fgIntensity*0.4921875);
			this.text = scale(c, fgIntensity*1.953125);
		} else {
			this.background = scale(c, fgIntensity*1.25);
			this.text = scale(c, fgIntensity*0.05);
		}
		
		return this;
	}
	
	
	
	//extends SynthStyle {
		@Override
		protected Color getColorForState(SynthContext context, ColorType type) {
			if (context.getComponent() instanceof JTextField) {
				if (type==ColorType.BACKGROUND) return textAreaBG;
				
			} else if (context.getRegion()==Region.ARROW_BUTTON || context.getRegion()==Region.BUTTON || context.getRegion()==Region.COMBO_BOX) {
				if (type==ColorType.BACKGROUND) return foreground; 
			}
			
			
			if (type==ColorType.BACKGROUND) return background;
			
			int state = context.getComponentState();
			//if ((state&SynthConstants.DISABLED) !=0) {
			//	if (type==ColorType.FOREGROUND || type==ColorType.TEXT_FOREGROUND) {
			//		return disabledFG;
			//	} else {
			//		return disabledBG;
			//	}
			//} else if ((state&SynthConstants.ENABLED) !=0) {
				if (type==ColorType.FOREGROUND) {
					return text;
				} else if (type==ColorType.TEXT_FOREGROUND) {
					return text;
				} else {
					if (type==ColorType.FOCUS) return text;
					return background;
				}
			//}
			
			//return disabledBG;
		}
	
		@Override
		protected Font getFontForState(SynthContext context) {
			Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
			return font;
		}
		
		@Override
		public SynthPainter getPainter(SynthContext context) {
			//System.out.println(context.getComponent().getClass().getCanonicalName());
			
			if (context.getRegion()==Region.TEXT_FIELD) {
				return new SynthPainter() {
					@Override
					public void paintTextFieldBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
						if ((context.getComponentState()&SynthConstants.FOCUSED) != 0) {
							g.setColor(text);
						} else {
							g.setColor(foreground);
						}
						paintSoftRect(g, x, y, w, h);
					}
					
					@Override
					public void paintTextFieldBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
						paintGradientRect(g, x+2, y+2, w-4, h-4, background, Color.BLACK);
					}
					
					
				};
			} else if (context.getRegion()==Region.COMBO_BOX) {
				return new SynthPainter() {
					@Override
					public void paintComboBoxBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
						if ((context.getComponentState()&SynthConstants.FOCUSED) != 0) {
							g.setColor(text);
						} else {
							g.setColor(foreground);
						}
						paintSoftRect(g, x, y, w, h);
					}
					
					@Override
					public void paintComboBoxBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
						//This method is basically useless since only a 1px gutter of this background is visible off the left and right sides.
					}
					
					
				};
			} else if (context.getRegion()==Region.ARROW_BUTTON) {
				return new SynthPainter() {
					@Override
					public void paintArrowButtonBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
						//g.setColor(Color.RED);
						//g.fillRect(x, y, w, h);
					}
					
					@Override
					public void paintArrowButtonForeground(SynthContext context, Graphics g, int x, int y, int w, int h, int direction) {
						//System.out.println("PaintArrowForeground: "+direction);
						switch(direction) {
						case SwingConstants.NORTH:
							g.setColor(text);
							//g.fillPolygon(new int[]{0}, new int[]{0}, 1);
							break;
						case SwingConstants.SOUTH:
							g.setColor(text);
							int arrowWidth = Math.min(w, h);
							if (arrowWidth>14) arrowWidth=14;
							int xofs = (w-arrowWidth)/2;
							int yofs = (h-arrowWidth)/2;
							
							paintSouthArrow(g, x+xofs, y+yofs, arrowWidth, arrowWidth);
							
							//g.fillPolygon(new int[]{x, x+(w/2), x+w}, new int[]{y, y+w, y}, 3);
							break;
						case SwingConstants.EAST:
							break;
						case SwingConstants.WEST:
							
							break;
						default:
							break;
						}
						// TODO Auto-generated method stub
						//super.paintArrowButtonForeground(context, g, x, y, w, h, direction);
					}
				};
			} else if (context.getRegion()==Region.BUTTON) {
				return new SynthPainter() {
					@Override
					public void paintButtonBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
						if ((context.getComponentState()&SynthConstants.FOCUSED) != 0) {
							g.setColor(text);
						} else {
							g.setColor(foreground);
						}
						paintSoftRect(g, x, y, w, h);
					}
					
					@Override
					public void paintButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
						if ((context.getComponentState()&SynthConstants.PRESSED) != 0) {
							paintGradientRect(g, x+2, y+2, w-4, h-4, Color.BLACK, background);
						} else {
							paintGradientRect(g, x+2, y+2, w-4, h-4, foreground, background);
						}
					}
				};
			} else if (context.getRegion()==Region.MENU || context.getRegion()==Region.MENU_ITEM) {
				return new SynthPainter() {
					@Override
					public void paintMenuBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
						g.setColor(foreground);
						g.fillRect(0, 0, w, h);
					}
					
					@Override
					public void paintMenuItemBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
						g.setColor(foreground);
						g.fillRect(0, 0, w, h);
					}
				};
			}
			
			// TODO Auto-generated method stub
			return super.getPainter(context);
		}
		
		@Override
		public Insets getInsets(SynthContext context, Insets insets) {
			
			if (context.getComponent() instanceof JTextField || context.getComponent() instanceof JComboBox || context.getComponent() instanceof JButton) {
				if (insets!=null) {
					return new Insets(insets.top+2, insets.left+4, insets.bottom+2, insets.right+4);
				} else {
					return new Insets(2, 4, 2, 4);
				}
			}
			
			return super.getInsets(context, insets);
		}
		
		private static Font makeNormalFont(double pointSize) {
			String[] fontPreferences = { "Noto Sans", "Liberation Sans", "Ubuntu", "Arial", Font.SANS_SERIF };
			
			for(String preferred : fontPreferences) {
				for(String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
					if (s.equals(preferred)) {
						//System.out.println("Selected "+s+" as display font.");
						return new Font(s, Font.PLAIN, (int)pointSize);
					}
					//System.out.println(s);
				}
			}
			
			//System.out.println("Selected Font.SANS_SERIF as display font.");
			return new Font(Font.SANS_SERIF, Font.PLAIN, (int)pointSize);
		}
		
		@Override
		public Font getFont(SynthContext context) {
			return defaultFont;
		}
	//}
		
	private static void paintSoftRect(Graphics g, int x, int y, int width, int height) {
		g.fillRect(x+1, y, width-2, 1);
		g.fillRect(x, y+1, width, 1);
		g.fillRect(x, y+2, 2, height-4);
		g.fillRect(x+width-2, y+2, 2, height-4);
		g.fillRect(x, y+height-2, width, 1);
		g.fillRect(x+1, y+height-1, width-2, 1);
	}
	
	private static void paintGradientRect(Graphics graphics, int x, int y, int width, int height, Color top, Color bottom) {
		double r = top.getRed()/255.0;
		double g = top.getGreen()/255.0;
		double b = top.getBlue()/255.0;
		double endR = bottom.getRed()/255.0;
		double endG = bottom.getGreen()/255.0;
		double endB = bottom.getBlue()/255.0;
		double dr = endR - r;
		double dg = endG - g;
		double db = endB - b;
		
		dr /= height;
		dg /= height;
		db /= height;
		
		for(int i=0; i<height; i++) {
			
			graphics.setColor(new Color((int)(r*255), (int)(g*255), (int)(b*255)));
			graphics.fillRect(x, y+i, width, 1);
			
			r += dr; if (r>1) r=1; if (r<0) r=0;
			g += dg; if (g>1) g=1; if (g<0) g=0;
			b += db; if (b>1) b=1; if (b<0) b=0;
		}
	}
	
	private static void paintSouthArrow(Graphics g, int x, int y, int w, int h) {
		g.fillPolygon(new int[]{x, x+(w/2), x+w}, new int[]{y, y+w, y}, 3);
	}
		
	private static Color color(String rgb) {
		if (rgb==null || rgb.trim().isEmpty()) throw new IllegalArgumentException("Color String was empty.");
		rgb = rgb.trim();
		if (rgb.startsWith("#")) rgb = rgb.substring(1);
		if (rgb.length()==3) {
			int r = Integer.valueOf(""+rgb.charAt(0)+rgb.charAt(0), 16);
			int g = Integer.valueOf(""+rgb.charAt(1)+rgb.charAt(1), 16);
			int b = Integer.valueOf(""+rgb.charAt(2)+rgb.charAt(2), 16);
			return new Color(r, g, b);
		} else if (rgb.length()==4) {
			int a = Integer.valueOf(""+rgb.charAt(0)+rgb.charAt(0), 16);
			int r = Integer.valueOf(""+rgb.charAt(1)+rgb.charAt(1), 16);
			int g = Integer.valueOf(""+rgb.charAt(2)+rgb.charAt(2), 16);
			int b = Integer.valueOf(""+rgb.charAt(3)+rgb.charAt(3), 16);
			return new Color(r, g, b, a);
		} else if (rgb.length()==6) {
			int r = Integer.valueOf(""+rgb.charAt(0)+rgb.charAt(1), 16);
			int g = Integer.valueOf(""+rgb.charAt(2)+rgb.charAt(3), 16);
			int b = Integer.valueOf(""+rgb.charAt(4)+rgb.charAt(5), 16);
			return new Color(r, g, b);
		} else if (rgb.length()==8) {
			int a = Integer.valueOf(""+rgb.charAt(0)+rgb.charAt(1), 16);
			int r = Integer.valueOf(""+rgb.charAt(2)+rgb.charAt(3), 16);
			int g = Integer.valueOf(""+rgb.charAt(4)+rgb.charAt(5), 16);
			int b = Integer.valueOf(""+rgb.charAt(6)+rgb.charAt(7), 16);
			return new Color(r, g, b, a);
		} else {
			throw new IllegalArgumentException("Color must be 3 or 6 characters");
		}
	}
	
	private static boolean isLight(Color c) {
		double lum = (c.getRed() + c.getGreen() + c.getBlue()) / (255.0*3.0);
		return lum>0.5;
	}
	
	//Gets the grayscale intensity of this color - this isn't perceptual luminance, just the average strength of the unweighted RGB components
	private static double getIntensity(Color c) {
		double r = c.getRed()/255.0;
		double g = c.getGreen()/255.0;
		double b = c.getBlue()/255.0;
		
		return (r+g+b)/3;
	}
	
	private static Color scale(Color c, double intensity) {
		double r = c.getRed()/255.0;
		double g = c.getGreen()/255.0;
		double b = c.getBlue()/255.0;
		double oldIntensity = (r+g+b)/3.0;
		
		//Convert to new intensity
		double conversion = intensity/oldIntensity;
		r *= conversion;
		g *= conversion;
		b *= conversion;
		
		//Scale and clamp
		r *= 255; if (r>255) r=255; if (r<0) r=0;
		g *= 255; if (g>255) g=255; if (g<0) g=0;
		b *= 255; if (b>255) b=255; if (b<0) b=0;
		
		return new Color((int) r, (int) g, (int) b);
	}
}

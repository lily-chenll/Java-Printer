import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.print.*;
import java.io.*;

/**
* @author Chen Lili
* this code aims at making .in file to be printed into .pdf
* every page will show 54 lines
*/


public class PrintTest implements Printable, ActionListener{ 
	
	//the line width of the paper
	public static int lineWidth = 450;
	//the path of the file you want to print
	public static File source =  new File("./printer/0.in");
  
  /**
  * GraphicS is the grafic environment for printing
  * PageFormat points out the format of the printing page
  * pageIndex is the page which is going to be printed
  */
    public int print(Graphics gra, PageFormat pf, int pageIndex) throws PrinterException { 
		//transfer into Graphics2D 
		Graphics2D g2 = (Graphics2D) gra; 
		//the print color is blank
		g2.setColor(Color.black); 
		//set the attributes about the font
		Font font = new Font("Serif", Font.PLAIN, 9);
		g2.setFont(font);
		
		//set the format of the printing paper
		Paper paper = pf.getPaper();
		paper.setSize(590,842);
		paper.setImageableArea(10, 10, 590, 842);
		pf.setPaper(paper);
	
		//ensure the boundary of the page
		g2.translate(pf.getImageableX(), pf.getImageableY());
		
		//deal with the file, store its data in the buffer
		BufferedReader reader = null;
		int pa = 0;
		try {
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(source));
			//allocate 2m memory for the file
			reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 2*1024*1024);
			//get the number of all pages in the file
			pa = getPages(g2, reader);	
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		if(pageIndex >= pa) {
			return NO_SUCH_PAGE;
		}
		
		String s = getText(pa, g2, source)[pageIndex];	
		String dText = "";
		
		float ascent = 16; //the line height of the paper
		int k = 0;
		int i = font.getSize();
		int lines = 0;
		//print the content of content[pageIndex]
		while(s.length() > 0 && lines < 54) {
			k = s.indexOf('\n');
			if(k != -1) {
				lines++;
				dText = s.substring(0, k);
				g2.drawString(dText, 0, ascent);
				if(s.substring(k + 1).length() > 0) {
					s = s.substring(k + 1);
					ascent += i;
				}
			} else {
				lines++;
				dText = s;
				g2.drawString(dText, 0, ascent);
				s="";
			}
			dText = null;
		}
		return PAGE_EXISTS;
    } 
	
	/**
	* this method get the number of all pages in the file
	* the lind width is 450
	* the number of lines in each page is 54
	*/
	public int getPages(Graphics2D g, BufferedReader r) {
		int p = 0;
		try {			
			String l = "";
			FontMetrics m = g.getFontMetrics();
			
			int count = 0;
			while((l = r.readLine()) != null) {			
				if(m.stringWidth(l) <= lineWidth) {
					count++;
				} else {
					String[] words = l.split("\\s+");
					if(words.length == 0) {
						count++;
					} else {
						String currentLine = words[0];
						for(int i = 1; i < words.length; i++) {
							if(m.stringWidth(currentLine + "\t" + words[i]) <= lineWidth) {
								currentLine = currentLine + "\t" + words[i];
							} else {
								currentLine = words[i];
								count++;
							}
						}	
						count++;
					}				
				}				
			}
			if(count > 0)
				p = count / 54 + 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
		
	}

    /**
	* this method make the input file to be transfered into arraies of String
	*/
	public String[] getText(int PAGES, Graphics2D g, File s) {
		FontMetrics m = g.getFontMetrics();
		String[] drawText = new String[PAGES];
		for(int i = 0; i <PAGES; i++) 
			drawText[i] = "";
		try {			
			int count = 0;
			int page = 0;

			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(s));
			//allocate 2m memory for the file
			BufferedReader re = new BufferedReader(new InputStreamReader(fis, "utf-8"), 2*1024*1024);
			
			String line = null;
			String currentLine = null;
			while((line = re.readLine()) != null) {
				if(count < 54) {
					if(m.stringWidth(line) <= lineWidth) {
						count++;
						drawText[page] = drawText[page] + line + '\n';
					} else {
						String[] words = line.split("\\s+");	//split by all blanks					
						if(words.length == 0) {//a long word
							count++;
							drawText[page] = drawText[page] + line + '\n';
						} else {// a sentence
							//count++;
							currentLine = words[0];
							for(int i = 1; i < words.length; i++)  {
								if(m.stringWidth(currentLine + "\t" + words[i]) <= lineWidth) {
									currentLine = currentLine + "\t" + words[i];
								} else {
									if(count < 54)
										count++;
									else {
										count = 0;
										page++;
										count++;
									}
									drawText[page] = drawText[page] + currentLine + '\n';
									currentLine = words[i];
								}
							}
							if(count < 54)
								count++;
							else {
								count = 0;
								page++;	
								count++;								
							}
							drawText[page] = drawText[page] + currentLine + '\n';
							
						}	
					}
				} else {
					page++;
					count = 0;	
					//the same method as the former
					if(m.stringWidth(line) <= lineWidth) {
						count++;
						drawText[page] = drawText[page] + line + '\n';
					} else {
						String[] words = line.split("\\s+");
						if(words.length == 0) {
							count++;
							drawText[page] = drawText[page] + line + '\n';
						} else {
							//count++;
							currentLine = words[0];
							for(int i = 1; i < words.length; i++)  {
								if(m.stringWidth(currentLine + "\t" + words[i]) <= lineWidth) {
									currentLine = currentLine + "\t" + words[i];
								} else {
									if(count < 54)
										count++;
									else {
										count = 0;
										page++;
										count++;
									}
									drawText[page] = drawText[page] + currentLine + '\n';
									currentLine = words[i];
								}
							}
							if(count < 54)
								count++;
							else {
								count = 0;
								page++;
								count++;
							}
							drawText[page] = drawText[page] + currentLine + '\n';
						}	
					}								
				}
				currentLine = null;
			}
			re.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawText;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		PrinterJob job = PrinterJob.getPrinterJob();     
		
		job.setPrintable(new PrintTest());
		try { 			
			 boolean a = job.printDialog(); 
			 if(a) {       
				job.print(); 
			 } else {
				job.close();
			 }
		 } catch (PrinterException ex) { 
			 ex.printStackTrace(); 
			 /* The job did not successfully complete */
		 } 
		 
	}


	public static void main(String[] args) { 
	   
	   try {
            String cn = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(cn); // Use the native L&F
        } catch (Exception cnf) {
        }
		JFrame f = new JFrame("Printing Example");
        f.addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        JButton printButton = new JButton("Print Pages");
        printButton.addActionListener(new PrintTest());
        f.add("Center", printButton);
        f.pack();
        f.setVisible(true); 
	} 
} 
package reader;

import java.awt.Color;

import javax.swing.text.AttributeSet;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JButton;

import java.awt.BorderLayout;

import javax.swing.JComboBox;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JRadioButtonMenuItem;

import java.awt.CardLayout;

import javax.swing.JTextField;

import org.jsoup.parser.Parser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.JRadioButton;
import javax.swing.border.MatteBorder;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.net.ftp.*;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
/**
 * 
 * @author mattl_000
 *
 */
public class AnnotatedTextReader {
	
	/**
	 * Initialize JSwing Objects
	 */
	
	 
	Date startTime;
	Date endTime;
	private  JFrame frame = new JFrame("Text");
	JTextPane textPane = new JTextPane();
	JCheckBoxMenuItem annotationCheckBox;
	JCheckBoxMenuItem wordCheckBox;
	JRadioButtonMenuItem arrowItemOn;
	JRadioButtonMenuItem arrowItemOff;
	JPanel editPanel;
	final JFileChooser fileChooser = new JFileChooser();
	final JButton btnNewButton = new JButton("Next");
	ArrayList<Integer> previouslyFoundIndex;
	ArrayList<DefaultStyledDocument> loadedDocumentList = new ArrayList<DefaultStyledDocument>();
	JLabel sectionLabel = new JLabel();
	ButtonGroup highlightGroup = new ButtonGroup();
	JRadioButtonMenuItem radioButtonYellow = new JRadioButtonMenuItem("Yellow");
	JRadioButton rdBtnBoldOn = new JRadioButton("On");
	final CardLayout cards = new CardLayout(0,0);
	javax.swing.text.DefaultHighlighter.DefaultHighlightPainter highlightPainterYellow =  new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
	javax.swing.text.DefaultHighlighter.DefaultHighlightPainter highlightPainterGrey = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(Color.lightGray);
	int previousCount;
	static final int MAXVALUE =2;
	static final int MINVALUE = -2;
	static final int INTRO_LAYOUT = 1;
	static final int TEXT_LAYOUT = 2;
	static final int BLANK_SPACE = 5;
	static  int number_layout = INTRO_LAYOUT;
	final int maxLineCount = 200;
	final int maxCharacterCount = 10000;
	static int annotationCountPerDocument=350;
	static String sessionFileName;
	static String ftpUserName="*************";
	static String ftpPassword="*************";
	static String ftpAddress="**************";
	int index =0;
	
	//Defines current location in document
	int documentLocation =0;
	int documentCount=0;
	int fontSize = 15;
	String idNumber;
	FileWriter writer;
	int existCount=0;
	JLabel sectionlabel2;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					
					AnnotatedTextReader window = new AnnotatedTextReader();
					window.frame.setVisible(true);
					window.frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the application.
	 * @throws SftpException 
	 */
	public AnnotatedTextReader(){
		
		
		initialize();
	}
	
	

	/**
	 * Finds next annotation and highlights
	 * @param textComp
	 * @throws BadLocationException
	 */
	

	
	
	public void findNext(JTextPane textComp) throws BadLocationException{
			
		 
                	 
                 
			//Symbol To be found// 
			String find = "< >";
			Document document = textComp.getDocument();
			boolean found = false;
		 
			//Search for next annotation
			while(found==false && index<document.getLength()){
				String match = document.getText(index, find.length());
			 
					//Return true if symbol found
					if(find.equals(match)){
				 	found = true;
			 		}
			 
			 //
			 if(index>=(document.getLength()-2)){
				
				 	if(documentCount<loadedDocumentList.size()-1){
					 		DefaultStyledDocument nextDocument= new DefaultStyledDocument(); 
					 		documentCount++;
					 		nextDocument = loadedDocumentList.get(documentCount);
					 		SimpleAttributeSet setA = new SimpleAttributeSet();
					 		StyleConstants.setFontSize(setA, fontSize);
					 		StyleConstants.setFontFamily(setA, "Serif");
					 		nextDocument.setCharacterAttributes(0, nextDocument.getLength(), setA, true);
					 		//Reset Index
					 		index=0;
					 		try {
					 				writer.write(textPane.getDocument().getText(0, textPane.getDocument().getLength()));
					 				} catch (IOException e) {

					 					e.printStackTrace();
					 				}
					 		textPane.setDocument(nextDocument);
					 		previouslyFoundIndex.clear();
					 		previouslyFoundIndex.add(101);
					 		findNext(textPane);
					 		previousCount=0;
		 
				 	}else{
				 		//Write Section to document if no more annotations remain//
				 			try {
				 					writer.write(textPane.getDocument().getText(0, textPane.getDocument().getLength()));
									writer.write(this.stringDivider("End of Item 7"));
				 					endTime = new Date();
				 					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
				 					writer.write("<!-- Experiment taken on "+simpleDateFormat.format(startTime)+" --!>\n");
				 					writer.write("<!-- Time spent on experiment: "+this.printDifference(startTime, endTime)+"--!> ");
				 					writer.close();
				 			} catch (IOException e) {
					
				 				e.printStackTrace();
				 			}
				 			JOptionPane.showMessageDialog(frame,
				 					"No more sections remain. Thank you for your participation. Please wait while file uploads and do not exit."); 
			 			 
				 			//Delete associated files
				 			File file = new File("extractedText.txt");
				 			file.delete();
				 			File file2 = new File("extractedText_ant.txt");
				 			file2.delete();
				 			//Create file to be uploaded
				 			File uploadedFile = new File(AnnotatedTextReader.sessionFileName);
				 			
				 						
				 						try{SFTP(uploadedFile);
				 							//JOptionPane.showMessageDialog(frame,
				 							//		"File could not uploaded. File has been saved locally. Please notify instructor."); 
				 						}catch(JSchException e){
				 							JOptionPane.showMessageDialog(frame,
				 									"Error uploading file. File has been saved locally. Please notify instructor."); 
				 							
				 						}
				 						
				 							
				 					//Exit Application
				 					this.cleanUpFiles();
				 					System.exit(0);
				 				
				 
				 	}
				 	return;
			 }
			 
			 //Increase Search Index
			 index++;
		 }
		 
		  //Add Index To Previous Array
		   previouslyFoundIndex.add(index);
		   this.unboldSurroundingText(textPane);
		   //Increment number of Annotations Found
		   previousCount++;
			char check = 'e';
		   int spaceIndex = index-1;
			while(check!=' '){
				check = textComp.getText().charAt(spaceIndex);
				spaceIndex--;
			}
			
		if(rdBtnBoldOn.isSelected()){
				this.boldSurroundingText(textPane,index);
			}
			
		
		if(radioButtonYellow.isSelected()){
				//Highlight Yellow
			
			
				if(annotationCheckBox.isSelected()){
				textComp.getHighlighter().addHighlight(index-1, index + 2,highlightPainterYellow);}
				if(wordCheckBox.isSelected()){
				textComp.getHighlighter().addHighlight(spaceIndex+2, index-1, highlightPainterYellow);
				}
		}
		else{
			//Highlight Grey
			
			if(annotationCheckBox.isSelected()){
			textComp.getHighlighter().addHighlight(index-1, index + 2,highlightPainterGrey);
			}
			if(wordCheckBox.isSelected()){
				textComp.getHighlighter().addHighlight(spaceIndex+2, index-1, highlightPainterGrey);
				}
		}
		
		
		
		
		
	       try{
	      //Reset View
	    	   
	       textComp.setCaretPosition(index+400);
	       }catch(Exception IllegalArgumentException){
	    	   
	       }
	}
	/**
	 * Finds previous annotation and highlights
	 * @param textPane
	 * @throws BadLocationException
	 */
	private void findPrevious(JTextPane textPane) throws BadLocationException {
		if(previousCount>0){
				char check = 'e';
				int spaceIndex = previouslyFoundIndex.get(previousCount)-1;
				while(check!=' '){
					check = textPane.getText().charAt(spaceIndex);
					spaceIndex--;
				}
				if(previousCount==previouslyFoundIndex.size()-1){
					
					int position = textPane.getCaretPosition();
					this.unboldSurroundingText(textPane);
					textPane.setCaretPosition(position+50);

					
					
					if(rdBtnBoldOn.isSelected()){
						
						this.boldSurroundingText(textPane, previouslyFoundIndex.get(previousCount)-1);
					}
			
				
					if(radioButtonYellow.isSelected()){
				
						if(annotationCheckBox.isSelected()){
							textPane.getHighlighter().addHighlight(previouslyFoundIndex.get(previousCount)-1, previouslyFoundIndex.get(previousCount)+2, highlightPainterYellow);
						}
						if(wordCheckBox.isSelected()){
							
							textPane.getHighlighter().addHighlight(spaceIndex+2, previouslyFoundIndex.get(previousCount)-1, highlightPainterYellow);
			 
						}
					}
					else{
			 		
						if(annotationCheckBox.isSelected()){
				 			textPane.getHighlighter().addHighlight(previouslyFoundIndex.get(previousCount)-1, previouslyFoundIndex.get(previousCount)+2, highlightPainterGrey);
				 		}
						if(wordCheckBox.isSelected()){
				 			textPane.getHighlighter().addHighlight(spaceIndex+2, previouslyFoundIndex.get(previousCount)-1, highlightPainterGrey);
						}
			 		}
				}else{
							int position = textPane.getCaretPosition();
							this.unboldSurroundingText(textPane);
							textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)-200);
							
							
							if(rdBtnBoldOn.isSelected()){
								
								this.boldSurroundingText(textPane, previouslyFoundIndex.get(previousCount)-1);
								
							}
		 		
					 		
					 		if(radioButtonYellow.isSelected()){
								if(annotationCheckBox.isSelected()){
						 		textPane.getHighlighter().addHighlight(previouslyFoundIndex.get(previousCount)-1, previouslyFoundIndex.get(previousCount)+4, highlightPainterYellow);
								}
								if(wordCheckBox.isSelected()){
						 		textPane.getHighlighter().addHighlight(spaceIndex+2, previouslyFoundIndex.get(previousCount)-1, highlightPainterYellow);
								}
					 		}else{
					 			if(annotationCheckBox.isSelected()){
							 textPane.getHighlighter().addHighlight(previouslyFoundIndex.get(previousCount)-1, previouslyFoundIndex.get(previousCount)+4, highlightPainterGrey);
					 			}
					 			if(wordCheckBox.isSelected()){
							 textPane.getHighlighter().addHighlight(spaceIndex+2, previouslyFoundIndex.get(previousCount)-1, highlightPainterGrey);
					 			}
						 }
				}
			previousCount--;
		}else{
			char check = 'e';
			   int spaceIndex = previouslyFoundIndex.get(previousCount+1)-1;
				while(check!=' '){
					check = textPane.getText().charAt(spaceIndex);
					spaceIndex--;
				}
				
			 JOptionPane.showMessageDialog(frame,
                     "Sorry, there are no previous items."); 
			 
			 if(radioButtonYellow.isSelected()){
				 if(annotationCheckBox.isSelected()){
				 textPane.getHighlighter().addHighlight(previouslyFoundIndex.get(previousCount+1)-1, previouslyFoundIndex.get(previousCount+1)+4, highlightPainterYellow);
				 }
				 if(wordCheckBox.isSelected()){
				 		textPane.getHighlighter().addHighlight(spaceIndex+2, previouslyFoundIndex.get(previousCount+1)-1, highlightPainterYellow);
						}
				 	}else{
				 if(annotationCheckBox.isSelected()){		
				 textPane.getHighlighter().addHighlight(previouslyFoundIndex.get(previousCount+1)-1, previouslyFoundIndex.get(previousCount+1)+4, highlightPainterGrey);
				 }
				 if(wordCheckBox.isSelected()){
				 		textPane.getHighlighter().addHighlight(spaceIndex+2, previouslyFoundIndex.get(previousCount+1)-1, highlightPainterGrey);
						}
				 
			 }
				 textPane.setCaretPosition(previouslyFoundIndex.get(previousCount+1)-200);
		}
	}
	

	
	
	
	/**
	 *  Removes previously highlighted annotation
	 * @param textComp
	 * @throws BadLocationException
	 */
	

	public void deSelectAnnotation(JTextPane textComp) throws BadLocationException{
	       textComp.getHighlighter().removeAllHighlights();
	}
	

	/**
	 * Reads in File with readFile pathname
	 * @param readFile
	 * @return
	 */
	public String readDocument(String readFile){
		
		BufferedReader br = null; 
		try {
			String currentLine;
			String totalText="";
			br = new BufferedReader(new FileReader(readFile));
 
			while ((currentLine = br.readLine()) != null) {
					totalText = totalText+" "+currentLine;
			}
			return totalText;
			} catch (IOException e) {
					e.printStackTrace();
						} finally {
							try {	
									if (br != null)br.close();
										} catch (IOException ex) {
												ex.printStackTrace();
												}
								}
			return "Problem with document reader";
			}
	
	class documentReadIn extends SwingWorker<ArrayList<DefaultStyledDocument>, DefaultStyledDocument>{
		
		private File annotatedText;
		public documentReadIn(File text){
			this.annotatedText=text;
		
		
		}
		@Override
		protected ArrayList<DefaultStyledDocument> doInBackground() throws Exception {
			BufferedReader br = null; 
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setLineSpacing(attributes, 1);
			ArrayList<DefaultStyledDocument> documentList= new ArrayList<DefaultStyledDocument>();
			
			try {
				String currentLine;
				String totalText="";
				br = new BufferedReader(new FileReader(annotatedText));
				int count =0;
				while ((currentLine = br.readLine()) != null) {
						totalText = totalText+" "+currentLine;
						count++;
						if(count>=maxLineCount){
						DefaultStyledDocument doc = new DefaultStyledDocument();
						doc.insertString(doc.getLength(), totalText,attributes);
						documentList.add(doc);
						totalText="";
						count=0;
						if(documentList.size()==1){
							publish(doc);
						}
						}
				}
				DefaultStyledDocument doc = new DefaultStyledDocument();
				doc.insertString(doc.getLength(), totalText,attributes);
				documentList.add(doc);
			//	return totalText;
				} catch (IOException e) {
						e.printStackTrace();
							} finally {
								try {	
										if (br != null)br.close();
											} catch (IOException ex) {
													ex.printStackTrace();
													}
									}
		//		return "Problem with document reader";
				
			return documentList;
		}
		@Override
		protected void process(List<DefaultStyledDocument> documentList){
			DefaultStyledDocument docFirst = documentList.get(documentList.size()-1);
			SimpleAttributeSet setA = new SimpleAttributeSet();
			StyleConstants.setFontSize(setA, fontSize);
			StyleConstants.setFontFamily(setA, "Serif");
			docFirst.setCharacterAttributes(0, docFirst.getLength(), setA, true);
			textPane.setStyledDocument(docFirst);
		
			
			try {
				findNext(textPane);
				previousCount=0;
			} catch (BadLocationException e1) {
				 JOptionPane.showMessageDialog(frame,
                         "No more annotations found, please save to text file"); 
				
			}
			
			
		}
		
	}
	
	
	
	
	
	
	/**
	 * Initialize application
	 * @throws SftpException 
	 */
	void addKeyBinding(JComponent jc) {
        jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0, false), "1 pressed");
        jc.getActionMap().put("1 pressed", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent ae) {
            	 if(previousCount<(previouslyFoundIndex.size()-2)){
					 
					 
					 try {
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
							
							//Negative/Negative
							doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
							doc.insertString(previouslyFoundIndex.get(previousCount+1), "-2",sas);
							
						}
						
						else{
							doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
							doc.insertString(previouslyFoundIndex.get(previousCount+1), "-2",sas);
							index++;

							for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
								
								previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)+1);
								}										
							}
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				 
				 previousCount=previousCount+2;
				try {
					deSelectAnnotation(textPane);
					findPrevious(textPane);
					textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
					
				} catch (BadLocationException e1) {
					e1.printStackTrace();
					
				}
			}else{
				
					
					Document doc =textPane.getDocument();
					SimpleAttributeSet sas = new SimpleAttributeSet();
					try {
						doc.insertString(index, "-2", sas);
							} catch (BadLocationException e2) {
										e2.printStackTrace();
										}
					try {
					deSelectAnnotation(textPane);
						} catch (BadLocationException e1) {
									e1.printStackTrace();
									}
					try {
					findNext(textPane);
						} catch (BadLocationException e1) {
						e1.printStackTrace();
							}
				
				}
            }
        });

        jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0, true), "2 released");
        jc.getActionMap().put("2 released", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent ae) {
            	 if(previousCount<(previouslyFoundIndex.size()-2)){
					 
					 
					 try {
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
							
							//Negative/Negative
							doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
							doc.insertString(previouslyFoundIndex.get(previousCount+1), "-1",sas);
							
						}
						
						else{
							doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
							doc.insertString(previouslyFoundIndex.get(previousCount+1), "-1",sas);
							index++;

							for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
								
								previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)+1);
								}										
							}
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				 
				 previousCount=previousCount+2;
				try {
					deSelectAnnotation(textPane);
					findPrevious(textPane);
					textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
					
				} catch (BadLocationException e1) {
					e1.printStackTrace();
					
				}
			}else{
				
					
					Document doc =textPane.getDocument();
					SimpleAttributeSet sas = new SimpleAttributeSet();
					try {
						doc.insertString(index, "-1", sas);
							} catch (BadLocationException e2) {
										e2.printStackTrace();
										}
					try {
					deSelectAnnotation(textPane);
						} catch (BadLocationException e1) {
									e1.printStackTrace();
									}
					try {
					findNext(textPane);
						} catch (BadLocationException e1) {
						e1.printStackTrace();
							}
				
				}
            }});
            
            jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0, true), "3 released");
            jc.getActionMap().put("3 released", new AbstractAction() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public void actionPerformed(ActionEvent ae) {
                	 if(previousCount<(previouslyFoundIndex.size()-2)){
                		 
						 try {
							Document doc =textPane.getDocument();
							SimpleAttributeSet sas = new SimpleAttributeSet();
							if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
															
									doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "0",sas);
									index--;
									for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
										
										previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)-1);
										}
								
								
							}else{
									//Positive/Positive
									doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "0",sas);
							
							}

						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					 
					 previousCount=previousCount+2;
					try {
						deSelectAnnotation(textPane);
						findPrevious(textPane);
						textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
						
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						
					}
				}else{
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						try {
							doc.insertString(index, "0", sas);
								} catch (BadLocationException e2) {
											e2.printStackTrace();
											}
						try {
						deSelectAnnotation(textPane);
							} catch (BadLocationException e1) {
										e1.printStackTrace();
										}
						try {
						findNext(textPane);
							} catch (BadLocationException e1) {
							e1.printStackTrace();
								}
					}
			                }
        });
            
            jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_4, 0, true), "4 released");
            jc.getActionMap().put("4 released", new AbstractAction() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public void actionPerformed(ActionEvent ae) {
                	 if(previousCount<(previouslyFoundIndex.size()-2)){
                		 
						 try {
							Document doc =textPane.getDocument();
							SimpleAttributeSet sas = new SimpleAttributeSet();
							if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
															
									doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "1",sas);
									index--;
									for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
										
										previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)-1);
										}
								
								
							}else{
									//Positive/Positive
									doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "1",sas);
							
							}

						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					 
					 previousCount=previousCount+2;
					try {
						deSelectAnnotation(textPane);
						findPrevious(textPane);
						textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
						
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						
					}
				}else{
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						try {
							doc.insertString(index, "1", sas);
								} catch (BadLocationException e2) {
											e2.printStackTrace();
											}
						try {
						deSelectAnnotation(textPane);
							} catch (BadLocationException e1) {
										e1.printStackTrace();
										}
						try {
						findNext(textPane);
							} catch (BadLocationException e1) {
							e1.printStackTrace();
								}
					}
			
                }
        });
            
            jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_5, 0, true), "5 released");
            jc.getActionMap().put("5 released", new AbstractAction() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public void actionPerformed(ActionEvent ae) {
                	 if(previousCount<(previouslyFoundIndex.size()-2)){
                		 
						 try {
							Document doc =textPane.getDocument();
							SimpleAttributeSet sas = new SimpleAttributeSet();
							if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
															
									doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "2",sas);
									index--;
									for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
										
										previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)-1);
										}
								
								
							}else{
									//Positive/Positive
									doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "2",sas);
							
							}

						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					 
					 previousCount=previousCount+2;
					try {
						deSelectAnnotation(textPane);
						findPrevious(textPane);
						textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
						
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						
					}
				}else{
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						try {
							doc.insertString(index, "2", sas);
								} catch (BadLocationException e2) {
											e2.printStackTrace();
											}
						try {
						deSelectAnnotation(textPane);
							} catch (BadLocationException e1) {
										e1.printStackTrace();
										}
						try {
						findNext(textPane);
							} catch (BadLocationException e1) {
							e1.printStackTrace();
								}
					
			}
                }
        });
            
            jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "Right released");
            jc.getActionMap().put("Right released", new AbstractAction() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public void actionPerformed(ActionEvent ae) {
                	 if(previousCount<(previouslyFoundIndex.size()-2)){
    					 previousCount=previousCount+2;
    						try {
    							deSelectAnnotation(textPane);
    							findPrevious(textPane);
    							textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
    							
    						} catch (BadLocationException e1) {
    							e1.printStackTrace();
    							
    						} 
    				
    					 
    				 }else{
    					 	try {
								String currentCase = textPane.getText(index,2);
								if(!currentCase.equals(" >")){
									findNext(textPane);
								}else{
									
		    						JOptionPane.showMessageDialog(frame, "Please select a numeric value for current annotation.");

									
								}
							} catch (BadLocationException e) {
								e.printStackTrace();
							}

    					
    				 }
                }
        });
            jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "Left released");
            jc.getActionMap().put("Left released", new AbstractAction() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public void actionPerformed(ActionEvent ae) {
                	
    					try {
    						deSelectAnnotation(textPane);
    						
    					} catch (BadLocationException e2) {
    						e2.printStackTrace();
    					}
    					try {
    						findPrevious(textPane);

    					} catch (BadLocationException e1) {
    						e1.printStackTrace();
    					}
    					
    				
                }
        });
            
            jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
            jc.getActionMap().put("Down released", new AbstractAction() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public void actionPerformed(ActionEvent ae) {
					if(arrowItemOn.isSelected()){
								Document doc =textPane.getDocument();
								//If selection is previous
								if(previousCount<(previouslyFoundIndex.size()-2)){
										
										
									 	try {
									 		
									 		
									 		String currentValue = textPane.getText(previouslyFoundIndex.get(previousCount+1), 2);
											SimpleAttributeSet a = new SimpleAttributeSet();
											StyleConstants.setFontSize(a, fontSize);
											StyleConstants.setFontFamily(a, "Serif");
											StyleConstants.setBold(a,true);
											
											switch(currentValue){
											 
											 case "0 ": 
											 		doc.remove(previouslyFoundIndex.get(previousCount+1),2);
											 		doc.insertString(previouslyFoundIndex.get(previousCount+1), "-1 ", a);
											 		index++;
											 		
											 		for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
														
														previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)+1);
														}
											 		break;
											 case "-1":
											 		doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
											 		doc.insertString(previouslyFoundIndex.get(previousCount+1),"-2",a);
											 		break;
											 case "1 ":
											 		doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
											 		doc.insertString(previouslyFoundIndex.get(previousCount+1),"0",a);
											 		break;
											 case "2 ":
											 		doc.remove(previouslyFoundIndex.get(previousCount+1),1);
											 		doc.insertString(previouslyFoundIndex.get(previousCount+1),"1",a);
											 		break;
											 default:
											 
											 }
									
									 	
									 	} catch (BadLocationException e) {
											e.printStackTrace();
										}
									 	
								 }
								//Current Selection
								else{
									 try {
										
										 SimpleAttributeSet a = new SimpleAttributeSet();
										 StyleConstants.setFontSize(a, fontSize);
										 StyleConstants.setFontFamily(a, "Serif");
										 StyleConstants.setBold(a,true);
										 
										 String currentValue = textPane.getText(index, 2);
										 
										 switch(currentValue){
										 
										 case " >":
													doc.insertString(index, "0", a);
											 		break;
										 case "0 ": 
											 		doc.remove(index,2);
											 		doc.insertString(index, "-1 ", a);
											 		break;
										 case "-1":
											 		doc.remove(index, 2);
											 		doc.insertString(index,"-2",a);
											 		break;
										 case "1 ":
											 		doc.remove(index, 1);
											 		doc.insertString(index,"0",a);
											 		break;
										 case "2 ":
											 		doc.remove(index,1);
											 		doc.insertString(index,"1",a);
											 		break;
										 default: 
											 	
										 }
									
										
									} catch (BadLocationException e) {
			
										e.printStackTrace();
									}
								
			                	} 
							}
				}  });
            
            jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "Up released");
            jc.getActionMap().put("Up released", new AbstractAction() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public void actionPerformed(ActionEvent ae) {
                
					if(arrowItemOn.isSelected()){
    					
								Document doc =textPane.getDocument();
								 SimpleAttributeSet a = new SimpleAttributeSet();
								StyleConstants.setFontSize(a, fontSize);
								StyleConstants.setFontFamily(a, "Serif");
								 StyleConstants.setBold(a,true);
								//If selection is previous
								if(previousCount<(previouslyFoundIndex.size()-2)){
										
										//PreviousTest
									 	try {
									 		String currentValue = textPane.getText(previouslyFoundIndex.get(previousCount+1), 2);
											 switch(currentValue){
											 
											
											 case "0 ": 
												 		doc.remove(previouslyFoundIndex.get(previousCount+1),1);
												 		doc.insertString(previouslyFoundIndex.get(previousCount+1), "1", a);
												 		break;
											 case "-1":
												 		doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
												 		doc.insertString(previouslyFoundIndex.get(previousCount+1),"0",a);
												 		index--;
												 		for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
															
															previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)-1);
															}
												 		break;
											 case "1 ":
												 		doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
												 		doc.insertString(previouslyFoundIndex.get(previousCount+1),"2",a);
												 		break;
											 case "-2":
												 		doc.remove(previouslyFoundIndex.get(previousCount+1),2);
												 		doc.insertString(previouslyFoundIndex.get(previousCount+1),"-1",a);
												 		break;
											 default: 
											 
											 
											 }
									
									
									 	
									 	} catch (BadLocationException e) {
											e.printStackTrace();
										} 
									 	
								 }
								//Current Selection
								else{
									 try {
										
										 
										 
										 String currentValue = textPane.getText(index, 2);
										 
										 switch(currentValue){
										 
										 case " >":
													doc.insertString(index, "0", a);
											 		break;
										 case "0 ": 
											 		doc.remove(index,1);
											 		doc.insertString(index, "1", a);
											 		break;
										 case "-1":
											 		doc.remove(index, 2);
											 		doc.insertString(index,"0",a);
											 		break;
										 case "1 ":
											 		doc.remove(index, 1);
											 		doc.insertString(index,"2",a);
											 		break;
										 case "-2":
											 		doc.remove(index,2);
											 		doc.insertString(index,"-1",a);
											 		break;
										 default: 
										 
										 }
									
										
									} catch (BadLocationException e) {
			
										e.printStackTrace();
									}
			
									 
									 
			                	} 
			
			
			    				
			                }
            		} });
            
            
  

}
	@SuppressWarnings("unchecked")
	private void initialize() {
		previouslyFoundIndex = new ArrayList<Integer>();
		previouslyFoundIndex.add(101);
		frame = new JFrame("CompLing DGS");
		frame.setFont(new Font("Dialog", Font.BOLD, 12));
		frame.setBackground(new Color(165, 42, 42));
		frame.setBounds(100, 100, 924, 690);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(e.getID()==WindowEvent.WINDOW_CLOSING){
				
					try {
						exitApplication();
					
					} catch (BadLocationException e1) {

						e1.printStackTrace();
					}
						
				}
			}
		});
		@SuppressWarnings("rawtypes")
		Vector comboBoxItems=new Vector();
		comboBoxItems.add("--");
		for(int i=MINVALUE;i<=MAXVALUE;i++){
			comboBoxItems.add(i);
		}
	   
	    @SuppressWarnings("rawtypes")
		final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
		
		/**
		 * Adds listener to next button
		 */
		/**
		 * Add action listener to save button
		 */
		
	 
		frame.getContentPane().setLayout(cards);
		final CardLayout cards2 = new CardLayout(0,0);
		 editPanel = new JPanel(cards2);
		 
		
		
		JPanel IdentificationPanel = new JPanel();
		
		IdentificationPanel.setLayout(null);
		
		
		
	
		JPanel ApplicationPanel = new JPanel();
		addKeyBinding(ApplicationPanel);
		frame.getContentPane().add(IdentificationPanel,"Identification Panel");
		
		JPanel panel = new JPanel();
		panel.setBorder(new MatteBorder(2, 2, 2, 2, (Color) new Color(0, 0, 0)));
		panel.setBounds(211, 241, 486, 148);
		IdentificationPanel.add(panel);
		panel.setLayout(null);
		
		final JButton btnSubmit = new JButton("Submit");
		btnSubmit.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnSubmit.setBounds(169, 107, 119, 30);
		panel.add(btnSubmit);
		
		final JTextField experimentCodetextField = new JTextField(15);
		experimentCodetextField.setDocument(new JTextFieldLimit(15));
		experimentCodetextField.setBounds(169, 67, 119, 20);
		panel.add(experimentCodetextField);
		experimentCodetextField.setColumns(10);
		
		JLabel lblEnterExpe = new JLabel("Enter Experiment Code:");
		lblEnterExpe.setBounds(169, 36, 144, 20);
		panel.add(lblEnterExpe);
		
		btnSubmit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
					if(e.getSource().equals(btnSubmit)){
					
					
						
							try {
							
								startTime = new Date();	
								Hashtable<String,Integer> documentHash = DocumentHashTable.getDocumentHashTable();
								String experimentCodeParse = experimentCodetextField.getText().substring(0,6);
								if(!documentHash.containsKey(experimentCodeParse)){
									
								 	JOptionPane.showMessageDialog(frame,
					                        "That is not a valid experiment code. Please try again."); 
									
								}else{
								
								
										String newfile = experimentCodetextField.getText()+".txt";
										
										
										
										File f = new File(newfile);
										sessionFileName=newfile;
										writer = new FileWriter(newfile);							
										
										cards.next(frame.getContentPane());
										setCurrentLayoutSetting(TEXT_LAYOUT);
										readXML(documentHash.get(experimentCodeParse));
								}
							}catch(Exception ex){
								System.out.println("Problem loading file");
							}
						
					}
				
			}
			
		});
		frame.getContentPane().add(ApplicationPanel, "Application Panel");
		ApplicationPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel textPanel = new JPanel();
		ApplicationPanel.add(textPanel, BorderLayout.CENTER);
		textPanel.setPreferredSize(new Dimension(660,600));
		textPanel.setLayout(new BorderLayout());
		JScrollPane jsp = new JScrollPane();
		jsp.setPreferredSize(new Dimension(500,550));
		textPane.setEditable(false);	
		textPane.setPreferredSize(new Dimension(600,500));
		jsp.setViewportView(textPane);
		textPanel.add(jsp,BorderLayout.CENTER);
		
		ApplicationPanel.add(editPanel, BorderLayout.EAST);
		editPanel.setPreferredSize(new Dimension(200,600));
		JPanel sideComposite = new JPanel();
		
		sideComposite.setPreferredSize(new Dimension(100,100));
		editPanel.add(sideComposite, "name_76811106641207");
		sideComposite.setLayout(null);
		
		JPanel sideContainer = new JPanel();
		sideContainer.setBounds(0, 20, 200, 610);
		sideComposite.add(sideContainer);
		sideContainer.setLayout(new BorderLayout(0, 0));
		
		JPanel prevNextPanel = new JPanel();
		sideContainer.add(prevNextPanel, BorderLayout.NORTH);
		prevNextPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		final JButton btnPrevious = new JButton("Previous");
		prevNextPanel.add(btnPrevious);
		@SuppressWarnings("rawtypes")
		final JComboBox comboBox = new JComboBox(model);
		comboBox.setBounds(0, 0, 200, 20);
		sideComposite.add(comboBox);
		
		final JButton btnNext = new JButton("Next");
		prevNextPanel.add(btnNext);
		
	
		sectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sectionLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 17));
		sideContainer.add(sectionLabel, BorderLayout.CENTER);
		
		JPanel sideOption2 = new JPanel();
		editPanel.add(sideOption2, "editPanel");
		sideOption2.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(10, 30, 174, 299);
		sideOption2.add(panel_1);
		panel_1.setLayout(null);
		
		final JButton btnNext2 = new JButton("Next");
		btnNext2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnNext2.setBounds(104, 267, 60, 21);
		panel_1.add(btnNext2);
	
		
		final JButton btn2 = new JButton("2");
		btn2.setBounds(60, 56, 53, 23);
		panel_1.add(btn2);
		
		final JButton btn1 = new JButton("1");
		btn1.setBounds(60, 90, 53, 23);
		panel_1.add(btn1);
		
		final JButton btn0 = new JButton("0");
		btn0.setBounds(60, 124, 53, 23);
		panel_1.add(btn0);
		
		final JButton btnPrevious2 = new JButton("Previous");
		btnPrevious2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnPrevious2.setBounds(10, 267, 84, 21);
		panel_1.add(btnPrevious2);
		btnPrevious2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==btnPrevious2){
					try {
						deSelectAnnotation(textPane);
					} catch (BadLocationException e2) {
						e2.printStackTrace();
					}
					try {
						findPrevious(textPane);

					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					
				}}	
		});
		
	
		final JButton btnNeg1 = new JButton("-1");
		btnNeg1.setBounds(60, 158, 53, 23);
		panel_1.add(btnNeg1);
		
		final JButton btnNeg2 = new JButton("-2");
		btnNeg2.setBounds(60, 192, 53, 23);
		panel_1.add(btnNeg2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 340, 200, 290);
		sideOption2.add(panel_2);
		
		sectionlabel2 = new JLabel();
		panel_2.add(sectionlabel2);
		sectionlabel2.setHorizontalAlignment(SwingConstants.CENTER);
		sectionlabel2.setFont(new Font("Trebuchet MS", Font.PLAIN, 17));
		
	
		
				btnNeg2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(e.getSource()==btnNeg2){
							 if(previousCount<(previouslyFoundIndex.size()-2)){
								 
									 
									 try {
										Document doc =textPane.getDocument();
										SimpleAttributeSet sas = new SimpleAttributeSet();
										if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
											
											//Negative/Negative
											doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
											doc.insertString(previouslyFoundIndex.get(previousCount+1), "-2",sas);
											
										}
										
										else{
											doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
											doc.insertString(previouslyFoundIndex.get(previousCount+1), "-2",sas);
		
											for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
												
												previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)+1);
												}										
											}
									} catch (BadLocationException e1) {
										e1.printStackTrace();
									}
								 
								 previousCount=previousCount+2;
								try {
									deSelectAnnotation(textPane);
									findPrevious(textPane);
									textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
									
								} catch (BadLocationException e1) {
									e1.printStackTrace();
									
								}
								comboBox.setSelectedItem("--");
							}else{
								
									
									Document doc =textPane.getDocument();
									SimpleAttributeSet sas = new SimpleAttributeSet();
									try {
										doc.insertString(index, "-2", sas);
											} catch (BadLocationException e2) {
														e2.printStackTrace();
														}
									try {
									deSelectAnnotation(textPane);
										} catch (BadLocationException e1) {
													e1.printStackTrace();
													}
									try {
									findNext(textPane);
										} catch (BadLocationException e1) {
										e1.printStackTrace();
											}
								comboBox.setSelectedItem("--");
								
								}
						}
					}
				});
		btnNeg1.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
			if(e.getSource()==btnNeg1){
				 if(previousCount<(previouslyFoundIndex.size()-2)){
					 
						 
						 try {
							Document doc =textPane.getDocument();
							SimpleAttributeSet sas = new SimpleAttributeSet();
							if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
								
								//Negative/Negative
								doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
								doc.insertString(previouslyFoundIndex.get(previousCount+1), "-1",sas);
								
							}
							
							else{
								doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
								doc.insertString(previouslyFoundIndex.get(previousCount+1), "-1",sas);
								index++;

								for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
									
									previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)+1);
									}										
								}
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					 
					 previousCount=previousCount+2;
					try {
						deSelectAnnotation(textPane);
						findPrevious(textPane);
						textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
						
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						
					}
					comboBox.setSelectedItem("--");
				}else{
					
						
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						try {
							doc.insertString(index, "-1", sas);
								} catch (BadLocationException e2) {
											e2.printStackTrace();
											}
						try {
						deSelectAnnotation(textPane);
							} catch (BadLocationException e1) {
										e1.printStackTrace();
										}
						try {
						findNext(textPane);
							} catch (BadLocationException e1) {
							e1.printStackTrace();
								}
					comboBox.setSelectedItem("--");
					
					}
			}
		}});
		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==btn0){
				 if(previousCount<(previouslyFoundIndex.size()-2)){
		 
						 try {
							Document doc =textPane.getDocument();
							SimpleAttributeSet sas = new SimpleAttributeSet();
							if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
															
									doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "0",sas);
									index--;
									for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
										
										previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)-1);
										}
								
								
							}else{
									//Positive/Positive
									doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "0",sas);
							
							}

						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					 
					 previousCount=previousCount+2;
					try {
						deSelectAnnotation(textPane);
						findPrevious(textPane);
						textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
						
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						
					}
					comboBox.setSelectedItem("--");
				}else{
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						try {
							doc.insertString(index, "0", sas);
								} catch (BadLocationException e2) {
											e2.printStackTrace();
											}
						try {
						deSelectAnnotation(textPane);
							} catch (BadLocationException e1) {
										e1.printStackTrace();
										}
						try {
						findNext(textPane);
							} catch (BadLocationException e1) {
							e1.printStackTrace();
								}
					comboBox.setSelectedItem("--");
					}
			}

				}
				
			})
		;
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==btn1){
				 if(previousCount<(previouslyFoundIndex.size()-2)){
		 
						 try {
							Document doc =textPane.getDocument();
							SimpleAttributeSet sas = new SimpleAttributeSet();
							if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
															
									doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "1",sas);
									index--;
									for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
										
										previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)-1);
										}
								
								
							}else{
									//Positive/Positive
									doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "1",sas);
							
							}

						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					 
					 previousCount=previousCount+2;
					try {
						deSelectAnnotation(textPane);
						findPrevious(textPane);
						textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
						
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						
					}
					comboBox.setSelectedItem("--");
				}else{
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						try {
							doc.insertString(index, "1", sas);
								} catch (BadLocationException e2) {
											e2.printStackTrace();
											}
						try {
						deSelectAnnotation(textPane);
							} catch (BadLocationException e1) {
										e1.printStackTrace();
										}
						try {
						findNext(textPane);
							} catch (BadLocationException e1) {
							e1.printStackTrace();
								}
					comboBox.setSelectedItem("--");
					}
			}

				}
				
			});
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==btn2){
				 if(previousCount<(previouslyFoundIndex.size()-2)){
		 
						 try {
							Document doc =textPane.getDocument();
							SimpleAttributeSet sas = new SimpleAttributeSet();
							if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
															
									doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "2",sas);
									index--;
									for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
										
										previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)-1);
										}
								
								
							}else{
									//Positive/Positive
									doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), "2",sas);
							
							}

						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					 
					 previousCount=previousCount+2;
					try {
						deSelectAnnotation(textPane);
						findPrevious(textPane);
						textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
						
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						
					}
					comboBox.setSelectedItem("--");
				}else{
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						try {
							doc.insertString(index, "2", sas);
								} catch (BadLocationException e2) {
											e2.printStackTrace();
											}
						try {
						deSelectAnnotation(textPane);
							} catch (BadLocationException e1) {
										e1.printStackTrace();
										}
						try {
						findNext(textPane);
							} catch (BadLocationException e1) {
							e1.printStackTrace();
								}
					comboBox.setSelectedItem("--");
					}
			}

				}
				
			});
		
		btnNext2.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==btnNext2){
				 if(previousCount<(previouslyFoundIndex.size()-2)){
					 previousCount=previousCount+2;
						try {
							deSelectAnnotation(textPane);
							findPrevious(textPane);
							textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
							
						} catch (BadLocationException e1) {
							e1.printStackTrace();
							
						} 
				
					 
				 }else{
					 
						JOptionPane.showMessageDialog(frame, "Please select a numeric value for current annotation.");

					
				 }
				 
				
				}}});
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==btnNext){
				 if(previousCount<(previouslyFoundIndex.size()-2)){
					 if(!comboBox.getSelectedItem().toString().equalsIgnoreCase("--")){
						 
						 try {
							Document doc =textPane.getDocument();
							SimpleAttributeSet sas = new SimpleAttributeSet();
							if(textPane.getText(previouslyFoundIndex.get(previousCount+1), 1).equals("-")){
								if(Integer.parseInt(comboBox.getSelectedItem().toString())>=0){								
									doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), comboBox.getSelectedItem().toString(),sas);
									index--;
									for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
										
										previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)-1);
										}
								}
								else{
								//Negative/Negative
								doc.remove(previouslyFoundIndex.get(previousCount+1), 2);
								doc.insertString(previouslyFoundIndex.get(previousCount+1), comboBox.getSelectedItem().toString(),sas);
								}
							}else{
								if(Integer.parseInt(comboBox.getSelectedItem().toString())>=0){
									//Positive/Positive
									doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
									doc.insertString(previouslyFoundIndex.get(previousCount+1), comboBox.getSelectedItem().toString(),sas);
								}
								else{
								doc.remove(previouslyFoundIndex.get(previousCount+1), 1);
								doc.insertString(previouslyFoundIndex.get(previousCount+1), comboBox.getSelectedItem().toString(),sas);
								index++;

								for(int i=previousCount+2;i<previouslyFoundIndex.size();i++){
									
									previouslyFoundIndex.set(i, previouslyFoundIndex.get(i)+1);
									}										
								}
							}

						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					 }
					 previousCount=previousCount+2;
					try {
						deSelectAnnotation(textPane);
						findPrevious(textPane);
						textPane.setCaretPosition(previouslyFoundIndex.get(previousCount)+400);
						
					} catch (BadLocationException e1) {
						e1.printStackTrace();
						
					}
					comboBox.setSelectedItem("--");
				}else{
					if(!comboBox.getSelectedItem().toString().equalsIgnoreCase("--")){
						
						Document doc =textPane.getDocument();
						SimpleAttributeSet sas = new SimpleAttributeSet();
						try {
							doc.insertString(index, comboBox.getSelectedItem().toString(), sas);
								} catch (BadLocationException e2) {
											e2.printStackTrace();
											}
						try {
						deSelectAnnotation(textPane);
							} catch (BadLocationException e1) {
										e1.printStackTrace();
										}
						try {
						findNext(textPane);
							} catch (BadLocationException e1) {
							e1.printStackTrace();
								}
					comboBox.setSelectedItem("--");
					}
						else{
								JOptionPane.showMessageDialog(frame, "Please select a numeric value.");
							
							}}
			}

				}
				
			});
		btnPrevious.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==btnPrevious){
					try {
						deSelectAnnotation(textPane);
					} catch (BadLocationException e2) {
						e2.printStackTrace();
					}
					try {
						findPrevious(textPane);

					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					comboBox.setSelectedItem("--");

					
					
				}
			}
		}
		);
		
		/**REMOVED FOR EXPERIMENT PURPOSES **/
//		JButton btnSave = new JButton("Save File");
//		btnSave.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				
//				int value =fileChooser.showSaveDialog(frame);
//				if(value ==JFileChooser.APPROVE_OPTION){
//					
//					File file = fileChooser.getSelectedFile();
//					if(!file.exists()){
//						try {
//						BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()+".txt"));
//						for(int i=0;i<loadedDocumentList.size();i++){
//							writer.write(loadedDocumentList.get(i).getText(0, loadedDocumentList.get(i).getLength()));
//						}
//						writer.close();
//						} catch (IOException e) {
//							 JOptionPane.showMessageDialog(frame,
//			                            "Problem with saving document");
//						} catch (BadLocationException e) {
//							e.printStackTrace();
//						}
//					
//					}else{
//						int confirmation = JOptionPane.showConfirmDialog(frame, "This file already exist, are you sure you would like to save?");
//						if(confirmation==0){
//							try {
//								BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()+".txt"));
//								for(int i=0;i<loadedDocumentList.size();i++){
//							
//									writer.write(loadedDocumentList.get(i).getText(0, loadedDocumentList.get(i).getLength()));
//									
//								}
//								
//								writer.close();
//								} catch (IOException e) {
//									 JOptionPane.showMessageDialog(frame,
//					                            "Problem with saving document");
//								} catch (BadLocationException e) {
//									e.printStackTrace();
//								}
//						}else{
//							JOptionPane.showMessageDialog(frame,
//		                            "File was not saved");	}
//						}
//					}
//				}
//			});
		
//				btnSave.setPreferredSize(new Dimension(100,20));
				
				JMenuBar menuBar = new JMenuBar();
				menuBar.setBackground(new Color(255, 255, 255));
				frame.setJMenuBar(menuBar);
				JMenu mnNewMenu = new JMenu("File");
				menuBar.add(mnNewMenu);
//				final JMenuItem mntmLoad = new JMenuItem("Load");
//				mnNewMenu.add(mntmLoad);
				
				final JMenuItem quitItem = new JMenuItem("Quit");
				quitItem.addActionListener(new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {
						if(e.getSource()==quitItem){
							
							try {
								exitApplication();
							} catch (BadLocationException e1) {
								System.out.println("Error in exiting");
								System.exit(0);
								e1.printStackTrace();
							}
							
						}
					}
				
				});
				mnNewMenu.add(quitItem);
				
				JMenu mnSettings_1 = new JMenu("Settings");
				menuBar.add(mnSettings_1);
				
				JMenu mnSettings = new JMenu("Font");
				mnSettings_1.add(mnSettings);
				
				
				
				final JRadioButtonMenuItem fontSmallButton = new JRadioButtonMenuItem("12 pt");
				fontSmallButton.addActionListener(new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {
						if(e.getSource().equals(fontSmallButton)){
							fontSize=12;
							String textString="";
							try {
								textString = textPane.getDocument().getText(0, textPane.getDocument().getLength());
							} catch (BadLocationException e2) {
								e2.printStackTrace();
							}
							SimpleAttributeSet setA = new SimpleAttributeSet();
							DefaultStyledDocument docFirst = new DefaultStyledDocument();
							StyleConstants.setFontSize(setA, fontSize);
							StyleConstants.setFontFamily(setA, "Serif");
							docFirst.setCharacterAttributes(0, docFirst.getLength(), setA, true);
							try {
								docFirst.insertString(0, textString, setA);
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
							textPane.setStyledDocument(docFirst);
							
							
						}
					}
				});
				
				mnSettings.add(fontSmallButton);
				
				final JRadioButtonMenuItem fontMediumButton = new JRadioButtonMenuItem("15 pt");
				mnSettings.add(fontMediumButton);
				fontMediumButton.addActionListener(new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {
						if(e.getSource().equals(fontMediumButton)){
							
							fontSize=15;
							String textString="";
							try {
								textString = textPane.getDocument().getText(0, textPane.getDocument().getLength());
							} catch (BadLocationException e2) {
								e2.printStackTrace();
							}
							SimpleAttributeSet setA = new SimpleAttributeSet();
							DefaultStyledDocument docFirst = new DefaultStyledDocument();
							StyleConstants.setFontSize(setA, fontSize);
							StyleConstants.setFontFamily(setA, "Serif");
							docFirst.setCharacterAttributes(0, docFirst.getLength(), setA, true);
							try {
								docFirst.insertString(0, textString, setA);
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
							textPane.setStyledDocument(docFirst);
							
						}
					}
				});
				fontMediumButton.setSelected(true);
				
				final JRadioButtonMenuItem fontLargeButton = new JRadioButtonMenuItem("20 pt");
				fontLargeButton.addActionListener(new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {
						if(e.getSource().equals(fontLargeButton)){
							fontSize=20;
							String textString="";
							try {
								textString = textPane.getDocument().getText(0, textPane.getDocument().getLength());
							} catch (BadLocationException e2) {
								e2.printStackTrace();
							}
							SimpleAttributeSet setA = new SimpleAttributeSet();
							DefaultStyledDocument docFirst = new DefaultStyledDocument();
							StyleConstants.setFontSize(setA, fontSize);
							StyleConstants.setFontFamily(setA, "Serif");
							docFirst.setCharacterAttributes(0, docFirst.getLength(), setA, true);
							try {
								docFirst.insertString(0, textString, setA);
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}
							textPane.setStyledDocument(docFirst);
							
						}
					}
				});
				mnSettings.add(fontLargeButton);
				ButtonGroup fontGroup = new ButtonGroup();
			
				fontGroup.add(fontLargeButton);
				fontGroup.add(fontMediumButton);
				fontGroup.add(fontSmallButton);
				
				JMenu mnDisplay = new JMenu("Display");
				mnSettings_1.add(mnDisplay);
				ButtonGroup displayGroup = new ButtonGroup();
				final JRadioButton radioBtnDrop = new JRadioButton("Default");
				radioBtnDrop.addActionListener(new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {
						
						if(e.getSource().equals(radioBtnDrop)){
							cards2.first(editPanel);
						}
						
						
					}
					
					
					
				});
				mnDisplay.add(radioBtnDrop);
				
				final JRadioButton rdbtnRadioButton = new JRadioButton("Button Rating");
				rdbtnRadioButton.addActionListener(new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {
		
						if(e.getSource().equals(rdbtnRadioButton)){
							cards2.last(editPanel);
						}
						
					}
					
				});
				
				mnDisplay.add(rdbtnRadioButton);
				displayGroup.add(radioBtnDrop);
				displayGroup.add(rdbtnRadioButton);
				radioBtnDrop.setSelected(true);
		
				JMenu highlightOptionMenu = new JMenu("Highlight Option");
				mnSettings_1.add(highlightOptionMenu);
		
				JMenu highlightMenu = new JMenu("Highlight Color");
				highlightOptionMenu.add(highlightMenu);
		
		
				highlightMenu.add(radioButtonYellow);
				radioButtonYellow.setSelected(true);
		
				JRadioButtonMenuItem radioButtonBlack = new JRadioButtonMenuItem("Greyscale");
				highlightMenu.add(radioButtonBlack);
		
				highlightGroup.add(radioButtonBlack);
				highlightGroup.add(radioButtonYellow);
				
				JMenu highlightPlacement = new JMenu("Highlight Location");
				highlightOptionMenu.add(highlightPlacement);
				
				wordCheckBox = new JCheckBoxMenuItem("Word");
				wordCheckBox.setSelected(true);
				highlightPlacement.add(wordCheckBox);
	
				
				annotationCheckBox = new JCheckBoxMenuItem("Annotation");
				annotationCheckBox.setSelected(true);;
				highlightPlacement.add(annotationCheckBox);

				/**DISABLE FOR EXPERIMENT **/
//				final ButtonGroup annotationNumber = new ButtonGroup();
				
////				JMenu numberOfAnnotations = new JMenu("Number Of Annotations");
////				mnSettings_1.add(numberOfAnnotations);
//				
//				final JRadioButtonMenuItem annotationNumber100 = new JRadioButtonMenuItem("100");
//				numberOfAnnotations.add(annotationNumber100);
//				annotationNumber.add(annotationNumber100);
//			
//				annotationNumber100.addActionListener(new ActionListener(){
//					@Override
//					public void actionPerformed(ActionEvent e) {
//
//						if(e.getSource().equals(annotationNumber100)){
//							annotationCountPerDocument=100;
//						}
//						
//					}
//					
//				});
//				
//				
//				
//				
//				final JRadioButtonMenuItem annotationNumber350 = new JRadioButtonMenuItem("350");
//				annotationNumber350.setSelected(true);
//				
//				numberOfAnnotations.add(annotationNumber350);
//				annotationNumber.add(annotationNumber350);
//		
//
//				annotationNumber350.addActionListener(new ActionListener(){
//					@Override
//					public void actionPerformed(ActionEvent e) {
//
//						if(e.getSource().equals(annotationNumber350)){
//							annotationCountPerDocument=350;
//						}
//						
//					}
//					
//				});
//				
//				
//				
//
//				
//				final JRadioButtonMenuItem annotationNumber500 = new JRadioButtonMenuItem("500");
//				numberOfAnnotations.add(annotationNumber500);
//				annotationNumber.add(annotationNumber500);
//
//				annotationNumber500.addActionListener(new ActionListener(){
//					@Override
//					public void actionPerformed(ActionEvent e) {
//
//						if(e.getSource().equals(annotationNumber500)){
//							annotationCountPerDocument=500;
//						}
//						
//					}
//					
//				});
//				
//				
//
//				
//				final JRadioButtonMenuItem annotationNumber1000 = new JRadioButtonMenuItem("1000");
//				numberOfAnnotations.add(annotationNumber1000);
//				annotationNumber.add(annotationNumber1000);
//				
//				
//				annotationNumber1000.addActionListener(new ActionListener(){
//					@Override
//					public void actionPerformed(ActionEvent e) {
//
//						if(e.getSource().equals(annotationNumber1000)){
//							annotationCountPerDocument=1000;
//						}
//						
//					}
//					
//				});
//				
//
//				
//			
//				
//				JMenuItem annotationNumberCustom = new JMenuItem("Custom...");
//				numberOfAnnotations.add(annotationNumberCustom);
				/**FTP Settings Disabled **/
	//			JMenu ftp_settings = new JMenu("FTP Settings");
	//			mnSettings_1.add(ftp_settings);			
	//			JMenuItem ftpChange = new JMenuItem("Change FTP Settings...");
	//			ftp_settings.add(ftpChange);
				//Initiate Bold Menu Options
				JMenu boldMenu = new JMenu("Bold Surrounding Words");
				mnSettings_1.add(boldMenu);
				
				rdBtnBoldOn.setSelected(true);
				boldMenu.add(rdBtnBoldOn);
				
				JRadioButton rdBtnBoldOff = new JRadioButton("Off");
				boldMenu.add(rdBtnBoldOff);
				
				final ButtonGroup boldSetting = new ButtonGroup();
				boldSetting.add(rdBtnBoldOff);
				boldSetting.add(rdBtnBoldOn);
				
				JMenu arrowRatingSetting = new JMenu("Directional Arrow Rating");
				mnSettings_1.add(arrowRatingSetting);
				
				
				final ButtonGroup arrowSetting = new ButtonGroup();
				
				arrowItemOn = new JRadioButtonMenuItem("On");
				arrowItemOn.setSelected(true);
				arrowRatingSetting.add(arrowItemOn);
				arrowSetting.add(arrowItemOn);
				
				 arrowItemOff = new JRadioButtonMenuItem("Off");
				arrowRatingSetting.add(arrowItemOff);
				arrowSetting.add(arrowItemOff);

				
	}
	/**DISABLE FOR EXPERIMENT **/
				//Custom FTP Listener
//	//			ftpChange.addActionListener(new ActionListener(){
//
//					@SuppressWarnings("deprecation")
//					@Override
//	//				public void actionPerformed(ActionEvent arg0) {
//
//						JTextField hostAddress = new JTextField(AnnotatedTextReader.ftpAddress);
//						JPasswordField name = new JPasswordField(AnnotatedTextReader.ftpUserName);
//						JTextField password = new JPasswordField(AnnotatedTextReader.ftpPassword);
//
//						Object[] message = {
//							"Host Address",hostAddress,
//						    "Username:", name,
//						    "Password:", password
//						};
//
//						int option = JOptionPane.showConfirmDialog(null, message, "Change Settings", JOptionPane.OK_CANCEL_OPTION);
//						if (option == JOptionPane.OK_OPTION) {
//								AnnotatedTextReader.ftpAddress = hostAddress.getText();
//								AnnotatedTextReader.ftpUserName = name.getText();
//								AnnotatedTextReader.ftpPassword = password.getText();
//							
//						}
//					}
//					
//				});
				
				//Custom Number Listener
//				annotationNumberCustom.addActionListener(new ActionListener(){
//
//					@Override
//					public void actionPerformed(ActionEvent arg0) {
//					
//						String output =JOptionPane.showInputDialog(frame,"Enter in number of annotations","Number of Annotations",1);
//						
//						
//						if(tryParseInt(output)==true){
//							
//							int annotationNumberInput =Integer.parseInt(output);
//							annotationCountPerDocument = annotationNumberInput;
//							annotationNumber.clearSelection();
//							
//							
//							
//							
//						}else if(output==null){
//							
//						}else{		
//							
//							JOptionPane.showMessageDialog(frame, "Sorry, but that is not a valid input.");
//						}
//						
//						
//						
//					}
//					
//				});
//
//		
//		
//	}
		
		
		/**
		 * DISABLED for Experiment usage
		 * Add listener to load button
		 */

//		mntmLoad.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if(e.getSource()==mntmLoad){
//					int value = fileChooser.showOpenDialog(frame);
//					if(value==JFileChooser.APPROVE_OPTION){
//						previouslyFoundIndex.clear();
//						 previouslyFoundIndex.add(101);
//						index=0;
//						try {
//							deSelectAnnotation(textPane);
//						} catch (BadLocationException e2) {
//						
//							e2.printStackTrace();
//						}
//						File selectedFile = fileChooser.getSelectedFile();
//						String ext ="";
//						int i = selectedFile.getName().lastIndexOf(".");
//						int p = selectedFile.getName().length();
//						ext = selectedFile.getName().substring(i, p);
//			
//						
//						if(ext.equals(".html") || ext.equals(".htm")){
//							
//							try {
//								readHTML(selectedFile);
//							} catch (URISyntaxException e1) {
//
//								e1.printStackTrace();
//							}
//								
//						}else if(ext.equals(".xml")){
//							
//							try {
//								readXML(null);
//							} catch (URISyntaxException | SAXException | IOException e1) {
//
//								e1.printStackTrace();
//							} catch (Exception e1) {
//								e1.printStackTrace();
//							}
//							
//							
//							
//						}
//							
//					
//						
//						else{
//							
//						documentReadIn worker1 = new documentReadIn(selectedFile);
//						worker1.execute();
//						
//						
//						try {
//							loadedDocumentList=worker1.get();
//						} catch (InterruptedException e1) {
//							e1.printStackTrace();
//						} catch (ExecutionException e1) {
//							e1.printStackTrace();
//						}}
//					}
//				
//			}
//			}
//			
//		});
//		
//		
//	}
	
	
	
	public void readXML(Integer business_code) throws Exception{
		System.out.println("Reading XML");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			InputStream is1 = null;
			InputStream is7 = null;
		
			File item1 = new File("item1.xml");
			if(item1.exists()){
				item1.delete();
			}
			item1.createNewFile();
			File item7 = new File("item7.xml");
			if(item7.exists()){
				item7.delete();
			}
			item7.createNewFile();
			switch(business_code){
			
			case 1:
				is1 = this.getClass().getResourceAsStream("businesses_formatted/MMM_2013_item-1.xml");
				is7 = this.getClass().getResourceAsStream("businesses_formatted/CareFusion_2013_item-7-formatted.xml");
				break;
				
			case 2:
				
				is1 = this.getClass().getResourceAsStream("businesses_formatted/CareFusion_2013_item-1-formatted.xml");
				is7 = this.getClass().getResourceAsStream("businesses_formatted/CareFusion_2013_item-7-formatted.xml");
				break;
		
			case 3:
				is1 = this.getClass().getResourceAsStream("businesses_formatted/ConocoPhillips_2013_item-1-formatted.xml");
				is7 = this.getClass().getResourceAsStream("businesses_formatted/ConocoPhillips_2013_item-7-formatted.xml");
				break;
				
			case 4:	
				is1 = this.getClass().getResourceAsStream("businesses_formatted/GE_2013_item-1.xml");
				is7 = this.getClass().getResourceAsStream("businesses_formatted/GE_2013_item-7.xml");

				break;
			case 5:
				is1 = this.getClass().getResourceAsStream("businesses_formatted/Intuit_2013_item-1-formatted.xml");
				is7 = this.getClass().getResourceAsStream("businesses_formatted/Intuit_2013_item-7-formatted.xml");
				break;
			case 6:
				is1 = this.getClass().getResourceAsStream("businesses_formatted/Marathon_2013_item-1-formatted.xml");
				is7 = this.getClass().getResourceAsStream("businesses_formatted/Marathon_2013_item-7-formatted.xml");
				break;
			case 7:
				
				break;
			case 8:
				
				break;
			case 9:
				is1 = this.getClass().getResourceAsStream("businesses_formatted/Regions_2013_item-1-formatted.xml");
				is7 = this.getClass().getResourceAsStream("businesses_formatted/Regions_2013_item-7-formatted.xml");
				break;
			default:
				is1 = this.getClass().getResourceAsStream("businesses_formatted/Visa_2013_item-1-formatted.xml");
				is7 = this.getClass().getResourceAsStream("businesses_formatted/Visa_2013_item-7-formatted.xml");
				
			}
			DocumentBuilderFactory factory = null;
			DocumentBuilder db = null;
			org.w3c.dom.Document doc1 = null;
			org.w3c.dom.Document doc7 = null;
			
			try{
				factory = DocumentBuilderFactory.newInstance();
				db = factory.newDocumentBuilder();
				if(is1!=null && is7!=null){
					doc1 = db.parse(is1);
					doc7 = db.parse(is7);
				}else{
					
					System.out.println("Null value returned");
				}
			
			
			}catch(Exception e){
				
				e.printStackTrace();
			}
			item1 = this.tagDocument(doc1,100,"item_1");
			@SuppressWarnings("resource")
			String content1 = new Scanner(item1).useDelimiter("\\Z").next();
			
			item7 = this.tagDocument(doc7,250,"item_7");	
			
			
		
		
		
			
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setFontSize(sas, fontSize);
			StyleConstants.setFontFamily(sas, "Serif");
			StyleConstants.setLineSpacing(sas, 1);
			ArrayList<DefaultStyledDocument> temp = new ArrayList<DefaultStyledDocument>();
			DefaultStyledDocument docFirst = new DefaultStyledDocument();
		
			try {
				docFirst.insertString(docFirst.getLength(), content1, sas);
			} catch (BadLocationException e1) {

				e1.printStackTrace();
			}

			temp.add(docFirst);
			
			/**Add Item 7 **/
			@SuppressWarnings("resource")
			String content = new Scanner(item7).useDelimiter("\\Z").next();
		
			DefaultStyledDocument docSeven = new DefaultStyledDocument();
		
			try {
				
				docSeven.insertString(docSeven.getLength(), content, sas);
			} catch (BadLocationException e1) {

				e1.printStackTrace();
			}
			DefaultStyledDocument docBreak = new DefaultStyledDocument();
			docBreak.insertString(docBreak.getLength(), this.stringDivider("End of Item 1"), sas);
			temp.add(docBreak);
			temp.add(docSeven);
			loadedDocumentList = temp;
			textPane.setStyledDocument(loadedDocumentList.get(0));
			
			try {
				findNext(textPane);
			} catch (BadLocationException e1) {

				e1.printStackTrace();
			}
			previousCount = 0;	
			
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}
	
	
	public void readHTML(File selectedFile) throws URISyntaxException{
		String content="";
		try {
			
			String args[] = {selectedFile.getAbsolutePath(),"negative.txt","positive.txt"};
			selectedFile = SymbolTagger.tagHTML(args);
	
			
		
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		} catch (IOException e1) {

			e1.printStackTrace();
		}	
		try {
			content = new Scanner(selectedFile).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}
		org.jsoup.nodes.Document selectedText = Parser.parse(content,selectedFile.getAbsolutePath());
		DefaultStyledDocument docFirst = new DefaultStyledDocument();
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setFontSize(sas, fontSize);
		StyleConstants.setFontFamily(sas, "Serif");
		StyleConstants.setLineSpacing(sas, 1);
		ArrayList<DefaultStyledDocument> temp = new ArrayList<DefaultStyledDocument>();
	
		try {
			
			docFirst.insertString(docFirst.getLength(), selectedText.body().text(), sas);
		} catch (BadLocationException e1) {

			e1.printStackTrace();
		}

		temp.add(docFirst);
		loadedDocumentList = temp;
		textPane.setStyledDocument(loadedDocumentList.get(0));
		
		try {
			findNext(textPane);
		} catch (BadLocationException e1) {

			e1.printStackTrace();
		}
		previousCount = 0;	
		
	}
	
	static boolean tryParseInt(String value)  
	{  
	     try  
	     {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch(NumberFormatException nfe)  
	      {  
	          return false;  
	      }  
	}
	
	
	public void boldSurroundingText(JTextPane textPane,int index) throws BadLocationException{
		int length = 30;
		int length2 = 30;
		StyledDocument temporary  = textPane.getStyledDocument();
		char testSpace = textPane.getText().charAt(index-length);
		while(testSpace!=' '){
			length++;
			testSpace = textPane.getText().charAt(index-length);
		}
		
		try{
		 char testSpace2 = textPane.getText().charAt(index+length2);
		while(testSpace2!=' '){
			length2++;
			testSpace2 = textPane.getText().charAt(index+length2);
			}
		
		}catch(Exception e){
			
			length2 = 2;
		}
	
		
		String text = temporary.getText(index-length, length+length2);
		
		temporary.remove(index-length, length+length2);
		SimpleAttributeSet a = new SimpleAttributeSet();
		StyleConstants.setFontSize(a, fontSize);
		StyleConstants.setFontFamily(a, "Serif");
		StyleConstants.setBold(a,true);
		temporary.insertString(index-length, text, a);
		
		
	}
	
	
	public void unboldSurroundingText(JTextPane textPane) throws BadLocationException{
		
		StyledDocument temporary  = textPane.getStyledDocument();
		String text = temporary.getText(0, temporary.getLength());
		temporary.remove(0, temporary.getLength());
		SimpleAttributeSet a = new SimpleAttributeSet();
		StyleConstants.setFontSize(a, fontSize);
		StyleConstants.setFontFamily(a, "Serif");
		StyleConstants.setBold(a,false);
		temporary.insertString(0, text, a);
		
		
	}
	
public static int getCurrentLayoutSetting(){
	
	return number_layout;
	
}


public void setCurrentLayoutSetting(int numberLayout){
	
	AnnotatedTextReader.number_layout = numberLayout;
	
}
	
public void unboldSurroundingText(JTextPane textPane, int index) throws BadLocationException{
		
	int length = 30;
	int length2 = 30;
	StyledDocument temporary  = textPane.getStyledDocument();
	char testSpace = textPane.getText().charAt(index-length);
	while(testSpace!=' '){
		length++;
		testSpace = textPane.getText().charAt(index-length);
	}
	char testSpace2 = textPane.getText().charAt(index+length2);
	while(testSpace2!=' '){
		length2++;
		testSpace2 = textPane.getText().charAt(index+length2);
	}
	
	String text = temporary.getText(index-length, length+length2);
	
	temporary.remove(index-length, length+length2);
	SimpleAttributeSet a = new SimpleAttributeSet();
	StyleConstants.setFontSize(a, fontSize);
	StyleConstants.setFontFamily(a, "Serif");
	StyleConstants.setBold(a,false);
	temporary.insertString(index-length, text, a);
		
		
	}
	
	public class JTextFieldLimit extends PlainDocument {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int limit;
	    // optional uppercase conversion
	    private boolean toUppercase = false;
	    
	    JTextFieldLimit(int limit) {
	        super();
	        this.limit = limit;
	    }
	    
	    JTextFieldLimit(int limit, boolean upper) {
	        super();
	        this.limit = limit;
	        toUppercase = upper;
	    }
	    
	    public void insertString
	            (int offset, String  str, AttributeSet attr)
	            throws BadLocationException {
	        if (str == null) return;
	        
	        if ((getLength() + str.length()) <= limit) {
	            if (toUppercase) str = str.toUpperCase();
	            super.insertString(offset, str, attr);
	        }
	    }
	}
	
	
	public void exitApplication() throws BadLocationException{
		
		  JDialog.setDefaultLookAndFeelDecorated(true);
		  int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit? Exiting before instructed could lead to a forfeit of credit.", "Confirm",
		        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		    
		    if(response==JOptionPane.YES_OPTION){
		    	if(AnnotatedTextReader.getCurrentLayoutSetting()==AnnotatedTextReader.TEXT_LAYOUT){
		    		
		    			 try {
		    		 
						writer.write(textPane.getDocument().getText(0, textPane.getDocument().getLength()));
						writer.write(this.stringDivider("End of Item 7"));
						endTime = new Date();
						 SimpleDateFormat simpleDateFormat = 
					                new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
						writer.write("<!-- Experiment taken on "+simpleDateFormat.format(startTime)+" --!>\n");
						writer.write("<!-- Experiment ended on  :"+simpleDateFormat.format(endTime)+" --!>\n");
						writer.write("<!-- Time Spent on experiment :"+this.printDifference(startTime, endTime)+"--!>");
						
						writer.close();
					} catch (IOException e) {
					
						e.printStackTrace();
					}
	 			 //Delete associated files
	 			 File file = new File("extractedText.txt");
	 			 file.delete();
	 			 File file2 = new File("extractedText_ant.txt");
	 			 file2.delete();
	 			 
	 			 
	 			 //Create file to be uploaded
	 			 File uploadedFile = new File(AnnotatedTextReader.sessionFileName);
	 			
	 			 	try{SFTP(uploadedFile);
	 			 		//JOptionPane.showMessageDialog(frame,
	 			 		//		"File could not uploaded. File has been saved locally. Please notify instructor."); 
	 			 		}catch(JSchException e){
					JOptionPane.showMessageDialog(frame,
							"Error uploading file. File has been saved locally. Please notify instructor."); 
					
	 			 		}
				
					
	 			 		//Exit Application
	 			 		this.cleanUpFiles();
	 			 		System.exit(0);	 				
	 			 			 
		    	}
		
		    }
	}
	
	
	//Not used for experiment purposes
	public boolean ftpFile(File localFile){
		
		
		String user = AnnotatedTextReader.ftpUserName;
		String password = AnnotatedTextReader.ftpPassword;
		int reply;
		FTPClient client = new FTPClient();
		try {
			InetAddress address = InetAddress.getByName(AnnotatedTextReader.ftpAddress);
			client.connect(address,21);
			
			
			//Added
			 reply = client.getReplyCode();

		      if(!FTPReply.isPositiveCompletion(reply)) {
		        client.disconnect();
		        System.err.println("FTP server refused connection.");
		        System.exit(1);
		      }
			client.login(user,password);
			System.out.println("Connection Established.");
			client.enterLocalPassiveMode();
			System.out.println("Passive mode initiated");
			
			
			
			
			
		
			String remoteFileName = AnnotatedTextReader.sessionFileName;
			
				
				InputStream inputStream = new FileInputStream(localFile);
				System.out.println("Upload in progress");
				boolean finished = client.storeFile(remoteFileName, inputStream);
				inputStream.close();
				if(finished){
					System.out.println("Successfully uploaded");
					client.logout();
					if(client.isConnected()){
						
						client.disconnect();
						System.out.println("Connection Terminated");
						
					}
					return true;
				}else{
				
			
				client.logout();
				if(client.isConnected()){
					
					client.disconnect();
					System.out.println("Connection Terminated");
					
				}
				return false;
				}
			
			
	
		} catch (SocketException e) {
			
			e.printStackTrace();
			return false;
		} catch (IOException e) {
		
			e.printStackTrace();
			return false;
		}
		
	}
	
	//Divides sections of annotated documents
	public String stringDivider(String Message){
		String divider = "";
		for(int i=0;i<50;i++){
			
			divider+="*";
		}
		 divider+= Message;
		for(int i=0;i<50;i++){
			
			divider+="*";
		}
		divider = divider+"\n";
		return divider;
		
	}
	
	//Delete temp files
	protected void cleanUpFiles(){
		
		File fileToDelete = new File("item_1.txt");
		if(fileToDelete.exists()){
			fileToDelete.delete();
			}
		fileToDelete = new File("item_7.txt");
		if(fileToDelete.exists()){
			fileToDelete.delete();
			}
		fileToDelete = new File("item_7_ant.txt");
		if(fileToDelete.exists()){
			fileToDelete.delete();
			}
		fileToDelete = new File("item_1_ant.txt");
		if(fileToDelete.exists()){
			fileToDelete.delete();
			}
		fileToDelete = new File("item7.xml");
		if(fileToDelete.exists()){
			fileToDelete.delete();
			}
		fileToDelete = new File("item1.xml");
		if(fileToDelete.exists()){
			fileToDelete.delete();
			}
		
		
		
	}
	
	//Print difference of start and end time
	public String printDifference(Date startDate, Date endDate){
		 
		//milliseconds
		long different = endDate.getTime() - startDate.getTime();
 

 
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;
 
		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;
 
		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;
 
		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;
 
		long elapsedSeconds = different / secondsInMilli;
 
		return elapsedHours +" hours, "+elapsedMinutes+" minutes, "+elapsedSeconds+" seconds";
		
	}

	
	//Tag Document//
	public File tagDocument(org.w3c.dom.Document doc, int count,String name) throws IOException, URISyntaxException{
		doc.getDocumentElement().normalize();
		
		//Commented part Used when needed to check number of annnotations
		int annotationInDocument =0;
		NodeList nodes = doc.getElementsByTagName("P");
		int i=0;
		String extractedText="";
		while(i<nodes.getLength() && annotationInDocument<count){
			Node node = nodes.item(i);
			Element element = (Element) node;
			int positiveTotalOfNode = Integer.parseInt(element.getAttribute("positivetotal"));
			int negativeTotalOfNode =Integer.parseInt(element.getAttribute("negativetotal"));
			int totalCount = positiveTotalOfNode +negativeTotalOfNode;
			annotationInDocument+=totalCount;
			extractedText+=" "+node.getTextContent();
		
			i++;
		}
		
	
		File extractedTextFile = null;
		
		extractedTextFile = new File(name+".txt");
		if(extractedTextFile.exists()){
			extractedTextFile.delete();
		}
		extractedTextFile.createNewFile();
		BufferedWriter writeFile = new BufferedWriter(new FileWriter(extractedTextFile));
		writeFile.write(extractedText);
		writeFile.close();
		
		String args[] = {extractedTextFile.getAbsolutePath(),"negative.txt","positive.txt"};
	
		
		return SymbolTagger.tagHTML(args);
		
		
		
		
		
	}
	
	//TABLE of Document Codes //
	public static class DocumentHashTable{
		
		
		static Hashtable<String,Integer> documentHash = new Hashtable<String,Integer>();
		
		
		public static Hashtable<String,Integer> getDocumentHashTable(){
			documentHash.put("3M2013", 1);
			documentHash.put("CF2013", 2);
			documentHash.put("CP2013", 3);
			documentHash.put("GE2013", 4);
			documentHash.put("IN2013", 5);
			documentHash.put("MA2013", 6);
		//	documentHash.put("MH2013", 7);
		//	documentHash.put("MT2013", 8);
			documentHash.put("RE2013", 9);
			documentHash.put("VI2013", 10);
			
			return documentHash;
			
		}
		
		
		
		
	}

	//SFTP of File //
	static public void SFTP(File file) throws JSchException{
		
		String SFTPHOST = "";
		int SFTPPORT = 22;
		String USER = "";
		String PW = "";
		
		JSch jsch = new JSch();
		try{
		Session session = jsch.getSession(USER,SFTPHOST,SFTPPORT);
		session.setPassword(PW);
		  java.util.Properties config = new java.util.Properties();
          config.put("StrictHostKeyChecking", "no");
          config.put("PreferredAuthentications", "password");
          session.setConfig(config);
          session.connect();
          System.out.println("Conenction established");
          ChannelSftp sftpchannel = (ChannelSftp)session.openChannel("sftp");
          sftpchannel.connect();
          if(sftpchannel.isConnected()){
        	  
        	  System.out.println("Connection established");
        	  sftpchannel.put(new FileInputStream(file),file.getName(),ChannelSftp.OVERWRITE);
          }
          
          sftpchannel.disconnect();
          
		
		
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
	}
	
}






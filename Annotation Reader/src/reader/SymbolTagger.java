package reader;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
/**
 * @author Nicholas Moss <nbmoss@uga.edu>
 * For the CompLing @ UGA research group.
 * Annotator program, matches words in a document to words on a list, and marks them if it finds a match 
 * Outputs a new annotated file with < > following each word marked 
 * To invoke the program: java Annotator document wordlist1 wordlist2 ... 
 * All files should be in the same directory as the program, and all ouput files are placed in this directory as well
 * TODO Want to change this to have a lists directory, documents directory, and output directory to allow for mass use
 * TODO write a shell script that allows entire directories to be fed in or enable the program to do this via a command line flag. 
 */
public class SymbolTagger {
    
    //the wordlist read from standard in
    private ArrayList<String> wordList;
    
    private PrintWriter outputStream;
    
    public SymbolTagger(){
        wordList = new ArrayList<String>();
    }

    /**
     * readList - reads a list of words and stores them in the program 
     * @param name String, name of the file to open may also need to include path
     * @return void
     * @throws URISyntaxException 
     */
     public void readList(String name) throws IOException, URISyntaxException{
         Scanner reader;
       
		 InputStream in = getClass().getResourceAsStream(name);
		 reader = new Scanner(new BufferedReader(new InputStreamReader(in)));
		 while(reader.hasNext()){
			 String next = reader.next();
		     wordList.add(next);
		     
		     
		 }
		 reader.close();
     }
     
     /**
      * findMatch - checks a string to see if it appears on the word list
      * @param word String checks this word to see if it appears on the word list
      * @return boolean 
      */
    public boolean findMatch(String word){
        for(int i=0; i<wordList.size(); i++){
            if(word.equalsIgnoreCase(wordList.get(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * annotate - reads the document, checks each token to see if it appears on a word list and marks it with < >.
     * Should maintain overall structure of the input document such as spacing and formatting. 
     * Outputs a new annotated file, some of the whitespace is more condensed.
     * @param name String name of the document
     * @return 
     */
    public File annotate(String name)throws IOException{
    	int i = name.indexOf(".");
        String outName = name.substring(0, i) + "_ant.txt";
    	File file = new File(outName);
        try {
       
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            
        } catch(IOException e){
            System.out.println("Error creating file...");
        }
        try{
            outputStream = new PrintWriter(new FileOutputStream(outName, true));
        }catch(FileNotFoundException e){}
        Scanner reader;
        try{
            reader = new Scanner(new BufferedReader(new FileReader(name)));
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                String m ="";
                while(!line.isEmpty()){
                    if(line.indexOf(" ") == -1){
                        m = line.substring(0);
                        line = "";
                    }
                    else{
                        m = line.substring(0, line.indexOf(" "));
                        line = line.substring(m.length());
                    }
                    line = line.trim();
                    String o = "";
                    if(m.contains(".") || m.contains("!") || m.contains("?") || m.contains(";") || m.contains(",")){
                        if(m.contains(".") && (m.indexOf(".") == m.length()-1)){
                            o = ".";
                        }
                        if(m.contains("!")){
                            o= "!";
                        }
                        if(m.contains("?")){
                            o="?";
                        }
                        if(m.contains(",")){
                            o=",";
                        }
                        if(m.contains(";")){
                            o=";";
                        }
                        m = m.substring(0, m.length()-1);
                    }
                    if(findMatch(m)){
                        String w = m + "< >";
                        outputStream.print(w + o + " " );
                     
                    }
                    else {
                        outputStream.print(m + o + " ");
                    }
                }
                outputStream.println();
            }
            outputStream.close();
            reader.close();
            return file;
        }catch(IOException e){
            System.out.println("Problem with document.");
            System.exit(0);
        }
		return null;
    }
    
   
    

    public static File tagHTML(String[] args)throws IOException, URISyntaxException{
         SymbolTagger a = new SymbolTagger();
         File taggedHTML = null;
         if(args == null){
             System.out.println("Need the arguments: document list1 list2 ...");
         }
         for(int i=1; i<args.length; i++){
            a.readList(args[i]);
         }
         if(args != null){
        	 System.out.println(args[0]);
             taggedHTML =a.annotate(args[0]);
         }
         return taggedHTML;
     }
}

          




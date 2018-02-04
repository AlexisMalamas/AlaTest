package fr.upmc.Thalasca.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * 
 * The class <code>Writer</code> allows to write data in File.
 * Each file corresponding to an application. 
 * The data stored will be used to compare the different data for each execution of the DataCenter.
 * 
 * 
 * @author Kevin GESNOUIN et Alexis MALAMAS 
 *
 */

public class Writer {

	private String nameFile;
	private PrintWriter writerFile = null;

	
	/**
	 * Constructor used to create the file with the name of the application
	 * 
	 * @param nameFile	nameFile corresponding to the application
	 */
	public Writer(String nameFile)  {
		this.nameFile = nameFile;

		try {
			this.writerFile = new PrintWriter("files/"+nameFile+".txt", "UTF-8");

		} catch (Exception e) {
			this.writerFile.close();
			e.printStackTrace();
		}
		this.writerFile.close();
	}

	/**
	 * 
	 * Function used to print a data in a file
	 * 
	 * @param data	data corresponding to the data to print
	 * @throws FileNotFoundException
	 */
	public void WriteInFile(String data) throws FileNotFoundException{

		try{
			this.writerFile = new PrintWriter(new FileOutputStream(new File("files/"+this.nameFile+".txt"),true));
			this.writerFile.println(data);
			this.writerFile.close();

		} catch (Exception e) {
			this.writerFile.close();
			e.printStackTrace();
		}
		this.writerFile.close();
	}

}

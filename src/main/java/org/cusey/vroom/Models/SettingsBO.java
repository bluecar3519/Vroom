package org.cusey.vroom.Models;

/**
 * The Setting implements an application that
 *
 *
 * @author  Cusey,John
 * @version 1.0
 * @since   2018-06-08
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.cusey.vroom.Models.Constants.*;

@Component
public class SettingsBO {

    private final static Logger slf4jLogger = LoggerFactory.getLogger(SettingsBO.class);

    private String successMessage = "";
    private String dangerMessage = "";

    @Value("C:" + BACKSLASH  + "Vroom")
    private String directory;
    @Value("Input")
    private String folderIn;
    @Value("Output")
    private String folderOut;
    
    /*
     * The Download Filter Field on the Options page is the percent of Right Answers divided by Total Tries.
     * FilterOut storage this calculation. 
     * 
     */
    private String filterOut = "0";

    private File[] files;

    private String currentLine = "";
    private BufferedReader br = null;
    private FileReader fr = null;

    private File flatFile;
    private BufferedWriter bw = null;

    public SettingsBO() {
    }

    /**
     * This method reads file in the C:\Vroom\Input folder and stores
     * the data in object called Dictionary. Input is the default folder
     * name.
     * @return List<Dictionary> Contains the list of dictionary entry.
     */
    public List<Dictionary> populateDictionary(){

        List<Dictionary> dicList = new ArrayList<Dictionary>();

        files = new File(directory + BACKSLASH + folderIn).listFiles();

        String path = directory + BACKSLASH  +  folderIn;

        try {

            if(files != null ){

                for(File file : files){

                    fr = new FileReader(  path + BACKSLASH  + file.getName() );
                    br = new BufferedReader(fr);

                    while ((currentLine = br.readLine()) != null) {

                        if (currentLine.length() > 0) {
                            slf4jLogger.debug(currentLine);

                            Dictionary dic = new Dictionary();

                                String[] items = currentLine.split(BACKSLASH +DELIMITER);

                                if(items.length > 6){
                                    dic.setWord(items[1]);
                                    dic.setPartOfSpeech(items[2]);
                                    dic.setDefinition(items[3]);
                                    dic.setWordUsage(items[4]);
                                    dic.setTotalTryCount( Integer.parseInt(items[5]) );
                                    dic.setRightCount(Integer.parseInt(items[6]));

                                    dicList.add(dic);


                            }

                        }
                    }
                }
            }else{
                slf4jLogger.debug("Check the file(s) in folder " + path);
            }


        } catch (IOException e) {
            slf4jLogger.error(e.getMessage());
        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException e) {
                slf4jLogger.error(e.getMessage());
            }
        }

        return dicList;
    }

    /**
     *  This method writes Dictionary object to flat file the
     *  methods uses the first letter of the word as the file
     *  name.
     *  and puts the file in C:\Vroom\Output
     * @param dic Is list of Dictionary objects which contains
     * the entries.
     * @return int Number row added to the file in the
     * folder C:\Vroom\Output. Output is the default folder name.
     */
    public int populateFlatFile(List<Dictionary> dic){

        Collections.sort(dic,Dictionary.WordComparator);

        int totalRowCount = 0;
        int correctPercentage = Integer.parseInt(this.filterOut);

        if ( (dic.size() > 0) ){
                for( int index = 0; index < dic.size(); index++){

                    Dictionary entry = dic.get(index);
                    Dictionary entryNext = new Dictionary( -1," ", " ", " ", " ",0,0);

                    if(index < dic.size() -1){
                        entryNext = dic.get(index +1);
                    }

                    if( index == 0){
                        createFile(entry);
                    }

                    slf4jLogger.debug( "[" + index + " ] " + getLine(entry) );

                    if(filterOut.matches("\\d+") ){


                        if( 0 <= correctPercentage && correctPercentage <= 100){

                            double entityCorrectPercentage = ((double) entry.getRightCount()/entry.getTotalTryCount())*100;
                            if(correctPercentage >= entityCorrectPercentage){
                                totalRowCount++;
                                writeLine(entry);
                                slf4jLogger.debug(entry.toString());
                            }
                        }else{
                            this.dangerMessage = "The perecent must between 0 and 100.";
                            index = dic.size();
                        }

                    }else{
                        totalRowCount++;
                        writeLine(entry);
                        slf4jLogger.debug(entry.toString());
                    }


                    boolean newLine = false;


                    if(! getFirstLetterWord ( entryNext ).equals( " ")) {
                        newLine = !(getFirstLetterWord(entry).equalsIgnoreCase(getFirstLetterWord(entryNext)));
                    }

                    //slf4jLogger.debug("Close file writer and create new file? " + (newLine == true ? "Yes" : "No" )  );
                    //slf4jLogger.debug("Entry Word: " + entry.getWord() +" New Entry Word: " + entryNext.getWord() );

                    if(newLine){
                        close();
                        if(entryNext.getId() != -1){
                            createFile(entryNext);
                        }else{
                            slf4jLogger.error("Next dictionary entry initialize error");
                        }

                    }



                }
                close();
        }

        return totalRowCount;

    }


    private void writeLine(Dictionary entry){
        try {
            bw.write(getLine(entry));
            bw.newLine();
        } catch (IOException e) {
            slf4jLogger.error(e.getMessage());
        }
    }

    private String getLine(Dictionary entry){

        return DELIMITER + entry.getWord()
                + DELIMITER + entry.getPartOfSpeech()
                + DELIMITER + entry.getDefinition()
                + DELIMITER + entry.getWordUsage()
                + DELIMITER + entry.getTotalTryCount()
                + DELIMITER + entry.getRightCount()
                + DELIMITER;

    }

    private String getFirstLetterWord(Dictionary entry){

        return entry != null ? entry.getWord().substring(0,1).toUpperCase() : " ";
    }

    private void createFile(Dictionary entry ){

        String fileName = getFirstLetterWord(entry);


        try {

            flatFile = new File(directory + BACKSLASH + folderOut + BACKSLASH + fileName.toUpperCase() + ".txt");

            if (flatFile.createNewFile()){
                slf4jLogger.debug(fileName + ".txt is created!");
            }else{
                slf4jLogger.debug("The file " + fileName + ".txt already exists.");
            }

            bw = new BufferedWriter(new FileWriter(flatFile));


        } catch (IOException e) {
            slf4jLogger.error(e.getMessage());
        }

    }

    private void close(){

            try {
                if(bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                slf4jLogger.error(e.getMessage());
            }
    }


    public String getSuccessMessage() {return successMessage;}
    public void setSuccessMessage(String successMessage) {this.successMessage = successMessage;}

    public String getDangerMessage() {return dangerMessage;}
    public void setDangerMessage(String dangerMessage) {this.dangerMessage = dangerMessage;}

    public String getDirectory() {return directory;}
    public void setDirectory(String directory) {this.directory = directory;}

    public String getFolderOut() { return folderOut; }
    public void setFolderOut(String folderOut) { this.folderOut = folderOut; }

    public String getFolderIn() { return folderIn; }
    public void setFolderIn(String folderIn) { this.folderIn = folderIn; }

    public File[] getFiles() {return files;}
    public void setFiles(File[] files) {this.files = files;}

    public String getFilterOut() { return filterOut; }
    public void setFilterOut(String filterOut) { this.filterOut = filterOut; }

}

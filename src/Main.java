import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Main {

	public static void main(String[] args) throws IOException {
		File file = new File("precomputed_results.csv");
		csvParser(file);
	}
	
	public static void csvParser(File file) throws IOException {
		PrintWriter printWriter = new PrintWriter("precomputed_attendees.txt", "UTF-8");
		String fileName = "precomputed_results.csv";
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = br.readLine()) != null) {
            String[] arr = line.split(",");
            String name = arr[0];
            printWriter.println("\"" + name + "\",");
        }
		printWriter.close();
	}
	
	public static void sanityCheck() throws IOException {
		FileInputStream xls = new FileInputStream("authordata.xls");
		Workbook workbook = new HSSFWorkbook(xls);
		Sheet sheet = workbook.getSheetAt(0);
		
		for(int i = 0; i < sheet.getLastRowNum() + 1; i++) {
			if(sheet.getRow(i).getCell(1) == null) {
				System.out.println(sheet.getRow(i).getCell(0));
			}
		}
	}
	
	public static void fillGaps() throws IOException
	{
		FileInputStream xlsEmpty = new FileInputStream("authordataempty.xls");
		Workbook workbookEmpty = new HSSFWorkbook(xlsEmpty);
		Sheet sheetEmpty = workbookEmpty.getSheetAt(0);
		
		FileInputStream xls = new FileInputStream("authordata.xls");
		Workbook workbook = new HSSFWorkbook(xls);
		Sheet sheet = workbook.getSheetAt(0);
		
		for(int i = 0; i < sheetEmpty.getLastRowNum() + 1; i++) {
			Row rowEmpty = sheetEmpty.getRow(i);
			String authorEmpty = rowEmpty.getCell(0).getStringCellValue();
			String citedAuthors = rowEmpty.getCell(1).getStringCellValue();
			String citingAuthors = rowEmpty.getCell(2).getStringCellValue();
			for(int j = 0; j < sheet.getLastRowNum() + 1; j++) {
				Row row = sheet.getRow(j);
				String author = row.getCell(0).getStringCellValue();
				if(authorEmpty.equals(author)) {
					System.out.println("Wrote: " + author + ", " + j);
					row.createCell(1).setCellValue(citedAuthors);
					row.createCell(2).setCellValue(citingAuthors);
					try 
				    {
				    	FileOutputStream fos = new FileOutputStream("authordata.xls");
				    	if(fos.equals(null)) {
				    		fos.close();
				    		return;
				    	}
				        workbook.write(fos);
				        fos.close();
				    } 
				    catch (Exception e)
				    {
				        e.printStackTrace();
				    }
					System.out.println("Wrote: " + author + ", " + j);
				}
			}
		}
	}
	
	public static void writeEmptyAuthorData() throws IOException 
	{
		ArrayList<String> emptyAuthors = readEmptyAuthors();
		FileInputStream xls = new FileInputStream("authordataempty.xls");
		Workbook workbook = new HSSFWorkbook(xls);
		Sheet sheet = workbook.getSheetAt(0);
		
		int start = 0;
		for(int i = start; i < emptyAuthors.size(); i++) {
			String attendee = emptyAuthors.get(i);
//			System.out.println("Writing: " + attendee + ", " + i);
//			writeRow(attendee, i, sheet, workbook);
			if(sheet.getRow(i) == null) {
				System.out.println("Writing: " + attendee + ", " + i);
//				writeRow(attendee, i, sheet, workbook);
//				System.out.println("Wrote: " + attendee + ", " + i);
			}
//			try {
//				writeRow(attendee, i, sheet, workbook);
//			} catch (Exception e) {
//				System.out.println("Exception");
//				Row row = sheet.createRow(i);
//				row.createCell(0).setCellValue(attendee);
//				row.createCell(1).setCellValue("Exception");
//				row.createCell(2).setCellValue("Exception");
//				FileOutputStream fos = new FileOutputStream("authordataempty.xls");
//		    	if(fos.equals(null)) {
//		    		System.out.println("fos is null");
//		    		fos.close();
//		    		return;
//		    	}
//		        workbook.write(fos);
//		        fos.close();
//				continue;
//			}
//			System.out.println("Wrote: " + attendee + ", " + i);
		}
	}
	
	public static void writeAPIData(HashSet<String> attendees) throws IOException {
		FileInputStream xls = new FileInputStream("authordataempty.xls");
		Workbook workbook = new HSSFWorkbook(xls);
		Sheet sheet = workbook.getSheetAt(0);
				
//		Row row = sheet.createRow(0);
//		row.createCell(0).setCellValue("Name");
//		row.createCell(1).setCellValue("Cited Authors");
//		row.createCell(2).setCellValue("Citing Authors");
		int start = sheet.getLastRowNum() + 1;
		
		ArrayList<String> existingAuthors = new ArrayList<String>();
		for(int i = 0; i < start; i++) {
			String author = sheet.getRow(i).getCell(0).getStringCellValue();
			Cell cell = sheet.getRow(i).getCell(1);
			if(cell == null) {
				existingAuthors.add(author);
			}
		}
		System.out.println(existingAuthors.size());
		writeEmptyAuthors(existingAuthors);
//		int index = 0;
//		for(String attendee : attendees) {
//			if(existingAuthors.contains(attendee)) {
//				continue;
//			}
//			if(attendee.length() != 0) {
//				System.out.println("Writing: " + attendee + ", " + index);
//				try {
//					writeRow(attendee, index, sheet, workbook);
//				} catch (Exception e) {
//					System.out.println("Exception: " + attendee + ", " + index);
//					index++;
//					Row row = sheet.createRow(index);
//					row.createCell(0).setCellValue(attendee);
//					row.createCell(1).setCellValue("Exception");
//					row.createCell(2).setCellValue("Exception");
//					FileOutputStream fos = new FileOutputStream("authordata.xls");
//			    	if(fos.equals(null)) {
//			    		System.out.println("fos is null");
//			    		fos.close();
//			    		return;
//			    	}
//			        workbook.write(fos);
//			        fos.close();
//					continue;
//				}
//				System.out.println("Wrote: " + attendee + ", " + index);
//				index++;
//			}
//		}
	}
	
	public static void writeRow(String attendee, int index, Sheet sheet, Workbook workbook) throws IOException {
		Row row = sheet.createRow(index);
		row.createCell(0).setCellValue(attendee);
		
		Author author = new Author(attendee);
		ArrayList<Result> citedAuthors = (Author.mergeAuthors(author.citedAuthors));
		ArrayList<Result> citingAuthors = (Author.mergeAuthors(author.citingAuthors));
		
		row.createCell(1).setCellValue(getAuthorsString(citedAuthors));
		row.createCell(2).setCellValue(getAuthorsString(citingAuthors));
				
		try 
	    {
	    	FileOutputStream fos = new FileOutputStream("authordataempty.xls");
	    	if(fos.equals(null)) {
	    		System.out.println("fos is null");
	    		fos.close();
	    		return;
	    	}
	        workbook.write(fos);
	        fos.close();
//	        workbook.close();
	    } 
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	
	public static String getAuthorsString(ArrayList<Result> authors) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		for(int i = 0; i < authors.size(); i++) {
			String authorName = authors.get(i).name;
			String toAppend = i != authors.size() - 1 ? authorName + "," : authorName;
			if(sb.toString().length() + toAppend.length() > 32766) {
				break;
			}
			sb.append(toAppend);
		}
		sb.append("\"");
		return sb.toString();
	}
	
	public static void writeInterestingTalks(HashMap<String, HashSet<Talk>> interestingTalks) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter printWriter = new PrintWriter("interestingTalks.txt", "UTF-8");
		for(String author: interestingTalks.keySet()) {
			printWriter.println(author + "\n");
			HashSet<Talk> talks = interestingTalks.get(author);
			for(int i = 0; i < talks.size(); i++) {
			}
			for(Talk talk: talks) {
				printWriter.println(talk.toString());
			}
		}
		printWriter.close();
	}
	
	public static void writeEmptyAuthors(ArrayList<String> emptyAuthors) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter printWriter = new PrintWriter("emptyauthors.txt", "UTF-8");
		for(String author: emptyAuthors) {
			printWriter.println(author);
		}
		printWriter.close();
	}
	
	public static ArrayList<String> readEmptyAuthors() throws IOException {
		ArrayList<String> emptyAuthors = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("emptyauthors.txt"));
		String line = br.readLine();
	    while (line != null) {
	    	emptyAuthors.add(line);
	    	line = br.readLine();
	    }
	    return emptyAuthors;
	}
	
	public static String parseLine(String line) {
		String[] result = line.split(",");
		String name = result[0].split(":")[1].trim();
		return name;
	}
	
	public static void writeAttendees() throws IOException {
		File file = new File("JSM2019-Online-Program-New.htm");
		HTMLParser htmlparser = new HTMLParser(file);
		htmlparser.parse();
		HashMap<String, Speaker> speakersMap = htmlparser.speakersMap;
		HashMap<String, Speaker> authorsMap = htmlparser.authorsMap;
		HashMap<String, Speaker> chairsMap = htmlparser.chairsMap;
			
		HashSet<String> attendees = new HashSet<String>();
		attendees.addAll(speakersMap.keySet());
		attendees.addAll(chairsMap.keySet());
		
		PrintWriter printWriter = new PrintWriter("newattendees.txt", "UTF-8");
		for(String attendee: attendees) {
			printWriter.println("\"" + attendee + "\",");
		}
		printWriter.close();
	}
}

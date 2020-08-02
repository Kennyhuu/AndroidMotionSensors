package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Test {
	
	private int truePos, falsePos, trueNeg, falseNeg, sum;
	private int truePosA, falsePosA, trueNegA, falseNegA, sumA;
	private int truePosE, falsePosE, trueNegE, falseNegE, sumE;
	private DataProcessorTest dp;

	
	public static void main(String[] args) {
		
		new Test().run();
	}
	
	public void run(){
		dp = new DataProcessorTest();
		//for(fallNum=1;fallNum<=15;fallNum++){
			dp.resetData();
			//dp.thGyroLongTerm = new Float(i);
			truePos=0; falsePos=0; trueNeg=0; falseNeg=0; sum=0;
			truePosA=0; falsePosA=0; trueNegA=0; falseNegA=0; sumA=0;
			truePosE=0; falsePosE=0; trueNegE=0; falseNegE=0; sumE=0;
			a=0;b=0;c=0;d=0;
			
			File dir = new File("SisFall");
			if(!dir.isDirectory())
				System.out.println("2");
			File[] directoryListing = dir.listFiles();
			if(directoryListing==null)
				System.out.println("1");
			else { 
				for (File child : directoryListing) {
				//File child=directoryListing[1]; {
					if (child.isDirectory()) {
						//System.out.println(child.getName());
						for(File dataFile : child.listFiles()) {
						//File dataFile = child.listFiles()[30];{
							if(!dataFile.getName().equals("desktop.ini")){
								//System.out.print(dataFile.getName());
								feed(dataFile);
								dp.resetData();
							}
						}
					}
				}
			//System.out.println(fallNum+" :\n"+(truePos+falseNeg)+"\n"+truePos+"\n"+falsePos+"\n"+falseNeg+"\n"+b+"\n"+c+"\n"+(d-a-b-c));
			//System.out.println("-----------------------");
			}
		//}
		System.out.println("\t\tELDERLY");
		System.out.println("+---------------+---------------+");
		System.out.println("|\t\t|Out\t\t|");
		System.out.println("|\t\t+-------+-------+");
		System.out.println("|\t\t|Pos\t|Neg\t|");
		System.out.println("+-------+-------+-------+-------+");
		System.out.println("|Exp"+'\t'+"|Pos\t|"+truePosE+"\t|"+falseNegE+"\t|");
		System.out.println("|\t+-------+-------+-------+");
		System.out.println("|"+'\t'+"|Neg\t|"+falsePosE+"\t|"+trueNegE+"\t|");
		System.out.println("+-------+-------+-------+-------+");
		System.out.println("\n\n\t\tADULT");
		System.out.println("+---------------+---------------+");
		System.out.println("|\t\t|Out\t\t|");
		System.out.println("|\t\t+-------+-------+");
		System.out.println("|\t\t|Pos\t|Neg\t|");
		System.out.println("+-------+-------+-------+-------+");
		System.out.println("|Exp"+'\t'+"|Pos\t|"+truePosA+"\t|"+falseNegA+"\t|");
		System.out.println("|\t+-------+-------+-------+");
		System.out.println("|"+'\t'+"|Neg\t|"+falsePosA+"\t|"+trueNegA+"\t|");
		System.out.println("+-------+-------+-------+-------+");
		System.out.println("\n\n\t\tTOTAL");
		System.out.println("+---------------+---------------+");
		System.out.println("|\t\t|Out\t\t|");
		System.out.println("|\t\t+-------+-------+");
		System.out.println("|\t\t|Pos\t|Neg\t|");
		System.out.println("+-------+-------+-------+-------+");
		System.out.println("|Exp"+'\t'+"|Pos\t|"+truePos+"\t|"+falseNeg+"\t|");
		System.out.println("|\t+-------+-------+-------+");
		System.out.println("|"+'\t'+"|Neg\t|"+falsePos+"\t|"+trueNeg+"\t|");
		System.out.println("+-------+-------+-------+-------+");
	}
	
	int a=0,b=0,c=0,d=0;
	public void feed(File f) {
		boolean isElderly;
		boolean isFallExpected;
		
		char[] name = f.getName().toCharArray();
		if(name[0]=='D') {
			isFallExpected = false;
			// d 1 5 7 11 14 15 16
			if(!((name[1]=='0' && (name[2]=='1' || name[2]=='5' || name[2]=='7')) || (name[1]=='1' && (name[2]=='1' || name[2]=='4' || name[2]=='5' || name[2]=='6')))) return;
		}
		else{
			if(name[1]=='1' && (name[2]=='1' || name[2]=='4')) return;
			isFallExpected = true;
		} 
		if(name[5]=='A') isElderly=false;
		else isElderly=true;
		
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line;
			int lineNum=0;
			int x=0;
			int detectedx = 4; 
			while ((line = r.readLine()) != null && detectedx!=0) {
				x++;
				if(lineNum==0){
					String[] nums = line.split(",");
					MovementDataTest md = new MovementDataTest();
					try{
					md.accX = Float.valueOf(nums[0].trim())*2*16/(float)Math.pow(2,13)*9.81f;
					md.accY = Float.valueOf(nums[1].trim())*2*16/(float)Math.pow(2,13)*9.81f;
					md.accZ = Float.valueOf(nums[2].trim())*2*16/(float)Math.pow(2,13)*9.81f;
					
					md.posX = Float.valueOf(nums[3].trim())*2*2000/(float)Math.pow(2,16);
					md.posX = Float.valueOf(nums[4].trim())*2*2000/(float)Math.pow(2,16);
					md.posX = Float.valueOf(nums[5].trim())*2*2000/(float)Math.pow(2,16);
					
					int s = dp.calc(md);
					if(s<detectedx) detectedx=s;
					}
					catch (NumberFormatException e) {
						System.out.println("Format error at: "+f.getName()+" "+x);
					}
				}
				if(lineNum==3) lineNum=0;
				else lineNum++;
			}
			
			if(isFallExpected) {
				d++;
				if(detectedx==0) a++;
				else if(detectedx==1) b++;
				else if(detectedx==2) c++;				
			}
			
			boolean detected = detectedx==0; 
			sum++;
			if(isElderly) {
				sumE++;
				if(isFallExpected  && detected)  {truePos++;truePosE++;}
				if(!isFallExpected && detected)  {falsePos++;falsePosE++;}
				if(isFallExpected  && !detected) {falseNeg++;falseNegE++;}
				if(!isFallExpected && !detected) {trueNeg++;trueNegE++;}
			}
			else {
				sumA++;
				if(isFallExpected  && detected)  {truePos++;truePosA++;}
				if(!isFallExpected && detected)  {falsePos++;falsePosA++;}
				if(isFallExpected  && !detected) {falseNeg++;falseNegA++;}
				if(!isFallExpected && !detected) {trueNeg++;trueNegA++;}
			}
			//System.out.println("\t"+isFallExpected+"\t"+detected+"\t"+detectedx);
		} catch (IOException e) {
		} finally {
	        if (r != null) {
	            try {
	                r.close();
	            } catch (IOException e) {
	                System.err.println("Exception:" + e.toString());
	            }
	        }
		}
	}
}

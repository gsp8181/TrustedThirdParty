package com.team2;

import static org.junit.Assert.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import org.junit.Test;

import com.team2.security.CertificateTools;
import com.team2.security.Sig;

public class AppTest{
	private static Parser p = new Parser();
	@Test
	public void testHelp() {
		String[] d={"help"};
		
		p.print(d);
	
	
	}

	@Test
	public void testGenerateSig() {
		String[] args={"gensig","-e","Z.Zhong4@newcastle.ac.uk"};
		
        p.print(args);		
		
   
		
	}
	
	@Test
	public void testSendContract() {
		String[] args={"sign","-d","Z.Zhong4@newcastle.ac.uk","-f","hello.txt"};
		p.print(args);
	}
	
	@Test
	public void testGetContract(){
		String[] args={"getcontracts"};
		p.print(args);	
	}
	
	@Test
	public void testSignContract(){
		String[] args={"countersign","-i","ce91df14-ebd9-4f71-a2c2-3387030d8eea"};
		p.print(args);	
	}
	
	@Test
	public void testGetCompletedContract(){
		String[] args={"getcompleted","-i","ce91df14-ebd9-4f71-a2c2-3387030d8eea"};
		p.print(args);	
	}
	
//	@Test
//	public void testAbort(){
//		String[] args = {"abort", "-i", "661063e1-c481-4d8f-8375-be2267bb4e33"};
//		p.print(args);
//	}


}

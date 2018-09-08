package com.sfblock.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class SCorechain {
	
	private List<SBlock> chain;	
	private MessageDigest digest; 
	public SCorechain(){
		try {
			digest= MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chain=new ArrayList<SBlock>();
		chain.add(SBlock.createGenesisBlock());
	}
	
	public void addblock(List<STransaction> transactions){
		SBlock lastblock=chain.get(chain.size()-1);
		SBlock newblock=lastblock.mineblock(lastblock, transactions, digest);
		chain.add(newblock);
	}
	
	

}

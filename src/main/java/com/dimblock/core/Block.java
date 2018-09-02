package com.dimblock.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {
	public int index, nonce;
	public String timestamp, previous_hash;
	public List<Transaction> transactions;
	
		
	public Block(int index, int nonce, String previous_hash, Transaction trans){
		this.index = index;
		this.nonce = nonce;
		timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		transactions.add(trans);
		this.previous_hash = previous_hash;
	}
}

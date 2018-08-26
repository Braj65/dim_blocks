package com.dimblock.core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Block {
	public int index, nonce;
	public String timestamp, previous_hash;
	
	public Block(int index, int nonce, String previous_hash){
		this.index = index;
		this.nonce = nonce;
		timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		this.previous_hash = previous_hash;
	}
}

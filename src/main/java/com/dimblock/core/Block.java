package com.dimblock.core;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Block {
	public int index, nonce;
	public String timestamp;
	public Long previous_hash;
	
	public Block(int index, int nonce, Long previous_hash){
		index = index;
		nonce = nonce;
		timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
		previous_hash = previous_hash;
	}
}

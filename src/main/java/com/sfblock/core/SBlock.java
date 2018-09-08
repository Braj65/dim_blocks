package com.sfblock.core;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

public class SBlock {
	public int index, nonce, difficulty;
	public Long timestamp;
	public String previous_hash, hash;
	public List<STransaction> transactions;

	public static final int MINE_RATE = 3000;
	public static int DIFFICULTY=3;

	public SBlock(int index, int nonce, String previous_hash, List<STransaction> trans, int difficulty) {
		this.index = index;
		this.nonce = nonce;
		timestamp = System.currentTimeMillis();
		transactions.addAll(trans);
		this.previous_hash = previous_hash;
		this.difficulty = difficulty;
	}

	public static SBlock createGenesisBlock() {
		return new SBlock(1, 1, "0", new ArrayList<STransaction>(), DIFFICULTY);
	}

	public SBlock mineblock(SBlock lastblock, List<STransaction> transaction, MessageDigest digest) {
		String previousHash = getPreviousHash(lastblock, digest);
		int[] new_nonce_difficulty=getProofofWorkAndDifficulty(lastblock, transaction, digest);
		return new SBlock(lastblock.index+1, new_nonce_difficulty[0], previousHash, transaction, new_nonce_difficulty[1]);
	}

	private int[] getProofofWorkAndDifficulty(SBlock lastblock, List<STransaction> transaction, MessageDigest digest) {
		boolean check_proof = false;
		int previous_nonce = lastblock.nonce;
		int new_nonce = 0;
		// Method described in A-Z
		/*
		 * while(!check_proof){ byte[]
		 * encodedHash=digest.digest(String.valueOf(IntMath.pow(new_nonce, 2) -
		 * IntMath.pow(previous_nonce, 2)).getBytes()); String
		 * hash_operation=bytesToHex(encodedHash);
		 * if("0000".equals(hash_operation.substring(0, 4))) check_proof=true;
		 * else ++new_nonce; } return new_nonce;
		 */
		// Method described in from scratch
		long currenttime;
		String hash="", zeroes="0";
		
		do {
			new_nonce++;
			currenttime = System.currentTimeMillis();
			difficulty = adjustDifficulty(lastblock, timestamp);
			hash=calculateHash(currenttime, transaction, new_nonce, difficulty, digest);
		} while (!hash.substring(0, difficulty).equals(Strings.repeat(zeroes, difficulty)));
		
		return new int[]{new_nonce, difficulty};
	}

	private static int adjustDifficulty(SBlock lastblock, long currenttime) {
		int difficulty = lastblock.difficulty;
		if (lastblock.timestamp + MINE_RATE > currenttime)
			difficulty++;
		else
			difficulty--;
		return difficulty;

	}

	private String calculateHash(long currenttime, List<STransaction> transaction, int nonce, int difficulty,
			MessageDigest digest) {
		String mixstrng = Long.toString(currenttime) + transaction.toString() + nonce + difficulty;
		byte[] encodedHash = digest.digest(mixstrng.getBytes());
		return bytesToHex(encodedHash);
	}

	private String getPreviousHash(SBlock element, MessageDigest digest) {
		String block_string = new String("{");
		block_string += "index:" + element.index;
		block_string += "nonce:" + element.nonce;
		block_string += "previous_hash:" + element.previous_hash;
		block_string += "transactions:" + element.transactions;
		block_string += "timestamp:" + element.timestamp + "}";
		byte[] encodedHash = digest.digest(block_string.getBytes());
		return bytesToHex(encodedHash);
	}

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

}

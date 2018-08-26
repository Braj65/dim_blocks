package com.dimblock.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.hash.Hashing;
import com.google.common.math.IntMath;

public class CoreChain {
	private List<Block> chain;

	public CoreChain() {
		chain = new ArrayList<Block>();
		createBlock(1, new String("0"));
	}

	// Part 1 Creating the blockchain
	private Block createBlock(Integer nonce, String previous_hash) {
		Block block = new Block(chain.size(), nonce, previous_hash);
		chain.add(block);
		return block;
	}

	private Block get_Previous_Block() {
		return chain.get(chain.size() - 1);
	}

	// solving the crypto puzzle to find the nonce. Using previous blocks nonce
	// as input
	private Integer proof_Of_Work(Integer previous_nonce) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Integer new_nonce = 1;
		boolean check_proof = false;
		while (!check_proof) {

			byte[] encodedHash = digest
					.digest(String.valueOf(IntMath.pow(new_nonce, 2) - IntMath.pow(previous_nonce, 2)).getBytes());
			String hash_operation = bytesToHex(encodedHash);

			/*String hash_operation = Hashing.sha256()
					.hashString("b" + String.valueOf(IntMath.pow(new_nonce, 2) - IntMath.pow(previous_nonce, 2)),
							StandardCharsets.UTF_8)
					.toString();*/

			if ("0000".equals(hash_operation.substring(0, 4)))
				check_proof = true;
			else
				new_nonce += 1;
			System.out.println(new_nonce);
		}
		return new_nonce;
	}

	// to find the sha 256 hash of a block using the fields. May be we need to
	// jsonize the fields
	private String hash(Block element) {
		String block_string = new String("{");
		block_string += "index:" + element.index;
		block_string += "nonce:" + element.nonce;
		block_string += "previous_hash:" + element.previous_hash;
		block_string += "timestamp:" + element.timestamp + "}";
		return Hashing.sha256().hashString(block_string, StandardCharsets.UTF_8).toString();
	}

	private boolean is_Chain_Valid(List<Block> chain) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Block previous_block = chain.get(0);
		int current_block_index = 1;
		while (current_block_index < chain.size()) {
			Block currentBlock = chain.get(current_block_index);
			// two validations to check validation of chain
			// 1) if previous_hash value of current block is equal to previous
			// block's calculated hash
			if (!currentBlock.previous_hash.toString().equals(hash(previous_block))) {
				return false;
			}
			// 2) If we take the previous nonce and current nonce and calculate
			// the sha256(using the same formula)
			// we have used in proof_Of_Work(), and we get leading 4 0s. Then
			// the chain is valid
			int previous_block_nonce = previous_block.nonce;
			int current_block_nonce = currentBlock.nonce;
			byte[] encodedHash = digest.digest(String
					.valueOf(IntMath.pow(current_block_nonce, 2) - IntMath.pow(previous_block_nonce, 2)).getBytes());
			String hash_operation = bytesToHex(encodedHash);
			/*
			 * String hash_operation = Hashing.sha256() .hashString(
			 * String.valueOf(IntMath.pow(current_block_nonce, 2) -
			 * IntMath.pow(previous_block_nonce, 2)), StandardCharsets.UTF_8)
			 * .toString();
			 */
			if (!("0000".equals(hash_operation.substring(0, 4)))) {
				return false;
			}
			previous_block = currentBlock;
			current_block_index += 1;
		}
		return true;
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
	// Part 2 Mining the blockchain
	public static void main(String[] args) {
		CoreChain chain = new CoreChain();
		// get previous block to mine next block nonce
		Block prev_block = chain.get_Previous_Block();
		// mine the nonce
		int nonce = chain.proof_Of_Work(prev_block.nonce);
		// get previous block hash to create new block
		String prev_hash = chain.hash(prev_block);
		// create new block
		Block new_block = chain.createBlock(nonce, prev_hash);
		//Add another block to the chain
		prev_block = chain.get_Previous_Block();
		nonce = chain.proof_Of_Work(prev_block.nonce);
		prev_hash = chain.hash(prev_block);
		new_block = chain.createBlock(nonce, prev_hash);
		//Check chain validity
		System.out.println(chain.is_Chain_Valid(chain.chain));
	}

	

}

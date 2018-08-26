package com.dimblock.core;

import java.nio.charset.StandardCharsets;
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
		createBlock(1, new Long(0));		
	}
	
	//Part 1 Creating the blockchain
	private CoreChain createBlock(Integer nonce, Long previous_hash) {
		Block block=new Block(chain.size() + 1, nonce, previous_hash);
		chain.add(block);
		return this;
	}

	private Block get_Previous_Block() {
		return chain.get(chain.size() - 1);
	}

	// solving the crypto puzzle to find the nonce. Using previous blocks nonce
	// as input
	private Integer proof_Of_Work(Integer previous_nonce) {
		Integer new_nonce = 1;
		boolean check_proof = false;
		while (!check_proof) {
			String hash_operation = Hashing.sha256()
					.hashString(String.valueOf(IntMath.pow(new_nonce, 2) - IntMath.pow(previous_nonce, 2)),
							StandardCharsets.UTF_8)
					.toString();
			if ("0000".equals(hash_operation.substring(0, 3)))
				check_proof = true;
			else
				new_nonce += 1;
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
		Block previous_block = chain.get(0);
		int current_block_index = 1;
		while (current_block_index < chain.size()) {
			Block currentBlock = chain.get(current_block_index);
			// two validations to check validation of chain
			// 1) if previous_hash value of current block is equal to previous
			// block's calculated hash
			if (currentBlock.previous_hash.toString() != hash(previous_block)) {
				return false;
			}
			// 2) If we take the previous nonce and current nonce and calculate
			// the sha256(using the same formula)
			// we have used in proof_Of_Work(), and we get leading 4 0s. Then
			// the chain is valid
			int previous_block_nonce = previous_block.nonce;
			int current_block_nonce = currentBlock.nonce;
			String hash_operation = Hashing.sha256()
					.hashString(
							String.valueOf(IntMath.pow(current_block_nonce, 2) - IntMath.pow(previous_block_nonce, 2)),
							StandardCharsets.UTF_8)
					.toString();
			if (!("0000".equals(hash_operation.substring(0, 3)))){
				return false;
			}
			previous_block=currentBlock;
			current_block_index+=1;
		}
		return true;
	}

	public static void main(String[] args) {
		System.out.println(Hashing.sha256().hashString("Jammy", StandardCharsets.UTF_8).toString());
	}
	
	//Part 2 Mining the blockchain

}

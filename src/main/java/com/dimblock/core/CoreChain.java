package com.dimblock.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.common.hash.Hashing;
import com.google.common.math.IntMath;

public class CoreChain {
	private List<Block> chain;
	
	private HashSet<String> nodes;

	public CoreChain() {
		chain = new ArrayList<Block>();
		nodes=new HashSet<String>();
		createBlock(1, new String("0"), new Transaction("fused", "UserName", "10Coins"));
	}

	// Part 1 Creating the blockchain
	private Block createBlock(Integer nonce, String previous_hash, Transaction transaction) {
		Block block = new Block(chain.size(), nonce, previous_hash,transaction);
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
		block_string+="transactions:"+element.transactions;
		block_string += "timestamp:" + element.timestamp + "}";
		return Hashing.sha256().hashString(block_string, StandardCharsets.UTF_8).toString();
	}
	
	//public @is_chain_valid
	//GET
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
	// public @mine_block
	//GET
	public static void main(String[] args) {
		CoreChain chain = new CoreChain();
		// get previous block to mine next block nonce
		Block prev_block = chain.get_Previous_Block();
		// mine the nonce
		int nonce = chain.proof_Of_Work(prev_block.nonce);
		// get previous block hash to create new block
		String prev_hash = chain.hash(prev_block);
		//add transaction to block before creating block
			//for current node create a random node address
			String node_address=UUID.randomUUID().toString().replace("-", "");
			//chain.add_transaction(node_address, "UserName", "10Coins");
		// create new block
		Block new_block = chain.createBlock(nonce, prev_hash, new Transaction(node_address, "UserName", "10Coins"));
		//Add another block to the chain
		prev_block = chain.get_Previous_Block();
		nonce = chain.proof_Of_Work(prev_block.nonce);
		prev_hash = chain.hash(prev_block);
		
		new_block = chain.createBlock(nonce, prev_hash, new Transaction(node_address, "UserName", "10Coins"));
		//Check chain validity
		System.out.println(chain.is_Chain_Valid(chain.chain));
	}
	
	//Get full blockchain
	//@get_chain
	//GET
	public String get_chain(){
		//returning full chain and chain size as response to /get_chain
		return this.chain.toString()+this.chain.size();
	}
	
	//Part 3 Adding decentralizing level shit. Like adding transaction, nodes, consensus, adding new node, update_chain
	//add a new transaction to list of transactions in a block
	//not required anymore. Added transaction as part of block
	public int add_transaction(String sender, String receiver, String amount){
		Transaction new_transaction=new Transaction(sender, receiver, amount);
		Block prev=get_Previous_Block();
		return prev.index+1;
	}
	
	//Add a new node to set of nodes
	public void add_node(String address){
		URL url=null;
		try {
			url=new URL(address);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nodes.add(url.getPath());
	}
	//public /connect_node POST
	//uses above method	
	public void connect_node(HashSet<String> nodes){
		this.nodes.addAll(nodes);
	}
	
	//Update chain in other nodes with longest chain present in every node
	public boolean update_chain(){
		HashSet<String> nodes=this.nodes;
		List<Block> longestChain=null;
		int max_length=this.chain.size();
		Iterator<String> iter_node=nodes.iterator();
		//Going to find which node has a list<block> with maximum no. of blocks in it and
		//replace current node's chain with longest chain
		while(iter_node.hasNext()){
			String node=iter_node.next();
			String response=new String("request.get(http://'{node}'/get_chain)");
			if("200".equals(response)){
				int node_length=Integer.parseInt("response.json()[length]");
				List<Block> node_chain=new ArrayList<Block>();//response.json()]
				if(node_length>max_length && is_Chain_Valid(node_chain)){
					max_length=node_length;
					longestChain=node_chain;
				}
			}
		}
		if(longestChain!=null){
			this.chain=longestChain;
			return true;
		}
		return false;
	}
	//public GET /replace_chain
	//uses above method
		public boolean replace_chain(HashSet<String> nodes){
			return this.replace_chain(nodes);
		}

	

}

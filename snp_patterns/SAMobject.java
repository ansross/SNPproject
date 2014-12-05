package snp_patterns;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.ArrayList;

class SAMobject{
	HashMap<String, SNPpattern> SNPpatterns;

	String SAM_file_name;
	HashMap<String, String> reference_database;	

	public SAMobject(String file_name, HashMap<String, String> ref_database){
		SAM_file_name = file_name;
		SNPpatterns = new HashMap<String, SNPpattern>();
		reference_database = ref_database;
	}

	public void parseSAMfile(){
		//TODO see what if(p.samFilePair.canRead()) in ContigWideStatistics means!!!!
		String line = "";
		try{
			BufferedReader bufRead = new BufferedReader(new FileReader(SAM_file_name));
			boolean startedSequences = false;
			while( (line= bufRead.readLine()) != null)
			{	//read over SAM header lines
				if (line.startsWith("@"))
				{	//if misplaced header, error with SAM file, exit
					if(startedSequences){
						System.out.println("ERROR::[SAMobject parseSAMfile] Bad SAM format. ");
					}else{
						continue;
					}
				}
				else
				{
					startedSequences = true;
				}
				//parse SAM and check format
				String [] alignment_section = line.split("\t");
				// 		TODO exit after error message		
				if(alignment_section.length < 9)
					System.out.println("ERROR::[SAMobject] Bad SAM format");
				parseAlignment(alignment_section);
				
				
			}
			bufRead.close();
			for(String id: SNPpatterns.keySet()){
				System.out.println(SNPpatterns.get(id).get_ID()+"~"+SNPpatterns.get(id).get_count());
			}
		}catch (Exception e){
			System.out.println("Error reading SAM file: " + e.toString());
		}

		finally{

		}
	}

	private void parseAlignment(String [] alignment_fields){
		String cigarString = alignment_fields[5];
		if(cigarString.contains("M")){
			//get all necessary info from alignemnt line 
			String qName = alignment_fields[0];
			int bitFlag = Integer.parseInt(alignment_fields[1]);
			String rName = alignment_fields[2];
			int pos = Integer.parseInt(alignment_fields[3]);
			//System.out.println("pos: "+pos);
			String sequence = alignment_fields[9];
			//get reference 
			String reference = reference_database.get(rName); 
			//TODO check if need this???
			//fix the starting poisting using CIGAR, i.e. fixes the 
			//BSW artifact where aligned reads starting with 
			// substituitions have incorret alignment starting 
			// positions
			/*
			int preSubst = Util.count_preceding_subst(cigarString, m_contig_name);
			if (preSubst > 10){
				pos = pos - preSubst
			}
			
			*/
			//iterators for tracking position in read and reference
			//TODO, check if needs to be adjusted for aligning mid sequence
			int position_in_sequence = 0;
			//strings are 0-based, pos in SAM file 1-based
			
			int position_in_reference = pos-1;
			
			//parse CIGAR
			int length_of_block = 0;
			SNPpattern pattern = new SNPpattern(rName);
			String [] operation_tokens = cigarString.split("\\d+");
			ArrayList<String> ops_arraylist = new ArrayList<String>();
			for(int i=0; i<operation_tokens.length; ++i){
				if((!operation_tokens[i].equals("")) && (!(operation_tokens[i]==null))){
					//System.out.println("adding op: "+operation_tokens[i]);
					ops_arraylist.add(operation_tokens[i]);
				}
			}
			String [] operations = new String[ops_arraylist.size()];
			operations = ops_arraylist.toArray(operations);
			//System.out.println("ops lenth: "+operations.length);
			//System.out.println(5);
			String [] string_op_counts = cigarString.split("[^\\d.]");
			ArrayList<String> op_count_arraylist = new ArrayList<String>();
			for(int i=0; i<string_op_counts.length; ++i){
				if(!string_op_counts[i].equals("") && !string_op_counts[i].equals(null)){
					op_count_arraylist.add(string_op_counts[i]);
				}
			}
			op_count_arraylist.toArray(string_op_counts);
			int [] op_counts = new int[string_op_counts.length];
			//System.out.println("op_counts length: "+string_op_counts.length);
			//System.out.println(6);	
			for(int i=0; i<string_op_counts.length;++i){
				if(!string_op_counts[i].equals("")){
					//System.out.println(6.5);
					//System.out.println("string: "+string_op_counts[i]);
					op_counts[i] = Integer.parseInt(string_op_counts[i]);
					//System.out.println("op count string: "+string_op_counts[i]);
				}
			}
			//System.out.println(7);
			//TODO print and exit
			//System.out.println("op length: "+operations.length+", op count length: "+op_counts.length);
			if(operations.length != op_counts.length){
				System.out.println("ERROR::parseAlignments; CIGAR string wrong "+ cigarString);
				for(int i=0; i<operations.length; ++i){
					System.out.println("op: "+operations[i]);
				}
				for(int i=0; i<op_counts.length; ++i){
					System.out.println("count: "+op_counts[i]);
				}
			}
			boolean success=true;
			for(int op=0; op<operations.length; ++op)
			{	
				if(!success){
					break;
				}
				//System.out.println("operations op: "+operations[op]);
				if(operations[op] != null){
					switch(operations[op].charAt(0)){
					//match or mismatch
					case 'M':
						for(int count=0; count<op_counts[op];count+=1){
							if((position_in_reference >= reference.length()) || (position_in_sequence >= sequence.length())){
								success=false;
								System.out.println("ERROR:Alignment beyond length of reference or sequence");
								break;
							}	
							if(reference.charAt(position_in_reference) != sequence.charAt(position_in_sequence))
							{
								//reference pos is one based but 
								//the iterator is 0-based
								pattern.addSNP(position_in_reference+1, reference.charAt(position_in_reference), sequence.charAt(position_in_sequence));
							}
							position_in_reference +=1;		
							position_in_sequence +=1;
											
						}
						break;
					//insertion to reference
					case 'I':
						position_in_sequence += op_counts[op];
						break;
					//deletion from reference
					case 'D':
						position_in_reference += op_counts[op];
						break;
					default:
						System.out.println("New CIGARA operation: " + operations[op]+". Not dealt with");
						
					}
				}
			}
			if(success && pattern.has_SNPs()){
				if(SNPpatterns.containsKey(pattern.get_ID())){
					SNPpatterns.get(pattern.get_ID()).increment_count();
				}
				else{
					SNPpatterns.put(pattern.get_ID(), pattern);
				}
				System.out.println("SNP pattern: "+pattern.get_ID());
			}
			
		} 	
		
				
	}
	
}

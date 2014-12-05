package snp_patterns;
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
			BuffereReader bufRead = new BufferedReader(new FileReader(SAM_file_name));
			boolean started_sequences = false;
			while( (line= bufRead.read()) != null)
			{	//read over SAM header lines
				if (line.startsWith("@"))
				{	//if misplaced header, error with SAM file, exit
					if(startedSequences){
						Util.print_exit("ERROR::[SAMobject parseSAMfile] Bad SAM format. ");
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
				// 				
				if(sline.length < 9)
					Util.print_exit("ERROR::[SAMobject] Bad SAM format");
				parseAlignment(alignment_section);
		
				
				
			}
			bufRead.close();
		}
	}

	private void parseAlignment(String [] alignemnt_fields){
		//get all necessary info from alignemnt line 
		String qName = alignment_fields[0];
		int bitFlag = Integer.parseInt(alignment_fields[1]);
		String rName = alignment_fields[2];
		int pos = Integer.parseInt(alignment_fields[3]);
		String cigarString = alignment_fields[5];
		String sequence = alignment_fields[9];

		//get reference 
		reference = reference_database[rName]; 
		
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
		int position_in_seq = 0;
		//strings are 0-based, pos in SAM file 1-based
		
		int position_in_ref = pos-1;
		
		//parse CIGAR
		int length_of_block = 0;
		//possible redundant check
		if(cigarString.contains("M")){
			SNPpattern pattern = new SNPpattern(rName);
			String [] operations = cigarString.split("\p{Digit}");
			String [] string_op_counts = cigarString.split("\p{Alpha}");
			int [] op_counts = new int[string_op_counts.length];	
			for(int i=0; i<string_op_counts.length;++i){
				op_counts[i] = Integer.parseInt(string_op_counts[i]);
			}
			if(operations.length != op_counts.length){
				Util.print_exit("ERROR::parseAlignments; CIGAR string wrong");
			}
			
			for(int op=0; op<operations.length; ++op)
			{
				switch(operations[op]){
				//match or mismatch
				case 'M':
					for(int count=0; count<op_counts[op];++counts){	
						if(reference[position_in_ref] != sequence[position_in_sequence])
						{
							//reference pos is one based but 
							//the iterator is 0-based
							pattern.addSNP(position_in_reference+1, reference[position_in_ref], sequence[position_in_sequence]);
						}
						++position_in_reference;		
						++position_in_sequence;
										
					}
					break;
				//insertion to reference
				case 'I':
					position_in_sequence += op_counts[op];
					break;
				//deletion from reference
				case 'D':
					position_in_reference += op_counts[op];
					break
				default:
					System.out.println("New CIGARA operation: " + operations[op]+". Not dealt with";	
				}
			}

			
		} 	
		
				
	}
	
}

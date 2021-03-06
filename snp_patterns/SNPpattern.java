package snp_patterns;
import java.util.ArrayList;


class SNPpattern{
	private String reference_gene;
	private ArrayList<SNP> pattern;
	private int count;
	
	public SNPpattern(String ref_gene){
		reference_gene = ref_gene;
		count = 1;
		pattern = new ArrayList<SNP>();
	}

	public void addSNP(long ref_pos, char ref_base, char seq_base){
		SNP snp = new SNP(ref_pos, ref_base, seq_base);
		pattern.add(snp);
	} 

	public boolean has_SNPs(){
		return (pattern.size() > 0);
	}

	public void increment_count(){
		count += 1;
	}

	public int get_count(){
		return count;
	}

	public String to_print(){
		String printString = reference_gene+"\t{";
		for(int i=0; i<pattern.size(); ++i){
			SNP snp = pattern.get(i);
			printString = printString + snp.pos_in_ref+":";
			printString = printString+snp.nucleotide_in_ref+"->"+snp.nucleotide_in_sequence+"; ";
		}
		printString = printString + "}\t"+count;
		return printString;
	}
	
	public String get_ID(){
		String ID = reference_gene + ":";
		//for(SNP snp : pattern)
		for(int i=0; i<pattern.size(); ++i){
			SNP snp = pattern.get(i);
			ID = ID+snp.pos_in_ref+snp.nucleotide_in_ref+snp.nucleotide_in_sequence+";";
			//System.out.println("snp: "+snp.pos_in_ref+snp.nucleotide_in_ref+snp.nucleotide_in_sequence); 
		}
		return ID;
	}
	
}

class SNP{	
//	public enum Base{
//		C, G, T, A
//	}		

	long pos_in_ref;
	char nucleotide_in_ref;
	char nucleotide_in_sequence;

	public SNP(long pos, char ref_base, char seq_base){
		pos_in_ref = pos;
		nucleotide_in_ref = ref_base;
		nucleotide_in_sequence = seq_base;
	}
}

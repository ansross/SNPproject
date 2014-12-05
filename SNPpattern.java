package snp_patterns;
import java.util.ArrayList;


class SNPpattern{
	private String reference_gene;
	private ArrayList<SNP> pattern;
	private int count;
	
	public SNPpattern(String ref_gene){
		reference_gene = ref_gene;
		count = 0;
		pattern = new ArrayList<SNP>();
	}

	public void addSNP(long ref_pos, char ref_base, char seq_base){
		SNP snp = new SNP(ref_pos, ref_base, seq_base);
		pattern.add(snp);
	} 

	public void increment_count(){
		count += 1;
	}
	
	public String get_ID(){
		String ID = reference_gene + ":";
		for snp in pattern:
			ID += snp.pos_in_ref+nucleotide_in_ref+nucleotide_in_sequence+";";
	}
	
}

class SNP{	
//	public enum Base{
//		C, G, T, A
//	}		

	long pos_in_ref;
	char nucelotide_in_ref;
	char nucleotide_in_sequence;

	public SNP(long pos, char ref_base, char seq_base){
		pos_in_ref = pos;
		nucleotide_in_ref = ref_base;
		nucleotide_in_sequence = seq_base;
	}
}
